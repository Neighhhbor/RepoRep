package com.hxxdemo.index.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("company_user")
public class CompanyUser {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String context;
	private String context_name;
	private String action;
	private Date createtime;
	private Integer delstatus;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getContext_name() {
		return context_name;
	}
	public void setContext_name(String context_name) {
		this.context_name = context_name;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Integer getDelstatus() {
		return delstatus;
	}
	public void setDelstatus(Integer delstatus) {
		this.delstatus = delstatus;
	}
}
