package io.renren.modules.core.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.modules.core.entity.WalletFundsEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.mapper.WalletFundsMapper;
import io.renren.modules.core.service.WalletFundsService;

@Service
public class WalletFundsServiceImpl  extends ServiceImpl<WalletFundsMapper, WalletFundsEntity> implements WalletFundsService {

	@Autowired
	WalletFundsMapper fundsMapper;
	
	public void updateFunds(WalletsEntity wallet,BigDecimal rewardEth) {
		WalletFundsEntity funds = fundsMapper.selectOne(new LambdaQueryWrapper<WalletFundsEntity>().eq(WalletFundsEntity::getWallet, wallet.getWallet()));
		if(funds == null) {
			funds = new WalletFundsEntity();
			funds.setWallet(wallet.getWallet());
			funds.setTotalReward(rewardEth);
			funds.setPoolsId(wallet.getPoolsId());
			funds.setBalanceReward(rewardEth);
			fundsMapper.insert(funds);
		}else {
			WalletFundsEntity updateFunds = new WalletFundsEntity();
			BigDecimal totalReward = funds.getTotalReward().add(rewardEth);
			BigDecimal balanceReward = funds.getBalanceReward().add(rewardEth);
			updateFunds.setId(funds.getId());
			updateFunds.setTotalReward(totalReward);
			updateFunds.setBalanceReward(balanceReward);
			fundsMapper.updateById(updateFunds);
		}
	}
}
