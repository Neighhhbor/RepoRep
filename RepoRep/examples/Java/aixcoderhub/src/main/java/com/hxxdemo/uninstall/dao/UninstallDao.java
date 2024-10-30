package com.hxxdemo.uninstall.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UninstallDao {

	@Insert("insert into uninstall_reason (reason,ip,create_time) values (#{reason},#{ip},now())")
	void insertUninstallReason(Map<String, Object> params);
	
	@Insert("insert into uninstall_contact (contact,ip,create_time) values (#{contact},#{ip},now())")
	void createContact(Map<String, Object> params);
}
