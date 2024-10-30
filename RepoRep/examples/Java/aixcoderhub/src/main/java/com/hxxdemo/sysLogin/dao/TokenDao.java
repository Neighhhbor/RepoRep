package com.hxxdemo.sysLogin.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hxxdemo.sysLogin.entity.TokenEntity;

@Mapper

public interface TokenDao {
	
	@Select("select * from tb_utiltoken where token = #{token} and expire_time > now()")
	TokenEntity queryByToken(String token);
	
	@Select("select * from tb_token where token = #{token} and expire_time > now()")
	TokenEntity queryByTokenInstall(String token);

	/**
	 * 生成token
	 * @param userId  用户ID
	 * @return        返回token信息
	 */
	@Insert("insert into tb_utiltoken (user_id,token,expire_time,update_time) values (#{userId},#{token},#{expireTime},#{updateTime})")
	void createToken(TokenEntity tokenEntity);
	
	@Delete("delete from tb_utiltoken where user_id =#{user_id} and expire_time<=now() ")
	void deleteToken(long user_id);
	
	@Update("update tb_utiltoken set expire_time = now() where user_id=#{userId}")
	void expireToken(long userId);
	/**
	 * 修改token为过期
	 * @param token 
	 */
	@Update("update tb_utiltoken set expire_time = now() where token=#{token}")
	void updateTokenBytoken(String token);
	
	/**
	 * 根据token查看id
	 * @param token 
	 */
	@Select("select user_id from  tb_utiltoken  where token=#{token}")
	Object getIdByToken(String token);

	/**
	 * 本人过期token集合
	 */
	@Select("SELECT * FROM tb_utiltoken WHERE expire_time > now() and token  <> #{token} and token is not null and user_id = (SELECT user_id from tb_utiltoken WHERE token = #{token}) ")
	List<Map<String,Object>> queryListByToken(String token) ;
	
	/**
	 * 通过用户id查询插件token
	 * @param userId
	 * @return
	 */
	@Select("select * from tb_utiltoken where user_id = #{userId} and expire_time > now()")
	TokenEntity queryPlugToken(long userId);
	/**
	 * 修改插件token过期时间
	 * @param token
	 */
	@Update("<script>update tb_utiltoken set expire_time = (now()+ ${time})<if test='null!=newToken'>,token=#{newToken}</if> where token=#{token}</script>")
	void updatePlugToken(Map<String,Object> params);
	/**
	 * logOut 设置token过期
	 * @param token 
	 */
	@Update("update tb_utiltoken set expire_time = now() where token=#{token}")
	void expireTokenByToken(String token);
	
	/**
	 * 通过用户id查询插件token
	 * @param userId
	 * @return
	 */
	@Select("select * from tb_token where user_id = #{userId} and expire_time > now()")
	TokenEntity queryPlugTokenInstall(long userId);
	/**
	 * 修改插件token过期时间
	 * @param token
	 */
	@Update("<script>update tb_token set expire_time = (now()+ ${time})<if test='null!=newToken'>,token=#{newToken}</if> where token=#{token}</script>")
	void updatePlugTokenInstall(Map<String,Object> params);
	/**
	 * 生成token
	 * @param userId  用户ID
	 * @return        返回token信息
	 */
	@Insert("insert into tb_token (user_id,token,expire_time,update_time) values (#{userId},#{token},#{expireTime},#{updateTime})")
	void createTokenInstall(TokenEntity tokenEntity);
	
	@Delete("delete from tb_token where user_id =#{user_id} and expire_time<=now() ")
	void deleteTokenInstall(long user_id);
	
	@Update("update tb_plug_token set expire_time =#{expireTime}, update_time=#{updateTime}  where token=#{token}")
	void updatePlugTokenEntity(TokenEntity tokenEntity);
	
	@Select("select * from tb_plug_token where user_id = #{userId} and expire_time > now()")
	TokenEntity queryPlugTokenByUserId(Long userId);
	
	@Insert("insert into tb_plug_token (user_id,token,expire_time,update_time) values (#{userId},#{token},#{expireTime},#{updateTime})")
	void createPlugLoginToken(TokenEntity tokenEntity);
	
	@Delete("delete from tb_plug_token where user_id =#{user_id} and expire_time<=now() ")
	void deletePlugLoginToken(long user_id);
	
}
