package com.hxxdemo.baidu.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.baidu.service.BaiduService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BaiduController {

	@Autowired
	private BaiduService service;
	
	@ResponseBody
	@RequestMapping("baidu")
	public String name() {
		String source = "baidu";
		int isopen = service.getSwitch(source);
		
		return isopen+"";
	}
}
