package com.hxxdemo.modeltrail.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ModelTrailDao {

	@Select("select IFNULL(`status`,0) `status` from modeltrail where userid = (select user_id from tb_plug_token where token = #{token}) limit 1")
	Integer getUserStatus(@Param(value = "token") String token);
	
	@Select("select IFNULL(`status`,0) `status` from modeltrail where userid = (select user_id from tb_utiltoken where token = #{token}) limit 1")
	Integer getWebUserStatus(@Param(value = "token") String token);
	
	@Insert("insert into modeltrail (userid,username,`status`,`language`,create_time,update_time) values "
			+ "(#{userId},(SELECT IFNULL(telephone,email) from sys_user where id = #{userId}),1,#{language},now(),now())")
	void saveTrail(@Param(value = "userId") Long userId,@Param(value = "language") String retLanguage);
	
	@Select("select user_id from tb_utiltoken where token=#{token} and expire_time>now()")
	Long checkUserLogin(@Param(value = "token") String token);
	
	@Select("select user_id from tb_plug_token where token=#{token} and expire_time>now()")
	Long checkPlugUserLogin(@Param(value = "token") String token);
	
	@Select("select ifnull(email,'') email from sys_user where id = (select user_id from tb_utiltoken where token = #{token})")
	String getUserEmail(@Param(value = "token")String token);
	
	@Select("select  COUNT(1) from modeltrail where userid = #{userId} and expire_time >now()")
	Integer getWebUserExpireStatus(@Param(value = "userId")Long userId);
	
	@Update("UPDATE modeltrail set `status` = 1 ,`type` = 0,`language` =#{retLanguage} ,update_time = now() where userid = #{userId}")
	void editTrial(@Param(value = "userId") Long userId,@Param(value = "retLanguage") String retLanguage);
	
	@Select("SELECT IFNULL(telephone,email) username from sys_user where id = #{userId}")
	String getUserName(@Param(value = "userId")Long userId);
	
	@Select("SELECT telephone  , email from sys_user where id = #{userId}")
	Map<String, Object> getUserNameByUserId(@Param(value = "userId")Long userId);
	
	@Select("SELECT id from modeltrail where userid = ${userId} and `status` =1 limit 1")
	Long getModelTrialId(@Param(value = "userId")Long userId);
	
	@Update("update `modeltrail` set `status` = 2 , type = 1, update_time = now() ,expire_time = now() + INTERVAL ${month} MONTH where id = ${id}")
	void approved(@Param(value = "id") Long id,@Param(value = "month") Integer month);
}
