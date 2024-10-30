package com.hxxdemo.weixinsaomalogin.entity;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("weixin_msglog")
public class Msglog {

	private static final long serialVersionUID = 1L;
		
	/**
	 * id	
	 */
	private Long id;
	
	/**
	 * 用户openid
	 */
	private String FromUserName;
	/**
	 * 内容
	 */
	private String content;
	/**
	 * 消息类型
	 */
	private String MsgType;
	/**
	 * 公众号id
	 */
	private String ToUserName;
	/**
	 * 消息创建时间
	 */
	private String CreateTime;
	/**
	 * 图片url
	 */
	private String PicUrl;
	/**
	 * 标记状态 0 未标记  1 标记
	 */
	private int status;
	/**
	 * 事件
	 */
	private String event;
	/**
	 * 单位（分钟）回复一次后 到这个时间后才能回去下一条 未命中不回复
	 */
	private String opentime;
	
	public String getOpentime() {
		return opentime;
	}
	public void setOpentime(String opentime) {
		this.opentime = opentime;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFromUserName() {
		return FromUserName;
	}
	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMsgType() {
		return MsgType;
	}
	public void setMsgType(String msgType) {
		MsgType = msgType;
	}
	public String getToUserName() {
		return ToUserName;
	}
	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
	public String getPicUrl() {
		return PicUrl;
	}
	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
