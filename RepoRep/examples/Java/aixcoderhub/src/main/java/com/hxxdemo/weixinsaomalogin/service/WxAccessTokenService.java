package com.hxxdemo.weixinsaomalogin.service;

import java.util.List;
import java.util.Map;

import com.hxxdemo.weixinsaomalogin.util.AccessToken;

public interface WxAccessTokenService {

	/**
	 * 获取accessToken
	 * @param appid
	 * @param appSecret
	 * @param bool
	 * @return
	 */
	AccessToken getAccessToken(String appid, String appSecret, boolean bool);
}
