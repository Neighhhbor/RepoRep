package com.hxxdemo.weixinsaomalogin.service;

import com.hxxdemo.weixinsaomalogin.entity.WechatSNSUserInfoVo;

public interface WebsiteUserService {

	/**
	 * 插入网站授权微信用户
	 * @param wechatSNSUserInfoVo
	 */
	void insertWebsiteUser(WechatSNSUserInfoVo wechatSNSUserInfoVo);
	
	/**
	 * 修改网站授权微信用户
	 * @param wechatSNSUserInfoVo
	 */
	void updateWebsiteUser(WechatSNSUserInfoVo wechatSNSUserInfoVo);
	
	/**
	 * 检查用户是否存在
	 * @param openid
	 * @return 0 不存在 1 存在
	 */
	int checkWebsiteUser(WechatSNSUserInfoVo wechatSNSUserInfoVo);
	
	/**
	 * 修改备注名称
	 * @param openid
	 * @param remark
	 */
	void editWebsiteUserRemark(WechatSNSUserInfoVo wechatSNSUserInfoVo);
}
