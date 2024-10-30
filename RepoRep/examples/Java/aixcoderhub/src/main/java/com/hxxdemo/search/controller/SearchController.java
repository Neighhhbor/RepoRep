package com.hxxdemo.search.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.config.Globals;
import com.hxxdemo.search.service.SearchService;

@Controller
@RequestMapping("search")
public class SearchController {

	@Autowired
	private SearchService service;
	
	@RequestMapping("statistics")
	@ResponseBody
	public Map<String, Object> statistics(@RequestParam(defaultValue = "1") Integer searchType,String channel) {
		//searchType 1 api   2 相似代码 3 自然语言搜索 4 stackoverflow问答搜索
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
		params.put("day",df.format(new Date()));
		if(searchType == 2) {
			params.put("liketimes","liketimes");
			if(null != channel && "yaxin".equals(channel)) {
				params.put("yaxinliketimes", "yaxinliketimes");
				params.put("yaxintimes", "yaxintimes");
			}
		}else if(searchType==3){
			params.put("naturaltimes", "naturaltimes");
			if(null != channel && "yaxin".equals(channel)) {
				params.put("yaxinnaturaltimes", "yaxinnaturaltimes");
				params.put("yaxintimes", "yaxintimes");
			}
		}else if(searchType==4){
			params.put("stackoverflowtimes", "stackoverflowtimes");
			if(null != channel && "yaxin".equals(channel)) {
				params.put("yaxinstackoverflowtimes", "yaxinstackoverflowtimes");
				params.put("yaxintimes", "yaxintimes");
			}
		}else{
			params.put("apitimes","apitimes");
			if(null != channel && "yaxin".equals(channel)) {
				params.put("yaxinapitimes", "yaxinapitimes");
				params.put("yaxintimes", "yaxintimes");
			}
		}
		service.createSearchTimes(params);
		map.put("errorcode",Globals.ERRORCODE0);
		map.put("errormessage",Globals.ERRORMESSAGE0);
		return map;
	}
}
