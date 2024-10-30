package com.hxxdemo.vote.service;

import java.util.Map;

public interface VoteService {

	/**
	 * 查询模型是否有投票
	 * @param modelId
	 * @return
	 */
	int countIsVote(Long modelId);
	
	/**
	 * 添加投票
	 * @param modelMap
	 */
	void addVote(Map<String,Object> modelMap);
	/**
	 * 修改投票
	 * @param modelId
	 */
	void editVote(Long modelId);
	/**
	 * 添加投票人
	 * @param modelMap
	 */
	void addVoter(Map<String,Object> modelMap);
	/**
	 * 是否投票了
	 * @param modelMap
	 * @return
	 */
	int isVoter(Map<String,Object> modelMap);
}
