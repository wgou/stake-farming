package io.renren.modules.core.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("s_reward")
public class RewardEntity extends BaseEntity {

	private String wallet;
	
	private BigDecimal usdc;
	
	private BigDecimal rewardEth; //收益
	
	private Integer invited; //推荐 0：否 1：是
	
	private Integer auto;
	
	private Date nextTime; //下次收益发放时间
	
	private Integer status; //0: 正常 -1：异常
	
	private String remark;
 }
