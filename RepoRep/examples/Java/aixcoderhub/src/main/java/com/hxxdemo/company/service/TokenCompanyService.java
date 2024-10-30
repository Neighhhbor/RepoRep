package com.hxxdemo.company.service;

import java.util.Map;

import org.apache.ibatis.annotations.Update;

import com.hxxdemo.company.dao.TokenCompanyEntity;
import com.hxxdemo.sysLogin.entity.TokenEntity;


public interface TokenCompanyService {


	/**
	 * 生成token
	 * @param userId  用户ID
	 * @return        返回token信息
	 */
	TokenCompanyEntity createCompanyToken(Long userId);
	
	
	TokenCompanyEntity getToken(Long userId);
	
	
	
}
