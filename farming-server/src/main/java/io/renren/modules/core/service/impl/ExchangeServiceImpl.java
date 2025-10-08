package io.renren.modules.core.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.common.exception.RRException;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.ExchangeEntity;
import io.renren.modules.core.entity.WalletFundsEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.mapper.ExchangeMapper;
import io.renren.modules.core.service.ExchangeService;
import io.renren.modules.core.service.WalletFundsService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.utils.StakeUtils;

@Service
public class ExchangeServiceImpl  extends ServiceImpl<ExchangeMapper, ExchangeEntity> implements ExchangeService {

	@Autowired
	WalletFundsService fundsService;
	@Autowired
	WalletsService walletsService;
	@Autowired
	ContractHandler handler;
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void exchange(String wallet) throws Exception {
		WalletsEntity walletEntity  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, wallet));
		if(walletEntity == null) throw new RRException("Invalid wallet");
		WalletFundsEntity fundsEntity = fundsService.getOne(new LambdaQueryWrapper<WalletFundsEntity>().eq(WalletFundsEntity::getWallet, wallet));
		if(fundsEntity == null) throw new RRException("No available balance");
		if(fundsEntity.getBalanceReward().compareTo(BigDecimal.ZERO) <= 0) throw new RRException("Insufficient balance");
		//奖励兑换 - 兑换之后扣减奖励余额 - 增加兑换总数 - 增加可提取数量
		BigDecimal ethPrice = handler.getEthPrice();
		BigDecimal newBalanceReward = BigDecimal.ZERO;
		BigDecimal newExchange = fundsEntity.getExchange().add(fundsEntity.getBalanceReward());
		BigDecimal extractableUsdc = StakeUtils.ethToUsdc(fundsEntity.getBalanceReward(), ethPrice);
		BigDecimal newExtractable = fundsEntity.getExtractable().add(extractableUsdc);
		fundsService.update(new LambdaUpdateWrapper<WalletFundsEntity>()
				.set(WalletFundsEntity::getBalanceReward, newBalanceReward)
				.set(WalletFundsEntity::getExchange, newExchange)
				.set(WalletFundsEntity::getExtractable, newExtractable)
				.eq(WalletFundsEntity::getWallet, wallet));
		ExchangeEntity exchangeEntity = new ExchangeEntity();
		exchangeEntity.setWallet(wallet);
		exchangeEntity.setPoolsId(walletEntity.getPoolsId());
		exchangeEntity.setEth(fundsEntity.getBalanceReward());
		exchangeEntity.setEthPrice(ethPrice);
		exchangeEntity.setUsdc(extractableUsdc);
		this.save(exchangeEntity);
	}

}
