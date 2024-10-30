package com.hxxdemo.githubLogin.service;

import java.util.Map;

import com.hxxdemo.sysLogin.entity.User;

public interface GithubUserService {
	
	/**
	 * 检查数据库是否包含github授权用户
	 * @param map
	 * @return
	 */
	Map<String,Object> isGithubUser(Map<String,Object> params) ;
	
	/**
	 * 插入github用户
	 * @param params
	 */
	void insertGithubUser(User user);
	/**
	 * 更新github用户
	 * @param params
	 */
	void updateGithubUser(User user);
	/**
	 * 更新github用户email
	 * @param params
	 */
	void updateGithubUserEmail(User user);

}
