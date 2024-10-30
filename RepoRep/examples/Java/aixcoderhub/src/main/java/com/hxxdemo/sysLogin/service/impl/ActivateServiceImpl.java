package com.hxxdemo.sysLogin.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.sysLogin.dao.ActivateDao;
import com.hxxdemo.sysLogin.service.ActivateService;

@Service("ActivateService")
public class ActivateServiceImpl implements ActivateService{
	@Autowired
	private ActivateDao activateDao;
	/**
	 * 30分钟内是否发送过验证码
	 * @param email
	 * @return
	 */
	public int isSendCode(String email) {
		return activateDao.isSendCode(email);
	}
	
	/**
	 * 插入邮箱和激活码
	 * @param params
	 */
	public void insertEmailCode(Map<String,Object> params) {
		activateDao.insertEmailCode(params);
	}
	/**
	 * 验证验证码是否有效
	 * @param code
	 * @return
	 */
	public Map<String,Object> isApplyCode(String code) {
		return activateDao.isApplyCode(code);
	}
	
	/**
	 * 更新验证码状态
	 * @param code
	 */
	public void updateCodeStatus(String code) {
		activateDao.updateCodeStatus(code);
	}
}
