package com.hxxdemo.wechat.service ;

import com.hxxdemo.wechat.util.AccessToken;

public interface AccessTokenService {
	/**
	 * 获取accessToken
	 * @param appid
	 * @param appSecret
	 * @param bool
	 * @return
	 */
	AccessToken getAccessToken(String appid, String appSecret, boolean bool);
}
