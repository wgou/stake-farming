package io.renren.modules.core.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageUserVO {
	
	@ApiModelProperty("用户Id")
	private Long id;
	
	@ApiModelProperty("钱包地址")
	private String wallet;
	
	private Long poolsId;
	
	private String ownerName;
	
	private String inviter;
	
	@ApiModelProperty("是否在线")
	private boolean online;
	
	@ApiModelProperty("未读消息条数")
	private Integer messageCount;
	
	private String lastDate;//最后一条消息的时间
	
	
}
