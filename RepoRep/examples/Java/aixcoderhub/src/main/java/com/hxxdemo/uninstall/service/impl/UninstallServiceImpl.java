package com.hxxdemo.uninstall.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.uninstall.dao.UninstallDao;
import com.hxxdemo.uninstall.service.UninstallService;

@Service("UninstallService")
public class UninstallServiceImpl implements UninstallService {

	@Autowired
	private UninstallDao dao;

	@Override
	public void insertUninstallReason(Map<String, Object> params) {
		dao.insertUninstallReason(params);
	}

	@Override
	public void createContact(Map<String, Object> params) {
		dao.createContact(params);
	}
	
	
	
}
