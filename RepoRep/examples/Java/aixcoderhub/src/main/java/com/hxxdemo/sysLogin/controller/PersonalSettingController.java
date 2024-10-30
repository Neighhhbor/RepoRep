package com.hxxdemo.sysLogin.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.config.BtoaEncode;
import com.hxxdemo.config.Globals;
import com.hxxdemo.sysLogin.Singleton;
import com.hxxdemo.sysLogin.entity.TokenEntity;
import com.hxxdemo.sysLogin.entity.User;
import com.hxxdemo.sysLogin.service.RegisterService;
import com.hxxdemo.sysLogin.service.TokenService;

@Controller
@RequestMapping(value="/personalSetting")
public class PersonalSettingController {
	
	@Autowired
	private RegisterService registerService;
	@Autowired
	private TokenService tokenService;
	
	@RequestMapping(value="/password")
	@ResponseBody
	public Map<String,Object> personalSetting(HttpServletRequest request,String password,String repassword,String token){
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
		//验证密码
		if(null == password || password.equals("")) {
			returnMap.put("errorcode", Globals.ERRORCODE1002);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1002);
			return returnMap;
		}else {
			password = BtoaEncode.decrypt(password);
			repassword = BtoaEncode.decrypt(repassword);
			if(password.trim().length() == password.length()) {
				if(password.trim().length() < 8 || password.trim().length() > 16) {
					returnMap.put("errorcode", Globals.ERRORCODE1003);
					returnMap.put("errormessage", Globals.ERRORMESSAGE1003);
					return returnMap;
				}
				if(null == repassword || "".equals(repassword)) {
					returnMap.put("errorcode", Globals.ERRORCODE1004);
					returnMap.put("errormessage", Globals.ERRORMESSAGE1004);
					return returnMap;
				}
				if(!password.equals(repassword)) {
					returnMap.put("errorcode", Globals.ERRORCODE1005);
					returnMap.put("errormessage", Globals.ERRORMESSAGE1005);
					return returnMap;
				}
			}else {
				returnMap.put("errorcode", Globals.ERRORCODE1006);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1006);
				return returnMap;
			}
		}
		User user = Singleton.getInstance().getMap().get(token);
		if(null == user ) {
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
		
		user.setPassword(password);
		registerService.updateUserPassword(user);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
	}
	
	
}
