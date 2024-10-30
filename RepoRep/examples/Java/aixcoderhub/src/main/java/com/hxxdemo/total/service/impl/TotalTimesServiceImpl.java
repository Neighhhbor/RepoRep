package com.hxxdemo.total.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.total.dao.TotalTimesDao;
import com.hxxdemo.total.service.TotalTimesService;

@Service("TotalTimesService")
public class TotalTimesServiceImpl implements TotalTimesService {

	@Autowired
	private TotalTimesDao totalTimesDao;
	
	/**
	 * 插入ip
	 * @param params
	 */
	public void insertIp(Map<String,Object> params) {
		totalTimesDao.insertIp(params);
	}
	/**
	 * 该ip当天是否访问过
	 * @param params
	 * @return
	 */
	public int isIp(Map<String,Object> params) {
		return totalTimesDao.isIp(params);
	}
	
	/**
	 * 模型计数
	 * @param params
	 */
	public void initCountModel(Map<String,Object> params) {
		//查询当天是否有该模型
		int isModel = totalTimesDao.isModel(params);
		if(isModel > 0) {
			totalTimesDao.updateTotalTimes(params);
		}else {
			totalTimesDao.insertTotalTimes(params);
		}
		
	}
}
