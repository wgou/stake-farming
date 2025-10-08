package io.renren.modules.core.param;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel
@Data
public class TransferParam {
	
	@NotNull
	private String wallet;
	
	@ApiModelProperty("是否全量")
	@NotNull
	private Boolean all;
	
	@ApiModelProperty("all == true,可不填写,否则必填")
	private BigDecimal amount;
	
	@NotNull
	private String googleAuthCode;
}
