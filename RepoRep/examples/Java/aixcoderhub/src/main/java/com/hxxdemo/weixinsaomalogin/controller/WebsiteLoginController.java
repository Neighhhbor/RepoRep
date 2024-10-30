package com.hxxdemo.weixinsaomalogin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hxxdemo.weixinsaomalogin.entity.WechatAccessTokenVo;
import com.hxxdemo.weixinsaomalogin.entity.WechatSNSUserInfoVo;
import com.hxxdemo.weixinsaomalogin.service.WebsiteUserService;
import com.hxxdemo.weixinsaomalogin.util.WechatConfigLoader;
import com.hxxdemo.weixinsaomalogin.util.WechatOAuthProcessUtil;

@Controller
@RequestMapping(value = "/login")
public class WebsiteLoginController {

	@Autowired
	private WebsiteUserService websiteUserService;
	@RequestMapping("/index")
	public ModelAndView index() {
        return new ModelAndView("index");
    }
	/**
	 * 获取二维码
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/qrOpen")
    public void getOAuthCodeauthtest(HttpServletRequest request, HttpServletResponse response) {
    	WechatOAuthProcessUtil.getOAuthCode(request,response);
    }
	/**
	 * 回调登录地址
	 * @param request
	 * @param response
	 * @param code
	 * @return
	 */
    @RequestMapping(value = "/websiteLogin")
    public ModelAndView getOauthAccessToken(HttpServletRequest request, HttpServletResponse response,String code) {
    	String appId = WechatConfigLoader.getAppId();
    	String appSecret = WechatConfigLoader.getAppSecret();
    	WechatAccessTokenVo wechatAccessTokenVo = WechatOAuthProcessUtil.getOauthAccessToken(appId,appSecret,code);
    	String openId = wechatAccessTokenVo.getOpenId();
    	String accessToken = wechatAccessTokenVo.getAccessToken();
    	WechatSNSUserInfoVo wechatSNSUserInfoVo = WechatOAuthProcessUtil.getSNSUserInfo(accessToken, openId);
    	//验证用户是否在数据库中
    	int checkUser = 0 ;
    	checkUser = websiteUserService.checkWebsiteUser(wechatSNSUserInfoVo);
    	if(checkUser>0) {
    		//修改用户信息
    		websiteUserService.updateWebsiteUser(wechatSNSUserInfoVo);
    	}else {
    		//插入用户信息
    		websiteUserService.insertWebsiteUser(wechatSNSUserInfoVo);
    	}
  	  	return new ModelAndView("index");
    }
}
