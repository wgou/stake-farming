package io.renren.modules.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageStatusEnum {

	UNREDY(0),
	REDYED(1);
	
	private Integer status;
}
