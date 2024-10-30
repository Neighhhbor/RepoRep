/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.power.io
 *
 * 版权所有，侵权必究！
 */

package com.hxxdemo.exception.utils;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	
	public R() {
		put("errorcode", "0");
		put("errormessage", "ok");
	}
	
	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR+"", "未知异常，请联系管理员");
	}
	
	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR+"", msg);
	}
	
	public static R error(String code, String msg) {
		R r = new R();
		r.put("errorcode", code);
		r.put("errormessage", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("errormessage", msg);
		return r;
	}
	
	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}
	
	public static R ok() {
		return new R();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}
}
