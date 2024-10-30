package com.hxxdemo.index.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("user")
public class User {
	private static final long serialVersionUID = 1L;
	
	private Long id ;
	private String email;
	private String name ;
	private Date createTime;
	private Integer delstatus;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Integer getDelstatus() {
		return delstatus;
	}
	public void setDelstatus(Integer delstatus) {
		this.delstatus = delstatus;
	}
}
