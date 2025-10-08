package io.renren.modules.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageTypeEnum {

	TEXT(0),
	IMG(1),
	PING(2),
	READY(3),
	DElETE(4);
	
	private Integer type;
}
