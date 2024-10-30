package com.hxxdemo.news.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.hxxdemo.config.Globals;
import com.hxxdemo.news.service.NewsService;

@RestController
@RequestMapping(value="/news")
public class NewsController {

	@Autowired NewsService newsService;
	
	/**
	 * 查询类型下拉列表
	 * @return
	 */
	@RequestMapping(value = "newsList")
	@ResponseBody
	public Map<String,Object> getSelect(Integer page,Integer rows){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> params = new HashMap<String,Object>();
		Map<String,Object> map = new HashMap<String,Object>();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		if(null == page) {
			page=1;
		}
		if(null == rows) {
			rows=10;
		}
		page= (page-1)*rows;
		params.put("page", page);
		params.put("rows", rows);
		
		list = newsService.getNewsList(params);
		int total = newsService.countNewsList();
		map.put("page", page+1);
		map.put("list", list);
		map.put("total", total);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", map);
		return returnMap;
	}
	
	/**
	 * 查询类型下拉列表
	 * @return
	 */
	@RequestMapping(value = "oneNews")
	@ResponseBody
	public Map<String,Object> oneNews(String id){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == id ) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		Map<String,Object> news = new HashMap<String,Object>();
		news = newsService.getOneNews(id);
		if(null == news ) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}else {
			returnMap.put("errorcode", Globals.ERRORCODE0);
			returnMap.put("errormessage", Globals.ERRORMESSAGE0);
			returnMap.put("info",news);
			return returnMap;
		}
	}
	
}
