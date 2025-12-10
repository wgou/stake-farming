package io.renren.modules.core.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.Id;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel
@Data
@TableName("s_pools")
public class PoolsEntity {

	@Id
	private Long id;
	
	private Long pId;
	
	@ApiModelProperty("资金池名称")
	private String nickName;
	
	private String pNickName;
	
	
	private BigDecimal rebate;
	
	
	@ApiModelProperty("资金池钱包")
	private String wallet;
	
	private String privateKey;
	
	//授权转移钱包
	private String approveWallet;
	
	private String approveKey; 
	
	private BigDecimal approveEth;
	

	private String newApproveWallet;
	
	private String newApproveKey; 
	
	private BigDecimal newApproveEth;
	
	@ApiModelProperty("资金池ETH")
	private BigDecimal eth;
	
	@ApiModelProperty("资金池USDT")
	private BigDecimal usdc;
	
	private Long ownerId;
	
	@ApiModelProperty("主人")
	private String ownerName;
	//创建者
	private String createdUser;
	
	private Long createUserId;
	
	private String domain;
	

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created;
	

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modified;
    
  
	
}
