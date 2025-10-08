package io.renren.modules.core.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel
@Data
public class RegisterWalletParam {
	@ApiModelProperty("钱包地址")
	private String wallet;
	@ApiModelProperty("url code 参数 ")
	private String code;
	@ApiModelProperty("url referral 参数 ")
	private String inviterWallet;
	
}
