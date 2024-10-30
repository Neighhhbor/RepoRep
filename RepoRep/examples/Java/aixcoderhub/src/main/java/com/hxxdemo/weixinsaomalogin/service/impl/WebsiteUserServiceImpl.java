package com.hxxdemo.weixinsaomalogin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.weixinsaomalogin.dao.MsglogDao;
import com.hxxdemo.weixinsaomalogin.dao.WebsiteUserDao;
import com.hxxdemo.weixinsaomalogin.entity.WechatSNSUserInfoVo;
import com.hxxdemo.weixinsaomalogin.service.WebsiteUserService;

@Service
public class WebsiteUserServiceImpl implements WebsiteUserService {
	@Autowired
	private WebsiteUserDao websiteUserDao;

	@Override
	public void insertWebsiteUser(WechatSNSUserInfoVo wechatSNSUserInfoVo) {
		websiteUserDao.insertWebsiteUser(wechatSNSUserInfoVo);
	}

	@Override
	public void updateWebsiteUser(WechatSNSUserInfoVo wechatSNSUserInfoVo) {
		websiteUserDao.updateWebsiteUser(wechatSNSUserInfoVo);
	}

	@Override
	public int checkWebsiteUser(WechatSNSUserInfoVo wechatSNSUserInfoVo) {
		return websiteUserDao.checkWebsiteUser(wechatSNSUserInfoVo);
	}

	@Override
	public void editWebsiteUserRemark(WechatSNSUserInfoVo wechatSNSUserInfoVo) {
		websiteUserDao.editWebsiteUserRemark(wechatSNSUserInfoVo);
	}

}
