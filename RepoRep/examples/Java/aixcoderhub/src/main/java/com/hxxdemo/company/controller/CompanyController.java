package com.hxxdemo.company.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.company.dao.TokenCompanyEntity;
import com.hxxdemo.company.service.CompanyService;
import com.hxxdemo.company.service.TokenCompanyService;
import com.hxxdemo.config.Globals;

@RequestMapping("/company")
@Controller
public class CompanyController {

	@Autowired
	private CompanyService service;
	
	@Autowired
	private TokenCompanyService tokenService;
	
	
	@RequestMapping("/companyLogin")
	@ResponseBody
	public Map<String, Object> companyLogin(@RequestParam String username,@RequestParam String password){
		Map<String, Object> map = new HashMap<String, Object>();
		if (null == username || null == password || "".equals(username.trim()) || "".equals(password.trim())) {
			map.put("errorcode",Globals.ERRORCODE8001);
			map.put("errormessage",Globals.ERRORMESSAGE8001);
			return map;
		}
		Map<String, Object> user = service.getCompany(username,password);
		if(null == user) {
			map.put("errorcode",Globals.ERRORCODE8002);
			map.put("errormessage",Globals.ERRORMESSAGE8002);
			return map;
		}
		//获取token
		TokenCompanyEntity entity  = tokenService.createCompanyToken(Long.valueOf(user.get("id").toString()));
		user.put("token",entity.getToken());
		user.remove("id");
		map.put("info",user);
		map.put("errorcode",Globals.ERRORCODE0);
		map.put("errormessage",Globals.ERRORMESSAGE0);
		return map;
	}
	
	@RequestMapping("/companyConfig")
	@ResponseBody
	public Map<String, Object> companyConfig(@RequestParam String username,@RequestParam String token){
		Map<String, Object> map = new HashMap<String, Object>();
		if(null == token || "".equals(token.trim())) {
			map.put("errorcode",Globals.ERRORCODE8003);
			map.put("errormessage",Globals.ERRORMESSAGE8003);
			return map;
		}
		if(null == username || "".equals(username.trim())) {
			map.put("errorcode",Globals.ERRORCODE8004);
			map.put("errormessage",Globals.ERRORMESSAGE8004);
			return map;
		}
		Long userId = service.getCompanyUserId(username);
		if(null == userId) {
			map.put("errorcode",Globals.ERRORCODE4001);
			map.put("errormessage",Globals.ERRORMESSAGE4001);
			return map;
		}
		TokenCompanyEntity entity = tokenService.getToken(userId);
		if(null == entity) {
			map.put("errorcode",Globals.ERRORCODE2001);
			map.put("errormessage",Globals.ERRORMESSAGE2001);
			return map;
		}else {
			if(!token.equals(entity.getToken())) {
				map.put("errorcode",Globals.ERRORCODE4001);
				map.put("errormessage",Globals.ERRORMESSAGE4001);
				return map;
			}
		}
		List<Map<String,Object>> info = service.getCompanyDoc(userId);
		map.put("list",info);
		map.put("flags", service.getFlags(userId));
		map.put("errorcode",Globals.ERRORCODE0);
		map.put("errormessage",Globals.ERRORMESSAGE0);
		return map;
	}
	@RequestMapping("/companyUserConfig")
	@ResponseBody
	public Map<String, Object> companyUerConfig(@RequestParam String username,@RequestParam String token){
		Map<String, Object> map = new HashMap<String, Object>();
		if(null == token || "".equals(token.trim())) {
			map.put("errorcode",Globals.ERRORCODE8003);
			map.put("errormessage",Globals.ERRORMESSAGE8003);
			return map;
		}
		if(null == username || "".equals(username.trim())) {
			map.put("errorcode",Globals.ERRORCODE8004);
			map.put("errormessage",Globals.ERRORMESSAGE8004);
			return map;
		}
		Long userId = service.getCompanyUserId(username);
		if(null == userId) {
			map.put("errorcode",Globals.ERRORCODE4001);
			map.put("errormessage",Globals.ERRORMESSAGE4001);
			return map;
		}
		TokenCompanyEntity entity = tokenService.getToken(userId);
		if(null == entity) {
			map.put("errorcode",Globals.ERRORCODE2001);
			map.put("errormessage",Globals.ERRORMESSAGE2001);
			return map;
		}else {
			if(!token.equals(entity.getToken())) {
				map.put("errorcode",Globals.ERRORCODE4001);
				map.put("errormessage",Globals.ERRORMESSAGE4001);
				return map;
			}
		}
		List<Map<String,Object>> info = service.getCompanyUserDoc(userId);
		map.put("list",info);
		map.put("flags", service.getFlags(userId));
		map.put("errorcode",Globals.ERRORCODE0);
		map.put("errormessage",Globals.ERRORMESSAGE0);
		return map;
	}
	
}
