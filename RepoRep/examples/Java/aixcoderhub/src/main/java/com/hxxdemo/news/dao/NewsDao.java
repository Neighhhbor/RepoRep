package com.hxxdemo.news.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface NewsDao {

	/**
	 * 获取新闻列表
	 */
	@Select("SELECT previewimg, id,title,author,DATE_FORMAT(createtime,'%Y-%m-%d %T') createtime,content FROM `article` where state  = 1  ORDER BY  createtime desc limit #{page},#{rows} ")
	List<Map<String,Object>> getNewsList(Map<String,Object> params);
	
	/**
	 * 新闻条数
	 * @return
	 */
	@Select("SELECT count(1) FROM `article` where state  = 1")
	int countNewsList();
	/**
	 * 查询一条指定新闻
	 * @param id
	 * @return
	 */
	@Select("select id,title,author,DATE_FORMAT(createtime,'%Y-%m-%d %T') createtime,content from `article` where state  = 1 and id =#{id}")
	Map<String,Object> getOneNews(String id);
	
}
