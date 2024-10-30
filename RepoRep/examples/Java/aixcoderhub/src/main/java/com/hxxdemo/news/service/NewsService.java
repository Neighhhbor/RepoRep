package com.hxxdemo.news.service;

import java.util.List;
import java.util.Map;

public interface NewsService {

	/**
	 * 获取新闻列表
	 * @param params
	 * @return
	 */
	List<Map<String,Object>> getNewsList(Map<String,Object> params); 
	
	/**
	 * 新闻条数
	 * @return
	 */
	int countNewsList();
	/**
	 * 查询一条指定新闻
	 * @param id
	 * @return
	 */
	Map<String,Object> getOneNews(String id);
}
