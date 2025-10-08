package io.renren.modules.core.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class TransferRecordVO {
	 
	private String wallet;
	
	private String reciverWallet;
	
	private BigDecimal usdc;
	
//	private BigDecimal withdraw;
	
	private String pools;
	
	private String inviteName;
	
	private String hash;
	
	private Integer status;
	
	private Integer auto;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created;
}
