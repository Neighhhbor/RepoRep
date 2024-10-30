package com.hxxdemo.wechat.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.wechat.dao.TicketDao;
import com.hxxdemo.wechat.service.TicketService;

@Service("TicketService")
public class TicketServiceImpl implements TicketService{
	
	@Autowired
	private TicketDao dao;
	/**
	 * 获取ticket
	 * @return
	 */
	@Override
	public String getTicket() {
		return dao.getTicket();
	}
	/**
	 * 设置超时不可用
	 */
	@Override
	public void editExpires_in() {
		dao.editExpires_in();
	}
	/**
	 * 插入ticket
	 * @param ticket
	 * @param expires_in
	 */
	@Override
	public void insertTicket(String ticket ,String expires_in) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("ticket", ticket);
		params.put("expires_in", expires_in);
		dao.insertTicket(params);
	}
}
