package com.hxxdemo.company.service;

import java.util.List;
import java.util.Map;

public interface CompanyService {

	
	Map<String, Object> getCompany(String username, String password);
	
	Long getCompanyUserId(String username);
	
	List<Map<String, Object>> getCompanyDoc(Long userId);
	
	List<Map<String, Object>> getCompanyUserDoc(Long userId);
	
	int getFlags(Long userId);
}
