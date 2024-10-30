package com.hxxdemo.user.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.config.BtoaEncode;
import com.hxxdemo.config.Globals;
import com.hxxdemo.exception.utils.R;
import com.hxxdemo.index.service.QemailService;
import com.hxxdemo.sysLogin.entity.TokenEntity;
import com.hxxdemo.sysLogin.service.TokenService;
import com.hxxdemo.user.service.UserService;

@Controller
@RequestMapping("account")
public class UserController {
	
	@Autowired
	private TokenService tokenService;
	@Autowired
	private UserService service;
	@Autowired
	private QemailService qemailService;

	
	@RequestMapping("userInfo")
	@ResponseBody
	public R userInfo(@RequestParam String token) {
		Map<String,Object> user = new HashMap<String, Object>();
		if (null == token || token.equals("")) {
    		return R.error(Globals.ERRORCODE7001,Globals.ERRORMESSAGE7001);
		}else {
			TokenEntity tokenEntity = tokenService.queryByToken(token);
			if (null == tokenEntity) {
				return R.error(Globals.ERRORCODE7004,Globals.ERRORMESSAGE7004);
			}else {
				//获取用户信息
				user = service.userInfo(token);
			}
		}
		return R.ok().put("info", user);
	}
	
	@RequestMapping("bindingTelephone")
	@ResponseBody
	public R bindingTelephone(@RequestParam String token,@RequestParam String telephone,@RequestParam String code) {
		Map<String,Object> params = new HashMap<String, Object>();
		if (null == code || code.equals("")) {
			return R.error(Globals.ERRORCODE1009, Globals.ERRORCODE1009);
		}
		if (null == telephone || telephone.equals("")) {
			return R.error(Globals.ERRORCODE10131,Globals.ERRORMESSAGE10131);
		}
		if (null == token || token.equals("")) {
    		return R.error(Globals.ERRORCODE7001,Globals.ERRORMESSAGE7001);
		}else {
			TokenEntity tokenEntity = tokenService.queryByToken(token);
			if (null == tokenEntity) {
				return R.error(Globals.ERRORCODE7004,Globals.ERRORMESSAGE7004);
			}
		}
		if(telephone.length()!=11) {
			return R.error(Globals.ERRORCODE1014, Globals.ERRORMESSAGE1014);
		}
		//验证手机号是否绑定
		int isBindingTelephone = service.isBindingTelephone(telephone);
		if (isBindingTelephone > 0) {
			params.put("email", telephone);
			params.put("type", Globals.MSGCODETYPE6);
			params.put("code", code);
			String id = qemailService.getEmailIdByMap(params);
			if(null == id) {
//				return R.error(Globals.ERRORCODE1012, Globals.ERRORMESSAGE1012);
			}else {
				qemailService.updateIsapplyByCode(params);
			}
			return R.error(Globals.ERRORCODE10133, Globals.ERRORMESSAGE10133);
		}
		if(code.trim().length()!=4) {
			return R.error(Globals.ERRORCODE1012, Globals.ERRORMESSAGE1012);
		}else {
			params.put("email", telephone);
			params.put("type", Globals.MSGCODETYPE6);
			params.put("code", code);
			String id = qemailService.getEmailIdByMap(params);
			if(null == id) {
				return R.error(Globals.ERRORCODE1012, Globals.ERRORMESSAGE1012);
			}else {
				qemailService.updateIsapplyByCode(params);
			}
		}
		//绑定手机号
		service.bindingTelephone(telephone,token);
		return R.ok();
	}
	@RequestMapping("bindingEmail")
	@ResponseBody
	public R bindingEmail(@RequestParam String token,@RequestParam String email,@RequestParam String code) {
		Map<String,Object> params = new HashMap<String, Object>();
		if (null == code || code.equals("")) {
			return R.error(Globals.ERRORCODE1009, Globals.ERRORCODE1009);
		}
		if (null == email || email.equals("")) {
			return R.error(Globals.ERRORCODE1001,Globals.ERRORMESSAGE1001);
		}
		if (null == token || token.equals("")) {
    		return R.error(Globals.ERRORCODE7001,Globals.ERRORMESSAGE7001);
		}else {
			TokenEntity tokenEntity = tokenService.queryByToken(token);
			if (null == tokenEntity) {
				return R.error(Globals.ERRORCODE7004,Globals.ERRORMESSAGE7004);
			}
		}
		//验证邮箱
		String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		if(!match(regex,email)) {
			return R.error(Globals.ERRORCODE1007, Globals.ERRORMESSAGE1007);
		}
		//验证邮箱是否绑定
		int isBindingTelephone = service.isBindingEmail(email);
		if (isBindingTelephone > 0) {
			params.put("email", email);
			params.put("type", Globals.MSGCODETYPE7);
			params.put("code", code);
			String id = qemailService.getEmailIdByMap(params);
			if(null == id) {
//				return R.error(Globals.ERRORCODE1012, Globals.ERRORMESSAGE1012);
			}else {
				qemailService.updateIsapplyByCode(params);
			}
			return R.error(Globals.ERRORCODE10134, Globals.ERRORMESSAGE10134);
		}
		if(code.trim().length()!=4) {
			return R.error(Globals.ERRORCODE1012, Globals.ERRORMESSAGE1012);
		}else {
			params.put("email", email);
			params.put("type", Globals.MSGCODETYPE7);
			params.put("code", code);
			String id = qemailService.getEmailIdByMap(params);
			if(null == id) {
				return R.error(Globals.ERRORCODE1012, Globals.ERRORMESSAGE1012);
			}else {
				qemailService.updateIsapplyByCode(params);
			}
		}
		//绑定邮箱
		service.bindingEmail(email,token);
		return R.ok();
	}
	
