package com.hxxdemo.company.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.*;

@Mapper
public interface CompanyDao {

	@Select("SELECT id, username ,(select `name` from companyinfo where id= a.companyid) `name` from companyuser a where a.username =#{username} and a.`password` = MD5(#{password})")
	Map<String, Object> getCompany(@Param(value = "username") String username,@Param(value = "password") String password);
	
	@Select("select id from companyuser where username =#{username}")
	Long getCompanyUserId(@Param(value = "username") String username);
	
	@Select("select * from companydoc where companyid = (select companyid from companyuser where id=#{userId})")
	List<Map<String, Object>> getCompanyDoc(@Param(value = "userId") Long userId);
	
	@Select("select * from companyuserdoc where userid = #{userId}")
	List<Map<String, Object>> getCompanyUserDoc(@Param(value = "userId") Long userId);
	
	@Select("SELECT flags from companyinfo where id = (select companyid from companyuser where id=#{userId})")
	int getFlags(@Param(value = "userId") Long userId);
}
