package com.hxxdemo.index.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("applyaix")
public class Applyaix {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long userid;
	private String product;
	private Date time;
	private Integer action ;
	private Integer delstatus;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Integer getAction() {
		return action;
	}
	public void setAction(Integer action) {
		this.action = action;
	}
	public Integer getDelstatus() {
		return delstatus;
	}
	public void setDelstatus(Integer delstatus) {
		this.delstatus = delstatus;
	}
}
