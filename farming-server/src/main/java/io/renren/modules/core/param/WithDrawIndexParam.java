package io.renren.modules.core.param;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import lombok.Data;
@ApiModel
@Data
public class WithDrawIndexParam {

	@NotNull
	private BigDecimal usdc;
}
