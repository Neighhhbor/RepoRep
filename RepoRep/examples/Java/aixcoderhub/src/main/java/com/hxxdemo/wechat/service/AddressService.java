package com.hxxdemo.wechat.service;

import java.util.Map;

public interface AddressService {

	/**
	 * 创建收件人信息
	 * @param openid
	 * @param addressee
	 * @param address
	 * @param mobile
	 * @param remark
	 */
	void createAddress(String openid, String addressee,String address,String mobile,String remark,String username);
	/**
	 * 修改收件人信息
	 * @param openid
	 * @param addressee
	 * @param address
	 * @param mobile
	 * @param remark
	 */
	void updateAddress(String openid, String addressee,String address,String mobile,String remark,String username);
	
	/**
	 * 获取收件人状态
	 * @param openid
	 * @return
	 */
	String getAddressType(String openid);
	
	/**
	 * 获取收件人信息
	 * @param openid
	 * @return
	 */
	Map<String, Object> getAddressMessage(String openid);
}
