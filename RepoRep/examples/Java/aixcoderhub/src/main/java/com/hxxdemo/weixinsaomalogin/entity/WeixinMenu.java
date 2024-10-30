package com.hxxdemo.weixinsaomalogin.entity;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("weixin_menu")
public class WeixinMenu {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 自增长id
	 */
	private Long id;
	/**
	 * 菜单名称
	 */
	private String name;
	/**
	 * 事件名称
	 */
	private String event;
	/**
	 * 父级id
	 */
	private Long parentid;
	/**
	 * url地址
	 */
	private String url;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 是否使用 0 否 1 是
	 */
	private Integer isapply;
	/**
	 * 菜单等级
	 */
	private Integer level;
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
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public Long getParentid() {
		return parentid;
	}
	public void setParentid(Long parentid) {
		this.parentid = parentid;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public Integer getIsapply() {
		return isapply;
	}
	public void setIsapply(Integer isapply) {
		this.isapply = isapply;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	
	
}
