package io.renren.modules.core.vo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WalletIndexVO {

	private BigDecimal usdc;
	
	private BigDecimal eth;
	
	private BigDecimal totalReward;
	
	private BigDecimal exchangeable;
	
	private BigDecimal accountBalance;
	
}
