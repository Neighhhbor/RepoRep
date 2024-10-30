package com.hxxdemo.baidu.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BaiduDao {

	@Select("select switch from switch where source=#{source}")
	int getSwitch(String source);
	
}
