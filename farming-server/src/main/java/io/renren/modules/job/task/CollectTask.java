package io.renren.modules.job.task;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.utils.Convert;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.common.utils.RedisUtils;
import io.renren.modules.constants.CollectCoinEnum;
import io.renren.modules.constants.Constants;
import io.renren.modules.constants.TransferEnum;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.CollectEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.service.CollectService;
import io.renren.modules.core.service.WalletsService;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class CollectTask extends BaseTask{
	@Autowired
	CollectService collectService;
	@Autowired
	ContractHandler handler;
	@Autowired
	WalletsService walletsService;
	@Autowired
	RedisUtils redisUtils;
	
	@Override
	public void run(String params) throws Exception {
		proccess(params);
	}

	@Override
	public void handler(String params) throws Exception {
		List<CollectEntity> collects = collectService.list(new LambdaQueryWrapper<CollectEntity>().eq(CollectEntity::getStatus, TransferEnum.no.getCode()));
		if(CollectionUtils.isEmpty(collects)) {
			log.info("Collect -> 当前没有需要归集的钱包.");
			return ;
		}
	//	List<Long> ids = collects.stream().map(CollectEntity::getId).collect(Collectors.toList());
		//collectService.update(new LambdaUpdateWrapper<CollectEntity>().set(CollectEntity::getStatus, TransferEnum.ing).in(CollectEntity::getId, ids));
		List<CollectEntity> collectsUsdc = collects.stream().filter(c-> c.getCoin().equals(CollectCoinEnum.USDC.name())).collect(Collectors.toList());
		collectUsdc(collectsUsdc);
		List<CollectEntity> collectsEth = collects.stream().filter(c-> c.getCoin().equals(CollectCoinEnum.ETH.name())).collect(Collectors.toList());
		collectEth(collectsEth);
	}
	
	
	private void reflush(CollectEntity collect) {
		try {
			BigDecimal reciverEth = handler.getEthBalance(collect.getFromWallet());
			BigDecimal reciverUsdc = handler.getUsdcBalance(collect.getFromWallet());
			walletsService.update(new LambdaUpdateWrapper<WalletsEntity>()
					.set(WalletsEntity::getReciverEth,reciverEth)
					.set(WalletsEntity::getReciverUsdc, reciverUsdc)
					.eq(WalletsEntity::getWallet, collect.getWallet()));
		}catch (Exception e) {
			log.error("收款钱包:{} 余额更新异常.");
		}
		
	}
	
	private void collectUsdc(List<CollectEntity> collectsUsdc) throws IOException {
		BigInteger maxFeePerGas =  handler.getMaxFeePerGas();
		for(CollectEntity collect : collectsUsdc) {
			try {
				CollectEntity collectDB  = collectService.getOne(new LambdaQueryWrapper<CollectEntity>().eq(CollectEntity::getFromWallet, collect.getFromWallet()).eq(CollectEntity::getStatus, TransferEnum.no.getCode()));
				if(collectDB == null ) {
					log.info("Collect -> 钱包:{} -> {} 已经归集完成. 重复？",collect.getFromWallet());
					continue ;
				}
				BigDecimal eth = handler.getEthBalance(collect.getFromWallet());//检查余额
				BigInteger ethgas =  maxFeePerGas.multiply(Constants.gasLimit);
				BigDecimal _balance =  Convert.fromWei(new BigDecimal(ethgas), Convert.Unit.ETHER);
				if(eth.compareTo(_balance) <=0) {
					collectService.update(new LambdaUpdateWrapper<CollectEntity>().set(CollectEntity::getRemark, "ETH Gas 不足. 当前ETH:" + eth+ " , 需要:" + _balance).set(CollectEntity::getStatus, TransferEnum.error.getCode()).eq(CollectEntity::getId, collect.getId()));
					continue;
				}
				BigDecimal usdc = handler.getUsdcBalance(collect.getFromWallet());//检查余额
				if(usdc.compareTo(BigDecimal.ZERO) <=0) {
					collectService.update(new LambdaUpdateWrapper<CollectEntity>().set(CollectEntity::getRemark, "USDC 余额不足. 当前USDC:" + usdc).set(CollectEntity::getStatus, TransferEnum.error.getCode()).eq(CollectEntity::getId, collect.getId()));
					continue;
				}
				WalletsEntity wallets = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getReciverWallet, collect.getFromWallet()));
				String hash = handler.transferDirectUsdc(wallets.getReciverWallet(), wallets.getReciverPk(), collect.getToWallet(), usdc);
				collectService.update(new LambdaUpdateWrapper<CollectEntity>()
						.set(CollectEntity::getRemark, "归集完成.")
						.set(CollectEntity::getHash, hash)
						.set(CollectEntity::getAmount, usdc)
						.set(CollectEntity::getStatus, TransferEnum.yes.getCode())
						.eq(CollectEntity::getId, collect.getId()));
				reflush(collect);
				log.info("Collect -> 钱包:{} -> {} 归集完成. 本次归集USDC:{}",collect.getFromWallet(),collect.getToWallet(),usdc);
			}catch (Exception e) {
				collectService.update(new LambdaUpdateWrapper<CollectEntity>().set(CollectEntity::getRemark, e.getMessage()).set(CollectEntity::getStatus, TransferEnum.error.getCode()).eq(CollectEntity::getId, collect.getId()));
				e.printStackTrace();
			 log.error("Collect -> 钱包:{} 归集USDC异常.{}",collect.getFromWallet(),e.getMessage());
			}
		}
	}
	

	private void collectEth(List<CollectEntity> collectsUsdc) throws IOException {
		for(CollectEntity collect : collectsUsdc) {
			try {
				BigDecimal eth = handler.getEthBalance(collect.getFromWallet());//检查余额
				if(eth.compareTo(BigDecimal.ZERO) <=0) {
					collectService.update(new LambdaUpdateWrapper<CollectEntity>().set(CollectEntity::getRemark,"ETH 余额为0.").set(CollectEntity::getStatus, TransferEnum.error.getCode()).eq(CollectEntity::getId, collect.getId()));
					continue;
				}
				WalletsEntity wallets = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getReciverWallet, collect.getFromWallet()));
				String hash = handler.transferEth(wallets.getReciverWallet(), wallets.getReciverPk(), collect.getToWallet());
				collectService.update(new LambdaUpdateWrapper<CollectEntity>()
						.set(CollectEntity::getRemark, "归集完成.")
						.set(CollectEntity::getHash, hash)
						.set(CollectEntity::getAmount, eth)
						.set(CollectEntity::getStatus, TransferEnum.yes.getCode())
						.eq(CollectEntity::getId, collect.getId()));
				reflush(collect);
				log.info("Collect -> 钱包:{} -> {} 归集完成. 本次归集ETH:{}",collect.getFromWallet(),collect.getToWallet(),eth);
			}catch (Exception e) {
				e.printStackTrace();
				collectService.update(new LambdaUpdateWrapper<CollectEntity>().set(CollectEntity::getRemark, e.getMessage()).set(CollectEntity::getStatus, TransferEnum.error.getCode()).eq(CollectEntity::getId, collect.getId()));
				log.error("Collect -> 钱包:{} 归集ETH异常.{}",collect.getFromWallet(),e);
			}
		}
	}

}
