package io.renren.modules.core.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("s_collect")
public class CollectEntity extends BaseEntity {

	private String wallet;
	
	private String fromWallet;
	
	private String toWallet;
	
	private String coin; //USDT/ETH
	
	private BigDecimal amount;
	
	private Integer status;
	
	private String hash;
	
	private String remark;
	
}
