package com.hxxdemo.sysLogin.service;

import java.util.Map;

import org.apache.ibatis.annotations.Update;

import com.hxxdemo.sysLogin.entity.TokenEntity;


public interface TokenService {

	TokenEntity queryByToken(String token);
	
	TokenEntity queryByTokenInstall(String token);

	/**
	 * 生成token
	 * @param userId  用户ID
	 * @return        返回token信息
	 */
	TokenEntity createToken(long userId);
	/**
	 * 生成token
	 * @param userId  用户ID
	 * @param  time  时间（小时）
	 * @return        返回token信息
	 */
	TokenEntity createTokenByTime(long userId,int time);

	/**
	 * 设置token过期
	 * @param userId 用户ID
	 */
	void expireToken(long userId);
	/**
	 * 修改token为过期
	 * @param token 
	 */
	void updateTokenBytoken(String token);
	/**
	 * 处理过期token
	 */
	void handleToken(String token);
	/**
	 * 生成plug token
	 * @param userId  用户ID
	 * @return        返回token信息
	 */
	TokenEntity createPlugToken(long userId);
	
	/**
	 * 通过用户id查询插件token
	 * @param userId
	 * @return
	 */
	TokenEntity queryPlugToken(long userId);
	/**
	 * 修改插件token过期时间
	 * @param token
	 */
	void updatePlugToken(Map<String,Object> params);
	/**
	 * logOut 设置token过期
	 * @param token 
	 */
	void expireTokenByToken(String token);
	/**
	 * 通过用户id查询插件token
	 * @param userId
	 * @return
	 */
	TokenEntity queryPlugTokenInstall(long userId);
	/**
	 * 修改插件token过期时间
	 * @param token
	 */
	void updatePlugTokenInstall(Map<String,Object> params);
	/**
	 * 生成token
	 * @param userId  用户ID
	 * @return        返回token信息
	 */
	TokenEntity createTokenInstall(long userId);
	
	Object getIdByToken(String token);
	
	
	/**
	 * @param tokenEntity
	 */
	void updatePlugTokenEntity(TokenEntity tokenEntity);
	/**
	 * @param userId
	 * @return
	 */
	TokenEntity queryPlugTokenByUserId(Long userId);
	/**
	 * 
	 * @param userId
	 * @return
	 */
	TokenEntity createPlugLoginToken(Long userId);
}
