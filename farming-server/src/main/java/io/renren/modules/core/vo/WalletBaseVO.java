package io.renren.modules.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class WalletBaseVO {

	private Long id;
	
	private Long poolsId;
	
	private Long poolsOwnerId;
	
	private String wallet;
}
