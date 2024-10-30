package com.hxxdemo.sysLogin.service;

import java.util.Map;

import com.hxxdemo.sysLogin.entity.User;

public interface RegisterService {

	/**
	 * 插入用户
	 * @param params
	 */
	void insertUser(User user);
	
	/**
	 * email是否被注册过了
	 * @param email
	 * @return
	 */
	int isregister(String email);
	/**
	 * 查询用户
	 * @param user
	 * @return
	 */
	User getUser(User user);
	/**
	 * 查询用户
	 * @param user
	 * @return
	 */
	Map<String,Object> getLoginUser(User user);
	
	/**
	 * 更新用户激活状态
	 * @param email
	 */
	void updateUserActivateStatus(String email);
	/**
	 * 更改用户密码
	 * @param user
	 */
	void updateUserPassword(User user);
	
	/**
	 * 通过用户id查询是否是本站用户
	 * @param userid
	 * @return
	 */
	int isUserById(Long userid);
}
