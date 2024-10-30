package com.hxxdemo.index.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("feedback")
public class Feedback {

	private static final long serialVersionUID = 1L;
	
	private String email ;
	private String aix_ver;
	private String development;
	private String profession;
	private String content;
	private Date createtime;
	private Integer isinstall;
	private Integer ishelp;
	private Integer isright;
	private Integer issearch;
	private Integer delstatus;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAix_ver() {
		return aix_ver;
	}
	public void setAix_ver(String aix_ver) {
		this.aix_ver = aix_ver;
	}
	public String getDevelopment() {
		return development;
	}
	public void setDevelopment(String development) {
		this.development = development;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Integer getIsinstall() {
		return isinstall;
	}
	public void setIsinstall(Integer isinstall) {
		this.isinstall = isinstall;
	}
	public Integer getIshelp() {
		return ishelp;
	}
	public void setIshelp(Integer ishelp) {
		this.ishelp = ishelp;
	}
	public Integer getIsright() {
		return isright;
	}
	public void setIsright(Integer isright) {
		this.isright = isright;
	}
	public Integer getIssearch() {
		return issearch;
	}
	public void setIssearch(Integer issearch) {
		this.issearch = issearch;
	}
	public Integer getDelstatus() {
		return delstatus;
	}
	public void setDelstatus(Integer delstatus) {
		this.delstatus = delstatus;
	}
	
}
