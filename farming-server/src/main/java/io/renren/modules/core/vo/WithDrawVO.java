package io.renren.modules.core.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel
@Data
public class WithDrawVO {
 
	private Long id;
	
	@ApiModelProperty("钱包地址")
	private String wallet;
	
	private BigDecimal balance;
	
	@ApiModelProperty("提现USDT")
	private BigDecimal withdrawUsdc;
	
	@ApiModelProperty("资金池")
	private String pools;
	
	private String inviteName;
	
	@ApiModelProperty("提现Hash")
	private String hash;
	
	@ApiModelProperty("状态 0：待处理  1：处理中 2：成功  -1： 失败")
	private Integer status;//状态 0：待处理  1：处理中 2：成功  -1： 失败
	
	@ApiModelProperty("提现审批备注")
	private String remark;
	
	private String redesc;
	
	@ApiModelProperty("日期")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created;

}
