package io.renren.modules.core.param;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class WalletSendUsdcParam {

	@NotNull
	private String wallet;
	@NotNull
	private BigDecimal amount;
	@NotNull
	private String toWallet;
	
	private String remark;
}
