package com.hxxdemo.pager.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.pager.dao.QuestionDao;
import com.hxxdemo.pager.entity.Answer;
import com.hxxdemo.pager.entity.Paper;
import com.hxxdemo.pager.entity.Question;
import com.hxxdemo.pager.entity.Token;
import com.hxxdemo.pager.service.QuestionService;

@Service("QuestionService")
public class QuestionServiceImpl  implements QuestionService{

	@Autowired
	private QuestionDao questionDao;
	public List<Paper> queryPaperById(String id) {
		return questionDao.queryPaperById(id);
	}
	

	public List<Map<String,Object>> queryQuestionListByPaperId(String id) {
		return questionDao.queryQuestionListByPaperId(id);
	}


	public List<Map<String,Object>> queryQuestionById(String ids) {
		String arrIds[] = ids.split(",");
		String temp = "";
		for (int i = 0; i < arrIds.length; i++) {
			if(i == 0 ) {
				ids = arrIds[i];
			}else {
				ids += " or pid = " + arrIds[i];
			}
		}
		return questionDao.queryQuestionById(ids);
	}
	
	public void insertAnswer(Answer answer) {
		questionDao.insertAnswer(answer);
	}
	
	public List<Map<String,Object>> queryPaperList(Map<String,Object> params){
		return questionDao.queryPaperList(params);
	}
	public List<Map<String,Object>> queryAnswerList(Map<String,Object> params){
		return questionDao.queryAnswerList(params);
	}
	public List<Map<String,Object>> queryAnswerOne(Map<String,Object> params){
		return questionDao.queryAnswerOne(params);
	}
	
	public void insertToken(Token token) {
		questionDao.insertToken(token);
	}
	
	public List<Map<String,Object>> getTokenById(Token token){
		return questionDao.getTokenById(token);
	}
	public List<Map<String,Object>> queryAllQuestion(String id){
		return questionDao.queryAllQuestion(id);
	}

	public List<Map<String,Object>> queryAllAnswer(String id){
		return questionDao.queryAllAnswer(id);
	}
	
	public int countPaperList(Map<String,Object> params) {
		return questionDao.countPaperList(params);
	}
	
	public int countAnswerList(Map<String,Object> params) {
		return questionDao.countAnswerList(params);
	}
}
