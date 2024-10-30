package com.hxxdemo.search.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SearchDao {

	@Insert("<script>INSERT into searchtimes (daytime,times "
			+ "<if test='apitimes!=null'>,apitimes</if>"
			+ "<if test='liketimes!=null'>,liketimes</if>"
			+ "<if test='yaxintimes!=null'>,yaxintimes</if>"
			+ "<if test='yaxinapitimes!=null'>,yaxinapitimes</if>"
			+ "<if test='yaxinliketimes!=null'>,yaxinliketimes</if>"
			+ "<if test='naturaltimes!=null'>,naturaltimes</if>"
			+ "<if test='stackoverflowtimes!=null'>,stackoverflowtimes</if>"
			+ "<if test='yaxinnaturaltimes!=null'>,yaxinnaturaltimes</if>"
			+ "<if test='yaxinstackoverflowtimes!=null'>,yaxinstackoverflowtimes</if>"
			+ ") values "
			+ "(#{day},1,1"
			+ "<if test='yaxintimes!=null'>,1</if>"
			+ "<if test='yaxinapitimes!=null'>,1</if>"
			+ "<if test='yaxinliketimes!=null'>,1</if>"
//			+ "<if test='naturaltimes!=null'>,1</if>"
//			+ "<if test='stackoverflowtimes!=null'>,1</if>"
			+ "<if test='yaxinnaturaltimes!=null'>,1</if>"
			+ "<if test='yaxinstackoverflowtimes!=null'>,1</if>"
			+ ") on DUPLICATE key  UPDATE   times = times+1"
			+ "<if test='apitimes!=null'>,apitimes=apitimes+1</if>"
			+ "<if test='liketimes!=null'>,liketimes=liketimes+1</if>"
			+ "<if test='yaxintimes!=null'>,yaxintimes=yaxintimes+1</if>"
			+ "<if test='yaxinapitimes!=null'>,yaxinapitimes=yaxinapitimes+1</if>"
			+ "<if test='yaxinliketimes!=null'>,yaxinliketimes=yaxinliketimes+1</if>"
			+ "<if test='naturaltimes!=null'>,naturaltimes=naturaltimes+1</if>"
			+ "<if test='stackoverflowtimes!=null'>,stackoverflowtimes=stackoverflowtimes+1</if>"
			+ "<if test='yaxinnaturaltimes!=null'>,yaxinnaturaltimes=yaxinnaturaltimes+1</if>"
			+ "<if test='yaxinstackoverflowtimes!=null'>,yaxinstackoverflowtimes=yaxinstackoverflowtimes+1</if>"
			+ "</script>")
	void createSearchTimes(Map<String, Object> params);
	

}
