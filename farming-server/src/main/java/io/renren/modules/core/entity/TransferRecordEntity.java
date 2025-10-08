package io.renren.modules.core.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("s_transfer_record")
public class TransferRecordEntity extends BaseEntity{

	private String wallet;
	
	private String reciverWallet;
	
	private BigDecimal usdc;
	
	private Long inviteId;
	
	private Integer status;
	
	private String hash;
	
	private Integer auto;
	
	private String remark;
	
}
