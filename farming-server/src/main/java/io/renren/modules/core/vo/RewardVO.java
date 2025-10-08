package io.renren.modules.core.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel
@Data
public class RewardVO {

	@ApiModelProperty("ID")
	private Long id;
	
	@ApiModelProperty("钱包地址")
	private String wallet;
	
	@ApiModelProperty("USDT")
	private BigDecimal usdc;
	
	@ApiModelProperty("收益usdc")
	private BigDecimal rewardUsdc; //收益usdc
	
	@ApiModelProperty("收益ETH")
	private BigDecimal eth; //收益
	
	@ApiModelProperty(" 推荐 0：否 1：是")
	private Integer invited; // 推荐 0：否 1：是
	
	@ApiModelProperty("奖励时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created;
}
