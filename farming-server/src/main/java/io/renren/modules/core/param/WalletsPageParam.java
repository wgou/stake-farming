package io.renren.modules.core.param;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel
@Data
public class WalletsPageParam extends PageParam{

	@ApiModelProperty(value = "钱包地址")
	private String wallet;
	
	@ApiModelProperty(value = "是否杀掉 0：未杀 1:已杀")
	private Integer kills;
	
	@ApiModelProperty(value = "是否授权 0：未授权 1:已授权")
	private Integer approve; //是否授权 0：未授权 1:已授权
	
	@ApiModelProperty(value = "招聘员ID")
	private Long inviteId; //招聘员ID
	
	@ApiModelProperty(value = "是否真实 0：真实 1:虚拟")
	private Integer reals; //是否真实 0：真实 1:虚拟
	
	private Integer balance;
	
	private BigDecimal reciverUsdc;
	
	private BigDecimal reciverEth;
	
	private String likeWallet;
	
	private String reciverWallet;
	
}
