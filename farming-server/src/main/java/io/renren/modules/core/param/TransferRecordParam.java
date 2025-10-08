package io.renren.modules.core.param;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel
@Data
public class TransferRecordParam extends PageParam{
	@ApiModelProperty("钱包地址")
	private String wallet;
	
	@ApiModelProperty("招聘人员")
	private Long inviteId;
	
	private Integer status;
	
	@ApiModelProperty("起始时间")
	 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
	private Date start;
	
	@ApiModelProperty("截止时间")
	 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
	private Date end;
}
