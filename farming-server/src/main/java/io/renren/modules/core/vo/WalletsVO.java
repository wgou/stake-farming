package io.renren.modules.core.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel(value="钱包信息")
@Data
public class WalletsVO {

	@ApiModelProperty
	private Long id;
	
	@ApiModelProperty(value="钱包地址")
	private String wallet;
	
	@ApiModelProperty(value="推荐钱包")
	private String inviteWallet;
	
	
	@ApiModelProperty(value="授权钱包")
	private String approveWallet; //授权钱包
	
	private BigDecimal approveEth;
	
	@ApiModelProperty(value="接收钱包")
	private String reciverWallet; //接收钱包
	
	@ApiModelProperty(value="钱包eth")
	private BigDecimal eth;
	
	@ApiModelProperty(value="钱包usdc")
	private BigDecimal usdc; //账户余额
	
	@ApiModelProperty(value="接收钱包eth")
	private BigDecimal reciverEth; //收款钱包ETH
	
	@ApiModelProperty(value="接收钱包usdc")
	private BigDecimal reciverUsdc; //收款钱包USDT
	
	@ApiModelProperty(value="城市地址")
	private String addr; //用户地址
	
	@ApiModelProperty(value="IP地址")
	private String ip; //IP
	
	@ApiModelProperty(value="资金池")
	private String pools; //资金池
	
	@ApiModelProperty(value="inviteName")
	private String inviteName; //招聘员
	
	@ApiModelProperty(value="是否杀掉 0：未杀 1：已杀")
	private Integer kills; //是否杀掉 0：未杀 1：已杀
	
	@ApiModelProperty(value="是否开启抢跑 0:不开启 1：开启")
	private Integer rob; //是否开启抢跑 0:不开启 1：开启
	
	@ApiModelProperty(value="是否冻结 0：不冻结 1：冻结")
	private Integer freeze; //是否冻结 0：不冻结 1：冻结
	
	@ApiModelProperty(value="是否封锁 0:不封锁 1：封锁")
	private Integer blockade; //是否封锁 0:不封锁 1：封锁
	
	@ApiModelProperty(value="是否真实 0：真实 1:虚拟")
	private Integer reals; //是否真实 0：真实 1:虚拟
	
	@ApiModelProperty(value="是否授权 0:未授权 1 已授权")
	private Integer approve; //是否授权 0:未授权 1 已授权
	
	private Boolean signData;
	
	@ApiModelProperty(value="资金池数据")
	private Item item;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastDate;
	
	private Integer auto;
	
	private Integer rewardType;
	
	private Integer approveOther;
	
	private Integer staking;
	
	@ApiModelProperty(value="创建时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created;
	
	@ApiModelProperty(value="最新更新时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modified;
	
	
	@ApiModel
	@Data
	public static class Item{
		
		@ApiModelProperty(value="收益 ETH")
		private BigDecimal rewardEth;//收益 ETH
		
		@ApiModelProperty(value="可换 ETH")
		private BigDecimal swapEth; //可换 ETH
		
		@ApiModelProperty(value="已换 USDT")
		private BigDecimal  swapedUsdc;//已换 USDT
		
		@ApiModelProperty(value="可提 USDT")
		private BigDecimal withdrawUsdc ; //可提USDT
		
		@ApiModelProperty(value="已提 USDT")
		private BigDecimal withdrwaedUsdc ; //已提 USDT
		
		@ApiModelProperty(value="虚拟 ETH")
		private BigDecimal virtualEth; //虚拟ETH
		
		@ApiModelProperty(value="虚拟 ETUSDTH")
		private BigDecimal virtualUsdc; //虚拟USDT
		
		@ApiModelProperty(value="客户 USDT（真实+虚拟）")
		private BigDecimal usdc;//客户 USDT（真实+虚拟）
	}
	
	
	

}
