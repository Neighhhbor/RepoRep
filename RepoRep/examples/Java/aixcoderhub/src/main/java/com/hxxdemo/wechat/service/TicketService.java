package com.hxxdemo.wechat.service;

public interface TicketService {
	/**
	 * 获取ticket
	 * @return
	 */
	String getTicket();
	/**
	 * 设置超时不可用
	 */
	void editExpires_in();
	
	/**
	 * 插入ticket
	 * @param ticket
	 * @param expires_in
	 */
	void insertTicket(String ticket ,String expires_in);
}
