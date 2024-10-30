package com.hxxdemo.company.service.impl;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.company.dao.TokenCompanyDao;
import com.hxxdemo.company.dao.TokenCompanyEntity;
import com.hxxdemo.company.service.TokenCompanyService;

@Service("TokenCompanyService")
public class TokenCompanyServiceImpl implements TokenCompanyService{

	/**
	 * 12小时后过期    --1天
	 */
	private final static int EXPIRE = 3600 * 12 * 1;
	@Autowired
	private TokenCompanyDao dao;

	@Override
	public TokenCompanyEntity createCompanyToken(Long userId) {
		TokenCompanyEntity tokenEntity =  dao.getToken(userId);
		//当前时间
		Date now = new Date();
		Date expireTime = new Date(now.getTime() + EXPIRE * 1000);
		if(null == tokenEntity) {
			tokenEntity = new TokenCompanyEntity();
			//生成token
			String token = generateToken();
			//保存或更新用户token
			tokenEntity.setUserId(userId);
			tokenEntity.setUpdateTime(now);
			tokenEntity.setExpireTime(expireTime);
			tokenEntity.setToken(token);
			dao.createCompanyToken(tokenEntity);
		}else {
			tokenEntity.setExpireTime(expireTime);
			this.updateTokenBytoken(tokenEntity);
		}
		return tokenEntity;
	}
	
	/**
	 * 修改token过期时间
	 * @param token 
	 */
	public void updateTokenBytoken(TokenCompanyEntity tokenEntity) {
		dao.updateTokenBytoken(tokenEntity);
	}
	
	private String generateToken(){
		return UUID.randomUUID().toString().replace("-", "");
	}

	@Override
	public TokenCompanyEntity getToken(Long userId) {
		return dao.getToken(userId);
	}
}
