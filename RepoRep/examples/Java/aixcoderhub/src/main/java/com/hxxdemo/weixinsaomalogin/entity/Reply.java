package com.hxxdemo.weixinsaomalogin.entity;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("weixin_reply")
public class Reply {

	private static final long serialVersionUID = 1L;
	/**
	 * 自增长唯一id
	 */
	private Long id;
	/**
	 * 回复消息
	 */
	private String msg;
	/**
	 * 消息类型
	 */
	private String msgtype;
	/**
	 * 是否使用 0 否 1 是
	 */
	private int isapply;
	/**
	 * 关键字
	 */
	private String keyword;
	/**
	 * 是否是未命中消息 0 否 1 是
	 */
	private int ismisfortune;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	public int getIsapply() {
		return isapply;
	}
	public void setIsapply(int isapply) {
		this.isapply = isapply;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getIsmisfortune() {
		return ismisfortune;
	}
	public void setIsmisfortune(int ismisfortune) {
		this.ismisfortune = ismisfortune;
	}
}
