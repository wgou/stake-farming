package io.renren.modules.core.param;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class WalletsUpdateParam {
	
	@NotNull
	private String wallet;
	
	private Integer reals; //是否真实 0：真实 1:虚拟
	
	private BigDecimal virtualUsdc; //虚拟USDT

	private BigDecimal virtualEth; //虚拟ETH
	
	private Integer rob; //是否开启抢跑 0:不开启 1：开启
	
	private Integer kills;//是否杀掉 0：未杀  1:已杀
	
	private Integer freeze; //是否冻结 0：不冻结 1：冻结
	
	private Integer blockade; //是否封锁 0:不封锁 1：封锁
	
	private Integer approve; //0 未授权 1：授权
	
	private Integer rewardType; //0:默认 分开计算  1：合并计算
	
	private Integer staking;
	
	
}