	@RequestMapping("userVerification")
	@ResponseBody
	public R userVerification(@RequestParam String token,@RequestParam String username,@RequestParam String code) {
		Map<String,Object> params = new HashMap<>();
		
    	if (null == code || code.equals("")) {
			return R.error(Globals.ERRORCODE1009, Globals.ERRORCODE1009);
		}
		if (null == username || username.equals("")) {
			return R.error(Globals.ERRORCODE1013,Globals.ERRORMESSAGE1013);
		}
		if (null == token || token.equals("")) {
    		return R.error(Globals.ERRORCODE7001,Globals.ERRORMESSAGE7001);
		}else {
			TokenEntity tokenEntity = tokenService.queryByToken(token);
			if (null == tokenEntity) {
				return R.error(Globals.ERRORCODE7004,Globals.ERRORMESSAGE7004);
			}
		}
    	params.put("code", code);
    	params.put("username", username);
    	int type = 0;
    	if(username.contains("@")) {
			type = Globals.MSGCODETYPE9;
			params.put("type", type);
			params.put("email", username);
			//验证邮箱
			String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
			if(!match(regex,username)) {
				return R.error(Globals.ERRORCODE1007, Globals.ERRORMESSAGE1007);
			}
		}else {
			type = Globals.MSGCODETYPE8;
			params.put("type", type);
			params.put("email", username);
			if(username.length()!=11) {
				return R.error(Globals.ERRORCODE1014, Globals.ERRORMESSAGE1014);
			}
		}
    	if(code.trim().length()!=4) {
			return R.error(Globals.ERRORCODE1012, Globals.ERRORMESSAGE1012);
		}else {
			params.put("email", username);
			params.put("type", type);
			params.put("code", code);
			String id = qemailService.getEmailIdByMap(params);
			if(null == id) {
				return R.error(Globals.ERRORCODE1012, Globals.ERRORMESSAGE1012);
			}else {
				qemailService.updateIsapplyByCode(params);
			}
		}
    	//验证是否是本人操作
    	int isOneSelf = service.isOneSelf(username,token);
    	if (isOneSelf == 0 ) {
			return R.error(Globals.ERRORCODE4001,Globals.ERRORMESSAGE4001);
		}
		return R.ok();
	}
	
