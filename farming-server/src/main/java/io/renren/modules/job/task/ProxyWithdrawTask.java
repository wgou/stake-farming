package io.renren.modules.job.task;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.utils.Convert;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.modules.constants.Constants;
import io.renren.modules.constants.TransferEnum;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.ProxyWithdrawEntity;
import io.renren.modules.core.service.ProxyWithdrawService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class ProxyWithdrawTask extends BaseTask{

	@Autowired
	ProxyWithdrawService proxyWithdrawService;
	@Autowired
	ContractHandler handler;
	@Autowired
	WalletsService walletsService;
	@Autowired
	SysConfigService configService;
	
	@Override
	public void run(String params) throws Exception {
		proccess(params);
	}
	@Override
	public void handler(String params) throws Exception {
		List<ProxyWithdrawEntity> withdraws = proxyWithdrawService.list(new LambdaQueryWrapper<ProxyWithdrawEntity>().eq(ProxyWithdrawEntity::getStatus, TransferEnum.no.getCode()));
		if(CollectionUtils.isEmpty(withdraws)) {
			log.info("ProxyWithdraw -> 当前没有平台提现任务..");
			return ;
		}
		BigInteger maxFeePerGas =  handler.getMaxFeePerGas();
		for(ProxyWithdrawEntity withdraw : withdraws) {
			BigDecimal usdc = handler.getUsdcBalance(withdraw.getFromWallet());//检查余额
			BigDecimal eth = handler.getEthBalance(withdraw.getFromWallet());//检查余额
			BigInteger ethgas =  maxFeePerGas.multiply(Constants.gasLimit);
			BigDecimal _balance =  Convert.fromWei(new BigDecimal(ethgas), Convert.Unit.ETHER);
			if(eth.compareTo(_balance) <=0) {
				proxyWithdrawService.update(new LambdaUpdateWrapper<ProxyWithdrawEntity>()
						.set(ProxyWithdrawEntity::getRemark, "ETH Gas 不足. 当前ETH:" + eth)
						.set(ProxyWithdrawEntity::getBeforeUsdc, usdc)
						.set(ProxyWithdrawEntity::getAfterUsdc, usdc)
						.set(ProxyWithdrawEntity::getStatus, TransferEnum.error.getCode())
						.eq(ProxyWithdrawEntity::getId, withdraw.getId()));
				continue;
			}
			if(usdc.compareTo(withdraw.getUsdc()) < 0) {
				proxyWithdrawService.update(new LambdaUpdateWrapper<ProxyWithdrawEntity>()
						.set(ProxyWithdrawEntity::getRemark, "USDT 余额不足. 当前USDT:" +usdc )
						.set(ProxyWithdrawEntity::getBeforeUsdc, usdc)
						.set(ProxyWithdrawEntity::getAfterUsdc, usdc)
						.set(ProxyWithdrawEntity::getStatus, TransferEnum.error.getCode())
						.eq(ProxyWithdrawEntity::getId, withdraw.getId()));
				continue;
			}
			String pk = configService.getValue(withdraw.getFromWallet());
			String hash = handler.transferDirectUsdc(withdraw.getFromWallet(), pk, withdraw.getToWallet(), withdraw.getUsdc());
			BigDecimal afterUsdc = handler.getUsdcBalance(withdraw.getFromWallet());
			proxyWithdrawService.update(new LambdaUpdateWrapper<ProxyWithdrawEntity>()
					.set(ProxyWithdrawEntity::getRemark, "提现完成.")
					.set(ProxyWithdrawEntity::getBeforeUsdc, usdc)
					.set(ProxyWithdrawEntity::getAfterUsdc, afterUsdc)
					.set(ProxyWithdrawEntity::getHash, hash)
					.set(ProxyWithdrawEntity::getStatus, TransferEnum.yes.getCode())
					.eq(ProxyWithdrawEntity::getId, withdraw.getId()));
			log.info("ProxyWithdraw -> 钱包:{} -> {} 提现完成. 本次提现USDT:{}",withdraw.getFromWallet(),withdraw.getToWallet(),usdc);
		}
	}

}
