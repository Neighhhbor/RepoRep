package com.hxxdemo.plug.service;

import java.util.List;
import java.util.Map;

public interface BuyerOrderService {

	void saveBuyOrder(Map<String,Object> params);
	
	List<Map<String, Object>> orderList(Map<String, Object> params);
	
	int userOrderCount(Map<String, Object> params);
	
	List<Map<String, Object>> userOrderList(Map<String, Object> params);
	
	String getKeyValue();
	
	int checkOrder(Map<String, Object> params);
}
