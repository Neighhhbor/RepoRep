package com.hxxdemo.sms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SmsDao {

	/**
	 * 插入验证码到数据库
	 * @param params
	 */
	@Insert("insert into validatecode (validatecode,telephone,createtime) values (#{validatecode},#{telephone},now())")
	void insertValidatecode(Map<String,Object> params);
	
	/**
	 * 通过手机号获取手机验证码信息
	 * @param telephone
	 * @return
	 */
	@Select("SELECT * FROM validatecode WHERE telephone =#{telephone} and (now()-INTERVAL 1 MINUTE) > createtime  ORDER BY  createtime desc LIMIT 1 ")
	Map<String,Object> getValidatecodeByTelephone(String telephone);
}
