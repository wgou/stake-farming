package io.renren.common.utils;

import java.util.Map;

import com.google.common.collect.Maps;

import lombok.Data;

@Data
public class PageParam {
	/**
     * 当前页码
     */
    public  String page ;
    /**
     * 每页显示记录数
     */
    public String limit;
    /**
     * 排序字段
     */
    public String sidx;
    /**
     * 排序方式
     */
    public String order ;
    /**
     *  升序
     */
    public String asc ; 
    
    public Map<String,Object> toMap(){
    	Map<String,Object> map = Maps.newHashMap();
    	map.put(Constant.PAGE, page);
    	map.put(Constant.LIMIT, limit);
    	map.put(Constant.ORDER, order);
    	map.put(Constant.ASC, asc);
    	map.put(Constant.ORDER_FIELD, sidx);
    	return map;
    }
    
}
