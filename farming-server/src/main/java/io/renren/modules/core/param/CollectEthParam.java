package io.renren.modules.core.param;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CollectEthParam {

	@NotNull
	private List<String> wallets;
	@NotNull
	private String reciverWallet;
}
