package com.hxxdemo.sysLogin.controller;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.config.BtoaEncode;
import com.hxxdemo.config.Globals;
import com.hxxdemo.sysLogin.Singleton;
import com.hxxdemo.sysLogin.entity.TokenEntity;
import com.hxxdemo.sysLogin.entity.User;
import com.hxxdemo.sysLogin.service.RegisterService;
import com.hxxdemo.sysLogin.service.TokenService;


@Controller
@RequestMapping(value = "/login")
public class SysLoginController {
	
	@Autowired
	private RegisterService registerService;
	@Autowired
	private TokenService tokenService;

	
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	/**
	 * 用户登录
	 * @param request
	 * @param email
	 * @param password
	 * @return
	 */
	@RequestMapping("/userLogin")
	@ResponseBody
	public Map<String,Object> index(HttpServletRequest request,String email,String password) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
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
			returnMap.put("errorcode", Globals.ERRORCODE2002);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2002);
			return returnMap;
		}
		//验证密码
		if(null == password || password.equals("")) {
			returnMap.put("errorcode", Globals.ERRORCODE1002);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1002);
			return returnMap;
		}else {
			password = BtoaEncode.decrypt(password);
			if(password.trim().length() == password.length()) {
				if(password.trim().length() < 8 || password.trim().length() > 16) {
					returnMap.put("errorcode", Globals.ERRORCODE2002);
					returnMap.put("errormessage", Globals.ERRORMESSAGE2002);
					return returnMap;
				}
			}else {
				returnMap.put("errorcode", Globals.ERRORCODE1006);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1006);
				return returnMap;
			}
		}
		User user = new User();
		user.setEmail(email);
		user.setPassword(password);
		Map<String,Object> userMap = registerService.getLoginUser(user);
		if(null == userMap) {
			returnMap.put("errorcode", Globals.ERRORCODE2002);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2002);
			return returnMap;
		}
//		User u = registerService.getUser(user);
		User u  = new User();
		u.setAvatarUrl(userMap.get("avatar_url")==null?"":userMap.get("avatar_url").toString());
		u.setCreatetime(userMap.get("createtime").toString());
		u.setEmail(user.email);
		u.setGitid(userMap.get("gitid")==null?"":userMap.get("gitid").toString());
		u.setLogin(userMap.get("login")==null?"":userMap.get("login").toString());
		u.setNodeId(userMap.get("node_id")==null?"":userMap.get("node_id").toString());
		u.setId(Long.valueOf(userMap.get("id").toString()));
		if(u==null) {
			returnMap.put("errorcode", Globals.ERRORCODE2002);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2002);
			return returnMap;
		}
		
		TokenEntity  tokenEntity=  tokenService.createToken(u.getId());
		tokenService.handleToken(tokenEntity.getToken());
		Singleton.getInstance().setMap(tokenEntity.getToken(), u);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", tokenEntity.getToken());
		return returnMap;
    }
	/**
	 * 查询用户状态
	 * @param request
	 * @return
	 */
	@RequestMapping("/user")
	@ResponseBody
	public Map<String,Object> getUser(HttpServletRequest request,String token){
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
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", Singleton.getInstance().getMap().get(token));
		return returnMap;
	}
	/**
	 * 注销用户
	 * @param request
	 * @return
	 */
	@RequestMapping("/logOut")
	@ResponseBody
	public Map<String,Object> logOut(HttpServletRequest request,String token){
//		HttpSession session = request.getSession();
//		session.removeAttribute("user");
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == token || "".equals(token)) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
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
		tokenService.expireToken(user.getId());
		tokenService.updateTokenBytoken(token);
		Singleton.getInstance().getMap().remove(token);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
//		return "redirect:../../communit/#/";
	}
}
