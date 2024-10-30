package com.hxxdemo.wechat.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hxxdemo.wechat.entity.WechatUser;

@Mapper
public interface WechatUserDao {
	
	@Insert("insert into weixin_webuser (openid,nickname,sex,country,province,city,headimgurl,privilege,create_time,edit_time) values "
			+ "(#{openid},#{nickname},#{sex},#{country},#{province},#{city},#{headimgurl},#{privilege},now(),now())")
	void insertWechatUser(WechatUser wechatUser);
	@Update("update weixin_webuser set nickname =#{nickname} ,sex=#{sex} ,country=#{country},province=#{province},"
			+ "city=#{city},headimgurl=#{headimgurl},privilege=#{privilege},edit_time=now() where openid=#{openid}")
	void editWechatUser(WechatUser wechatUser);
	@Select("select count(1) from weixin_webuser where openid=#{openid}")
	int isWechatUser(String openid);
	
}
