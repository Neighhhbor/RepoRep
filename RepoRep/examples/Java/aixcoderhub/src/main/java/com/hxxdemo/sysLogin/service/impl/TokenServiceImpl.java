package com.hxxdemo.sysLogin.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.sysLogin.Singleton;
import com.hxxdemo.sysLogin.dao.TokenDao;
import com.hxxdemo.sysLogin.entity.TokenEntity;
import com.hxxdemo.sysLogin.service.TokenService;



@Service("TokenService")
public class TokenServiceImpl  implements TokenService{

	@Autowired
	private TokenDao tokenDao; 
	/**
	 * 12小时后过期   
	 */
	private final static int EXPIRE = 3600 * 12 ;
	
	/**
	 *   --15天
	 */
	public static long FIFTEEN_EXPIRE = 3600 * 24 * 15 * 1000;
	
	public static int TIME = 3600 * 24 * 1000;

	@Override
	public TokenEntity queryByToken(String token) {
		TokenEntity tokenEntity = tokenDao.queryByToken(token);
		return tokenEntity;
	}
	@Override
	public TokenEntity queryByTokenInstall(String token) {
		TokenEntity tokenEntity = tokenDao.queryByTokenInstall(token);
		return tokenEntity;
	}
	@Override
	public TokenEntity createToken(long userId) {
		//当前时间
		Date now = new Date();
//		//过期时间
		Date expireTime = new Date(now.getTime() + EXPIRE * 1000);
		//生成token
		String token = generateToken();
		//保存或更新用户token
		TokenEntity tokenEntity = new TokenEntity();
		tokenEntity.setUserId(userId);
		tokenEntity.setUpdateTime(now);
		tokenEntity.setExpireTime(expireTime);
		tokenEntity.setToken(token);
		//删除过期的token
		tokenDao.deleteToken(userId);
		//创建token
		tokenDao.createToken(tokenEntity);
		
		return tokenEntity;
	}
	@Override
	public TokenEntity createTokenByTime(long userId,int time) {
		//当前时间
		Date now = new Date();
//		//过期时间
		Date expireTime = new Date(now.getTime() + TIME  * time * 1000);
		//生成token
		String token = generateToken();
		//保存或更新用户token
		TokenEntity tokenEntity = new TokenEntity();
		tokenEntity.setUserId(userId);
		tokenEntity.setUpdateTime(now);
		tokenEntity.setExpireTime(expireTime);
		tokenEntity.setToken(token);
		tokenDao.createToken(tokenEntity);
		
		return tokenEntity;
	}

	@Override
	public void expireToken(long userId){
		Date now = new Date();

		TokenEntity tokenEntity = new TokenEntity();
		tokenEntity.setUserId(userId);
		tokenEntity.setUpdateTime(now);
		tokenEntity.setExpireTime(now);
		tokenDao.expireToken(userId);
	}

	private String generateToken(){
		return UUID.randomUUID().toString().replace("-", "");
	}
	/**
	 * 修改token为过期
	 * @param token 
	 */
	public void updateTokenBytoken(String token) {
		tokenDao.updateTokenBytoken(token);
	}
	
	/**
	 * 处理过期token
	 */
	public void handleToken(String token) {
		List<Map<String,Object>> list = tokenDao.queryListByToken(token);
		if(null != list && list.size()>0) {
			for (int i = 0; i < list.size(); i++) {
				tokenDao.updateTokenBytoken(list.get(i).get("token").toString());
				Singleton.getInstance().getMap().remove(list.get(i).get("token").toString());
			}
		}
	}
	@Override
	public TokenEntity createPlugToken(long userId) {
		//当前时间
		Date now = new Date();
//		//过期时间
		Date expireTime = new Date(now.getTime() + EXPIRE * 30 * 1000);
		//生成token
		String token = generateToken();
		//保存或更新用户token
		TokenEntity tokenEntity = new TokenEntity();
		tokenEntity.setUserId(userId);
		tokenEntity.setUpdateTime(now);
		tokenEntity.setExpireTime(expireTime);
		tokenEntity.setToken(token);
		tokenDao.createToken(tokenEntity);
		
		return tokenEntity;
	}
	/**
	 * 通过用户id查询插件token
	 * @param userId
	 * @return
	 */
	public TokenEntity queryPlugToken(long userId) {
		return tokenDao.queryPlugToken(userId);
	}
	/**
	 * 修改插件token过期时间
	 * @param token
	 */
	public void updatePlugToken(Map<String,Object> params) {
		tokenDao.updatePlugToken(params);
	}
	/**
	 * logOut 设置token过期
	 * @param token 
	 */
	public void expireTokenByToken(String token) {
		tokenDao.expireTokenByToken(token);
	}
	/**
	 * 通过用户id查询插件token
	 * @param userId
	 * @return
	 */
	public TokenEntity queryPlugTokenInstall(long userId) {
		return tokenDao.queryPlugTokenInstall(userId);
	}
	/**
	 * 修改插件token过期时间
	 * @param token
	 */
	public void updatePlugTokenInstall(Map<String,Object> params) {
		tokenDao.updatePlugTokenInstall(params);
	}
	/**
	 * 生成token
	 * @param userId  用户ID
	 * @return        返回token信息
	 */
	public TokenEntity createTokenInstall(long userId) {
		//当前时间
		Date now = new Date();
//				//过期时间
		Date expireTime = new Date(now.getTime() + EXPIRE * 30 * 1000);
		//生成token
		String token = generateToken();
		//保存或更新用户token
		TokenEntity tokenEntity = new TokenEntity();
		tokenEntity.setUserId(userId);
		tokenEntity.setUpdateTime(now);
		tokenEntity.setExpireTime(expireTime);
		tokenEntity.setToken(token);
		tokenDao.deleteTokenInstall(userId);
		tokenDao.createTokenInstall(tokenEntity);
		
		return tokenEntity;
	}
	
	@Override
	public Object getIdByToken(String token) {
		return tokenDao.getIdByToken(token);
	}

	
	@Override
	public void updatePlugTokenEntity(TokenEntity tokenEntity) {
		tokenDao.updatePlugTokenEntity(tokenEntity);
	}
	@Override
	public TokenEntity queryPlugTokenByUserId(Long userId) {
		return tokenDao.queryPlugTokenByUserId(userId);
	}
	@Override
	public TokenEntity createPlugLoginToken(Long userId) {
		//当前时间
		Date now = new Date();
		//过期时间
		Date expireTime = new Date(now.getTime() + FIFTEEN_EXPIRE * 1000);
		//生成token
		String token = generateToken();
		//保存或更新用户token
		TokenEntity tokenEntity = new TokenEntity();
		tokenEntity.setUserId(userId);
		tokenEntity.setUpdateTime(now);
		tokenEntity.setExpireTime(expireTime);
		tokenEntity.setToken(token);
		tokenDao.deletePlugLoginToken(userId);
		tokenDao.createPlugLoginToken(tokenEntity);
		return tokenEntity;
	}
}
