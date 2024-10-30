package com.hxxdemo.uninstall.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.config.Globals;
import com.hxxdemo.task.dingding.DingdingUtils;
import com.hxxdemo.task.dingding.TextEntity;
import com.hxxdemo.uninstall.service.UninstallService;
import com.hxxdemo.util.CommonUtil;
import com.taobao.api.ApiException;

@RequestMapping("uninstall")
@Controller
public class UninstallController {

	@Autowired
	private UninstallService service;
	
	@RequestMapping("reasonOld")
	@ResponseBody
	public Map<String, Object> ressonOld(HttpServletRequest request,@RequestParam String reason ){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		if(null == reason || "".equals(reason.trim())) {
			map.put("errorcode",Globals.ERRORCODE1019);
			map.put("errormessage", Globals.ERRORMESSAGE1019);
			return map;
		}
		params.put("reason", reason);
		String ip = CommonUtil.getIpAddress(request);
		params.put("ip", ip);
		service.insertUninstallReason(params);
		map.put("errorcode",Globals.ERRORCODE0);
		map.put("errormessage", Globals.ERRORMESSAGE0);
		return map;
	}
	@RequestMapping("reason")
	@ResponseBody
	public Map<String, Object> resson(HttpServletRequest request,@RequestParam String reason ,String contact ){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		String ip = getIpAddress(request);
		String uuid = UUID.randomUUID().toString().replace("-", "");
		ip = ip + "-" + uuid;
		params.put("ip", ip);
		if(null == reason || "".equals(reason.trim())) {
			map.put("errorcode",Globals.ERRORCODE1019);
			map.put("errormessage", Globals.ERRORMESSAGE1019);
			return map;
		}
		params.put("reason", reason);
		params.put("ip", ip);
		service.insertUninstallReason(params);
		if(null != contact && !"".equals(contact)) {
			params.put("contact", contact);
			service.createContact(params);
			TextEntity textEntity = new TextEntity();
			textEntity.setIsAtAll(false);
			textEntity.setContent("有新的卸载反馈信息，\n"
					+ "联系方式："+contact+"，\n"
					+ "反馈内容:"+reason+"！");
//			textEntity.setContent("这是一个卸载反馈测试！");
			try {
				boolean bool = sendTextMessage(textEntity);
			} catch (ApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		map.put("errorcode",Globals.ERRORCODE0);
		map.put("errormessage", Globals.ERRORMESSAGE0);
		return map;
	}
	@RequestMapping("contact")
	@ResponseBody
	public Map<String, Object> contact(HttpServletRequest request,@RequestParam String contact){
		Map<String, Object> map = new HashMap<String, Object>();
		if(null == contact) {
			map.put("errorcode",Globals.ERRORCODE9007);
			map.put("errormessage", Globals.ERRORMESSAGE9007);
			return map;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contact", contact);
		String ip = getIpAddress(request);
		params.put("ip", ip);
		service.createContact(params);
		map.put("errorcode",Globals.ERRORCODE0);
		map.put("errormessage", Globals.ERRORMESSAGE0);
		return map;
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
	private static String webhook = "https://oapi.dingtalk.com/robot/send?access_token=c90228ed212fda5cdb65918556940d027912867ebd290c56435ac9aea70acd08";
	public static boolean sendTextMessage(TextEntity text) throws ApiException {
		return DingdingUtils.sendToDingding(text,webhook);
	}
}
