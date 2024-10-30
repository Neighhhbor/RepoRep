package com.hxxdemo.cooperation.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.cooperation.dao.Cooperationdao;
import com.hxxdemo.cooperation.service.CooperationService;

@Service("CooperationService")
public class CooperationServiceImpl implements CooperationService{

	@Autowired
	private Cooperationdao dao;
	
	public void insertCooperation(Map<String, Object> params) {
		dao.insertCooperation(params);
//		int count = dao.countCompany(params.get("ip").toString());
//		if(count==0) {
//			dao.insertCooperation(params);
//		}else {
//			dao.insertCooperationOther(params);
//		}
	}
}
