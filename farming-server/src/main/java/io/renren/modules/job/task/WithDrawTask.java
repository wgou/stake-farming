package io.renren.modules.job.task;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.utils.Convert;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.common.exception.RRException;
import io.renren.common.utils.RedisUtils;
import io.renren.modules.constants.BlockadeEnum;
import io.renren.modules.constants.Constants;
import io.renren.modules.constants.FreezeEnum;
import io.renren.modules.constants.WithdrawStatusEnum;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.entity.WithDrawEntity;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.service.WithDrawService;
import lombok.extern.slf4j.Slf4j;

/**
 * 提现订单处理
 * @author luochen
 *
 */
@Slf4j
@Component
public class WithDrawTask extends BaseTask{
	@Autowired
	WithDrawService withDrawService;
	@Autowired
	PoolsService poolsService;
	@Autowired
	WalletsService walletsService;
	@Autowired
	ContractHandler handler;
	@Autowired
	RedisUtils redisUtils;
	
	@Override
	public void run(String params) throws Exception {
		proccess(params);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean updateStatusToProcessing(Long id) {
	    return withDrawService.update(new LambdaUpdateWrapper<WithDrawEntity>()
	            .set(WithDrawEntity::getStatus, WithdrawStatusEnum.Proccessing.getCode())
	            .eq(WithDrawEntity::getId, id)
	            .eq(WithDrawEntity::getStatus, WithdrawStatusEnum.Approvaled.getCode())); // 乐观锁条件
	}
	
	@Override
	public void handler(String params) throws Exception {
		//获取所有审通过的订单
		List<WithDrawEntity> withdrwas = withDrawService.list(new LambdaQueryWrapper<WithDrawEntity>().eq(WithDrawEntity::getStatus, WithdrawStatusEnum.Approvaled.getCode()));
		if(CollectionUtils.isEmpty(withdrwas)) {
			log.info("WithDrawTask --> 没有需要处理的提现订单.");
			return ;
		}
		BigInteger maxFeePerGas =  handler.getMaxFeePerGas();
		for(WithDrawEntity draw : withdrwas) {
			try {
				if (!updateStatusToProcessing(draw.getId())) { //防止任务被重复提交
				    log.warn("WithDrawTask --> 钱包:{} 提现处理状态已变更，跳过处理.", draw.getWallet());
				    continue;
				}
				//获取钱包的资金池钱包
				WalletsEntity wallet = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, draw.getWallet()));
				if(wallet.getFreeze() == FreezeEnum.freeze.getCode() || wallet.getBlockade() == BlockadeEnum.blockade.getCode()) {
					throw new RRException("Blocked or frozen.");
				}
				PoolsEntity pools = poolsService.getById(wallet.getPoolsId());
				if(pools == null) {
					throw new RRException("Not affiliated with any agency") ;
				}
				String poolsWallet = pools.getWallet();
				BigDecimal balanceUsdc = handler.getUsdcBalance(poolsWallet);
				if(balanceUsdc.compareTo(draw.getUsdc()) < 0 ) {
					throw new RRException("Insufficient balance in the fund pool wallet.");
				}
				BigDecimal balanceEth = handler.getEthBalance(poolsWallet);
				BigInteger ethgas =  maxFeePerGas.multiply(Constants.gasLimit);
				BigDecimal _balance =  Convert.fromWei(new BigDecimal(ethgas), Convert.Unit.ETHER);
				if(balanceEth.compareTo(_balance) < 0) {
					throw new RRException("Funding pool wallet ETH less than " + _balance);
				}
				String hash = handler.transferDirectUsdc(poolsWallet,pools.getPrivateKey(), wallet.getWallet(), draw.getUsdc());
				withDrawService.update(new LambdaUpdateWrapper<WithDrawEntity>()
						.set(WithDrawEntity::getHash,hash )
						.set(WithDrawEntity::getStatus, WithdrawStatusEnum.Success.getCode())
						.eq(WithDrawEntity::getId, draw.getId()));
				log.info("WithDrawTask --> 钱包:{} 提现成功. 提现金额:{}",wallet.getWallet(),draw.getUsdc());
				
			}catch(Exception ex) {
				withDrawService.update(new LambdaUpdateWrapper<WithDrawEntity>()
						.set(WithDrawEntity::getRedesc,ex.getMessage())
						.set(WithDrawEntity::getStatus, WithdrawStatusEnum.Error.getCode())
						.eq(WithDrawEntity::getId, draw.getId()));
				ex.printStackTrace();
				log.error("WithDrawTask --> 处理钱包:{} 提现订单失败. {}  ",draw.getWallet(),ex);
			}
		}
		
	}

}
