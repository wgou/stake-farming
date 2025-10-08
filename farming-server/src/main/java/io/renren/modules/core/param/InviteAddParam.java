package io.renren.modules.core.param;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel
@Data
public class InviteAddParam {
	
	@ApiModelProperty("邀请码")
	@NotNull
	private String code;
	
	@ApiModelProperty("名称")
	@NotNull
	private String name;

}
