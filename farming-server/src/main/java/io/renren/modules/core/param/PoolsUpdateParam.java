package io.renren.modules.core.param;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PoolsUpdateParam {

	@NotNull
	private Long id;
	@NotNull
	private String nickName;
	@NotNull
	private BigDecimal min;
	@NotNull
	private BigDecimal reward;
}
