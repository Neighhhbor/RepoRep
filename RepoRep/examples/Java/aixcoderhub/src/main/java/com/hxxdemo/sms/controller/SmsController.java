package com.hxxdemo.sms.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.ToStringExclude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.hxxdemo.captcha.service.CaptchaService;
import com.hxxdemo.config.Globals;
import com.hxxdemo.githubLogin.util.CommonUtil;
import com.hxxdemo.index.service.QemailService;
import com.hxxdemo.plug.service.PlugService;
import com.hxxdemo.sms.service.SmsService;
import com.hxxdemo.sms.util.SendSms;
import com.hxxdemo.sms.util.SmsConfigLoader;
import com.hxxdemo.sysLogin.service.RegisterService;


@Controller
@RequestMapping(value = "/sms")
public class SmsController {
	@Autowired
	private SmsService smsService;
	@Autowired
    private QemailService qemailService;
	@Autowired
	private RegisterService registerService;
	@Autowired
	private CaptchaService captchaService;
	@Autowired
	private PlugService plugService;
	/**
     * 插件注册码发送短信
     * @param telephone 
     * @return
     */
    @RequestMapping(value="plugRegisterSms")
    @ResponseBody
    public Map<String,Object> plugRegisterSms(String telephone,String retLanguage){
    	Map<String,Object> returnMap = new HashMap<String,Object>();
    	//是否是发送邮箱验证码
    	boolean isSendEmail = false;
    	int type= 0;
    	if(null == telephone) {
    		returnMap.put("errorcode", Globals.ERRORCODE1013);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
    		return returnMap;
    	}else {
    		if(telephone.contains("@")) {
    			isSendEmail = true;
    			//验证是否存在该用户
//				String uuid = plugService.getUUID(telephone);
//				if(null == uuid || uuid.equals("")) {
//					returnMap.put("errorcode", Globals.ERRORCODE9018);
//					returnMap.put("errormessage", Globals.ERRORMESSAGE9018);
//					return returnMap; 
//				}
    			type = Globals.MSGCODETYPE5;
    			//验证邮箱
    			if(null == telephone || telephone.equals("")) {
    				returnMap.put("errorcode", Globals.ERRORCODE1001);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE1001);
    				return returnMap;
    			}else {
    				String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    				if(!match(regex,telephone)) {
    					returnMap.put("errorcode", Globals.ERRORCODE1007);
    					returnMap.put("errormessage", Globals.ERRORMESSAGE1007);
    					return returnMap;
    				}
    			}
    		}else {
    			type = Globals.MSGCODETYPE4;
    			if(telephone.length()!=11) {
    				returnMap.put("errorcode", Globals.ERRORCODE1014);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE1014);
    				return returnMap;
    			}else {
    				if(!CommonUtil.isTelephone(telephone)) {
    					returnMap.put("errorcode", Globals.ERRORCODE1015);
    					returnMap.put("errormessage", Globals.ERRORMESSAGE1015);
    					return returnMap;
    				}
    			}
    		}
    	}
    	Map<String,Object> param  = new HashMap<String,Object>();
		param.put("email", telephone);
		param.put("type", type);
		int codeis = qemailService.countEmailByName(param);
		if(codeis>0) {
			returnMap.put("errorcode", Globals.ERRORCODE1010);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1010);
			return returnMap;
		}
    	String validatecode = (int)((Math.random()*9+1)*1000)+"";
    	
    	if(isSendEmail) {
    		Boolean bool =  false;
//    		bool = qemailService.sendhub("aiXcoder插件验证码", "您的验证码是 :"+validatecode, telephone);
    		if (null != retLanguage && retLanguage.equals("en")) {
    			bool = qemailService.sendhub("aiXcoder Verification Code", "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'><html>	<head>		<title>			aiXcoder试用		</title>	</head>	<style>	</style>	<body>		<div style=\"\"> <p><h3>Dear Customer, </h3></p><p><h3>Your current account is : "+telephone+"</h3></p></p><p><h3>Your verification code is : <label style=\"font-size:20px;\">"+validatecode+"</label></h3></p></br> <p style=\"color: #6A6A69;\">Notes: The verification code is valid in 2 minutes. </p><p style=\"color: #6A6A69;\">For the safety of your account, do not share this code with anyone else.</p><p style=\"color: #6A6A69;\">Sincerely.</p><p style=\"color: #6A6A69;\">The aiXcoder Team</p></div></body></html>", telephone);
			}else {
				bool = qemailService.sendhub("aiXcoder验证码", "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'><html>	<head>		<title>			aiXcoder试用		</title>	</head>	<style>	</style>	<body>		<div style=\"\"> <p><h3>尊敬的用户：您好</h3></p><p><h3>您正在使用 "+telephone+" 邮箱账户进行操作，验证码是：<label style=\"font-size:20px;\">"+validatecode+"</label></h3></p></br> <p style=\"color: #6A6A69;\">温馨提示：验证码有效期2分钟，为了您的账户安全，请勿泄露给他人。</p><p style=\"color: #6A6A69;\">谢谢！</p><p style=\"color: #6A6A69;\">aiXcoder团队</p></div></body></html>", telephone);
			}
    		if(bool) {
    			returnMap.put("errorcode", Globals.ERRORCODE0);
    			returnMap.put("errormessage", Globals.ERRORMESSAGE0);
    		}else {
    			returnMap.put("errorcode", Globals.ERRORCODE1011);
    			returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
    		}
    	}else {
    		
        	//调用注册模板
        	String templeteCode = SmsConfigLoader.getTempleteCodePlug();
        	if (null != retLanguage && retLanguage.equals("en")) {
        		templeteCode = SmsConfigLoader.getTempleteCodePlugEn();
        	}
        	try {
    			SendSmsResponse sendSmsResponse  = SendSms.sendSms(telephone, validatecode, templeteCode);
    			if(null != sendSmsResponse.getCode() && sendSmsResponse.getCode().equals("OK")) {
    				returnMap.put("errorcode", Globals.ERRORCODE0);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE0);
    			}else {
    				returnMap.put("errorcode", Globals.ERRORCODE1011);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
    				return returnMap;
    			}
    		} catch (ClientException e) {
    			e.printStackTrace();
    			returnMap.put("errorcode", Globals.ERRORCODE1011);
    			returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
    			return returnMap;
    		}
    	}
    	Map<String,Object> params = new HashMap<String,Object>();
		params.put("isapply", "0");
		params.put("code", validatecode);
		params.put("email", telephone);
		params.put("type", type);
