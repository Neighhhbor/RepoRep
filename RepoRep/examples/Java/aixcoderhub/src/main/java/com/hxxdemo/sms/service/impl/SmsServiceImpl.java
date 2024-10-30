package com.hxxdemo.sms.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.sms.dao.SmsDao;
import com.hxxdemo.sms.service.SmsService;
@Service("SmsService")
public class SmsServiceImpl implements SmsService{

	@Autowired
	private SmsDao smsDao;
	
	/**
	 * 插入验证码到数据库
	 * @param params
	 */
	public void insertValidatecode(Map<String,Object> params) {
		smsDao.insertValidatecode(params);
	}
	/**
	 * 通过手机号获取手机验证码信息
	 * @param telephone
	 * @return
	 */
	public Map<String,Object> getValidatecodeByTelephone(String telephone){
		return smsDao.getValidatecodeByTelephone(telephone);
	}
}
