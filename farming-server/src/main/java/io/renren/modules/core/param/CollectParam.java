package io.renren.modules.core.param;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CollectParam {

	@NotNull
	private List<String> wallets;
}
