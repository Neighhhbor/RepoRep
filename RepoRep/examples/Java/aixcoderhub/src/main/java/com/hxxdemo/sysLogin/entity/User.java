package com.hxxdemo.sysLogin.entity;

import java.sql.Date;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("sys_user")
public class User {
	private static final long serialVersionUID = 1L;
	public Long id;
	public String email;
	public String password;
	public String createtime;
	public String nodeId;
	public String avatarUrl;
	public String login;
	public String gitid;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getGitid() {
		return gitid;
	}
	public void setGitid(String gitid) {
		this.gitid = gitid;
	}

}
