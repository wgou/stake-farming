package io.renren.modules.core.service;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.extension.service.IService;

import io.renren.modules.core.entity.WalletFundsEntity;
import io.renren.modules.core.entity.WalletsEntity;

public interface WalletFundsService  extends IService<WalletFundsEntity> {

	
	public void updateFunds(WalletsEntity wallet,BigDecimal rewardEth);
}
