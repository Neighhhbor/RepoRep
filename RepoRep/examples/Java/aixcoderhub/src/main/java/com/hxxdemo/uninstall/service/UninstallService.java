package com.hxxdemo.uninstall.service;

import java.util.Map;

public interface UninstallService {

	void insertUninstallReason(Map<String, Object> params);
	
	void createContact(Map<String, Object> params);
}
