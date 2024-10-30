package com.hxxdemo.total.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TotalTimesDao {

	/**
	 * 插入ip
	 * @param params
	 */
	@Insert("insert into total_ip (ip,create_time,model) values (#{ip},now(),#{model})")
	void insertIp(Map<String,Object> params);
	
	/**
	 * 该ip当天是否访问过
	 * @param params
	 * @return
	 */
	@Select("select count(1) from total_ip where ip = #{ip} and create_time >#{create_time} and model=#{model}")
	int isIp(Map<String,Object> params);
	
	/**
	 * 查询当天是否有该模型
	 * @return
	 */
	@Select("select count(1) from total_times where model=#{model} and create_time > #{create_time}")
	int isModel(Map<String,Object> params);
	
	/**
	 * 插入计数
	 * @param params
	 */
	@Insert("insert into total_times (times,create_time,edit_time,model) values (1,now(),now(),#{model})")
	void insertTotalTimes(Map<String,Object> params);
	
	/**
	 * 更新计数
	 * @param params
	 */
	@Update("update total_times set times =times+1 ,edit_time = now() where model = #{model} and create_time >#{create_time}")
	void updateTotalTimes(Map<String,Object> params);
	
}
