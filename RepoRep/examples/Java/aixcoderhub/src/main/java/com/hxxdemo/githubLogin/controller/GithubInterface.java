package com.hxxdemo.githubLogin.controller;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hxxdemo.config.Globals;
import com.hxxdemo.githubLogin.util.CommonUtil;
import com.hxxdemo.sysLogin.Singleton;
import com.hxxdemo.sysLogin.entity.TokenEntity;
import com.hxxdemo.sysLogin.entity.User;
import com.hxxdemo.sysLogin.service.TokenService;

@Controller
@RequestMapping(value = "/git")
public class GithubInterface {
	
	@Autowired
	private TokenService tokenService;
	/**
	 * 获取个人项目信息
	 * @return
	 */
	@RequestMapping(value = "/personalProject")
	@ResponseBody
	public Map<String,Object> presonalProject(HttpServletRequest request,Integer page,String language,Integer stars,String name,String token){
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
		Map<String,Object> params = new HashMap<String,Object>();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String url = "https://api.github.com/search/repositories?q=";//language%3Apython+stars%3A%3E1600
		if(null != name && !"".equals(name)) {
			url += name;
		}
		if(null !=language && !"".equals(language)) {
			url += "+language:"+URLEncoder.encode(language);
		}
		if(null !=stars ) {
			url += "+stars:"+URLEncoder.encode(">=")+stars;
		}
		if(null == user.getLogin() || "".equals(user.getLogin())) {
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
		url += "+user:"+user.getLogin();
		url += "&page="+page;
		url += "&sort=stars&order=desc";
		String res =CommonUtil.sendGet(url);
		JSONObject openProject = (JSONObject) JSON.parse(res);
		String total_count = openProject.getString("total_count");
		Boolean incomplete_results = openProject.getBoolean("incomplete_results");
		String items = openProject.getString("items");
		params.put("total_count", total_count);
		params.put("incomplete_results", incomplete_results);
		JSONArray projectList = (JSONArray) JSON.parse(items);
		for (int i = 0; i < projectList.size(); i++) {
			Map<String,Object> map = new HashMap<String,Object>();
			JSONObject json = (JSONObject) JSON.parse(projectList.getString(i));
			
			//项目地址
			String html_url = json.getString("html_url");
			//星数
			String star_count = json.getString("watchers")==null?"0":json.getString("watchers");
			//语言
			language = json.get("language")==null?"":json.getString("language");
			//项目名称
			name = json.getString("name");
			//项目全名
			String full_name = json.getString("full_name");
			//描述
			String description = json.getString("description");
			String owner = json.getString("owner");
			JSONObject ownerObject = (JSONObject) JSON.parse(owner);
			map.put("html_url", html_url);
			map.put("star_count", star_count);
			map.put("language", language);
			map.put("name", name);
			map.put("full_name", full_name);
			map.put("description", description);
			list.add(map);
			
		}
		params.put("list", list);
		params.put("page", page);
		System.out.println(params.toString());
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", params);
		return returnMap;
//		Map<String,Object> returnMap = new HashMap<String,Object>();
//		Map<String,Object> params = new HashMap<String,Object>();
//		HttpSession session = request.getSession();
//		if(null == session.getAttribute("user")) {
//			returnMap.put("errorcode", Globals.ERRORCODE2001);
//			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
//			return returnMap;
//		}
//		User user = (User)session.getAttribute("user");
//		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
//		if(null == page) {
//			page=1;
//		}
//		if(null == user.getLogin() || "".equals(user.getLogin())) {
//			returnMap.put("errorcode", Globals.ERRORCODE5002);
//			returnMap.put("errormessage", Globals.ERRORMESSAGE5002);
//			return returnMap;
//		}
//		String url = "https://api.github.com/users/"+user.getLogin()+"/repos?page="+page;
//		String res =CommonUtil.sendGet(url);
//		System.out.println(res);
//		JSONArray projectList = (JSONArray) JSON.parse(res);
//		if(projectList.size()==30 ) {
//			url = "https://api.github.com/users/"+user.getLogin()+"/repos?page="+(page+1);
//			res =CommonUtil.sendGet(url);
//			if(res.equals("[]")) {
//				params.put("success", false);
//			}else {
//				params.put("success", true);
//			}
//		}else if(projectList.size()==0){
//			params.put("success", false);
//		}else{
//			params.put("success", false);
//		}
//		for (int i = 0; i < projectList.size(); i++) {
//			Map<String,Object> map = new HashMap<String,Object>();
//			JSONObject json = (JSONObject) JSON.parse(projectList.getString(i));
//			//项目地址
//			String html_url = json.getString("html_url");
//			//星数
//			String star_count = json.getString("watchers")==null?"0":json.getString("watchers");
//			//语言
//			String language = json.getString("language")==null?"":json.getString("language");
//			//项目名称
//			String name = json.getString("name");
//			//项目全名
//			String full_name = json.getString("full_name");
//			//描述
//			String description = json.getString("description");
//			
//			map.put("html_url", html_url);
//			map.put("star_count", star_count);
//			map.put("language", language);
//			map.put("name", name);
//			map.put("full_name", full_name);
//			map.put("description", description);
//			list.add(map);
//		}
//		params.put("list", list);
//		params.put("page", page);
//		returnMap.put("errorcode", Globals.ERRORCODE0);
//		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
//		returnMap.put("info", params);
//		return returnMap;
	}
	@RequestMapping(value = "/openProject")
	@ResponseBody
	public Map<String,Object> openProject(HttpServletRequest request,Integer page,String language,Integer stars,String name){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> params = new HashMap<String,Object>();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String url = "https://api.github.com/search/repositories?q=";//language%3Apython+stars%3A%3E1600
		if(null == name || "".equals(name)) {
//			returnMap.put("errorcode", Globals.ERRORCODE5001);
//			returnMap.put("errormessage", Globals.ERRORMESSAGE5001);
//			return returnMap;
		}else {
			url += name;
		}
		if(null !=language && !"".equals(language)) {
			url += "+language:"+URLEncoder.encode(language);
		}
		if(null !=stars ) {
			url += "+stars:"+URLEncoder.encode(">")+stars;
		}
		url += "&page="+page;
		url += "&sort=stars&order=desc";
		String res =CommonUtil.sendGet(url);
		System.out.println(url);
		JSONObject openProject = (JSONObject) JSON.parse(res);
		String total_count = openProject.getString("total_count");
		Boolean incomplete_results = openProject.getBoolean("incomplete_results");
		String items = openProject.getString("items");
		params.put("total_count", total_count);
		params.put("incomplete_results", incomplete_results);
		JSONArray projectList = (JSONArray) JSON.parse(items);
		for (int i = 0; i < projectList.size(); i++) {
			Map<String,Object> map = new HashMap<String,Object>();
			JSONObject json = (JSONObject) JSON.parse(projectList.getString(i));
			
			//项目地址
			String html_url = json.getString("html_url");
			//星数
			String star_count = json.getString("watchers")==null?"0":json.getString("watchers");
			//语言
			language = json.get("language")==null?"":json.getString("language");
			//项目名称
			name = json.getString("name");
			//项目全名
			String full_name = json.getString("full_name");
			//描述
			String description = json.getString("description");
			String owner = json.getString("owner");
			JSONObject ownerObject = (JSONObject) JSON.parse(owner);
			map.put("login", ownerObject.getString("login"));
			map.put("avatar_url", ownerObject.getString("avatar_url"));
			map.put("id", ownerObject.getString("id"));
			map.put("node_id", ownerObject.getString("node_id"));
			map.put("html_url", html_url);
			map.put("star_count", star_count);
			map.put("language", language);
			map.put("name", name);
			map.put("full_name", full_name);
			map.put("description", description);
			list.add(map);
			
		}
		params.put("list", list);
		params.put("page", page);
		System.out.println(params.toString());
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", params);
		return returnMap;
	}
}
