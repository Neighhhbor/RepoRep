package com.hxxdemo.wechat.service;

import java.util.List;
import java.util.Map;

public interface OrderService {

	String createProductid(Long userid,Long priceid);
	
	void saveOrder(Map<String, String> params);
	
	boolean isPrice(Long priceid);
	
	int getPriceByPriceid(Long priceid);
	
	List<Map<String, Object>> getPrice();
	
	String getOrderSubject(Long priceid);
	
	int countOrder(String productid);
	
	List<Map<String, Object>> getOrderList(Map<String, Object> params);
	
	int countOrderList(Map<String, Object> params);
}
