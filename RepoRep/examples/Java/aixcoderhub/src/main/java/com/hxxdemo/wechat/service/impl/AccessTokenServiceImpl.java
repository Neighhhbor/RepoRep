package com.hxxdemo.wechat.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.wechat.dao.AccessTokenDao;
import com.hxxdemo.wechat.service.AccessTokenService;
import com.hxxdemo.wechat.util.AccessToken;
import com.hxxdemo.wechat.util.WechatUtil;

@Service("AccessTokenService")
public class AccessTokenServiceImpl implements AccessTokenService{

	@Autowired
	private AccessTokenDao accessTokenDao;
	/**
	 * 获取accessToken
	 * @param appid
	 * @param appSecret
	 * @param bool
	 * @return
	 */
	public AccessToken getAccessToken(String appid, String appSecret, boolean bool) {
		AccessToken accesstoken=new AccessToken();
		if(bool) {
			accesstoken = WechatUtil.getAccessToken(appid, appSecret);
			accessTokenDao.deleteAccessToken();
			accessTokenDao.insertAccessToken(accesstoken);
		}else {
			//查看数据是否有AccessToken
			int count = accessTokenDao.countAccessToken();
			if(count==0) {
				accesstoken = WechatUtil.getAccessToken(appid, appSecret);
				accessTokenDao.insertAccessToken(accesstoken);
			}else if(count==1){
				List<Map<String,Object>> list = accessTokenDao.getAccessToken();
				if(null !=list && list.size()>0) {
					accesstoken.setExpiresIn(Integer.valueOf(list.get(0).get("expires_in").toString()));
					accesstoken.setToken(list.get(0).get("accesstoken").toString());
				}else {
					accesstoken = WechatUtil.getAccessToken(appid, appSecret);
					accessTokenDao.deleteAccessToken();
					accessTokenDao.insertAccessToken(accesstoken);
				}
			}else {
				accessTokenDao.deleteAccessToken();
				accesstoken = WechatUtil.getAccessToken(appid, appSecret);
				accessTokenDao.insertAccessToken(accesstoken);
			}
		}
		return accesstoken;
	}
	
}
