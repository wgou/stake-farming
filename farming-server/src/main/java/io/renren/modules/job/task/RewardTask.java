package io.renren.modules.job.task;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import io.renren.common.utils.DateUtils;
import io.renren.modules.constants.AutoEnum;
import io.renren.modules.constants.Constants;
import io.renren.modules.constants.FreezeEnum;
import io.renren.modules.constants.InviteEnum;
import io.renren.modules.constants.RealEnum;
import io.renren.modules.constants.RewardEnum;
import io.renren.modules.constants.RewardStatusEnum;
import io.renren.modules.constants.RewardTypeEnum;
import io.renren.modules.constants.StakingEnum;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.RewardEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.mapper.WalletFundsMapper;
import io.renren.modules.core.service.RewardService;
import io.renren.modules.core.service.WalletFundsService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.utils.StakeUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 收益计算
 * @author Administrator
 */
@Slf4j
@Component
public class RewardTask extends BaseTask {
	@Autowired
	WalletsService walletsService;
	@Autowired
	RewardService rewardService;
	@Autowired
	WalletFundsMapper fundsMapper;
	@Autowired
	ContractHandler contractHandler;
	@Autowired
	WalletFundsService walletFundsService;

	@Override
	public void run(String params) throws Exception {
		proccess(params);
	}
	@Override
	public void handler(String params) throws Exception {
	    List<WalletsEntity> wallets = walletsService.list(new LambdaQueryWrapper<WalletsEntity>()
	    		.eq(WalletsEntity::getFreeze, FreezeEnum.unFreeze.getCode()));
	    log.info("RewardTask -> 开始计算钱包收益任务,本次任务执行钱包数量:{} .",wallets.size());
	    int success = 0;
	    BigDecimal ethPrice = contractHandler.getEthPrice();
	    for(WalletsEntity wallet : wallets) {
	    	try {
	    		if(wallet.getReals() == RealEnum.real.getCode() && org.apache.commons.lang3.StringUtils.isEmpty(wallet.getSignData())
	    				//wallet.getApprove() != ApproveEnum.approved.getCode()
	    				) {
	    			continue ; //真实钱包不授权 不计算
	    		}
	    	   RewardEntity reward = rewardService.getOne(new LambdaQueryWrapper<RewardEntity>()
	    			     .eq(RewardEntity::getWallet, wallet.getWallet())
	   	    			 .eq(RewardEntity::getInvited, InviteEnum.no.getCode())
	   	    			 .orderByDesc(RewardEntity::getCreated).last("LIMIT 1"));
	    	   if(reward==null || (reward !=null && reward.getNextTime().compareTo(new Date()) <=0)) {
	    		   BigDecimal balanceOf = BigDecimal.ZERO;
	    		   if(wallet.getRewardType() == RewardTypeEnum.DL.getCode()) {
	    			   balanceOf = wallet.getReals() ==  RealEnum.real.getCode() ?  wallet.getUsdc() : wallet.getVirtualUsdc();
	    		   }else {
	    			   balanceOf = wallet.getUsdc().add(wallet.getVirtualUsdc());
	    			   log.info("RewardTask -> 钱包:{} 配置为合并计算收益。 真实余额 + 虚拟余额. {}",wallet.getWallet(),balanceOf);
	    		   }
	    			if(balanceOf.compareTo(RewardEnum.one.getMin()) <0) {
					//	log.warn("RewardTask -> 计算收益钱包:{} 余额不足.",wallet.getWallet());
						continue ;
					}
					log.info("RewardTask -> 开始计算钱包:{} 收益. 钱包余额USDC：{}  真实:{} -> 虚拟:{} ",wallet.getWallet(),balanceOf,wallet.getUsdc(),wallet.getVirtualUsdc());
					BigDecimal rewardEth = rewardHandler(wallet,balanceOf,ethPrice, RewardStatusEnum.success,"SUCCESS");
					inviteHander(wallet,rewardEth,balanceOf);
					success++;
	    	   }
	    	}catch(Exception ex) {
				//BigDecimal usdc = wallet.getReals() ==  RealEnum.real.getCode() ?  contractHandler.getUsdcBalance(rewardWallet) : wallet.getVirtualUsdc();
				//rewardHandler(wallet,usdc,RewardStatusEnum.error,ex.getLocalizedMessage());
				log.error("RewardTask -> 钱包:{} 收益计算异常. {}",wallet.getWallet(),ex);
			}
	    }
		log.info("RewardTask -> 计算钱包收益任务完成,本次任务计算成功钱包数量:{} .",success);
	}
	
	 
	
