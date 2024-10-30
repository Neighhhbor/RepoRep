package com.hxxdemo.wechatservice.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CoreDao {

	@Insert("insert into wechat_eventkey (language,wechatid,eventkey,create_time,update_time,expire_time) values ("
			+ "#{language},#{wechatid},#{eventkey},now(),now(),now() + INTERVAL 1 minute ) ")
	void insertWechatEventKey(@Param(value = "language")String language,@Param(value = "wechatid")String wechatid,@Param(value = "eventkey")String eventkey) ;
	
	@Update("update wechat_eventkey set wechatid = #{wechatid} ,update_time = now() where eventkey = #{eventkey}")
	void updateWechatEventKey(@Param("wechatid") String wechatid,@Param("eventkey") String eventkey);
	
	@Update("update sys_user set wechatid = null where wechatid = #{wechatid}")
	void unsubscribe(@Param(value = "wechatid")String wechatid);
	
	@Select("select * from wechat_eventkey where eventkey = #{eventkey} and expire_time > now()")
	Map<String, Object> checkEventKey(@Param(value = "eventkey")String eventkey);
	
	@Update("update wechat_eventkey set expire_time = now()  where eventkey = #{eventkey}")
	void expireEventKeyDisplay(@Param(value = "eventkey")String eventkey);
	
	@Select("select * from sys_user where wechatid= #{wechatid}")
	Map<String, Object> getUserIdByWechatId(@Param(value = "wechatid")String wechatid);
	
	@Select("select wechatid from wechat_eventkey where eventkey = #{eventkey}")
	String getWechatIdByEventKey(@Param(value = "eventkey")String eventkey);
	
	@Select("select id,uuid,wechatid from sys_user where telephone = #{username} or email = #{username}")
	Map<String, Object> getUserWechatByUsername(String username);
	
	@Insert("<script>insert into sys_user (<if test='email!=null'>email</if><if test='telephone!=null'>telephone</if>,uuid,createtime "
			+ ",wechatid ) "
			+ "values (<if test='email!=null'>#{email}</if><if test='telephone!=null'>#{telephone}</if>,#{uuid},now()"
			+ ", #{wechatid} )</script>")
	void insertUser(Map<String, Object> params);
	
	@Select("update sys_user set wechatid = #{wechatid} where id = #{id}")
	void updateUser(Map<String, Object> params);
	
	@Update("update sys_user set wechatid = NULL where id = (select user_id from  tb_utiltoken  where token=#{token})")
	void cancelBindingWechat(@Param(value = "token")String token);
	
	@Select("select wechatid from sys_user where wechatid = #{wechatid}")
	String getWechatIdByWechatId(@Param(value = "wechatid")String wechatid);
}
