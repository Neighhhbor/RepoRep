package com.hxxdemo.sysLogin.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.config.Globals;
import com.hxxdemo.index.service.QemailService;
import com.hxxdemo.sysLogin.Singleton;
import com.hxxdemo.sysLogin.entity.TokenEntity;
import com.hxxdemo.sysLogin.entity.User;
import com.hxxdemo.sysLogin.service.ActivateService;
import com.hxxdemo.sysLogin.service.RegisterService;
import com.hxxdemo.sysLogin.service.TokenService;
import com.hxxdemo.sysLogin.util.EmailConfigLoader;

@Controller
@RequestMapping(value = "/account")
public class ActivateController {

	@Autowired
	private RegisterService registerService;
	@Autowired
	private QemailService qemailService;
	@Autowired
	private ActivateService activateService;
	@Autowired
	private TokenService tokenService;
	
	/**
	 * 获取激活码
	 * @param request
	 * @return
	 */
	@RequestMapping("/activateCode")
	@ResponseBody
	public Map<String,Object> activateCode(String token) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == token || "".equals(token)) {
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
		//验证token是否过期
		TokenEntity tokenEntity = tokenService.queryByToken(token);
		if(null == tokenEntity) {
			Singleton.getInstance().getMap().remove(token);
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
		User user = Singleton.getInstance().getMap().get(token);
		if(null == user ) {
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
//		if(user.getIsactivation()!=0) {
//			returnMap.put("errorcode", Globals.ERRORCODE6002);
//			returnMap.put("errormessage", Globals.ERRORMESSAGE6002);
//			return returnMap;
//		}
		String email = user.getEmail();
		//检查30分钟内是否发送邮件 
		int isSendCode = activateService.isSendCode(email);
		if(isSendCode>0) {
			returnMap.put("errorcode", Globals.ERRORCODE6003);
			returnMap.put("errormessage", Globals.ERRORMESSAGE6003);
			return returnMap;
		}
		String code = UUID.randomUUID().toString();
		//邮箱和激活码存入数据库
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("email", email);
		params.put("code", code);
		Boolean bool =  false;
		bool = qemailService.sendhub("aiXcoderHub社区邀您激活账户", "尊贵的用户，您好！\n    感谢您关注aixcoder社区，点击以下链接激活账户 \n "+EmailConfigLoader.getActivateUrl()+"?code="+code, email);
		activateService.insertEmailCode(params);
		if(bool) {
			returnMap.put("errorcode", Globals.ERRORCODE0);
			returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		}else {
			returnMap.put("errorcode", Globals.ERRORCODE1011);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
		}
		return returnMap;
	}
	/**
	 * 激活账户
	 * @param request
	 * @param code
	 * @return
	 */
	@RequestMapping("/activate")
	@ResponseBody
	public Map<String,Object> activate(HttpServletRequest request,String code,String token) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == token || "".equals(token)) {
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
		//验证token是否过期
		TokenEntity tokenEntity = tokenService.queryByToken(token);
		if(null == tokenEntity) {
			Singleton.getInstance().getMap().remove(token);
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
		if(null == code || "".equals(code)) {
			returnMap.put("errorcode", Globals.ERRORCODE1009);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1009);
			return returnMap;
		}
		//验证验证码是否有效
		Map<String,Object> applyMap = activateService.isApplyCode(code);
		if(null == applyMap) {
			returnMap.put("errorcode", Globals.ERRORCODE1012);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1012);
			return returnMap;
		}
		
		//更新用户状态
		registerService.updateUserActivateStatus(applyMap.get("email").toString());
		//更新验证码状态
		activateService.updateCodeStatus(code);
		User user = Singleton.getInstance().getMap().get(token);
		if(null == user ) {
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
		if(user.getEmail().equals(applyMap.get("email").toString())) {
//				user.setIsactivation(1);
			Singleton.getInstance().setMap(token, user);
		}
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
	}
	
	
}
