package com.hxxdemo.evaluate.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface EvaluateDao {

	/**
	 * 获取下拉列表
	 */
	@Select("SELECT * FROM feedback_type WHERE isapply =1 ORDER BY type ASC , childtype ASC")
	List<Map<String,Object>> getSelect();
	
	/**
	 * 创建评论消息
	 */
//	@Insert("insert into evaluate (type,content,createtime,version,ip,scene,email,imagesurl,fullname) values (#{type},#{content},now(),#{version},#{ip},#{scene},#{email},#{imagesurl},#{fullname})")
	@Insert("insert into evaluate (type,content,createtime,ip,email,imagesurl,fullname,ide,language) values (#{type},#{content},now(),#{ip},#{email},#{imagesurl},#{fullname},#{ide},#{language})")
	void createEvaluate(Map<String,Object> params);
	/**
	 * 评论反馈列表
	 * @param params
	 * @return
	 */
	@Select("<script>set session sql_mode='STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION,pipes_as_concat';SELECT type,content,DATE_FORMAT(createtime,'%Y-%m-%d %T') createtime,version,scene,ide,fullname,imagesurl,((select substring_index(ip, '.', 2) )  || '.*.' || ( select substring_index(ip, '.', -1))) ip,reply FROM evaluate where 1=1 and isdelete =0 <if test='type!=null'> and type=#{type}</if>  ORDER BY createtime DESC LIMIT #{page},#{rows}</script>")
	List<Map<String,Object>> evaluateList(Map<String,Object> params);
	
	/**
	 * 统计评论反馈个数
	 * @param parasm
	 * @return
	 */
	@Select("select count(1) from evaluate ")
	int countEvaluate();
	
	/**
	 * 统计分类个数
	 * @return
	 */
	@Select("SELECT COUNT(1) total,type  FROM evaluate GROUP BY  type")
	List<Map<String,Object>> countEvaluateType();
}
