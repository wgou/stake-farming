package io.renren.modules.constants;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public enum BlockadeEnum {
	unBlockaded(0,"未封锁"),
	blockade(1,"封锁");
	private Integer code;
	private String desc;
	
	 public static Map<Integer, String> toMap() {
        Map<Integer, String> map = new HashMap<>();
        for (BlockadeEnum killEnum : BlockadeEnum.values()) {
            map.put(killEnum.getCode(), killEnum.getDesc());
        }
        return map;
    }
}
