package com.hxxdemo.wechat.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "wechatService")
public class ServiceNumberController {

	
	@RequestMapping("oauth")
	@ResponseBody
	public Map<String, Object> oauth(HttpServletRequest request, HttpServletResponse response,String code){
		
		Map<String, Object> returnMap = new HashMap<String,Object>();
		
		returnMap.put("ok", "ok");
		
		return returnMap;
		
	}

}
