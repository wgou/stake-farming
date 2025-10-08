package io.renren.modules.core.entity;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class WalletSummary {
	private BigDecimal usdc;
    private BigDecimal virtualUsdc;
}
