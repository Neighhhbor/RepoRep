package com.hxxdemo.modeltrail.service;

public interface ModelTrailService {

	Integer getWebUserStatus(String token);
	
	Integer getUserStatus(String token);
	
	void saveTrail(Long userId,String retLanguage);
	
	Long checkUserLogin(String token);
	
	Long checkPlugUserLogin(String token);
	
	String getUserEmail(String token);
	
	Integer getWebUserExpireStatus(Long userId);
	
	void editTrial(Long userId,String retLanguage);
	
	String getUserName(Long userId);
}
