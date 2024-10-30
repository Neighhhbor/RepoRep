package com.hxxdemo.wechat.entity;

public class WechatUser {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 自增id
	 */
	private Long id ;
	/**
	 * 微信用户openid
	 */
	private String openid ;
	/**
	 * 微信用户openid
	 */
	private String nickname ;
	/**
	 * 性别 
	 */
	private int sex ;
	/**
	 * 语言
	 */
	private String language ;
	/**
	 * 城市
	 */
	private String city ;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getHeadimgurl() {
		return headimgurl;
	}
	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}
	public String getPrivilege() {
		return privilege;
	}
	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}
	/**
	 * 省份
	 */
	private String province ;
	/**
	 * 国家
	 */
	private String country ;
	/**
	 * 头像地址
	 */
	private String headimgurl ;
	/**
	 * 微信用户标签组
	 */
	private String privilege ;
	
}
