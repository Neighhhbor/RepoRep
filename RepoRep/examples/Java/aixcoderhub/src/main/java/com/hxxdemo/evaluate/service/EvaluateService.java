package com.hxxdemo.evaluate.service;

import java.util.List;
import java.util.Map;

public interface EvaluateService {

	/**
	 * 获取下拉列表
	 */
	List<Map<String,Object>> getSelect(); 
	/**
	 * 创建评论消息
	 */
	void createEvaluate(Map<String,Object> params);
	
	/**
	 * 评论反馈列表
	 * @param params
	 * @return
	 */
	List<Map<String,Object>> evaluateList(Map<String,Object> params);
	/**
	 * 统计评论反馈个数
	 * @param parasm
	 * @return
	 */
	int countEvaluate();
	/**
	 * 统计分类个数
	 * @return
	 */
	List<Map<String,Object>> countEvaluateType();
}
