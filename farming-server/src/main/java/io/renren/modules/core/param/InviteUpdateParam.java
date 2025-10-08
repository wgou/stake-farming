package io.renren.modules.core.param;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class InviteUpdateParam extends InviteAddParam{
	
	@NotNull
	private Long id;
}
