package io.renren.modules.constants;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public enum RewardStatusEnum {
	success(0,"成功"),
	error(-1,"失败");
	private Integer code;
	private String desc;
	
	 public static Map<Integer, String> toMap() {
        Map<Integer, String> map = new HashMap<>();
        for (RewardStatusEnum killEnum : RewardStatusEnum.values()) {
            map.put(killEnum.getCode(), killEnum.getDesc());
        }
        return map;
    }
}
