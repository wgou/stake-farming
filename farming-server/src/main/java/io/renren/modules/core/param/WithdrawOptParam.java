package io.renren.modules.core.param;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class WithdrawOptParam extends PoolsParam{

	@NotNull
	private Long id;
	@NotNull
	private String googleAuthCode;
}
