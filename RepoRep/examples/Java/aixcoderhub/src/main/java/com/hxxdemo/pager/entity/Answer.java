package com.hxxdemo.pager.entity;

import java.sql.Date;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("answer")
public class Answer {
	
	private static final long serialVersionUID = 1L;

	/**
	 * guid
	 */
	private String id ;
	/**
	 * 答案
	 */
	private String answer;
	/**
	 * 问卷id
	 */
	private String paperid;
	/**
	 * 提交时间
	 */
	private Date time;
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getPaperid() {
		return paperid;
	}
	public void setPaperid(String paperid) {
		this.paperid = paperid;
	}
	
}
