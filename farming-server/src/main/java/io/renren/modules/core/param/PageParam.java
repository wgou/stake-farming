package io.renren.modules.core.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel
@Data
public class PageParam extends PoolsParam{
	
	@ApiModelProperty(value = "当前页数", example = "1")
	private long current = 1L;
	@ApiModelProperty(value = "每页条数", example = "10")
	private long size = 10L;
	
}
