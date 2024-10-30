package com.hxxdemo.message.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.message.dao.MessageDao;

@Service("MessageService")
public class MessageServiceImpl implements MessageService {

	@Autowired
	private MessageDao messageDao;
	@Override
	public int insert(Map<String, Object> params) {
		return messageDao.insertMessage(params);
	}


}
