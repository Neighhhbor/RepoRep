package com.hxxdemo.wechat.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AddressDao {

	/**
	 * 创建收件人信息
	 * @param params
	 */
	@Insert("insert into addressee (openid,addressee,address,mobile,remark,username) values (#{openid},#{addressee},#{address},#{mobile},#{remark},#{username})")
	void createAddress(Map<String, Object> params);
	/**
	 * 修改收件人信息
	 * @param params
	 */
	@Insert("update addressee set addressee = #{addressee},address=#{address},mobile=#{mobile},remark=#{remark} ,username =#{username}  where openid = #{openid}")
	void updateAddress(Map<String, Object> params);
	
	/**
	 * 获取收件人状态
	 * @param userId
	 * @return
	 */
	@Select("select type from addressee where openid = #{openid} ")
	String getAddressType(String openid);
	
	/**
	 * 获取收件人信息
	 * @param userId
	 * @return
	 */
	@Select("select addressee,address,mobile,remark,username from addressee where openid =#{openid}")
	Map<String, Object> getAddressMessage(String openid);
}
