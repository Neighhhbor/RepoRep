package com.hxxdemo.search.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.search.dao.SearchDao;
import com.hxxdemo.search.service.SearchService;

@Service("SearchService")
public class SearchServiceImpl implements SearchService{

	@Autowired
	private SearchDao dao;
	@Override
	public void createSearchTimes(Map<String, Object> params) {
		dao.createSearchTimes(params);
	}
	
	
}
