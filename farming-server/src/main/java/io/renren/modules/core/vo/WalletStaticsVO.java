package io.renren.modules.core.vo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class WalletStaticsVO {
	
	private int walletCount = 0;
	
	private BigDecimal transferUsdc = BigDecimal.ZERO;
	
	private BigDecimal allowanceUsdc = BigDecimal.ZERO;
}
