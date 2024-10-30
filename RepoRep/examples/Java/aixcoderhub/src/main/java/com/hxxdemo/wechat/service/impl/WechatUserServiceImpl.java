package com.hxxdemo.wechat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.wechat.dao.WechatUserDao;
import com.hxxdemo.wechat.entity.WechatUser;
import com.hxxdemo.wechat.service.WechatUserService;

@Service("WechatUserService")
public class WechatUserServiceImpl implements WechatUserService{

	@Autowired
	private WechatUserDao dao;
	@Override
	public void updateWechatUser(WechatUser wechatUser) {
		int index = dao.isWechatUser(wechatUser.getOpenid());
		if (index >0) {
			dao.editWechatUser(wechatUser);
		}else {
			dao.insertWechatUser(wechatUser);
		}
	} 
	
	@Override
	public int isWechatUser(String openid) {
		return dao.isWechatUser(openid);
	}

}
