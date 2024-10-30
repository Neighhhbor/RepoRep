package com.hxxdemo.total.service;

import java.util.Map;

public interface TotalTimesService {

	/**
	 * 插入ip
	 * @param params
	 */
	void insertIp(Map<String,Object> params);
	/**
	 * 该ip当天是否访问过
	 * @param params
	 * @return
	 */
	int isIp(Map<String,Object> params);
	
	/**
	 * 模型计数
	 * @param params
	 */
	void initCountModel(Map<String,Object> params);
}
