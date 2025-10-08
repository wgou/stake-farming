package io.renren.modules.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StakingEnum {
	NORMAL(0,"普通计算"),
	ADD(1,"增加1%计算");
	private Integer code;
	private String desc;
}