	// 校验微信二维码
    @RequestMapping("checkEventKeyBindingWechat")
    @ResponseBody 
    public Map<String, Object> checkEventKeyBindingWechat(@RequestParam String eventKey,@RequestParam String token){
        // 根据scene_str查询数据库，获取对应记录
    	if (null == token || token.equals("")) {
    		return R.error(Globals.ERRORCODE7001,Globals.ERRORMESSAGE7001);
		}
    	//校验是否扫码
    	Map<String,Object> eventMap = service.checkEventKey(eventKey);
    	if (null == eventMap) {
			return R.ok().put("result", false);
		}
    	int isBindingWechat = service.isBindingWechat(token);
    	if (isBindingWechat > 0) {
			return R.error(Globals.ERRORCODE1021,Globals.ERRORMESSAGE1021);
		}
    	TokenEntity tokenEntity = tokenService.queryByToken(token);
    	if (null == tokenEntity) {
    		return R.error(Globals.ERRORCODE7004,Globals.ERRORMESSAGE7004);
    	}
    	String wechatid = eventMap.get("wechatid").toString();
    	//用户绑定微信
    	service.userBindingWechat(wechatid,token);
    	return R.ok().put("result", true);
    }
    @RequestMapping("setPassword")
    @ResponseBody
    public R setPassword(@RequestParam String token,@RequestParam String password,@RequestParam String repassword) {
    	if (null == token || token.equals("")) {
    		return R.error(Globals.ERRORCODE7001,Globals.ERRORMESSAGE7001);
		}
    	if(null == password || "".equals(password.trim())) {
    		return R.error(Globals.ERRORCODE1002,Globals.ERRORMESSAGE1002);
		}else {
			password = BtoaEncode.decrypt(password);
			repassword = BtoaEncode.decrypt(repassword);
			if(password.trim().length() == password.length()) {
				if(password.trim().length() < 8 || password.trim().length() > 16) {
					return R.error(Globals.ERRORCODE1003,Globals.ERRORMESSAGE1003);
				}
				if(null == repassword || "".equals(repassword)) {
					return R.error(Globals.ERRORCODE1004,Globals.ERRORMESSAGE1004);
				}
				if(!password.equals(repassword)) {
					return R.error(Globals.ERRORCODE1005,Globals.ERRORMESSAGE1005);
				}
			}else {
				return R.error(Globals.ERRORCODE1006,Globals.ERRORMESSAGE1006);
			}
		}
    	TokenEntity tokenEntity = tokenService.queryByToken(token);
    	if (null == tokenEntity) {
    		return R.error(Globals.ERRORCODE7004,Globals.ERRORMESSAGE7004);
    	}
    	service.setPassword(token,password);
    	
    	return R.ok();
    }
    
