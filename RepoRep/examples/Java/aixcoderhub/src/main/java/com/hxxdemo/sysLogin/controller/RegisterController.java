package com.hxxdemo.sysLogin.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.config.BtoaEncode;
import com.hxxdemo.config.Globals;
import com.hxxdemo.githubLogin.util.ValidateCode;
import com.hxxdemo.index.service.QemailService;
import com.hxxdemo.sysLogin.entity.User;
import com.hxxdemo.sysLogin.service.RegisterService;

@Controller
@RequestMapping(value = "/register")
public class RegisterController {
	@Autowired
    private QemailService qemailService;
	@Autowired
	private RegisterService registerService;
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	/**
	 * 发送邮箱注册验证码
	 * @param email
	 * @return
	 */
	@RequestMapping(value="/sendEmail")  
	@ResponseBody
	public Map<String,Object> sendemail(String email){
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
		if(isregister==1) {
			returnMap.put("errorcode", Globals.ERRORCODE1008);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1008);
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
		}
		String randcode = (int)(Math.random()*(999999-100000+1)+100000)+"";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("isapply", "0");
		params.put("code", randcode);
		params.put("email", email);
		params.put("type", Globals.MSGCODETYPE2);
		params.put("time", Globals.CODEEMAILTIME);
		//插入数据到数据库
		qemailService.insertEmail(params);
		Boolean bool =  false;
		bool = qemailService.sendhub("注册账户验证码", "您的验证码是 :"+randcode, email);
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
	 * 验证邮箱是否可用
	 * @param email
	 * @return
	 */
	@RequestMapping(value="/validateEmail")  
	@ResponseBody
	public Map<String,Object> validateEmail(String email){
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
		if(isregister>0) {
			returnMap.put("errorcode", Globals.ERRORCODE1008);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1008);
			return returnMap;
		}
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
	}
	/**
     * 邮箱注册用户
     * @param email 
     * @param password
     * @param repassword 
     */
	@RequestMapping(value="/registerUser")  
	@ResponseBody
	public Map<String,Object> registerUser(HttpServletRequest request,String email,String password,String repassword,String code) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> params = new HashMap<String,Object>();
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
		//验证code
		if(null == code || "".equals(code)) {
			returnMap.put("errorcode", Globals.ERRORCODE1009);
			returnMap.put("errormessage",Globals.ERRORMESSAGE1009);
			return returnMap;
		}
		String id =  "" ;
		params.put("email", email);
		params.put("code", code);
		params.put("type", Globals.MSGCODETYPE2);
		id = qemailService.getEmailIdByMap(params);
		if(null == id || "".equals(id)) {
			returnMap.put("errorcode", Globals.ERRORCODE1012);
			returnMap.put("errormessage",Globals.ERRORMESSAGE1012);
			return returnMap;
		}else {
			//改变isapply状态
			qemailService.updateIsapply(id);
		}
//		//验证验证码
//		HttpSession session = request.getSession();
//		String code = (String) session.getAttribute("code");
//		if(null == validateCode || validateCode.equals("")) {
//			returnMap.put("success", false);
//			returnMap.put("msg", "验证码不能为空！");
//			return returnMap;
//		}else {
//			if(!code.toLowerCase().equals(validateCode.toLowerCase())) {
//				returnMap.put("success", false);
//				returnMap.put("msg", "验证码错误！");
//				return returnMap;
//			}
//		}
//		//验证手机号码
//		if(null == telephone || telephone.equals("")) {
//			returnMap.put("success", false);
//			returnMap.put("msg", "手机号码不能为空！");
//			return returnMap;
//		}else {
//			if(telephone.length()!=11) {
//				returnMap.put("success", false);
//				returnMap.put("msg", "手机号码为11位数字！");
//				return returnMap;
//			}
//			if(!CommonUtil.isTelephone(telephone)) {
//				returnMap.put("success", false);
//				returnMap.put("msg", "手机号码不能为空！");
//				return returnMap;
//			}
//			
//		}
//		//验证手机验证码
//		if(null == smsCode || smsCode.equals("")) {
//			returnMap.put("success", false);
//			returnMap.put("msg", "手机验证码不能为空！");
//			return returnMap;
//		}else {
//			if(smsCode.trim().length()!=4) {
//				returnMap.put("success", false);
//				returnMap.put("msg", "手机验证码错误！");
//				return returnMap;
//			}else {
//				//查询验证码
//				Map<String,Object> validateMap = smsService.getValidatecodeByTelephone(telephone);
//				if(null == validateMap ) {
//					returnMap.put("success", false);
//					returnMap.put("msg", "手机验证码无效！");
//					return returnMap;
//				}else {
//					if(!validateCode.equals(validateMap.get("validatecode").toString())) {
//						returnMap.put("success", false);
//						returnMap.put("msg", "手机验证码错误！");
//						return returnMap;
//					}
//				}
//			}
//		}
		//验证邮箱是否被注册过了
		int isregister = registerService.isregister(email);
		if(isregister>0) {
			returnMap.put("errorcode", Globals.ERRORCODE1008);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1008);
			return returnMap;
		}
		//插入用户 
		User user = new User();
		user.setEmail(email);
		user.setPassword(password);
		registerService.insertUser(user);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
	}
	/** 
     * 响应验证码页面 
     * @return 
     */  
    @RequestMapping(value="/validateCode")  
    public String validateCode(HttpServletRequest request,HttpServletResponse response) throws Exception{  
        // 设置响应的类型格式为图片格式  
        response.setContentType("image/jpeg");  
        //禁止图像缓存。  
        response.setHeader("Pragma", "no-cache");  
        response.setHeader("Cache-Control", "no-cache");  
        response.setDateHeader("Expires", 0);  
        ValidateCode vCode = new ValidateCode(100,35,4,150);  
        HttpSession session = request.getSession();
        session.setAttribute("code", vCode.getCode());  
        vCode.write(response.getOutputStream());  
        return null;  
    } 
}
