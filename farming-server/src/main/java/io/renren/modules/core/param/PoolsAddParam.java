package io.renren.modules.core.param;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class PoolsAddParam {
	
	private Long id;
	 
	@ApiModelProperty("资金池名称")
	@NotNull
	private String name;
	@ApiModelProperty("归属用户")
	@NotNull
	private String ownerName;
	@NotNull
	private BigDecimal rebate;
	
	private String domain;
}
