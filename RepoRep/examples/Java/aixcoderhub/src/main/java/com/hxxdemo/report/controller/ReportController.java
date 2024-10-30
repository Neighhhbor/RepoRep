package com.hxxdemo.report.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.config.Globals;
import com.hxxdemo.report.service.ReportService;


@Controller
@RequestMapping("report")
public class ReportController {

	@Autowired
	private ReportService service;
	
	@RequestMapping("address")
	@ResponseBody
	public Map<String, Object> address(HttpServletRequest request,String city){
		Map<String,Object> returnMap =  new HashMap<String,Object>();
		if(null == city || "".equals(city)) {
			returnMap.put("errorcode", Globals.ERRORCODE9016);
			returnMap.put("errormessage", Globals.ERRORMESSAGE9016);
			return returnMap;
		}
		//一个IP 一天限制一次  不会提醒用户
		String ip = getIpAddress(request);
		Map<String,Object> params =  new HashMap<String,Object>();
		params.put("ip", ip);
		params.put("city", city);
		service.insertReportCity(params);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
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
