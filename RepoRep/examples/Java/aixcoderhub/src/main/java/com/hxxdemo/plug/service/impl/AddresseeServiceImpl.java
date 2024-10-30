package com.hxxdemo.plug.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.plug.dao.AddresseeDao;
import com.hxxdemo.plug.service.AddresseeService;

@Service("AddresseeService")
public class AddresseeServiceImpl implements AddresseeService {
	@Autowired
	private AddresseeDao addresseeDao;
	
	@Override
	/**
	 * 创建收件人信息
	 * @param userId
	 * @param addressee
	 * @param address
	 * @param mobile
	 * @param remark
	 */
	public void createAddressee(long userId, String addressee, String address, String mobile, String remark) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("userId", userId);
		params.put("addressee", addressee);
		params.put("address", address);
		params.put("mobile", mobile);
		params.put("remark", remark);
		addresseeDao.createAddressee(params);
	
	}
	@Override
	/**
	 * 修改收件人信息
	 * @param userId
	 * @param addressee
	 * @param address
	 * @param mobile
	 * @param remark
	 */
	public void updateAddressee(long userId, String addressee, String address, String mobile, String remark) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("userId", userId);
		params.put("addressee", addressee);
		params.put("address", address);
		params.put("mobile", mobile);
		params.put("remark", remark);
		addresseeDao.updateAddressee(params);
		
	}
	/**
	 * 获取收件人状态
	 * @param userId
	 * @return
	 */
	@Override
	public String getAddresseeType(long userId) {
		return addresseeDao.getAddresseeType(userId);
	}

	/**
	 * 获取收件人信息
	 * @param userId
	 * @return
	 */
	@Override
	public Map<String, Object> getAddresseeMessage(long userId){
		return  addresseeDao.getAddresseeMessage(userId);
	}
}
