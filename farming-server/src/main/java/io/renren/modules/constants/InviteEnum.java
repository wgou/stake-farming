package io.renren.modules.constants;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public enum InviteEnum {
	no(0,"否"),
	yes(1,"是");
	private Integer code;
	private String desc;
	
	 public static Map<Integer, String> toMap() {
        Map<Integer, String> map = new HashMap<>();
        for (InviteEnum killEnum : InviteEnum.values()) {
            map.put(killEnum.getCode(), killEnum.getDesc());
        }
        return map;
    }
}
