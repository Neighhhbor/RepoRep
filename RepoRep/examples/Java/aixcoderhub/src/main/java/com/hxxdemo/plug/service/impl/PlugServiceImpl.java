package com.hxxdemo.plug.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.hxxdemo.config.Globals;
import com.hxxdemo.plug.dao.PlugDao;
import com.hxxdemo.plug.service.PlugService;
import com.hxxdemo.util.CommonUtil;

@Service("PlugService")
public class PlugServiceImpl implements PlugService{

	@Autowired
	private PlugDao plugDao;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	/**
	 * 保存插件用户
	 * @param params
	 */
	public void insertPlugUser(Map<String,Object> params) {

		plugDao.insertPlugUser(params);
	}
	/**
	 * 保存插件用户password
	 * @param params
	 */
	public void insertPlugUserPassword(Map<String,Object> params) {
		
		plugDao.insertPlugUserPassword(params);
	}

	/**
	 * 通过电话号码获取uuid
	 * @param telephone
	 * @return
	 */
	public String getUUID(String telephone) {
		
		return plugDao.getUUID(telephone);
	}
	/**
	 * 通过电话号码或邮箱获取用户id
	 * @param telephone
	 * @return
	 */
	public Long getUserId(String telephone) {
		return plugDao.getUserId(telephone);
	}
	/**
	 * 通过uuid查询是否是有效的
	 * @param uuid
	 * @return
	 */
	public int countUserByUUID(String uuid) {
		return plugDao.countUserByUUID(uuid);
	}
	/**
	 * 是否绑定了
	 * @return
	 */
	public int countBinding(Map<String,Object> params) {
		return plugDao.countBinding(params);
	}
	/**
	 * 绑定macid uuid
	 * @param params
	 */
	public void insertBinding(Map<String,Object> params) {
		plugDao.insertBinding(params);
	}
	/**
	 * 获取vip等级列表
	 * @return
	 */
	public List<Map<String,Object>> getVipLevel(){
		return plugDao.getVipLevel();
	}
	/**
	 * 获取插件vip等级
	 * @return
	 */
	public int getPlugVipLevel(Long userId){
		return plugDao.getPlugVipLevel(userId).get(0);
	}
	/**
	 * 是否是企业版
	 * @param userId
	 * @return
	 */
	public boolean isBusiness(Long userId) {
		boolean bool = false;
		List<Integer> list  = plugDao.isBusiness(userId);
		if(list.get(0) == 1) {
			bool = true;
		}
		return bool;
	}
	/**
	 * 获取vip模型列表 
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> getvipModels(Map<String,Object> params){
		return plugDao.getvipModels(params);
	}
	/**
	 * 是否是注册用户
	 * @param username
	 * @return
	 */
	public int isRegister(String username) {
		return plugDao.isRegister(username);
	}
	/**
	 * 通过用户名和密码检查用户
	 * @param username
	 * @param password
	 * @return
	 */
	public Long checkUserByUsernamePassword(String username ,String password) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("username", username);
		params.put("password", password);
		return plugDao.checkUserByUsernamePassword(params);
	}
	/**
	 * 修改密码
	 * @param username
	 * @param password
	 */
	public void updateUserPassword(String username,String password) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("username", username);
		params.put("password", password);
		plugDao.updateUserPassword(params);
	}
	/**
	 * 获取用户名称
	 * @param token
	 * @return
	 */
	public String getUserNameByToken(String token) {
		return plugDao.getUserNameByToken(token);
	}
	
	/**
	 * 是否是临时vip
	 * @param userId
	 * @return
	 */
	public boolean isVipShort(Long userId) {
		boolean bool = false;
		int count = plugDao.isVipShort(userId) ; 
		if (count == 1 ) {
			bool = true;
		}else {
			bool = false;
		}
		return bool;
	}
	/**
	 * 获取过期时间
	 * @param userId
	 * @return
	 */
	public String getExpireTime(Long userId){
		return plugDao.getExpireTime(userId);
	}
	
	/**
	 * 校验machineID
	 * @param machineID
	 * @param realIP
	 * @return
	 */
	public boolean checkMachineID(String machineID,String realIP,Long userid,String code) {
		
		int count = plugDao.countMachineID(machineID,userid);
		if(count == 0 ) {
			//查看白名单个数
			int num = plugDao.countNumUserid(userid);
			if(num < Globals.WHITELISTMIN) {
				//记录白名单
				plugDao.insertMachineID(machineID,realIP,userid);
				return true;
			}else {
				plugDao.inserMachineCode(machineID,realIP,userid,code);
				return false;
			}
		}else {
			//验证ip
			int index= plugDao.countMachineIDIP(machineID,realIP,userid);
			if(index ==0 ) {
				//删除机器id
				plugDao.delMachineid(machineID,userid);
				//记录白名单
				plugDao.insertMachineID(machineID,realIP,userid);
			}
			return true;
		}
		
	}
	
	/**
	 * 检查机器code是否存在
	 * @param code
	 * @return
	 */
	public boolean checkCode(String code) {
		//检查code是否存在
		int count = plugDao.countCode(code);
		if(count == 0 ) {
			return false; 
		}else {
			return true;
		}
	}
	
	public void agreeYes(String code) {
		plugDao.insertMachineByCode(code);
		plugDao.delMachineCode(code);
	}
	
	public void agreeNo(String code) {
		plugDao.delMachineCode(code);
	}
	
	public 	void savePlugLog(Map<String, Object> params) {
		plugDao.savePlugLog(params);
	}
	
	@Override
	public void savePlugSmsCode(Map<String, Object> params) {
		plugDao.savePlugSmsCode(params);
	}
	@Override
	public void expireSmsCode(Map<String, Object> params) {
		Integer tokenid = plugDao.getSmsCodeId(params);
		if (null != tokenid) {
			params.put("tokenid", tokenid);
			plugDao.expireSmsCode(params);
		}
	}
	
	@Override
	public String getSmsToken(Map<String, Object> params) {
		return plugDao.getSmsToken(params);
	}
	@Override
	public int getPlugLoginId(String loginId) {
		return plugDao.getPlugLoginId(loginId);
	}
	@Override
	public void savePlugLoginId(String loginId, String telephone, Long userId, int expire) {
		plugDao.savePlugLoginId(loginId, telephone, userId, expire);
	}
	@Override
	public Map<String, Object> getPlugLoginByIdName(String loginId){
		return plugDao.getPlugLoginByIdName(loginId);
	}
	@Override
	public Integer remoteCheckAuth(String proToken) {
		return plugDao.remoteCheckAuth(proToken);
	}
	@Override
	public int remoteCheckCloseAuth(String proToken) {
		return plugDao.remoteCheckCloseAuth(proToken);
	}
	@Override
	public void issueFeedback(Map<String, Object> params) {
		plugDao.issueFeedback(params);
	}

	@Override
	public void logOff(Object userID) throws Exception{
		Map<String, Object> objectMap = jdbcTemplate.queryForMap("select * from sys_user where id = ?", userID);
		if (objectMap == null) {
			throw new Exception("删除用户失败: 用户不存在");
		}
		// 把数据插入到sys_user_del表中
		try {
			int update = jdbcTemplate.update("insert into sys_user_del (email, password, telephone, node_id, avatar_url, login, gitid, isactivation, createtime, head_url, uuid, viplevel, source, tag, expire_time, wechatid) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					objectMap.get("email"), objectMap.get("password"),
					objectMap.get("telephone"), objectMap.get("node_id"),
					objectMap.get("avatar_url"), objectMap.get("login"),
					objectMap.get("gitid"), objectMap.get("isactivation"),
					objectMap.get("createtime"), objectMap.get("head_url"),
					objectMap.get("uuid"), objectMap.get("viplevel"),
					objectMap.get("source"), objectMap.get("tag"),
					objectMap.get("expire_time"), objectMap.get("wechatid"));
			if (update > 0) {
				jdbcTemplate.update("delete from sys_user where id = ?", userID);
				// 删除 插件申请token
				jdbcTemplate.update("delete from modeltrail where userid = ?", userID);
			}else{
				throw new Exception("删除用户失败: 插入sys_user_del失败");
			}
		}catch (DataAccessException e) {
			throw new Exception("删除用户失败: " + e.getMessage());
		}
	}
}
