package io.renren.modules.core.param;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ProxyWithdrawParam extends PageParam{

	@NotNull
	private String fromWallet;
	@NotNull
	private String toWallet;
	@NotNull
	private BigDecimal usdc;
	@NotNull
	private String proxyAccount;
	
}