//		params.put("time", Globals.CODESMSTIMESECOND);
		if (isSendEmail) {
			params.put("time", Globals.CODEEMAILTIME);
		}else {
			params.put("time", Globals.CODESMSTIME);
		}
		//插入数据到数据库
		qemailService.insertEmail(params);
    	return returnMap;
    }
    /**
     * 绑定手机或邮箱验证码
     * @param telephone 
     * @return
     */
    @RequestMapping(value="bindingVerificationCode")
    @ResponseBody
    public Map<String,Object> bindingVerificationCode(String username,String retLanguage){
    	Map<String,Object> returnMap = new HashMap<String,Object>();
    	//是否是发送邮箱验证码
    	boolean isSendEmail = false;
    	int type= 0;
    	if(null == username) {
    		returnMap.put("errorcode", Globals.ERRORCODE1013);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
    		return returnMap;
    	}else {
    		if(username.contains("@")) {
    			isSendEmail = true;
    			type = Globals.MSGCODETYPE7;
    			//验证邮箱
    			if(null == username || username.equals("")) {
    				returnMap.put("errorcode", Globals.ERRORCODE1001);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE1001);
    				return returnMap;
    			}else {
    				String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    				if(!match(regex,username)) {
    					returnMap.put("errorcode", Globals.ERRORCODE1007);
    					returnMap.put("errormessage", Globals.ERRORMESSAGE1007);
    					return returnMap;
    				}
    			}
    		}else {
    			type = Globals.MSGCODETYPE6;
    			if(username.length()!=11) {
    				returnMap.put("errorcode", Globals.ERRORCODE1014);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE1014);
    				return returnMap;
    			}else {
    				if(!CommonUtil.isTelephone(username)) {
    					returnMap.put("errorcode", Globals.ERRORCODE1015);
    					returnMap.put("errormessage", Globals.ERRORMESSAGE1015);
    					return returnMap;
    				}
    			}
    		}
    	}
    	Map<String,Object> param  = new HashMap<String,Object>();
    	param.put("email", username);
    	param.put("type", type);
    	int codeis = qemailService.countEmailByName(param);
    	if(codeis>0) {
    		returnMap.put("errorcode", Globals.ERRORCODE1010);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1010);
    		return returnMap;
    	}
    	String validatecode = (int)((Math.random()*9+1)*1000)+"";
    	
    	if(isSendEmail) {
    		Boolean bool =  false;
    		if (null != retLanguage && retLanguage.equals("en")) {
    			bool = qemailService.sendhub("aiXcoder Verification Code", "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'><html>	<head>		<title>			aiXcoder绑定		</title>	</head>	<style>	</style>	<body>		<div style=\"\"> <p><h3>Dear Customer, </h3></p><p><h3>Your current account is : "+username+"</h3></p></p><p><h3>Your verification code is : <label style=\"font-size:20px;\">"+validatecode+"</label></h3></p></br> <p style=\"color: #6A6A69;\">Notes: The verification code is valid in 2 minutes. </p><p style=\"color: #6A6A69;\">For the safety of your account, do not share this code with anyone else.</p><p style=\"color: #6A6A69;\">Sincerely.</p><p style=\"color: #6A6A69;\">The aiXcoder Team</p></div></body></html>", username);
    		}else {
    			bool = qemailService.sendhub("aiXcoder验证码", "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'><html>	<head>		<title>			aiXcoder绑定		</title>	</head>	<style>	</style>	<body>		<div style=\"\"> <p><h3>尊敬的用户：您好</h3></p><p><h3>您正在使用 "+username+" 邮箱账户进行绑定操作，验证码是：<label style=\"font-size:20px;\">"+validatecode+"</label></h3></p></br> <p style=\"color: #6A6A69;\">温馨提示：验证码有效期2分钟，为了您的账户安全，请勿泄露给他人。</p><p style=\"color: #6A6A69;\">谢谢！</p><p style=\"color: #6A6A69;\">aiXcoder团队</p></div></body></html>", username);
    		}
    		if(bool) {
    			returnMap.put("errorcode", Globals.ERRORCODE0);
    			returnMap.put("errormessage", Globals.ERRORMESSAGE0);
    		}else {
    			returnMap.put("errorcode", Globals.ERRORCODE1011);
    			returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
    		}
    	}else {
    		
    		//调用绑定微信模板
    		String templeteCode = SmsConfigLoader.getTempleteBindingWechat();
    		if (null != retLanguage && retLanguage.equals("en")) {
    			templeteCode = SmsConfigLoader.getTempleteBindingWechatEn();
    		}
    		try {
    			SendSmsResponse sendSmsResponse  = SendSms.sendSms(username, validatecode, templeteCode);
    			if(null != sendSmsResponse.getCode() && sendSmsResponse.getCode().equals("OK")) {
    				returnMap.put("errorcode", Globals.ERRORCODE0);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE0);
    			}else {
    				returnMap.put("errorcode", Globals.ERRORCODE1011);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
    				return returnMap;
    			}
    		} catch (ClientException e) {
    			e.printStackTrace();
    			returnMap.put("errorcode", Globals.ERRORCODE1011);
    			returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
    			return returnMap;
    		}
    	}
    	Map<String,Object> params = new HashMap<String,Object>();
    	params.put("isapply", "0");
    	params.put("code", validatecode);
    	params.put("email", username);
    	params.put("type", type);
    	if (isSendEmail) {
    		params.put("time", Globals.CODEEMAILTIME);
    	}else {
    		params.put("time", Globals.CODESMSTIME);
    	}
    	//插入数据到数据库
    	qemailService.insertEmail(params);
    	return returnMap;
    }
    /**
     * 绑定手机或邮箱验证码
     * @param telephone 
     * @return
     */
    @RequestMapping(value="statusVerificationCode")
    @ResponseBody
    public Map<String,Object> statusVerificationCode(String username,String retLanguage){
    	Map<String,Object> returnMap = new HashMap<String,Object>();
    	//是否是发送邮箱验证码
    	boolean isSendEmail = false;
    	int type= 0;
    	if(null == username) {
    		returnMap.put("errorcode", Globals.ERRORCODE1013);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
    		return returnMap;
    	}else {
    		if(username.contains("@")) {
    			isSendEmail = true;
    			type = Globals.MSGCODETYPE9;
    			//验证邮箱
    			if(null == username || username.equals("")) {
    				returnMap.put("errorcode", Globals.ERRORCODE1001);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE1001);
    				return returnMap;
    			}else {
    				String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    				if(!match(regex,username)) {
    					returnMap.put("errorcode", Globals.ERRORCODE1007);
    					returnMap.put("errormessage", Globals.ERRORMESSAGE1007);
    					return returnMap;
    				}
    			}
    		}else {
    			type = Globals.MSGCODETYPE8;
    			if(username.length()!=11) {
    				returnMap.put("errorcode", Globals.ERRORCODE1014);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE1014);
    				return returnMap;
    			}else {
    				if(!CommonUtil.isTelephone(username)) {
    					returnMap.put("errorcode", Globals.ERRORCODE1015);
    					returnMap.put("errormessage", Globals.ERRORMESSAGE1015);
    					return returnMap;
    				}
    			}
    		}
    	}
    	Map<String,Object> param  = new HashMap<String,Object>();
    	param.put("email", username);
    	param.put("type", type);
    	int codeis = qemailService.countEmailByName(param);
    	if(codeis>0) {
    		returnMap.put("errorcode", Globals.ERRORCODE1010);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1010);
    		return returnMap;
    	}
    	String validatecode = (int)((Math.random()*9+1)*1000)+"";
    	
    	if(isSendEmail) {
    		Boolean bool =  false;
    		if (null != retLanguage && retLanguage.equals("en")) {
    			bool = qemailService.sendhub("aiXcoder Verification Code", "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'><html>	<head>		<title>			aiXcoder身份验证		</title>	</head>	<style>	</style>	<body>		<div style=\"\"> <p><h3>Dear Customer, </h3></p><p><h3>Your current account is : "+username+"</h3></p></p><p><h3>Your verification code is : <label style=\"font-size:20px;\">"+validatecode+"</label></h3></p></br> <p style=\"color: #6A6A69;\">Notes: The verification code is valid in 2 minutes. </p><p style=\"color: #6A6A69;\">For the safety of your account, do not share this code with anyone else.</p><p style=\"color: #6A6A69;\">Sincerely.</p><p style=\"color: #6A6A69;\">The aiXcoder Team</p></div></body></html>", username);
    		}else {
    			bool = qemailService.sendhub("aiXcoder验证码", "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'><html>	<head>		<title>			aiXcoder身份验证		</title>	</head>	<style>	</style>	<body>		<div style=\"\"> <p><h3>尊敬的用户：您好</h3></p><p><h3>您正在使用 "+username+" 邮箱账户进行身份验证，验证码是：<label style=\"font-size:20px;\">"+validatecode+"</label></h3></p></br> <p style=\"color: #6A6A69;\">温馨提示：验证码有效期2分钟，为了您的账户安全，请勿泄露给他人。</p><p style=\"color: #6A6A69;\">谢谢！</p><p style=\"color: #6A6A69;\">aiXcoder团队</p></div></body></html>", username);
    		}
    		if(bool) {
    			returnMap.put("errorcode", Globals.ERRORCODE0);
    			returnMap.put("errormessage", Globals.ERRORMESSAGE0);
    		}else {
    			returnMap.put("errorcode", Globals.ERRORCODE1011);
    			returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
    		}
    	}else {
    		
    		//调用身份验证码模板
    		String templeteCode = SmsConfigLoader.getTempleteStautsVerificationCode();
    		if (null != retLanguage && retLanguage.equals("en")) {
    			templeteCode = SmsConfigLoader.getTempleteStautsVerificationCodeEn();
    		}
    		try {
    			SendSmsResponse sendSmsResponse  = SendSms.sendSms(username, validatecode, templeteCode);
    			if(null != sendSmsResponse.getCode() && sendSmsResponse.getCode().equals("OK")) {
    				returnMap.put("errorcode", Globals.ERRORCODE0);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE0);
    			}else {
    				returnMap.put("errorcode", Globals.ERRORCODE1011);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
    				return returnMap;
    			}
    		} catch (ClientException e) {
    			e.printStackTrace();
    			returnMap.put("errorcode", Globals.ERRORCODE1011);
    			returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
    			return returnMap;
    		}
    	}
    	Map<String,Object> params = new HashMap<String,Object>();
    	params.put("isapply", "0");
    	params.put("code", validatecode);
    	params.put("email", username);
    	params.put("type", type);
    	if (isSendEmail) {
    		params.put("time", Globals.CODEEMAILTIME);
    	}else {
    		params.put("time", Globals.CODESMSTIME);
    	}
    	//插入数据到数据库
    	qemailService.insertEmail(params);
    	return returnMap;
    }
	/**
     * 注册码发送短信
     * @param telephone 
     * @return
     */
    @RequestMapping(value="sendRegisterSms")
    @ResponseBody
    public Map<String,Object> sendRegisterSms(String telephone){
    	Map<String,Object> returnMap = new HashMap<String,Object>();
    	if(null == telephone) {
    		returnMap.put("errorcode", Globals.ERRORCODE1013);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
    		return returnMap;
    	}else {
    		if(telephone.length()!=11) {
    			returnMap.put("errorcode", Globals.ERRORCODE1014);
        		returnMap.put("errormessage", Globals.ERRORMESSAGE1014);
        		return returnMap;
    		}else {
    			if(!CommonUtil.isTelephone(telephone)) {
    				returnMap.put("errorcode", Globals.ERRORCODE1015);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE1015);
    				return returnMap;
    			}
    		}
    	}
    	Map<String,Object> param  = new HashMap<String,Object>();
		param.put("email", telephone);
		param.put("type", Globals.MSGCODETYPE4);
		int codeis = qemailService.countEmailByName(param);
		if(codeis>0) {
			returnMap.put("errorcode", Globals.ERRORCODE1010);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1010);
		}
    	String validatecode = (int)((Math.random()*9+1)*1000)+"";
    	//插入数据库
