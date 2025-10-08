package io.renren.modules.core.param;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Api
@Data
public class AdminMessageParam {
	
	@NotNull
	@ApiModelProperty("用户ID")
	private Long id;

}
