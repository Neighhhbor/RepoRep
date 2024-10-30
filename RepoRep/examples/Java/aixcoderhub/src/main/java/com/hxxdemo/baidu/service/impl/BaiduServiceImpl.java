package com.hxxdemo.baidu.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.baidu.dao.BaiduDao;
import com.hxxdemo.baidu.service.BaiduService;

@Service("BaiduService")
public class BaiduServiceImpl implements BaiduService {
	
	@Autowired
	private BaiduDao dao;
	
	public int getSwitch(String source) {
		return dao.getSwitch(source);
	}
}
