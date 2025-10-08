/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.common.utils;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	
	public R() {
		put("code", 0);
		put("msg", "success");
	}
	
	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Unknown exception");
	}
	
	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
	}
	
	public static R error(int code, String msg) {
		R r = new R();
		r.put("success", false);
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		r.put("success", true);
		return r;
	}
	
	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.put("success", true);
		r.putAll(map);
		return r;
	}
	
	
	public static R ok(Object data) {
		R r = new R();
		r.put("success", true);
		r.put("data",data);
		return r;
	}
	
	public static R ok() {
		R r = new R();
		r.put("success", true);
		return r;
	}
	
	public static R ok(boolean isOk) {
		return isOk ? R.ok() :R.error();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}
}
