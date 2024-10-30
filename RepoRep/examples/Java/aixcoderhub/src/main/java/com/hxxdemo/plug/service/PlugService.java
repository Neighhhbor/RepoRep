package com.hxxdemo.plug.service;

import java.util.List;
import java.util.Map;

import com.hxxdemo.config.Globals;


public interface PlugService {

	/**
	 * 保存插件用户
	 * @param params
	 */
	void insertPlugUser(Map<String,Object> params);
	/**
	 * 保存插件用户password
	 * @param params
	 */
	void insertPlugUserPassword(Map<String,Object> params);

	/**
	 * 通过电话号码获取uuid
	 * @param telephone
	 * @return
	 */
	String getUUID(String telephone);
	/**
	 * 通过电话好吗获取用户id
	 * @param telephone
	 * @return
	 */
	Long getUserId(String telephone);
	/**
	 * 通过uuid查询是否是有效的
	 * @param uuid
	 * @return
	 */
	int countUserByUUID(String uuid);
	/**
	 * 是否绑定了
	 * @return
	 */
	int countBinding(Map<String,Object> params);
	/**
	 * 绑定macid uuid
	 * @param params
	 */
	void insertBinding(Map<String,Object> params) ;
	/**
	 * 获取vip等级列表
	 * @return
	 */
	List<Map<String,Object>> getVipLevel();
	/**
	 * 获取插件vip等级 
	 * @return
	 */
	int getPlugVipLevel(Long userId);
	/**
	 * 是否是企业版
	 * @param userId
	 * @return
	 */
	boolean isBusiness(Long userId);
	
	/**
	 * 获取vip模型列表 
	 * @param params
	 * @return
	 */
	List<Map<String,Object>> getvipModels(Map<String,Object> params);
	
	/**
	 * 是否是注册用户
	 * @param username
	 * @return
	 */
	int isRegister(String username);
	
	/**
	 * 通过用户名和密码检查用户
	 * @param username
	 * @param password
	 * @return
	 */
	Long checkUserByUsernamePassword(String username ,String password);
	
	/**
	 * 修改密码
	 * @param username
	 * @param password
	 */
	void updateUserPassword(String username,String password);
	/**
	 * 获取用户名称
	 * @param token
	 * @return
	 */
	String getUserNameByToken(String token);
	
	/**
	 * 是否是临时vip
	 * @param userId
	 * @return
	 */
	boolean isVipShort(Long userId);
	
	/**
	 * 获取过期时间
	 * @param userId
	 * @return
	 */
	String getExpireTime(Long userId);
	
	/**
	 * 校验machineID
	 * @param machineID
	 * @param realIP
	 * @return
	 */
	boolean checkMachineID(String machineID,String realIP,Long userid,String code) ;
	
	/**
	 * 检查机器code是否存在
	 * @param code
	 * @return
	 */
	boolean checkCode(String code);
	
	
	void agreeYes(String code);
	
	void agreeNo(String code);
	
	void savePlugLog(Map<String, Object> params);
	
	void savePlugSmsCode(Map<String, Object> params);
	
	void expireSmsCode(Map<String, Object> params);
	
	String getSmsToken(Map<String, Object> params);
	
	/**
	 * @param loginId
	 * @return
	 */
	int getPlugLoginId(String loginId);
	
	/**
	 * @param loginId
	 * @param telephone
	 * @param userId
	 * @param expire
	 */
	void savePlugLoginId(String loginId,String telephone,Long userId,int expire);
	
	/**
	 * @param loginId
	 * @param userName
	 * @return
	 */
	Map<String, Object> getPlugLoginByIdName(String loginId);
	
	/**
	 * @param proToken
	 * @return
	 */
	Integer remoteCheckAuth(String proToken);
	/**
	 * @param proToken
	 * @return
	 */
	int remoteCheckCloseAuth(String proToken);
	
	/**
	 * @param params
	 */
	void issueFeedback(Map<String, Object> params);

	void logOff(Object userID) throws Exception;
}
