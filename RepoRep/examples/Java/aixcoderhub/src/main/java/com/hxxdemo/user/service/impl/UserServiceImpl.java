package com.hxxdemo.user.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.user.dao.UserDao;
import com.hxxdemo.user.service.UserService;

@Service("UserService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao dao;
	
	public Map<String, Object> userInfo(String token){
		return dao.userInfo(token);
	}
	
	public int isBindingTelephone(String telephone) {
		return dao.isBindingTelephone(telephone);
	}
	
	public void bindingTelephone(String telephone,String token) {
		dao.bindingTelephone(telephone,token);
	}
	
	public int isBindingEmail(String email) {
		return dao.isBindingEmail(email);
	}
	
	public void bindingEmail(String email,String token) {
		dao.bindingEmail(email,token);
	}
	
	public int isOneSelf(String username,String token) {
		return dao.isOneSelf(username, token);
	}
	
	public Map<String, Object> checkEventKey(String eventkey){
		return dao.checkEventKey(eventkey);
	}

	public int isBindingWechat(String token) {
		return dao.isBindingWechat(token);
	}
	
	public void userBindingWechat(String wechatid,String token) {
		dao.userBindingWechat(wechatid, token);
	}
	

	public void setPassword(String token,String password) {
		dao.setPassword(token, password);
	}
	
	public int checkUser(Map<String, Object> params) {
		return dao.checkUser(params);
	}
	public int countTelephoneEmail(String token) {
		return dao.countTelephoneEmail(token);
	}

	@Override
	public void editTelephoneIsNull(String token) {
		dao.editTelephoneIsNull(token);
	}

	@Override
	public void editEmailIsNull(String token) {
		dao.editEmailIsNull(token);
	}
	
}
