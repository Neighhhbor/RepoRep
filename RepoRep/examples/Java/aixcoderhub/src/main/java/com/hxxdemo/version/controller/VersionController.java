package com.hxxdemo.version.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VersionController {
	
	@Value("${version}")
	public String version;
	
	@RequestMapping("getVersion")
	@ResponseBody
	public String getVersion() {
		
		return version;
	}

}
