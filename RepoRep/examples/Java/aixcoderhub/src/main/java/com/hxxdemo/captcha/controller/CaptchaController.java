package com.hxxdemo.captcha.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hxxdemo.captcha.service.CaptchaService;
import com.hxxdemo.captcha.util.CreateImageCode;
import com.hxxdemo.exception.RRException;


@Controller
public class CaptchaController {

	@Autowired
	private CaptchaService service;
	
	@RequestMapping("captcha.jpg")
	public void captcha(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam String token
			) throws IOException {
		Map<String, Object> params = new HashMap<>();
		CreateImageCode vCode = new CreateImageCode(100,30,5,10);
		if(token == null || token.trim().equals("")) {
			//记录ip
			String ip = getIpAddress(request); 
			service.saveIp(ip);
			throw new RRException("token不能为空");
		}
		//验证sessionid长度
		if(token.length()>32 || token.length() < 24) {
			//记录ip
			String ip = getIpAddress(request);
			service.saveIp(ip);
			throw new RRException("token不符合要求");
		}
		String code = vCode.getCode();
		params.put("code", code);
		params.put("uuid", token);
		//验证uuid 是否存在
		int index = service.countUuid(params);
		if(index > 0 ) {
			String ip = getIpAddress(request);
			service.saveIp(ip);
			throw new RRException("uuid重复");
		}
		//验证码添加至数据库
		service.save(params);
        vCode.write(response.getOutputStream());
	}
	public static String getIpAddress(HttpServletRequest request) {		
		String ip = request.getHeader("x-forwarded-for");		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {			
			ip = request.getHeader("Proxy-Client-IP");		
		}		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {			
			ip = request.getHeader("WL-Proxy-Client-IP");		
		}		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {	
			ip = request.getHeader("HTTP_CLIENT_IP");		
		}		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {	
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");		
		}		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {	
			ip = request.getRemoteAddr();		
		}		
		return ip;
	}
	
}
