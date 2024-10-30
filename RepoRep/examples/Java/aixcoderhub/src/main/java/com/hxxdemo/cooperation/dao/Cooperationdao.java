package com.hxxdemo.cooperation.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface Cooperationdao {

	@Insert("insert into cooperation (company,`name`,contacts,`require`,ip,email,position,staff_num,create_time) values "
			+ "(#{company},#{name},#{contacts},#{require},#{ip},#{email},#{position},#{staff_num},now())")
	void insertCooperation(Map<String, Object> params);

	@Insert("insert into cooperation (company,`name`,contacts,`require`,ip,email,position,staff_num,create_time) values "
			+ "(#{company},#{name},#{contacts},#{require},#{ip},#{email},#{position},#{staff_num},now())")
	void insertCooperationOther(Map<String, Object> params);
	
	@Select("select count(1) from cooperation WHERE create_time > DATE_FORMAT(NOW(),'%Y-%m-%d') AND ip=#{ip} ")
	int countCompany(String ip);
}
