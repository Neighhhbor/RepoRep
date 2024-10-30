package com.hxxdemo.evaluate.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.evaluate.dao.EvaluateDao;
import com.hxxdemo.evaluate.service.EvaluateService;
@Service("EvaluateService")
public class EvaluateServiceImpl implements EvaluateService {

	@Autowired EvaluateDao evaluateDao;
	/**
	 * 获取下拉列表
	 */
	public List<Map<String,Object>> getSelect(){
		
		return evaluateDao.getSelect();
	}
	
	/**
	 * 创建评论消息
	 */
	public void createEvaluate(Map<String,Object> params) {
		evaluateDao.createEvaluate(params);
	}
	/**
	 * 评论反馈列表
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> evaluateList(Map<String,Object> params){
		return evaluateDao.evaluateList(params);
	}
	/**
	 * 统计评论反馈个数
	 * @param parasm
	 * @return
	 */
	public int countEvaluate() {
		return evaluateDao.countEvaluate();
	}
	/**
	 * 统计分类个数
	 * @return
	 */
	public List<Map<String,Object>> countEvaluateType(){
		return evaluateDao.countEvaluateType();
	}
}
