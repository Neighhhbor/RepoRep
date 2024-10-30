package com.hxxdemo.weixinsaomalogin.service;

import java.util.List;
import java.util.Map;

import com.hxxdemo.weixinsaomalogin.entity.SNSUserInfo;

public interface WxUserService {

	/**
	 * 插入微信用户
	 * @param snsUserInfo
	 */
	void insertWxUser(SNSUserInfo snsUserInfo);
	/**
	 * 根据unionid 查询微信用户
	 * @param unionid
	 * @return int
	 */
	int queryWxUserCountByUnionid(String unionid);
	
	/**
	 * 根据openid 查询微信用户
	 * @param openid
	 * @return int
	 */
	int queryWxUserCountByOpenid(String openid);
	
	/**
	 * 修改微信用户
	 * @param snsUserInfo
	 */
	void updateWxUser(SNSUserInfo snsUserInfo);
	
	/**
	 * 微信用户取消关注
	 * @param snsUserInfo
	 */
	void wxUserCancelFollow(SNSUserInfo snsUserInfo);
	
	/**
	 * 设置订阅号关注者取消关注
	 * @param snsUserInfo
	 */
	void wxUserAllCancelFollow();
	
	/**
	 * 修改微信用户备注
	 * @param snsUserInfo
	 */
	void updateWxUserRemark(SNSUserInfo snsUserInfo);
	/**
	 * 查询微信公众号关注者分页列表
	 * @param params
	 * @return
	 */
	List<Map<String,Object>> queryWxUserList(Map<String,Object> params);
	/**
	 * 统计微信公众号关注者分页列表
	 * @param params
	 * @return
	 */
	int countWxUserList(Map<String,Object> params);
	
	/**
	 * 查询一条微信用户信息
	 * @param id
	 * @return
	 */
	Map<String ,Object> oneWxUserInfo(Long id);
}
