package com.hxxdemo.index.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("download_count")
public class DownloadCount {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long userid;
	private String dowloadcode;
	private Integer downnum;
	private Date createtime;
	private Integer product;
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
	public String getDowloadcode() {
		return dowloadcode;
	}
	public void setDowloadcode(String dowloadcode) {
		this.dowloadcode = dowloadcode;
	}
	public Integer getDownnum() {
		return downnum;
	}
	public void setDownnum(Integer downnum) {
		this.downnum = downnum;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Integer getProduct() {
		return product;
	}
	public void setProduct(Integer product) {
		this.product = product;
	}
}
