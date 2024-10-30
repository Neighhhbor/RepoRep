package com.hxxdemo.captcha.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.captcha.dao.CaptchaDao;
import com.hxxdemo.captcha.service.CaptchaService;

@Service("CaptchaService")
public class CaptchaServiceImpl implements CaptchaService{

	@Autowired
	private CaptchaDao dao;
	
	public int countUuid(Map<String, Object> params) {
		return dao.countUuid(params);
	}
	
	public void save(Map<String, Object> params) {
			dao.save(params);
	}
	public void update(Map<String, Object> params) {
		dao.update(params);
	}
	public void saveIp(String ip) {
		dao.saveIp(ip);
	}
	public int  countCode(Map<String, Object> params) {
		return dao.countCode(params);
	}
}
