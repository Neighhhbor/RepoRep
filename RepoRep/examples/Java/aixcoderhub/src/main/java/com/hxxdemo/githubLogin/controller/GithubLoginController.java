package com.hxxdemo.githubLogin.controller;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hxxdemo.config.Globals;
import com.hxxdemo.githubLogin.service.GithubUserService;
import com.hxxdemo.githubLogin.util.CommonUtil;
import com.hxxdemo.githubLogin.util.GithubConfigLoader;
import com.hxxdemo.index.service.QemailService;
import com.hxxdemo.sysLogin.Singleton;
import com.hxxdemo.sysLogin.entity.TokenEntity;
import com.hxxdemo.sysLogin.entity.User;
import com.hxxdemo.sysLogin.service.RegisterService;
import com.hxxdemo.sysLogin.service.TokenService;

@Controller
@RequestMapping(value = "/github")
public class GithubLoginController {
	@Autowired
	private RegisterService registerService;
	@Autowired
	private GithubUserService githubUserService;
	@Autowired
    private QemailService qemailService;
	@Autowired
	private TokenService tokenService;
	/**
     * 授权github用户登录
     * @return
     */
    @RequestMapping(value="callback")//callback url
//    public ModelAndView RegisteredByGithub(HttpServletRequest request,String code){
	public String RegisteredByGithub(HttpServletRequest request,String code){
        String me =CommonUtil.sendPost
                ("https://github.com/login/oauth/access_token?client_id="+GithubConfigLoader.getClentId()+"&client_secret="+GithubConfigLoader.getClentSecret()+"&code="+code+"&redirect_uri="+GithubConfigLoader.getBackUrl(),null);
        String atoke = me.split("&")[0];
        String res = CommonUtil.sendGet("https://api.github.com/user?"+atoke+"");
        JSONObject jsonUser = (JSONObject) JSON.parse(res);
        if(null == jsonUser) {
//        	return new ModelAndView("redirect:../index");
        	return "redirect:../../communit/#/";
        }
        Map<String, Object> params = JSONObject.toJavaObject(jsonUser, Map.class);
        User user = new User();
        user.setGitid(jsonUser.getString("id"));
        user.setNodeId(jsonUser.getString("node_id"));
        user.setAvatarUrl(jsonUser.getString("avatar_url"));
        user.setLogin(jsonUser.getString("login"));
        //检查数据库是否包含github授权用户
        Map<String,Object> githubUser = githubUserService.isGithubUser(params);
        
        String token = UUID.randomUUID().toString().replace("-", "");
        Singleton.getInstance().setMap(token, user);
        if(null == githubUser) {
//        	return new ModelAndView("../setting#/GitHubRes");
        	return "redirect:../../communit/#/GitHubRes?token="+token;
        }else {
        	if(null == githubUser.get("email") || "".equals(githubUser.get("email").toString())) {
//        		return new ModelAndView("../setting#/GitHubRes");
        		return "redirect:../../communit/#/GitHubRes?token="+token;
        	}
        }
        TokenEntity  tokenEntity =  tokenService.createToken(Long.valueOf(githubUser.get("id").toString()));
        tokenService.handleToken(tokenEntity.getToken());
        user.setEmail(githubUser.get("email").toString());
        Singleton.getInstance().setMap(tokenEntity.getToken(), user);
        Singleton.getInstance().getMap().remove(token);
//        session.setAttribute("user", user);
//        return new ModelAndView("redirect:../index");
        return "redirect:../../communit/#/modelList?token="+tokenEntity.getToken();
//        return CommonUtil.constructResponse(1,"user_Person_Notice",user);
    }
    private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
    /**
     * 绑定该账号
     * @param request
     * @param email
     * @param password
     * @return
     */
    @RequestMapping(value="setEmail")
    @ResponseBody
    public Map<String,Object> setEmail(HttpServletRequest request,String email,String password,String token) {
    	Map<String,Object> returnMap = new HashMap<String,Object>();
    	if(null == token || "".equals(token)) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		User user  = Singleton.getInstance().getMap().get(token);
		if(null == user) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
    	if(null == user.getEmail() || user.getEmail().equals("")) {
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
    		if(null == password || "".equals(password)) {
    			returnMap.put("errorcode", Globals.ERRORCODE1002);
    			returnMap.put("errormessage", Globals.ERRORMESSAGE1002);
    			return returnMap;
    		}
    		User regUser = new User();
    		regUser.setEmail(email);
    		regUser.setPassword(password);
    		User u = registerService.getUser(regUser);
    		if(null == u ) {
    			returnMap.put("errorcode", Globals.ERRORCODE4001);
    			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
    			return returnMap;
    		}
    		if(null != u.getNodeId() && !"".equals(u.getNodeId())) {
    			returnMap.put("errorcode", Globals.ERRORCODE4001);
    			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
    			return returnMap;
    		}
    		regUser.setAvatarUrl(user.getAvatarUrl());
    		regUser.setNodeId(user.getNodeId());
    		regUser.setLogin(user.getLogin());
    		regUser.setGitid(user.getGitid());
    		u.setAvatarUrl(user.getAvatarUrl());
    		u.setNodeId(user.getNodeId());
    		u.setLogin(user.getLogin());
    		u.setGitid(user.getGitid());
    		//更新用户数据
    		githubUserService.updateGithubUser(regUser);
    		TokenEntity  tokenEntity = tokenService.createToken(u.getId());
    		tokenService.handleToken(tokenEntity.getToken());
    		Singleton.getInstance().getMap().remove(token);
    		Singleton.getInstance().setMap(tokenEntity.getToken(), u);
    		returnMap.put("errorcode", Globals.ERRORCODE0);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
    		returnMap.put("info", tokenEntity.getToken());
    		return returnMap;
    	}else {
    		returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
    		return returnMap;
    	}
    }
    /**
     * 新建账号
     * @param request
     * @param email
     * @param password
     * @param repassword
     * @param code
     * @return
     */
    @RequestMapping(value="createUser")
    @ResponseBody
    public Map<String,Object> createUser(HttpServletRequest request,String email,String password,String repassword,String code,String token) {
    	Map<String,Object> returnMap = new HashMap<String,Object>();
    	if(null == token || "".equals(token)) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
    	User user = Singleton.getInstance().getMap().get(token);
		if(null == user) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
    	if(null != user.getEmail() && !"".equals(user.getEmail())) {
    		returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
    		return returnMap;
    	}
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
		//验证邮箱是否被注册过了
		int isregister = registerService.isregister(email);
		if(isregister>0) {
			returnMap.put("errorcode", Globals.ERRORCODE1008);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1008);
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
		user.setEmail(email);
		user.setPassword(password);
		githubUserService.insertGithubUser(user);
		User u = registerService.getUser(user);
		TokenEntity  tokenEntity=  tokenService.createToken(u.getId());
		user.setPassword(null);
		Singleton.getInstance().setMap(tokenEntity.getToken(), user);
		tokenService.handleToken(tokenEntity.getToken());
		
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", tokenEntity.getToken());
    	return returnMap;
    }
    
}
