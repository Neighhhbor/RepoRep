package com.hxxdemo.weixinsaomalogin.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.weixinsaomalogin.dao.WebsiteUserDao;
import com.hxxdemo.weixinsaomalogin.dao.WxAccessTokenDao;
import com.hxxdemo.weixinsaomalogin.service.WxAccessTokenService;
import com.hxxdemo.weixinsaomalogin.util.AccessToken;
import com.hxxdemo.weixinsaomalogin.util.CommonUtil;
import com.hxxdemo.weixinsaomalogin.util.WeiXinUtil;

import net.sf.json.JSONObject;

@Service
public class WxAccessTokenServiceImpl implements WxAccessTokenService {
	@Autowired
	private WxAccessTokenDao wxAccessTokenDao;

	private Logger logger = LoggerFactory.getLogger(getClass());


	@Override
	public  AccessToken getAccessToken(String appid, String appSecret,boolean bool) {
		logger.info("getAccessToken:","appid=",appid," - appSecret:",appSecret," - bool:",bool);
		AccessToken accesstoken=new AccessToken();
		if(bool) {
			accesstoken = WeiXinUtil.getAccessToken(appid, appSecret);
			wxAccessTokenDao.deleteAccessToken();
			wxAccessTokenDao.insertAccessToken(accesstoken);
		}else {
			//查看数据是否有AccessToken
			int count = wxAccessTokenDao.countAccessToken();
			if(count==0) {
				accesstoken = WeiXinUtil.getAccessToken(appid, appSecret);
				wxAccessTokenDao.insertAccessToken(accesstoken);
			}else if(count==1){
				List<Map<String,Object>> list = wxAccessTokenDao.getAccessToken();
				if(null !=list && list.size()>0) {
					accesstoken.setExpiresIn(Integer.valueOf(list.get(0).get("expires_in").toString()));
					accesstoken.setToken(list.get(0).get("accesstoken").toString());
				}else {
					accesstoken = WeiXinUtil.getAccessToken(appid, appSecret);
					wxAccessTokenDao.deleteAccessToken();
					wxAccessTokenDao.insertAccessToken(accesstoken);
				}
			}else {
				wxAccessTokenDao.deleteAccessToken();
				accesstoken = WeiXinUtil.getAccessToken(appid, appSecret);
				wxAccessTokenDao.insertAccessToken(accesstoken);
			}
		}
		return accesstoken;
	} 

}
