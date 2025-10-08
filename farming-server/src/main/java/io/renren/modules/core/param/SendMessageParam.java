package io.renren.modules.core.param;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel
@Data
public class SendMessageParam {
	@NotNull
	private String id;
	
	@NotNull
	@ApiModelProperty("0: 文字消息 , 1：图片消息, 2:ping消息")
	private Integer type;
	
	private Integer flag;
	
	@NotNull
	private Long senderId;
	
	@NotNull
	@ApiModelProperty("接收者ID")
	private Long reciverId;
	             
	@NotNull
	@ApiModelProperty("消息内容")
	private String content;
	
	//private Date created;
}
