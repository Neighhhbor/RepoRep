package com.hxxdemo.sysLogin.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.config.Globals;
import com.hxxdemo.index.service.QemailService;
import com.hxxdemo.sysLogin.entity.User;
import com.hxxdemo.sysLogin.service.RegisterService;
import com.hxxdemo.sysLogin.service.RetrievePasswordService;

@Controller
@RequestMapping(value = "/retrieve")
public class RetrievePasswordController {
	@Autowired
	private RegisterService registerService;
	@Autowired
    private QemailService qemailService;
	@Autowired
	private RetrievePasswordService retrievePasswordService;
	
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	/**
	 * 找回密码发送邮件验证码
	 * @param email
	 * @return
	 */
	@RequestMapping(value="/sendEmail")  
	@ResponseBody
	public Map<String,Object> sendEmail(String email){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		//验证邮箱
		if(null == email || email.equals("")) {
			returnMap.put("errorcode", Globals.ERRORCODE1001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1001);
			return returnMap;
		}else {
			String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
			if(!match(regex,email)) {
				returnMap.put("errorcode", Globals.ERRORCODE1007);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1007);
				return returnMap;
			}
		}
		//验证邮箱是否被注册过了
		int isregister = registerService.isregister(email);
		if(isregister==0) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("email", email);
		param.put("type", Globals.MSGCODETYPE3);
		int codeis = qemailService.countEmailByName(param);
		if(codeis>0) {
			returnMap.put("errorcode", Globals.ERRORCODE1010);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1010);
			return returnMap;
		}else {
			String randcode = (int)(Math.random()*(999999-100000+1)+100000)+"";
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("isapply", "0");
			params.put("code", randcode);
			params.put("email", email);
			params.put("type", Globals.MSGCODETYPE3);
			params.put("time", Globals.CODEEMAILTIME);
			//插入数据到数据库
			qemailService.insertEmail(params);
			Boolean bool =  false;
			bool = qemailService.sendhub("找回密码验证码", "您的验证码是 :"+randcode, email);
			if(bool) {
				returnMap.put("errorcode", Globals.ERRORCODE0);
				returnMap.put("errormessage", Globals.ERRORMESSAGE0);
			}else {
				returnMap.put("errorcode", Globals.ERRORCODE1011);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
			}
		}
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
	}
	/**
	 * 
	 * @param email
	 * @param code
	 * @return
	 */
	@RequestMapping(value="/validateEmail")  
	@ResponseBody
	public Map<String,Object> validateEmail(String email,String code){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		//验证邮箱
		if(null == email || email.equals("")) {
			returnMap.put("errorcode", Globals.ERRORCODE1001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1001);
			return returnMap;
		}else {
			String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
			if(!match(regex,email)) {
				returnMap.put("errorcode", Globals.ERRORCODE1007);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1007);
				return returnMap;
			}
		}
		//验证邮箱是否被注册过了
		int isregister = registerService.isregister(email);
		if(isregister==0) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("email", email);
		//验证code
		if(null == code || "".equals(code)) {
			returnMap.put("errorcode", Globals.ERRORCODE1009);
			returnMap.put("errormessage",Globals.ERRORMESSAGE1009);
			return returnMap;
		}
		String id =  "" ;
		params.put("code", code);
		params.put("type", Globals.MSGCODETYPE3);
		id = qemailService.getEmailIdByMap(params);
		if(null == id || "".equals(id)) {
			returnMap.put("errorcode", Globals.ERRORCODE1012);
			returnMap.put("errormessage",Globals.ERRORMESSAGE1012);
			return returnMap;
		}else {
			//改变isapply状态
			qemailService.updateIsapply(id);
		}
		//插入pwdtoken
		String token = UUID.randomUUID().toString();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("token", token);
		paramMap.put("email", email);
		paramMap.put("isapply", 1);
		retrievePasswordService.insertPwdToken(paramMap);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("email", email);
		map.put("token", token);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", map);
		return returnMap;
	}
	
	/**
	 * 重置密码
	 * @param email
	 * @param token
	 * @param code
	 * @param password
	 * @param repassword
	 * @return
	 */
	@RequestMapping(value="/resetPassword")  
	@ResponseBody
	public Map<String,Object> resetPassword(String email,String token,String password,String repassword){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		//验证邮箱
		if(null == email || email.equals("")) {
			returnMap.put("errorcode", Globals.ERRORCODE1001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1001);
			return returnMap;
		}else {
			String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
			if(!match(regex,email)) {
				returnMap.put("errorcode", Globals.ERRORCODE1007);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1007);
				return returnMap;
			}
		}
		//验证邮箱是否被注册过了
		int isregister = registerService.isregister(email);
		if(isregister==0) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		//验证密码
		if(null == password || password.equals("")) {
			returnMap.put("errorcode", Globals.ERRORCODE1002);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1002);
			return returnMap;
		}else {
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
		
		
		//验证token
		if(null == token || "".equals(token)) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("email", email);
		params.put("token", token);
		int isToken = retrievePasswordService.countPwdTokenByEmailToken(params);
		if(isToken == 0 ) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		
		User user = new User();
		user.setEmail(email);
		user.setPassword(password);
		registerService.updateUserPassword(user);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
	}
	
}