//    	Map<String,Object> params = new HashMap<String,Object>();
//    	params.put("validatecode", validatecode);
//    	params.put("telephone", telephone);
    	//验证码存入数据库
//    	smsService.insertValidatecode(params);
    	Map<String,Object> params = new HashMap<String,Object>();
		params.put("isapply", "0");
		params.put("code", validatecode);
		params.put("email", telephone);
		params.put("type", Globals.MSGCODETYPE4);
		params.put("time", Globals.CODESMSTIME);
		//插入数据到数据库
		qemailService.insertEmail(params);
    	//调用注册模板
    	String templeteCode = SmsConfigLoader.getTempleteCodeRegister();
    	try {
			SendSmsResponse sendSmsResponse  = SendSms.sendSms(telephone, validatecode, templeteCode);
			if(null != sendSmsResponse.getCode() && sendSmsResponse.getCode().equals("OK")) {
				returnMap.put("errorcode", Globals.ERRORCODE0);
				returnMap.put("errormessage", Globals.ERRORMESSAGE0);
			}else {
				returnMap.put("errorcode", Globals.ERRORCODE1011);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
			}
		} catch (ClientException e) {
			e.printStackTrace();
			returnMap.put("errorcode", Globals.ERRORCODE1011);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
			return returnMap;
		}
    	return returnMap;
    }

    /**
     * 注册码发送短信
     * @param telephone 
     * @return
     */
    @RequestMapping(value="sendPasswordSms")
    @ResponseBody
    public Map<String,Object> RegisteredByGithub(String telephone){
    	Map<String,Object> returnMap = new HashMap<String,Object>();
    	if(null == telephone) {
    		returnMap.put("errorcode", Globals.ERRORCODE1013);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
    		return returnMap;
    	}else {
    		if(telephone.length()!=11) {
    			returnMap.put("errorcode", Globals.ERRORCODE1014);
        		returnMap.put("errormessage", Globals.ERRORMESSAGE1014);
        		return returnMap;
    		}else {
    			if(!CommonUtil.isTelephone(telephone)) {
    				returnMap.put("errorcode", Globals.ERRORCODE9003);
    	    		returnMap.put("errormessage", Globals.ERRORMESSAGE9003);
    				return returnMap;
    			}
    		}
    	}
    	Map<String,Object> param  = new HashMap<String,Object>();
		param.put("email", telephone);
		param.put("type", Globals.MSGCODETYPE4);
		int codeis = qemailService.countEmailByName(param);
		if(codeis>0) {
			returnMap.put("errorcode", Globals.ERRORCODE1010);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1010);
			return returnMap;
		}
    	String validatecode = (int)((Math.random()*9+1)*1000)+"";
