package com.hxxdemo.pager.entity;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("paper")
public class Paper {
	private static final long serialVersionUID = 1L;
	/**
	 * guid
	 */
	private String id;
	/**
	 * 问卷名称
	 */
	private String name;
	/**
	 * 公司名称及描述
	 */
	private String companyremark;
	/**
	 * 问卷描述
	 */
	private String remark;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCompanyremark() {
		return companyremark;
	}
	public void setCompanyremark(String companyremark) {
		this.companyremark = companyremark;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
