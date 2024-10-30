package com.hxxdemo.wechatservice.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface CoreService {
	
	void insertWechatEventKey(String language,String wechatid,String eventKey);

	String processRequest(HttpServletRequest request);
	
	Map<String, Object> checkEventKey(String eventkey);
	
	void expireEventKeyDisplay(String eventkey);
	
	Map<String, Object> getUserIdByWechatId(String wechatid);
	
	String getWechatIdByEventKey(String eventkey);
	
	Map<String,Object>  getUserWechatByUsername(String username);
	
	void insertUser(Map<String, Object> params);
	
	void updateUser(Map<String, Object> params);
	
	void cancelBindingWechat(String token);
	
	String getWechatIdByWechatId(String wechatid);
}
