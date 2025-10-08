package io.renren.modules.core.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WalletTradeVO {
	
	private String wallet;
	
	private String direction;
	
	private BigDecimal usdc;
	
	private String hash;
	
	private String pools;
	
	private String inviteName;
	

	@ApiModelProperty("日期")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created;
	

}
