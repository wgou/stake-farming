package io.renren.modules.core.param;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel
@Data
public class RewardParam extends PageParam{

	@ApiModelProperty("钱包地址")
	private String wallet;
	
	@ApiModelProperty("是否推荐奖励 0：否 1：是")
	private Integer invited;  // 推荐 0：否 1：是
}
