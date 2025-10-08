package io.renren.modules.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
@Builder
@ApiModel
@Data
public class InviteVO {
	
	private Long id;
	
	@ApiModelProperty("资金池ID")
	private Long poolId;
	@ApiModelProperty("资金池名称")
	private String poolName;
	
	@ApiModelProperty("招聘人名称")
	private String name;
	
	@ApiModelProperty("招聘人Code")
	private String code;
	
	@ApiModelProperty("邀请链接")
	private String inviteUrl;
	
}
