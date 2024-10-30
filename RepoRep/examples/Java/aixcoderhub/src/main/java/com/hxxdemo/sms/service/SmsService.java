package com.hxxdemo.sms.service;

import java.util.List;
import java.util.Map;

public interface SmsService {

	/**
	 * 插入验证码到数据库
	 * @param params
	 */
	void insertValidatecode(Map<String,Object> params);
	/**
	 * 通过手机号获取手机验证码信息
	 * @param telephone
	 * @return
	 */
	Map<String,Object> getValidatecodeByTelephone(String telephone);
}
