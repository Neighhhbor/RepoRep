package com.hxxdemo.index.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hxxdemo.index.entity.Applyaix;
import com.hxxdemo.index.entity.CompanyUser;
import com.hxxdemo.index.entity.DownloadCount;
import com.hxxdemo.index.entity.Feedback;
import com.hxxdemo.index.entity.User;

@Mapper
public interface QemailDao {

	
	@Select("select count(1) from mailcode where 1=1 and useremail = #{email} and isapply=0 and outtime>now() and type=#{type}")
	int countEmailByName(Map<String,Object> param);
	
	@Insert("insert into mailcode (createtime,outtime,isapply,code,useremail,type) values (now(), (now()+${time}),#{isapply},#{code},#{email},#{type})")
	void insertEmail(Map<String,Object> params);
	
	@Select("select id from mailcode where 1=1 and useremail = #{email} and code =#{code} and isapply=0 and outtime>now() and type=#{type}")
	String getEmailIdByMap(Map<String,Object> params);
	
	@Update("update mailcode set isapply=1 where id = #{id}")
	void updateIsapply(String id);
	
	@Update("<script>update mailcode set isapply=1 where 1=1 and useremail=<if test='null!=email'>#{email}</if><if test='null!=telephone'>#{telephone}</if>  and code=#{code}</script>")
	void updateIsapplyByCode(Map<String,Object> params);
	
	@Select("select * from user where email=#{email} and name =#{name}")
	List<User> queryUserByEmail(Map<String,Object> params);
	
	@Insert("insert into user (email ,name ,createtime,delstatus) values (#{email},#{name},now(),#{delstatus})")
	void  inserUser(User user) ;
	
	@Insert("insert into applyaix (userid,product,time,action,delstatus) values (#{userid},#{product},now(),#{action},0 )")
	void insertApplyaix(Applyaix applyaix);
	
	@Insert("insert into company_user (name,context,context_name,action,createtime) values (#{name},#{context},#{context_name},#{action},now())")
	void insertCompanyUser(CompanyUser companyUser);
	
	@Insert("insert into feedback (email,aix_ver,development,profession,content,createtime) values(#{email},#{aix_ver},#{development},#{profession},#{content},now())")
	void insertFeedback(Feedback feedback);
	
	@Select("select * from applycode where useremail = #{email} and code= #{applycode}  and outtime>now()")
	List<Map<String,Object>> getApplyIdByMap(Map<String,Object> params);
	
	@Update("update applycode set isapply=1 where id = #{id}")
	void updateApplyIsapply(String id);
	
	@Select("select * from applyaix where id = #{applyid} ")
	List<Map<String,Object>> queryApplyaixById(String applyid);
	
	@Insert("insert into download_count (userid,dowloadcode,downnum,createtime,product) values(#{userid},#{dowloadcode},#{downnum},now(),#{product})")
	void insertDownLoadCount(DownloadCount downLoadCount);
	
	@Select("select id from download_count where dowloadcode=#{code}")
	String queryDownLoadIdByMd5Code(String code);
	
	@Update("update download_count set downnum = (downnum +1) where dowloadcode=#{code}")
	void updateDownloadNumByCode(String code);
}