    @RequestMapping("resetPassword")
    @ResponseBody
    public R resetPassword(@RequestParam String token,@RequestParam String password,@RequestParam String username,@RequestParam String code,@RequestParam String repassword) {
    	if (null == token || token.equals("")) {
    		return R.error(Globals.ERRORCODE7001,Globals.ERRORMESSAGE7001);
		}
    	if(null == password || "".equals(password.trim())) {
    		return R.error(Globals.ERRORCODE1002,Globals.ERRORMESSAGE1002);
		}else {
			password = BtoaEncode.decrypt(password);
			repassword = BtoaEncode.decrypt(repassword);
			if(password.trim().length() == password.length()) {
				if(password.trim().length() < 8 || password.trim().length() > 16) {
					return R.error(Globals.ERRORCODE1003,Globals.ERRORMESSAGE1003);
				}
				if(null == repassword || "".equals(repassword)) {
					return R.error(Globals.ERRORCODE1004,Globals.ERRORMESSAGE1004);
				}
				if(!password.equals(repassword)) {
					return R.error(Globals.ERRORCODE1005,Globals.ERRORMESSAGE1005);
				}
			}else {
				return R.error(Globals.ERRORCODE1006,Globals.ERRORMESSAGE1006);
			}
		}
    	TokenEntity tokenEntity = tokenService.queryByToken(token);
    	if (null == tokenEntity) {
    		return R.error(Globals.ERRORCODE7004,Globals.ERRORMESSAGE7004);
    	}
    	Map<String,Object> params = new HashMap<>();
    	Map<String,Object> param = new HashMap<>();
    	param.put("token", token);
    	if (null == code || code.equals("")) {
			return R.error(Globals.ERRORCODE1009, Globals.ERRORCODE1009);
		}else {
	    	params.put("code", code);
	    	int type = 0;
	    	if(username.contains("@")) {
				type = Globals.MSGCODETYPE5;
				params.put("type", type);
				params.put("email", username);
				param.put("email", username);
				//验证邮箱
				String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
				if(!match(regex,username)) {
					return R.error(Globals.ERRORCODE1007, Globals.ERRORMESSAGE1007);
				}
			}else {
				type = Globals.MSGCODETYPE4;
				params.put("type", type);
				params.put("email", username);
				param.put("telephone", username);
				if(username.length()!=11) {
					return R.error(Globals.ERRORCODE1014, Globals.ERRORMESSAGE1014);
				}
			}
	    	if(code.trim().length()!=4) {
				return R.error(Globals.ERRORCODE1012, Globals.ERRORMESSAGE1012);
			}else {
				params.put("email", username);
				params.put("type", type);
				params.put("code", code);
				String id = qemailService.getEmailIdByMap(params);
				if(null == id) {
					return R.error(Globals.ERRORCODE1012, Globals.ERRORMESSAGE1012);
				}else {
					qemailService.updateIsapplyByCode(params);
				}
			}
		}
    	//校验用户
    	int checkUser = service.checkUser(param);
    	if (checkUser == 0 ) {
    		return R.error(Globals.ERRORCODE4001, Globals.ERRORMESSAGE4001);
		}else {
			service.setPassword(token,password);
		}
    	return R.ok();
    }
    @RequestMapping("cancelBindingTelephone")
    @ResponseBody
    public R cancelBindingTelephone(@RequestParam String token) {
    	if (null == token || token.equals("")) {
    		return R.error(Globals.ERRORCODE7001,Globals.ERRORMESSAGE7001);
		}
    	TokenEntity tokenEntity = tokenService.queryByToken(token);
    	if (null == tokenEntity) {
    		return R.error(Globals.ERRORCODE7004,Globals.ERRORMESSAGE7004);
    	}
    	int countTelephoneEmail = service.countTelephoneEmail(token);
    	if (countTelephoneEmail == 0 ) {
			return R.error(Globals.ERRORCODE4001, Globals.ERRORMESSAGE4001);
		}
    	service.editTelephoneIsNull(token);
    	return R.ok();
    }
    @RequestMapping("cancelBindingEmail")
    @ResponseBody
    public R cancelBindingEmail(@RequestParam String token) {
    	if (null == token || token.equals("")) {
    		return R.error(Globals.ERRORCODE7001,Globals.ERRORMESSAGE7001);
    	}
    	TokenEntity tokenEntity = tokenService.queryByToken(token);
    	if (null == tokenEntity) {
    		return R.error(Globals.ERRORCODE7004,Globals.ERRORMESSAGE7004);
    	}
    	int countTelephoneEmail = service.countTelephoneEmail(token);
    	if (countTelephoneEmail == 0 ) {
    		return R.error(Globals.ERRORCODE4001, Globals.ERRORMESSAGE4001);
    	}
    	service.editEmailIsNull(token);
    	return R.ok();
    }
    
    
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
    }
}
