package com.hxxdemo.plug.service;

import java.util.Map;

public interface AddresseeService {

	/**
	 * 创建收件人信息
	 * @param userId
	 * @param addressee
	 * @param address
	 * @param mobile
	 * @param remark
	 */
	void createAddressee(long userId, String addressee,String address,String mobile,String remark);
	/**
	 * 修改收件人信息
	 * @param userId
	 * @param addressee
	 * @param address
	 * @param mobile
	 * @param remark
	 */
	void updateAddressee(long userId, String addressee,String address,String mobile,String remark);
	
	/**
	 * 获取收件人状态
	 * @param userId
	 * @return
	 */
	String getAddresseeType(long userId);
	
	/**
	 * 获取收件人信息
	 * @param userId
	 * @return
	 */
	Map<String, Object> getAddresseeMessage(long userId);
}
