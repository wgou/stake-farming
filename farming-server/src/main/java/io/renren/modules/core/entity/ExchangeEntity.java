package io.renren.modules.core.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("s_exchange")
public class ExchangeEntity extends BaseEntity{

	private String wallet;
	
	private BigDecimal eth;
	
	private BigDecimal usdc;
	
	private BigDecimal ethPrice;
	
	
}
