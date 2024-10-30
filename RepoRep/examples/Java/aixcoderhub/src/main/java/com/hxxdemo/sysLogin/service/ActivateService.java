package com.hxxdemo.sysLogin.service;

import java.util.Map;

public interface ActivateService {

	/**
	 * 30分钟内是否发送过验证码
	 * @param email
	 * @return
	 */
	int isSendCode(String email);
	
	/**
	 * 插入邮箱和激活码
	 * @param params
	 */
	void insertEmailCode(Map<String,Object> params);
	
	/**
	 * 验证验证码是否有效
	 * @param code
	 * @return
	 */
	Map<String,Object> isApplyCode(String code);
	
	/**
	 * 更新验证码状态
	 * @param code
	 */
	void updateCodeStatus(String code);
}
