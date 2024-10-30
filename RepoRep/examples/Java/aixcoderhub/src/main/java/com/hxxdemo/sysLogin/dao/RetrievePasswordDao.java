package com.hxxdemo.sysLogin.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RetrievePasswordDao {

	/**
	 * 添加token
	 * @param params
	 */
	@Insert("insert into pwdtoken (token ,isapply ,createtime,outtime,email) values (#{token},#{isapply},now(),(now()+interval 5 minute),#{email})")
	void insertPwdToken(Map<String,Object> params);
	
	/**
	 * 是否token
	 * @param params
	 * @return
	 */
	@Select("select count(1) from pwdtoken where isapply=1 and email=#{email} and token=#{token} and outtime>now() ")
	int countPwdTokenByEmailToken(Map<String,Object> params);
	
}
