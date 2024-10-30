package com.hxxdemo.report.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReportDao {

	@Select("SELECT count(1) FROM report_city WHERE create_time > DATE_FORMAT(NOW(),'%Y-%m-%d') AND ip=#{ip}")
	int countToday(String ip);
	
	@Insert("insert into report_city (ip ,city ,create_time) values"
			+ "(#{ip},#{city},now())")
	void insertReportCity(Map<String, Object> params);
	
	@Insert("insert into report_city_other (ip ,city ,create_time) values"
			+ "(#{ip},#{city},now())")
	void insertReportCityOther(Map<String, Object> params);
	
}
