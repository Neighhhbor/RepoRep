package com.hxxdemo.pager.service;

import java.util.List;
import java.util.Map;

import com.hxxdemo.pager.entity.Answer;
import com.hxxdemo.pager.entity.Paper;
import com.hxxdemo.pager.entity.Question;
import com.hxxdemo.pager.entity.Token;


public interface QuestionService {

	List<Paper> queryPaperById(String id);
	
	List<Map<String,Object>> queryQuestionListByPaperId(String id);
	
	List<Map<String,Object>> queryQuestionById(String  ids);
	
	void insertAnswer(Answer answer);
	
	List<Map<String,Object>> queryPaperList(Map<String,Object> params);
	
	List<Map<String,Object>> queryAnswerList(Map<String,Object> params);
	
	List<Map<String,Object>> queryAnswerOne(Map<String,Object> params);
	
	void insertToken(Token token);
	
	List<Map<String,Object>> getTokenById(Token token);
	
	List<Map<String,Object>> queryAllQuestion(String id);
	
	List<Map<String,Object>> queryAllAnswer(String id);
	
	int countPaperList(Map<String,Object> params);
	
	int countAnswerList(Map<String,Object> params);
}
