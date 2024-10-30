package com.hxxdemo.weixinsaomalogin.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.weixinsaomalogin.dao.ReplyDao;
import com.hxxdemo.weixinsaomalogin.entity.Reply;
import com.hxxdemo.weixinsaomalogin.service.ReplyService;

@Service
public class ReplyServiceImpl implements ReplyService {
	
	@Autowired
	private ReplyDao replyDao;

	@Override
	public void insertTextReply(Reply reply) {
		replyDao.insertTextReply(reply);
	}

	@Override
	public void insertImageReply(Reply reply) {
		replyDao.insertImageReply(reply);
	}

	/**
	 * 插入未命中回复消息
	 * @param reply
	 */
	@Override
	public void insertMisfortuneReply(Reply reply) {
		replyDao.insertMisfortuneReply(reply);
	}

	@Override
	public List<Map<String, Object>> queryMisfortuneList(Map<String, Object> params) {
		return replyDao.queryMisfortuneList(params);
	}

	@Override
	public int countMisfortuneList(Map<String,Object> params) {
		return replyDao.countMisfortuneList(params);
	}

	@Override
	public void updateMisfortune(Reply reply) {
		replyDao.updateMisfortune(reply);
	}

	@Override
	public Map<String, Object> oneMisfortune(Long id) {
		return replyDao.oneMisfortune(id);
	}

	@Override
	public void delMisfortune(Reply reply) {
		replyDao.delMisfortune(reply);
	}

	@Override
	public Map<String, Object> queryReplyConfig() {
		return replyDao.queryReplyConfig();
	}

	@Override
	public void insertReplyConfig(Map<String, Object> param) {
		replyDao.insertReplyConfig(param);
	}

	@Override
	public void updateReplyConfg(Map<String, Object> param) {
		replyDao.updateReplyConfg(param);
	}

	@Override
	public String queryReplyConfigTime() {
		return replyDao.queryReplyConfigTime();
	}

	@Override
	public int countReplyConfig() {
		return replyDao.countReplyConfig();
	}

	@Override
	public void resetReplyConfg() {
		replyDao.resetReplyConfg();
	}
	@Override
	public List<Map<String,Object>> getMsgReplayByKeyword(String keyword){
		return replyDao.getMsgReplayByKeyword(keyword);
	}

	@Override
	public List<Map<String, Object>> getMisfortune() {
		return replyDao.getMisfortune();
	}

	@Override
	public List<Map<String, Object>> getReplyTextList(Map<String, Object> params) {
		return replyDao.getReplyTextList(params);
	}

	@Override
	public int countReplyTextList(Map<String, Object> params) {
		return replyDao.countReplyTextList(params);
	}

	@Override
	public Map<String, Object> oneReplyText(Long id) {
		return replyDao.oneReplyText(id);
	}

	@Override
	public void updateReplyText(Reply reply) {
		replyDao.updateReplyText(reply);
	}

	@Override
	public void delReplyText(Reply reply) {
		replyDao.delReplyText(reply);
	}

	@Override
	public List<Map<String, Object>> queryReplyImgTextList(Map<String, Object> params) {
		return replyDao.queryReplyImgTextList(params);
	}

	@Override
	public int countReplyImgTextList(Map<String, Object> params) {
		return replyDao.countReplyImgTextList(params);
	}

	@Override
	public Map<String, Object> oneReplyImgText(Long id) {
		return replyDao.oneReplyImgText(id);
	}

	@Override
	public void delReplyImgText(Reply reply) {
		replyDao.delReplyImgText(reply);
	}

	@Override
	public void updateReplyImgText(Reply reply) {
		replyDao.updateReplyImgText(reply);
	}

	@Override
	public int countReplyTextByKeyword(String keyword) {
		return replyDao.countReplyTextByKeyword(keyword);
	}

	@Override
	public int countReplyImgTextByKeyword(String keyword) {
		return replyDao.countReplyImgTextByKeyword(keyword);
	}

	@Override
	public int countReplyConfigIsopen() {
		return replyDao.countReplyConfigIsopen();
	}

	@Override
	public int countFollowConfig(Map<String, Object> params) {
		return replyDao.countFollowConfig(params);
	}

	@Override
	public void insertFollowConfig(Map<String, Object> params) {
		replyDao.insertFollowConfig(params);
	}

	@Override
	public void delFollowConfig(Map<String, Object> params) {
		replyDao.delFollowConfig(params);
	}

	@Override
	public Map<String, Object> oneFollowConfig(Map<String, Object> params) {
		return replyDao.oneFollowConfig(params);
	}

	@Override
	public void updateFollowConfig(Map<String, Object> params) {
		replyDao.updateFollowConfig(params);
	}

	@Override
	public List<Map<String, Object>> queryTextFollowReplyList(Map<String, Object> params) {
		return replyDao.queryTextFollowReplyList(params);
	}

	@Override
	public int countTextFollowReplyList(Map<String, Object> params) {
		return replyDao.countTextFollowReplyList(params);
	}

	@Override
	public void updateTextFollowReply(Reply reply) {
		replyDao.updateTextFollowReply(reply);
	}

	@Override
	public void delTextFollowReply(Reply reply) {
		replyDao.delTextFollowReply(reply);
	}

	@Override
	public Map<String, Object> oneTextFollowReply(Long id) {
		return replyDao.oneTextFollowReply(id);
	}

	@Override
	public List<Map<String, Object>> queryNewsFollowReplyList(Map<String, Object> params) {
		return replyDao.queryNewsFollowReplyList(params);
	}

	@Override
	public int countImageFollowReplyList(Map<String, Object> params) {
		return replyDao.countImageFollowReplyList(params);
	}

	@Override
	public Map<String, Object> oneImageFollowReplyList(Long id) {
		return replyDao.oneImageFollowReplyList(id);
	}

	@Override
	public void delImageFollowReplyList(Long id) {
		replyDao.delImageFollowReplyList(id);
	}

	@Override
	public void updateImageFollowReply(Reply reply) {
		replyDao.updateImageFollowReply(reply);
	}

	@Override
	public List<Map<String, Object>> queryImageFollowReplyList(Map<String, Object> params) {
		return replyDao.queryImageFollowReplyList(params);
	}

	@Override
	public List<Map<String, Object>> queryClickReplyList(Long id) {
		return replyDao.queryClickReplyList(id);
	}
	
}
