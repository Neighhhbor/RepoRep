package com.hxxdemo.wechat.service;

import com.hxxdemo.wechat.entity.WechatUser;

public interface WechatUserService {

	void updateWechatUser(WechatUser wechatUser);
	
	int isWechatUser(String openid);
}