//    	//插入数据库
//    	Map<String,Object> params = new HashMap<String,Object>();
//    	params.put("validatecode", validatecode);
//    	params.put("telephone", telephone);
//    	//验证码存入数据库
//    	smsService.insertValidatecode(params);
    	Map<String,Object> params = new HashMap<String,Object>();
		params.put("isapply", "0");
		params.put("code", validatecode);
		params.put("email", telephone);
		params.put("type", Globals.MSGCODETYPE4);
		params.put("time", Globals.CODESMSTIME);
		//插入数据到数据库
		qemailService.insertEmail(params);
    	//调用找回密码模板
    	String templeteCode = SmsConfigLoader.getTempleteCodePassword();
    	try {
			SendSmsResponse sendSmsResponse  = SendSms.sendSms(telephone, validatecode, templeteCode);
			if(null != sendSmsResponse.getCode() && sendSmsResponse.getCode().equals("OK")) {
				returnMap.put("errorcode", true);
				returnMap.put("errormessage", "发送成功！");
			}else {
				returnMap.put("errorcode", Globals.ERRORCODE1011);
	    		returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
			}
		} catch (ClientException e) {
			e.printStackTrace();
			returnMap.put("errorcode", Globals.ERRORCODE1011);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
			return returnMap;
		}
    	return returnMap;
    }
    private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
    /**
     * 发送短信或邮件
     * @param telephone 
     * @return
     */
    @RequestMapping(value="plugCaptcha")
    @ResponseBody
    public Map<String,Object> plugCaptcha(String telephone,String token,String code){
    	
    	Map<String,Object> returnMap = new HashMap<String,Object>();
    	//验证token 为验证码uuid
    	if(null == token || "".equals(token)) {
    		returnMap.put("errorcode", Globals.ERRORCODE4001);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
    		return returnMap;
    	}else {
    		//验证验证码
    		if(null == code || "".equals(code)){
    			returnMap.put("errorcode", Globals.ERRORCODE1017);
    			returnMap.put("errormessage", Globals.ERRORMESSAGE1017);
    			return returnMap;
    		}
    		Map<String, Object> codeMap = new HashMap<String,Object>();
    		codeMap.put("uuid", token);
    		codeMap.put("code", code);
    		int isCode = captchaService.countCode(codeMap);
    		if(isCode == 0 ) {
    			returnMap.put("errorcode", Globals.ERRORCODE1018);
    			returnMap.put("errormessage", Globals.ERRORMESSAGE1018);
    			return returnMap;
    		}else {
    			//设置验证码不可用
    			captchaService.update(codeMap);
    		}
    	}
    	
    	//是否是发送邮箱验证码
    	boolean isSendEmail = false;
    	int type= 0;
    	if(null == telephone) {
    		returnMap.put("errorcode", Globals.ERRORCODE1013);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
    		return returnMap;
    	}else {
    		if(telephone.contains("@")) {
    			isSendEmail = true;
    			type = Globals.MSGCODETYPE5;
    			//验证邮箱
    			if(null == telephone || telephone.equals("")) {
    				returnMap.put("errorcode", Globals.ERRORCODE1001);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE1001);
    				return returnMap;
    			}else {
    				String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    				if(!match(regex,telephone)) {
    					returnMap.put("errorcode", Globals.ERRORCODE1007);
    					returnMap.put("errormessage", Globals.ERRORMESSAGE1007);
    					return returnMap;
    				}
    			}
    		}else {
    			type = Globals.MSGCODETYPE4;
    			if(telephone.length()!=11) {
    				returnMap.put("errorcode", Globals.ERRORCODE1014);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE1014);
    				return returnMap;
    			}else {
    				if(!CommonUtil.isTelephone(telephone)) {
    					returnMap.put("errorcode", Globals.ERRORCODE1015);
    					returnMap.put("errormessage", Globals.ERRORMESSAGE1015);
    					return returnMap;
    				}
    			}
    		}
    	}
    	Map<String,Object> param  = new HashMap<String,Object>();
		param.put("email", telephone);
		param.put("type", type);
		int codeis = qemailService.countEmailByName(param);
		if(codeis>0) {
			returnMap.put("errorcode", Globals.ERRORCODE1010);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1010);
			return returnMap;
		}
    	String validatecode = (int)((Math.random()*9+1)*1000)+"";
    	
    	if(isSendEmail) {
    		Boolean bool =  false;
    		bool = qemailService.sendhub("aiXcoder插件验证码", "您的验证码是 :"+validatecode, telephone);
    		if(bool) {
    			returnMap.put("errorcode", Globals.ERRORCODE0);
    			returnMap.put("errormessage", Globals.ERRORMESSAGE0);
    		}else {
    			returnMap.put("errorcode", Globals.ERRORCODE1011);
    			returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
    		}
    	}else {
    		
        	//调用注册模板
        	String templeteCode = SmsConfigLoader.getTempleteCodePlug();
        	try {
    			SendSmsResponse sendSmsResponse  = SendSms.sendSms(telephone, validatecode, templeteCode);
    			if(null != sendSmsResponse.getCode() && sendSmsResponse.getCode().equals("OK")) {
    				returnMap.put("errorcode", Globals.ERRORCODE0);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE0);
    			}else {
    				returnMap.put("errorcode", Globals.ERRORCODE1011);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
    				return returnMap;
    			}
    		} catch (ClientException e) {
    			e.printStackTrace();
    			returnMap.put("errorcode", Globals.ERRORCODE1011);
    			returnMap.put("errormessage", Globals.ERRORMESSAGE1011);
    			return returnMap;
    		}
    	}
    	Map<String,Object> params = new HashMap<String,Object>();
		params.put("isapply", "0");
		params.put("code", validatecode);
		params.put("email", telephone);
		params.put("type", type);
		if (isSendEmail) {
			params.put("time", Globals.CODEEMAILTIME);
		}else {
			params.put("time", Globals.CODESMSTIME);
		}
		//插入数据到数据库
		qemailService.insertEmail(params);
    	return returnMap;
    }
}
