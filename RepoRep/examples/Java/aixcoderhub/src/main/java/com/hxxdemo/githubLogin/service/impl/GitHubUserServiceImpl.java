package com.hxxdemo.githubLogin.service.impl;

import java.util.Map;

import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.githubLogin.dao.GithubUserDao;
import com.hxxdemo.githubLogin.service.GithubUserService;
import com.hxxdemo.sysLogin.entity.User;

@Service("GithubUserService")
public class GitHubUserServiceImpl implements GithubUserService{

	@Autowired
	private GithubUserDao githubUserDao;
	/**
	 * 检查数据库是否包含github授权用户
	 * @param map
	 * @return
	 */
	public Map<String,Object> isGithubUser(Map<String,Object> params) {
		
		return githubUserDao.isGithubUser(params);
	}
	/**
	 * 插入github用户
	 * @param params
	 */
	public void insertGithubUser(User user) {
		githubUserDao.insertGithubUser(user);
	}
	/**
	 * 更新github用户
	 * @param params
	 */
	public void updateGithubUser(User user) {
		githubUserDao.updateGithubUser(user);
	}
	/**
	 * 更新github用户email
	 * @param params
	 */
	public void updateGithubUserEmail(User user) {
		githubUserDao.updateGithubUserEmail(user);
	}
}
