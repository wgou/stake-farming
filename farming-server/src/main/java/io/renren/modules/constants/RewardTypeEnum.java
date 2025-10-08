package io.renren.modules.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RewardTypeEnum {
	DL(0,"独立计算"),
	HB(1,"合并计算");
	private Integer code;
	private String desc;
}
