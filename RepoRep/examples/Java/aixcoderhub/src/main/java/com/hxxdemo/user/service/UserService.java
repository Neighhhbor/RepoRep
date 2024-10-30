package com.hxxdemo.user.service;

import java.util.Map;

public interface UserService {
	
	Map<String, Object> userInfo(String token);

	int isBindingTelephone(String telephone);
	
	void bindingTelephone(String telephone,String token);
	
	int isBindingEmail(String email);

	void bindingEmail(String email,String token);
	
	int isOneSelf(String username,String token);
	
	Map<String, Object> checkEventKey(String eventkey);
	
	int isBindingWechat(String token);
	
	void userBindingWechat(String wechatid,String token);
	
	void setPassword(String token,String password);
	
	int checkUser(Map<String, Object> params);
	
	int countTelephoneEmail(String token);
	
	void editTelephoneIsNull(String token);

	void editEmailIsNull(String token);
	
}
