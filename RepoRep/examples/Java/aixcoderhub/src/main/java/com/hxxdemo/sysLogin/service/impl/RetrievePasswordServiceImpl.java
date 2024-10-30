package com.hxxdemo.sysLogin.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.sysLogin.dao.RetrievePasswordDao;
import com.hxxdemo.sysLogin.service.RetrievePasswordService;

@Service("RetrievePasswordService")
public class RetrievePasswordServiceImpl implements RetrievePasswordService{

	@Autowired 
	private RetrievePasswordDao retrievePasswordDao;
	/**
	 * 添加token
	 * @param params
	 */
	public void insertPwdToken(Map<String,Object> params) {
		retrievePasswordDao.insertPwdToken(params);
	}
	/**
	 * 是否token
	 * @param params
	 * @return
	 */
	public int countPwdTokenByEmailToken(Map<String,Object> params) {
		
		return retrievePasswordDao.countPwdTokenByEmailToken(params);
	}
}
