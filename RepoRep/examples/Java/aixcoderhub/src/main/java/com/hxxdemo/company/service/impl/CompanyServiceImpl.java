package com.hxxdemo.company.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.company.dao.CompanyDao;
import com.hxxdemo.company.service.CompanyService;

@Service("CompanyService")
public class CompanyServiceImpl implements CompanyService{

	@Autowired
	private CompanyDao dao;

	@Override
	public Map<String, Object> getCompany(String username, String password) {
		return dao.getCompany(username, password);
	}

	@Override
	public Long getCompanyUserId(String username) {
		return dao.getCompanyUserId(username);
	}

	@Override
	public List<Map<String, Object>> getCompanyDoc(Long userId) {
		return dao.getCompanyDoc(userId);
	}
	
	@Override
	public List<Map<String, Object>> getCompanyUserDoc(Long userId) {
		return dao.getCompanyUserDoc(userId);
	}

	@Override
	public int getFlags(Long userId) {
		return dao.getFlags(userId);
	}
	
}
