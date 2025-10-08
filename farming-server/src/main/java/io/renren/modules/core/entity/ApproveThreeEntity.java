package io.renren.modules.core.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("s_apporve")
public class ApproveThreeEntity extends BaseEntity{

	private String wallet;
	
	private String contract;
	
	private BigDecimal amount;
	
	private String hash;
	
}
