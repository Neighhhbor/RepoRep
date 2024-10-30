package com.hxxdemo.pager.entity;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("token")
public class Token {
		
	private static final long serialVersionUID = 1L;
	
	public String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
