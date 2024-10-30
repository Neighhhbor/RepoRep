package com.hxxdemo.sysLogin.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.sysLogin.dao.RegisterDao;
import com.hxxdemo.sysLogin.entity.User;
import com.hxxdemo.sysLogin.service.RegisterService;

@Service("RegisterService")
public class RegisterServiceImpl implements RegisterService {

	@Autowired
	private RegisterDao registerDao;
	/**
	 * 插入用户
	 * @param params
	 */
	public void insertUser(User user) {
		registerDao.insertUser(user);
	}
	
	/**
	 * email是否被注册过了
	 * @param email
	 * @return
	 */
	public int isregister(String email) {
		return registerDao.isregister(email);
	}

	/**
	 * 查询用户
	 * @param user
	 * @return
	 */
	public User getUser(User user) {
		return registerDao.getUser(user);
	}
	/**
	 * 查询用户
	 * @param user
	 * @return
	 */
	public Map<String,Object> getLoginUser(User user){
		return registerDao.getLoginUser(user);
	}
	/**
	 * 更新用户激活状态
	 * @param email
	 */
	public void updateUserActivateStatus(String email) {
		registerDao.updateUserActivateStatus(email);
	}
	/**
	 * 更改用户密码
	 * @param user
	 */
	public void updateUserPassword(User user) {
		registerDao.updateUserPassword(user);
	}
	/**
	 * 通过用户id查询是否是本站用户
	 * @param userid
	 * @return
	 */
	public int isUserById(Long userid) {
		return registerDao.isUserById(userid);
	}
	
	
}