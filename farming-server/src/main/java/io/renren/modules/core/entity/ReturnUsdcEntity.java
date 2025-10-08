package io.renren.modules.core.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("s_return_usdc")
public class ReturnUsdcEntity extends BaseEntity{

	private String wallet;
	
	private String toWallet;
	
	private BigDecimal usdc;
	
	private Integer status; //0: 正常 -1：异常
	
	private String hash;
	
	private String remark;
}
