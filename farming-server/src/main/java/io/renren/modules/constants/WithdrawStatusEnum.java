package io.renren.modules.constants;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WithdrawStatusEnum {
	Approvaling(0,"待审批"),
	Approvaled(1,"已审批"),
	Proccessing(2,"执行中"),
	Success(3,"成功"),
	Error(-1,"失败");
	private int code;
	private String desc;
	
	 public static Map<Integer, String> toMap() {
	        Map<Integer, String> map = new HashMap<>();
	        for (WithdrawStatusEnum killEnum : WithdrawStatusEnum.values()) {
	            map.put(killEnum.getCode(), killEnum.getDesc());
	        }
	        return map;
	    }
}
