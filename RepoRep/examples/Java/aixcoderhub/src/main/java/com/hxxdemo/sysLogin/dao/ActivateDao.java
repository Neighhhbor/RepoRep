package com.hxxdemo.sysLogin.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ActivateDao {
	/**
	 * 30分钟内是否发送过验证码
	 * @param email
	 * @return
	 */
	@Select("SELECT count(1) FROM activatecode WHERE  createtime > (now()-INTERVAL 30 MINUTE) and email =#{email}")
	int isSendCode(String email);
	
	/**
	 * 插入邮箱和激活码
	 * @param params
	 */
	@Insert("insert into activatecode (email,code,createtime) values (#{email},#{code},now())")
	void insertEmailCode(Map<String,Object> params);
	
	/**
	 * 验证验证码是否有效
	 * @param code
	 * @return
	 */
	@Select("select * from activatecode where code=#{code} and isapply=0 and createtime > (now()-INTERVAL 30 MINUTE)")
	Map<String,Object> isApplyCode(String code);
	
	/**
	 * 更新验证码状态
	 * @param code
	 */
	@Update("update activatecode set isapply=1  where code=#{code}")
	void updateCodeStatus(String code);
}
