package com.hxxdemo.captcha.service;

import java.util.Map;

public interface CaptchaService {
	
	int countUuid(Map<String, Object> params);
	
	void save(Map<String, Object> params);
	
	void update(Map<String, Object> params);
	
	void saveIp(String ip);
	
	int  countCode(Map<String, Object> params);

}
