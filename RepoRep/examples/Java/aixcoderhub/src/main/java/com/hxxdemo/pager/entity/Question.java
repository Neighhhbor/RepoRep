package com.hxxdemo.pager.entity;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("question")
public class Question {

	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private Long id ; 
	/**
	 * 问题及选项
	 */
	private String title ;
	/**
	 *  pid 父级id 
	 */
	private Long pid ;
	/**
	 * 是否是必填项 1 是 0 否
	 */
	private Integer flag;
	/**
	 *  选项排序
	 */
	private Integer order;
	/**
	 * 类型 1、填空 2、单选、 3 多选 4、问答题
	 */
	private String type;
	/**
	 * 问卷id
	 */
	private String paperid;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Long getPid() {
		return pid;
	}
	public void setPid(Long pid) {
		this.pid = pid;
	}
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPaperid() {
		return paperid;
	}
	public void setPaperid(String paperid) {
		this.paperid = paperid;
	}
}
