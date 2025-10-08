package io.renren.modules.core.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ApproveIndexVO {

	private String wallet;
	
	private String contract; //授权合约
	
	private BigDecimal amount; 
	
	private String hash;
	
	private String pools; //资金池
	
	private String inviteName; //招聘员
	
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
	private Date created;
	

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
	private Date modified;
	
}
