package com.hxxdemo.plug.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AddresseeDao {

	/**
	 * 创建收件人信息
	 * @param params
	 */
	@Insert("insert into addressee (userid,addressee,address,mobile,remark) values (#{userId},#{addressee},#{address},#{mobile},#{remark})")
	void createAddressee(Map<String, Object> params);
	/**
	 * 修改收件人信息
	 * @param params
	 */
	@Insert("update addressee set addressee = #{addressee},address=#{address},mobile=#{mobile},remark=#{remark} where userid = #{userId}")
	void updateAddressee(Map<String, Object> params);
	
	/**
	 * 获取收件人状态
	 * @param userId
	 * @return
	 */
	@Select("select type from addressee where userid = #{userId} ")
	String getAddresseeType(long userId);
	
	/**
	 * 获取收件人信息
	 * @param userId
	 * @return
	 */
	@Select("select addressee,address,mobile,remark from addressee where userid =#{userId}")
	Map<String, Object> getAddresseeMessage(long userId);
}
