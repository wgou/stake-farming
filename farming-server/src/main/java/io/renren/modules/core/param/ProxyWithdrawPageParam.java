package io.renren.modules.core.param;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ProxyWithdrawPageParam extends PageParam{

	private String fromWallet;
	
	private String toWallet;
	
	private String proxyAccount;
	
	private Integer status;
	 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
	private Date start;
	 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
	private Date end;
	
}