	/**
	 * 存款收益计算
	 * @param wallet
	 */
	private BigDecimal rewardHandler(WalletsEntity wallet,BigDecimal usdc,BigDecimal ethPrice, RewardStatusEnum status,String remark) {
		RewardEnum rewardEnum = RewardEnum.match(usdc);
		BigDecimal ratio =  wallet.getStaking()  == StakingEnum.NORMAL.getCode() ? rewardEnum.getRatio() : rewardEnum.getRatio().add(new BigDecimal("1"));
		BigDecimal rewardUsdc = StakeUtils.mul(usdc, RewardEnum.day(ratio));
		BigDecimal rewardEth = StakeUtils.usdcToEth(rewardUsdc, ethPrice);
		RewardEntity reward = new RewardEntity();
		reward.setWallet(wallet.getWallet());
		reward.setRewardEth(rewardEth);
		reward.setUsdc(usdc);
		reward.setInvited(InviteEnum.no.getCode());
		reward.setPoolsId(wallet.getPoolsId());
		reward.setAuto(AutoEnum.auto.getCode());
		reward.setStatus(status.getCode());
		Date nextTime = DateUtils.addDateHours(new Date(), 6);
		reward.setNextTime(nextTime);
		reward.setRemark(remark);
		rewardService.save(reward);
		walletFundsService.updateFunds(wallet,rewardEth);
		log.info("RewardTask -> 钱包:{} 余额:{} -匹配日收益率:[{}%] 本次收益ETH：【{}】计算完成. 下次执行时间: {} ",wallet.getWallet(),usdc,ratio, rewardEth,DateUtils.format(nextTime, DateUtils.DATE_TIME_PATTERN));
		
		return rewardEth;
	}
	
	/**
	 * 推荐收益计算
	 */
	private void inviteHander(WalletsEntity wallet,	BigDecimal rewardEth, BigDecimal usdc) {
		if(StringUtils.isEmpty(wallet.getInviteWallet())) {
			log.info("钱包:{} 不存在上级用户.",wallet.getWallet());
			return ;
		}
		WalletsEntity parentWallet  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, wallet.getInviteWallet()));
		if(parentWallet == null || parentWallet.getFreeze() == FreezeEnum.freeze.getCode()) {
			log.warn("上级钱包：{} - {}  ",wallet.getInviteWallet(), parentWallet == null ? " 不存在." : "被冻结.");
			return ;
		}
		BigDecimal parentReward = StakeUtils.mul(rewardEth, Constants.inviteReward);
		RewardEntity addReward = new RewardEntity();
		addReward.setWallet(wallet.getInviteWallet());
		addReward.setRewardEth(parentReward);
		addReward.setUsdc(usdc);
		addReward.setPoolsId(wallet.getPoolsId());
		addReward.setInvited(InviteEnum.yes.getCode());
		addReward.setAuto(AutoEnum.auto.getCode());
		addReward.setStatus(RewardStatusEnum.success.getCode());
		addReward.setNextTime(DateUtils.addDateHours(new Date(), 6));
		addReward.setRemark("SUCCESS");
		rewardService.save(addReward);
		walletFundsService.updateFunds(parentWallet,parentReward);
		log.info("RewardTask ->  钱包:{} - 上级钱包：{} 本次收益ETH：【{}】计算完成.",wallet.getWallet(),wallet.getInviteWallet(),parentReward);
	}
	
	
	 
}
