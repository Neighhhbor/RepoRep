package com.hxxdemo.wechat.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.wechat.dao.OrderDao;
import com.hxxdemo.wechat.service.OrderService;
import com.sun.corba.se.spi.ior.ObjectKey;

@Service("OrderService")
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderDao dao;
	@Override
	public String createProductid(Long userid,Long priceid) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("userid", userid);
		params.put("priceid", priceid);
		//检查用户productid
		String productid = null ;
		int count = dao.countProductId(params);
		if(count == 0) {
			productid = UUID.randomUUID().toString().replace("-", "");
			params.put("productid", productid);
			dao.createProductid(params);
		}else {
			productid = dao.getProductid(params);
		}
		return productid;
	}
	@Override
	public void saveOrder(Map<String, String> params) {
		try {
			//修改预支付productid状态
			dao.updateProductidStatus(params.get("out_trade_no").toString());
			dao.updateUserExpire_time(params);
			//保存订单信息
			dao.saveOrder(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public boolean isPrice(Long priceid) {
		boolean bool = false;
		int index = dao.isPrice(priceid);
		if(index > 0 ) {
			bool = true;
		}
		return bool;
	}
	@Override
	public int getPriceByPriceid(Long priceid) {
		return dao.getPriceByPriceid(priceid);
	}
	
	@Override
	public List<Map<String, Object>> getPrice(){
		return dao.getPrice();
	}
	
	@Override
	public String getOrderSubject(Long priceid) {
		return dao.getOrderSubject(priceid);
	}
	
	@Override
	public int countOrder(String productid) {
		return dao.countOrder(productid);
	}
	
	@Override
	public List<Map<String, Object>> getOrderList(Map<String, Object> params){
		return dao.getOrderList(params);
	}
	
	@Override
	public int countOrderList(Map<String, Object> params) {
		return dao.countOrderList(params);
	}
	
}
