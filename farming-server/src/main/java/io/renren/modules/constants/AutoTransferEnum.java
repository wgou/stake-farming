package io.renren.modules.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AutoTransferEnum {
	auto(1,"自动"),
	manual(0,"手动");
	private Integer code;
	private String desc;
}
