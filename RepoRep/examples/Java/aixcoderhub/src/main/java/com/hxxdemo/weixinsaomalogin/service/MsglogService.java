package com.hxxdemo.weixinsaomalogin.service;

import java.util.List;
import java.util.Map;


import com.hxxdemo.weixinsaomalogin.entity.Msglog;

public interface MsglogService {

	/**
	 * 插入消息记录
	 * @param msglog
	 */
	void insertMsgLog(Msglog msglog);
	
	/**
	 * 时间内是否发送过未命中消息 
	 * @param msglog
	 * @return 0 否 1 是
	 */
	int countMisfortuneTimeNum(Msglog msglog);
	
	/**
	 * 公众号用户所有回复消息列表
	 * @param params
	 * @return
	 */
	List<Map<String,Object>> queryMsgLogList(Map<String,Object> params);
	/**
	 * 统计公众号用户所有回复消息
	 * @param params
	 * @return
	 */
	int countMsgLogList(Map<String,Object> params);
}
