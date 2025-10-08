package io.renren.modules.core.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("s_wallet_trade_record")
public class WalletTradeEntity extends BaseEntity{

	private String wallet;
	
	private String direction;
	
	private BigDecimal usdc;
	
	private String hash;
}
