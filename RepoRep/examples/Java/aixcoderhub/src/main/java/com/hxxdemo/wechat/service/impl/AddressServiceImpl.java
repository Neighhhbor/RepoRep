package com.hxxdemo.wechat.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.wechat.dao.AddressDao;
import com.hxxdemo.wechat.service.AddressService;

@Service("AddressService")
public class AddressServiceImpl implements AddressService {
	@Autowired
	private AddressDao addressDao;
	
	@Override
	/**
	 * 创建收件人信息
	 * @param openid
	 * @param addressee
	 * @param address
	 * @param mobile
	 * @param remark
	 */
	public void createAddress(String openid, String addressee, String address, String mobile, String remark,String username) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("openid", openid);
		params.put("addressee", addressee);
		params.put("address", address);
		params.put("mobile", mobile);
		params.put("remark", remark);
		params.put("username", username);
		addressDao.createAddress(params);
	
	}
	@Override
	/**
	 * 修改收件人信息
	 * @param openid
	 * @param addressee
	 * @param address
	 * @param mobile
	 * @param remark
	 */
	public void updateAddress(String openid, String addressee, String address, String mobile, String remark,String username) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("openid", openid);
		params.put("addressee", addressee);
		params.put("address", address);
		params.put("mobile", mobile);
		params.put("remark", remark);
		params.put("username", username);
		addressDao.updateAddress(params);
		
	}
	/**
	 * 获取收件人状态
	 * @param openid
	 * @return
	 */
	@Override
	public String getAddressType(String openid) {
		return addressDao.getAddressType(openid);
	}

	/**
	 * 获取收件人信息
	 * @param openid
	 * @return
	 */
	@Override
	public Map<String, Object> getAddressMessage(String openid){
		return  addressDao.getAddressMessage(openid);
	}
}
