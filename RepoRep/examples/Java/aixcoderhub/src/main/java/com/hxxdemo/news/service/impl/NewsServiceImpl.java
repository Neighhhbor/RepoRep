package com.hxxdemo.news.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.news.dao.NewsDao;
import com.hxxdemo.news.service.NewsService;
@Service("NewsService")
public class NewsServiceImpl implements NewsService {

	@Autowired NewsDao newsDao;
	/**
	 * 获取新闻列表
	 */
	public List<Map<String, Object>> getNewsList(Map<String,Object> params) {
		return newsDao.getNewsList(params);
	}
	/**
	 * 新闻条数
	 * @return
	 */
	public int countNewsList() {
		return newsDao.countNewsList();
	}
	/**
	 * 查询一条指定新闻
	 * @param id
	 * @return
	 */
	public Map<String,Object> getOneNews(String id){
		return newsDao.getOneNews(id);
	}
}
