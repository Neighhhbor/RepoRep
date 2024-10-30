package com.hxxdemo.captcha.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface CaptchaDao {

	@Select("select count(1) from captcha where uuid = #{uuid}")
	int countUuid(Map<String, Object> params);
	
	@Insert("insert into captcha (uuid,code,create_time,edit_time) values (#{uuid},#{code},now(),now())")
	void save(Map<String, Object> params);
	
	@Update("update captcha set edit_time = now() ,isdisplay =1 where uuid=#{uuid}")
	void update(Map<String, Object> params);
	
	@Insert("insert into captcha_ip (ip,create_time) values ( #{ip},now())")
	void saveIp(String ip);
	
	@Select("select count(1) from captcha where uuid=#{uuid} and (create_time + interval  2  minute) >now() and isdisplay = 0 ")
	int  countCode(Map<String, Object> params);
}
