package io.renren.modules.core.param;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class MessageDelParam {

	@NotEmpty
	private List<String> messageIds;
}
