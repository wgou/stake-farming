package io.renren.modules.constants;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public enum TransferEnum {
	no(0,"等待"),
	ing(2,"进行中"),
	error(-1,"失败"),
	yes(1,"完成");
	private Integer code;
	private String desc;
	
	 public static Map<Integer, String> toMap() {
        Map<Integer, String> map = new HashMap<>();
        for (TransferEnum killEnum : TransferEnum.values()) {
            map.put(killEnum.getCode(), killEnum.getDesc());
        }
        return map;
    }
}
