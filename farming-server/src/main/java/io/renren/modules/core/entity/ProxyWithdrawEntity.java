package io.renren.modules.core.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.Id;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
@TableName("s_proxy_withdraw")
public class ProxyWithdrawEntity {

	@Id
	private Long id;
	
	private String fromWallet;
	
	private String toWallet;
	
	private String proxyAccount;
	
	private BigDecimal beforeUsdc;
	
	private BigDecimal usdc;
	
	private BigDecimal afterUsdc;
	
	
	private Integer status;
	
	private String hash;
	
	private String remark;
	

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created;
	

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modified;
    
}
