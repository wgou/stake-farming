package io.renren.modules.core.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
/**
 * 钱包资金管理
 * @author Administrator
 *
 */
@Data
@TableName("s_wallet_funds")
public class WalletFundsEntity extends BaseEntity{

	private String wallet;
	
	private BigDecimal totalReward; //奖励总数ETH
	
	private BigDecimal balanceReward; //奖励余额ETH
	
	private BigDecimal exchange; //已经兑换 ETH
	
	private BigDecimal extractable; //可提取USDT
	
	private BigDecimal withdraw; //提取总数USDT
	
}
