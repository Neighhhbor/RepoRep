package com.hxxdemo.vote.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.vote.dao.VoteDao;
import com.hxxdemo.vote.service.VoteService;

@Service("VoteService")
public class VoteServiceImpl implements VoteService{

	@Autowired
	private VoteDao voteDao;
	
	/**
	 * 查询模型是否有投票
	 * @param modelId
	 * @return
	 */
	public int countIsVote(Long modelId) {
		return voteDao.countIsVote(modelId);
	}

	/**
	 * 添加投票
	 * @param modelMap
	 */
	public void addVote(Map<String,Object> modelMap) {
		voteDao.addVote(modelMap);
	}
	/**
	 * 修改投票
	 * @param modelId
	 */
	public void editVote(Long modelId) {
		voteDao.editVote(modelId);
	}
	/**
	 * 添加投票人
	 * @param modelMap
	 */
	public void addVoter(Map<String,Object> modelMap) {
		voteDao.addVoter(modelMap);
	}
	/**
	 * 是否投票了
	 * @param modelMap
	 * @return
	 */
	public int isVoter(Map<String,Object> modelMap) {
		return voteDao.isVoter(modelMap);
	}
}
