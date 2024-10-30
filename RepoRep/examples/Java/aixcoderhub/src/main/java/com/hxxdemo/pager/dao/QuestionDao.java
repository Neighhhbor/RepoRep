package com.hxxdemo.pager.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.hxxdemo.pager.entity.Answer;
import com.hxxdemo.pager.entity.Paper;
import com.hxxdemo.pager.entity.Token;


@Mapper
public interface QuestionDao {

	 @Select("select * from paper where id =#{id}")
	 List<Paper> queryPaperById(String id);
	 
	 @Select("select * from question where paperid =#{id} and isdel= 0")
	 List<Map<String,Object>> queryQuestionListByPaperId(String id);
	 
	 @Select("select * from question where pid in(#{ids}) and isdel= 0")
	 List<Map<String,Object>> queryQuestionById(String  ids);
	 
	 @Insert("insert into answer  values (#{id}, #{answer},#{paperid},now()) ")
	 void insertAnswer(Answer answer);
	 
	 @Select("<script>select * from paper where 1=1 <if test='name!=null and name != null '>and name like concat('%', #{name}, '%') </if> limit #{page}, #{rows} </script>")
	 List<Map<String,Object>> queryPaperList(Map<String,Object> params);
	 
	 @Select("<script>select id,answer,paperid,DATE_FORMAT(time,'%Y-%m-%d %T') create_time from answer where 1=1 <if test='paperid!=null and paperid != null '>and paperid=#{paperid} </if> limit #{page}, #{rows} </script>")
	 List<Map<String,Object>> queryAnswerList(Map<String,Object> params);

	 @Select("<script>select id,answer,paperid,DATE_FORMAT(time,'%Y-%m-%d %T') create_time from answer where 1=1 <if test='id!=null and id != null '>and id=#{id} </if>  </script>")
	 List<Map<String,Object>> queryAnswerOne(Map<String,Object> params);
	 
	 @Insert("insert into token  values (#{id}) ")
	 void insertToken(Token token);
	
	 @Select("select * from token where id =#{id}")
	 List<Map<String,Object>> getTokenById(Token token);
	 
	 
	 List<Map<String,Object>> queryAllQuestion(String id);
	 
	 @Select("select * from answer where paperid =#{id}")
	 List<Map<String,Object>> queryAllAnswer(String id);
	 
	 @Select("<script>select count(1) from paper where 1=1 <if test='name!=null and name != null '>and name like concat('%', #{name}, '%') </if></script>")
	 int countPaperList(Map<String,Object> params);
	 
	 @Select("<script>select count(1) from answer where 1=1 <if test='paperid!=null and paperid != null '>and paperid=#{paperid} </if></script>")
	 int countAnswerList(Map<String,Object> params);
}
