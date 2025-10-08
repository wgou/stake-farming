package io.renren.modules.core.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("s_withdraw")
public class WithDrawEntity extends BaseEntity{

	private String wallet;
	
	private BigDecimal balance;
	
	private BigDecimal usdc;
	
	private Long inviteId;
	
	private String hash;
	
	private Integer status;//状态 0：待处理  1：处理中 2：成功  -1： 失败
	
	private Integer reals;
	
	private String remark;
	
	private String redesc;
	
	
	
	
	
}
