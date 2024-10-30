package com.hxxdemo.company.dao;

import org.apache.ibatis.annotations.*;

@Mapper
public interface TokenCompanyDao {

	@Insert("insert into tb_company_token (user_id,token,expire_time,update_time) values (#{userId},#{token},#{expireTime},#{updateTime})")
	void createCompanyToken(TokenCompanyEntity tokenEntity);
	
	@Update("update tb_company_token set expire_time = #{expireTime} where token=#{token} ")
	void updateTokenBytoken(TokenCompanyEntity tokenEntity);
	
	@Select("select * from tb_company_token where user_id=#{userId} and expire_time>now()")
	TokenCompanyEntity getToken(@Param(value = "userId") Long userId);
}
