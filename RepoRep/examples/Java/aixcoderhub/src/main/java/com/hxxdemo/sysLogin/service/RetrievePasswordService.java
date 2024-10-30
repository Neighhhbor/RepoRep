package com.hxxdemo.sysLogin.service;

import java.util.Map;

public interface RetrievePasswordService {

	/**
	 * 添加token
	 * @param params
	 */
	void insertPwdToken(Map<String,Object> params);
	
	/**
	 * 是否token
	 * @param params
	 * @return
	 */
	int countPwdTokenByEmailToken(Map<String,Object> params);
}
