package com.hxxdemo.evaluate.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hxxdemo.config.Globals;
import com.hxxdemo.evaluate.service.EvaluateService;
import com.hxxdemo.oss.util.EvaluateConfigLoader;

@RestController
@RequestMapping(value="/evaluate")
public class EvaluateController {

	@Autowired 
	private EvaluateService evaluateService;
	
	/**
	 * 查询类型下拉列表
	 * @return
	 */
	@RequestMapping(value = "getSelect")
	@ResponseBody
	public Map<String,Object> getSelect(){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		
		//查询分类列表
		//SELECT * FROM feedback_type WHERE isapply =1 ORDER BY type ASC , childtype ASC
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>(); 
		list = evaluateService.getSelect();
		if(null == list || list.size()==0) {
			returnMap.put("errorcode", Globals.ERRORCODE4003);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4003);
		}else {
			returnMap.put("errorcode", Globals.ERRORCODE0);
			returnMap.put("errormessage", Globals.ERRORMESSAGE0);
			returnMap.put("info", list);
		}
		return returnMap;
	}
	
	/**
	 * 创建评论消息
	 * @param request
	 * @param type
	 * @param version
	 * @param scene
	 * @param content
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "createEvaluate")
	@ResponseBody
	public Map<String,Object> createEvaluate(HttpServletRequest request,String fullname,String type,String ide,String content,String email,String imagesurl,String language){
//		public Map<String,Object> createEvaluate(HttpServletRequest request,String fullname,Integer type,Integer version,Integer scene,String content,String email,String imagesurl){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		System.out.println(getIpAddress(request));
		Map<String,Object> params = new HashMap<String,Object>();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		if(null == type || null == ide || null == content || "".equals(content) || "".equals(fullname) ) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		if(null == language) {
			language="";
		}
//		if(null!=email && !"".equals(email)){
//			String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
//			if(!match(regex,email)) {
//				returnMap.put("errorcode", Globals.ERRORCODE1007);
//				returnMap.put("errormessage", Globals.ERRORMESSAGE1007);
//				return returnMap;
//			}
//		}
		if(null == email) {
			email="";
		}
		boolean boolType = false;
		boolean boolVersion = false;
		boolean boolScene = false;
		list = evaluateService.getSelect();
		if(null==list || list.size()==0) {
			returnMap.put("errorcode", Globals.ERRORCODE4002);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4002);
			return returnMap;
		}
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i).get("type").toString().equals("1")) {
				if(type.contains(list.get(i).get("childtype").toString())) {
//					if(list.get(i).get("childtype").toString().equals(type+"")) {
					boolType = true;
				}
			}
//			if(list.get(i).get("type").toString().equals("2")) {
//				if(list.get(i).get("childtype").toString().equals(version+"")) {
//					boolVersion = true;
//				}
//			}
//			if(list.get(i).get("type").toString().equals("3")) {
//				if(list.get(i).get("childtype").toString().equals(scene+"")) {
//					boolScene = true;
//				}
//			}
		}
		if(!boolType ) {
//			if(!boolType || !boolVersion || !boolScene) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		if(null == imagesurl) {
			imagesurl="";
		}
		if(!"".equals(imagesurl)) {
			if(imagesurl.split(",").length>8) {
				returnMap.put("errorcode", Globals.ERRORCODE3004);
				returnMap.put("errormessage", Globals.ERRORMESSAGE3004);
				return returnMap;
			}
		}
		String ip = getIpAddress(request);
		params.put("type", type);
		params.put("fullname", fullname);
//		params.put("version", version);
//		params.put("scene", scene);
		params.put("ide", ide);
		params.put("content", content);
		params.put("email", email);
		params.put("ip", ip);
		params.put("imagesurl", imagesurl);
		params.put("language", language);
		
		evaluateService.createEvaluate(params);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
	}
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
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

	//评论反馈列表
	@RequestMapping(value = "evaluateList")
	@ResponseBody
	public Map<String,Object> evaluateList(Integer page, Integer rows,Integer type){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> params = new HashMap<String,Object>();
		List<Map<String,Object>> countList = new ArrayList<Map<String,Object>>();
		if(null == page) {
			page=1;
		}
		if(null == rows) {
			rows=30;
		}
		if(null != type) {
			List<Map<String,Object>> selectList = new ArrayList<Map<String,Object>>(); 
			selectList = evaluateService.getSelect();
			boolean istype= false;
			for(int i = 0 ; i < selectList.size() ; i++) {
				if(selectList.get(i).get("type").toString().equals("1")) {
					if(type == Integer.valueOf(selectList.get(i).get("childtype").toString())) {
						istype = true;
					}
				}
			}
			if(!istype) {
				returnMap.put("errorcode", Globals.ERRORCODE4001);
				returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
				return returnMap;
			}else {
				params.put("type", type);
			}
		}
		page= (page-1)*rows;
		params.put("page", page);
		params.put("rows", rows);
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		list = evaluateService.evaluateList(params);
		
		int total = 0;
		total = evaluateService.countEvaluate();
		countList = evaluateService.countEvaluateType();
		map.put("list", list);
		map.put("alltotal", total);
		map.put("typetotal", countList);
		map.put("basepath", EvaluateConfigLoader.getFileHost());
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", map);
		return returnMap;
	}
}
