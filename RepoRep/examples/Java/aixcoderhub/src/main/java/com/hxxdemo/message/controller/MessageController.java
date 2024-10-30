package com.hxxdemo.message.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.config.Globals;
import com.hxxdemo.message.service.impl.MessageService;


@Controller
@RequestMapping(value = "/message")
public class MessageController {

	@Autowired
	private  MessageService messageService;
	
	@RequestMapping(value = "createMessage")
	@ResponseBody
	public Map<String,Object> createMessage(String fullname,String email,String messageType,String question){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> params = new HashMap<String,Object>();
		if (null == fullname
			|| null == email 
			|| null == messageType
			|| null == question) {
			returnMap.put("errorcode", Globals.ERRORCODE9014);
			returnMap.put("errormessage", Globals.ERRORMESSAGE9014);
			return returnMap;
		}
		String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		if(!match(regex,email)) {
			returnMap.put("errorcode", Globals.ERRORCODE1007);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1007);
			return returnMap;
		}
		params.put("fullname", fullname);
		params.put("email", email);
		params.put("messageType", messageType);
		params.put("question", question);
		
		try {
			messageService.insert(params);
			returnMap.put("errorcode", Globals.ERRORCODE0);
			returnMap.put("errormessage", Globals.ERRORMESSAGE0);
			return returnMap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
	}
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
}
