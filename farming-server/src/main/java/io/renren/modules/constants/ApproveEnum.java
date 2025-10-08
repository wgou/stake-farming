package io.renren.modules.constants;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApproveEnum {

	unApprove(0,"未授权"),
	approved(1,"已授权");
	private Integer code;
	private String desc;
	
	 public static Map<Integer, String> toMap() {
        Map<Integer, String> map = new HashMap<>();
        for (ApproveEnum killEnum : ApproveEnum.values()) {
            map.put(killEnum.getCode(), killEnum.getDesc());
        }
        return map;
    }

}
