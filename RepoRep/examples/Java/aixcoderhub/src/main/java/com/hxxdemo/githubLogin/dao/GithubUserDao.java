package com.hxxdemo.githubLogin.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hxxdemo.sysLogin.entity.User;

@Mapper
public interface GithubUserDao {

	/**
	 * 检查数据库是否包含github授权用户
	 * @param map
	 * @return
	 */
	@Select("select * from sys_user where node_id=#{node_id}")
	Map<String,Object> isGithubUser(Map<String,Object> map) ;
	
	/**
	 * 插入github用户
	 * @param params
	 */
	@Insert("insert into sys_user (email,password,node_id,avatar_url,login,gitid,createtime) values (#{email},md5(#{password}),#{nodeId},#{avatarUrl},#{login},#{gitid},now())")
	void insertGithubUser(User user);
	/**
	 * 更新github用户
	 * @param params
	 */
	@Update("update sys_user set node_id=#{nodeId},avatar_url=#{avatarUrl},login=#{login},gitid=#{gitid} where email=#{email} and password=md5(#{password})")
	void updateGithubUser(User user);
	/**
	 * 更新github用户email
	 * @param params
	 */
	@Update("update sys_user set personalemail=#{personalemail} where node_id=#{nodeId}")
	void updateGithubUserEmail(User user);
	
}
