package com.hxxdemo.weixinsaomalogin.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.weixinsaomalogin.dao.MsglogDao;
import com.hxxdemo.weixinsaomalogin.entity.Msglog;
import com.hxxdemo.weixinsaomalogin.service.MsglogService;
@Service("MsglogService")
public class MsglogServiceImpl implements MsglogService {
	@Autowired
	private MsglogDao msglogDao;

	@Override
	public void insertMsgLog(Msglog msglog) {
		msglogDao.insertMsgLog(msglog);
	}

	@Override
	public int countMisfortuneTimeNum(Msglog msglog) {
		return msglogDao.countMisfortuneTimeNum(msglog);
	}

	@Override
	public List<Map<String, Object>> queryMsgLogList(Map<String, Object> params) {
		return msglogDao.queryMsgLogList(params);
	}

	@Override
	public int countMsgLogList(Map<String, Object> params) {
		return msglogDao.countMsgLogList(params);
	}
}
