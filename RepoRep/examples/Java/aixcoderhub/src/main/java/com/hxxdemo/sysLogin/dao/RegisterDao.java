package com.hxxdemo.sysLogin.dao;


import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hxxdemo.sysLogin.entity.User;
@Mapper
public interface RegisterDao {

	/**
	 * 插入用户
	 * @param params
	 */
	@Insert("insert into sys_user (email,password,createtime) values (#{email},md5(#{password}),now())")
	void insertUser(User user);
	
	/**
	 * email是否被注册过了
	 * @param email
	 * @return
	 */
	@Select("select count(1) from sys_user where email=#{email}")
	int isregister(String email);
	
	/**
	 * 查询用户
	 * @param user
	 * @return
	 */
	@Select("select * from sys_user where email=#{email} and password=md5(#{password})")
	User getUser(User user);
	/**
	 * 查询用户
	 * @param user
	 * @return
	 */
	@Select("select * from sys_user where email=#{email} and password=md5(#{password})")
	Map<String,Object> getLoginUser(User user);
	/**
	 * 更新用户激活状态
	 * @param email
	 */
	@Update("update sys_user set isactivation=1 where email=#{email}")
	void updateUserActivateStatus(String email);
	
	/**
	 * 更改用户密码
	 * @param user
	 */
	@Update("update sys_user set password=md5(#{password}) where email=#{email}")
	void updateUserPassword(User user);
	
	/**
	 * 通过用户id查询是否是本站用户
	 * @param userid
	 * @return
	 */
	@Select("select id from sys_user where id=#{userid} ")
	int isUserById(Long userid);
}