package com.hxxdemo.user.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserDao {

	@Select("SELECT IFNULL(email,false) email, "
			+ "case when `password` is null then false when `password` is not null  then true end as `password`,"
			+ "IFNULL(telephone,false) telephone,"
			+ "case when `wechatid` is null then false when `wechatid` is not null  then true end as wechatid FROM `sys_user` where id = (select user_id from  tb_utiltoken  where token=#{token}) ")
	Map<String, Object> userInfo(@Param(value = "token")String token);
	
	@Select("select count(1) from sys_user where telephone = #{telephone}")
	int isBindingTelephone(@Param(value = "telephone")String telephone);
	
	@Update("update sys_user set telephone = #{telephone} where id = (select user_id from  tb_utiltoken  where token=#{token})")
	void bindingTelephone(@Param(value = "telephone")String telephone,@Param(value = "token")String token);
	
	@Select("select count(1) from sys_user where email = #{email}")
	int isBindingEmail(@Param(value = "email")String email);
	
	@Update("update sys_user set email = #{email} where id = (select user_id from  tb_utiltoken  where token=#{token})")
	void bindingEmail(@Param(value = "email")String email,@Param(value = "token")String token);
	
	@Select("select count(1) from sys_user where id = (select user_id from  tb_utiltoken  where token=#{token}) and (email = #{username} or telephone = #{username})")
	int isOneSelf(@Param(value = "username")String username,@Param(value = "token")String token);
	
	@Select("select * from wechat_eventkey where eventkey = #{eventkey} and expire_time > now()")
	Map<String, Object> checkEventKey(@Param(value = "eventkey")String eventkey);
	
	@Select("select count(1) from sys_user where id = (select user_id from  tb_utiltoken  where token=#{token}) and wechatid is not null ")
	int isBindingWechat(@Param(value = "token")String token);
	
	@Update("update sys_user set wechatid = #{wechatid} where id = (select user_id from  tb_utiltoken  where token=#{token})")
	void userBindingWechat(@Param(value = "wechatid")String wechatid,@Param(value = "token")String token);
	
	@Update("update sys_user set password = md5(#{password}) where id = (select user_id from  tb_utiltoken  where token=#{token})")
	void setPassword(@Param(value = "token")String token,@Param(value = "password")String password);
	
	@Select("<script>select count(1) from sys_user where id = (select user_id from  tb_utiltoken  where token=#{token})"
			+ "<if test='email!=null'> and email= #{email}</if> "
			+ "<if test='telephone!=null'> and telephone= #{telephone}</if> </script>")
	int checkUser(Map<String, Object> params);
	
	@Select("select count(1) from sys_user where id = (select user_id from  tb_utiltoken  where token=#{token}) and telephone is not null and email is not null")
	int countTelephoneEmail(String token);
	
	@Update("update sys_user set telephone = NULL where id = (select user_id from  tb_utiltoken  where token=#{token})")
	void editTelephoneIsNull(String token);

	@Update("update sys_user set email = NULL where id = (select user_id from  tb_utiltoken  where token=#{token})")
	void editEmailIsNull(String token);
}
