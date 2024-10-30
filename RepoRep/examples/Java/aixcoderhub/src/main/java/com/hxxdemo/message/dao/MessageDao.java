package com.hxxdemo.message.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageDao {

	@Insert("insert into message (fullname,email,messagetype,question) values (#{fullname},#{email},#{messageType},#{question})")
	int insertMessage(Map<String, Object> params);
}
