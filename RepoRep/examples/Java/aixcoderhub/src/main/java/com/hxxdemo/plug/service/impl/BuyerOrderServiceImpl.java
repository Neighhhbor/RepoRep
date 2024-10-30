package com.hxxdemo.plug.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hxxdemo.datasourse.TargetDataSource;
import com.hxxdemo.plug.dao.BuyerOrderDao;
import com.hxxdemo.plug.service.BuyerOrderService;

@Service("BuyerOrderService")
public class BuyerOrderServiceImpl implements BuyerOrderService {

	@Autowired BuyerOrderDao dao;
	
	@Override
	public void saveBuyOrder(Map<String,Object> params) {
		dao.saveBuyOrder(params);
	}
	
	@Override
	@Transactional
	@TargetDataSource("secend")	
	public List<Map<String, Object>> orderList(Map<String, Object> params){
		return dao.orderList(params);
	}
	@Override
	@Transactional
	@TargetDataSource("secend")
	public String getKeyValue() {
		return dao.getKeyValue();
	}
	@Override
	public List<Map<String, Object>> userOrderList(Map<String, Object> params){
		return dao.userOrderList(params);
	}
	@Override
	public int userOrderCount(Map<String, Object> params){
		return dao.userOrderCount(params);
	}
	@Override
	public int checkOrder(Map<String, Object> params) {
		return dao.checkOrder(params);
	}
	
}
