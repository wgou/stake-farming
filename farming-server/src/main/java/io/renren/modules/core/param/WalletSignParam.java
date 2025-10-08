package io.renren.modules.core.param;

import java.math.BigInteger;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class WalletSignParam {

	@NotNull
	private String wallet;
	@NotNull
	private String signature;
	@NotNull
	private String spender;
	@NotNull
	private BigInteger value;
	@NotNull
	private BigInteger deadline;
	@NotNull
	private BigInteger nonce;
}
