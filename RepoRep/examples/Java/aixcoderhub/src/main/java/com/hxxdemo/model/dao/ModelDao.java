package com.hxxdemo.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ModelDao {

	/**
	 * 模型列表
	 * @param params
	 * @return
	 */
	@Select("<script>SELECT a.* ,IFNULL(d.votesnum,0) votesnum,d.ranking ,e.avatar_url,e.login,h.num,IFNULL(h.usenum,0) usenum FROM train_model  a  LEFT JOIN  " + 
			"( " + 
			"	  select b.*,(@rowNum:=@rowNum+1) as ranking from train_vote b , (Select (@rowNum :=0) ) c   order by b.votesnum  DESC ,b.createtime ASC " + 
			") d " + 
			"ON a.id = d.modelid  LEFT JOIN  sys_user e ON a.userid = e.id  LEFT JOIN  "+
			"( " + 
			"	  select f.*,(@rowNum:=g.row+1) as num from train_use f , (Select (@rowNum :=0) row ) g   order by f.usenum  DESC ,f.createtime ASC " + 
			") h " + 
			" on a.id = h.modelid "+
			" where 1=1 and del_flag=0 <if test='status!=null'> and a.status = #{status} </if> <if test='userid!=null'> and a.userid = #{userid} </if> <if test='name!=null'> and a.name like concat('%', #{name}, '%') </if> ORDER BY d.ranking asc   limit #{page},#{rows}</script> ")
	List<Map<String,Object>> ModelList(Map<String,Object> params);
	
	/**
	 * 模型详情
	 * @param id
	 * @return
	 */
	@Select("SELECT a.* ,IFNULL(d.votesnum,0) votesnum,d.ranking ,e.avatar_url,e.login,h.num,IFNULL(h.usenum,0) usenum  FROM train_model  a  LEFT JOIN  " + 
			"( " + 
			"	  select b.*,(@rowNum:=@rowNum+1) as ranking from train_vote b , (Select (@rowNum :=0) ) c   order by b.votesnum  DESC ,b.createtime ASC " + 
			") d  " + 
			"ON a.id = d.modelid  LEFT JOIN  sys_user e ON a.userid = e.id   LEFT JOIN  "+
			"( " + 
			"	  select f.*,(@rowNum:=g.row+1) as num from train_use f , (Select (@rowNum :=0) row ) g   order by f.usenum  DESC ,f.createtime ASC " + 
			") h " + 
			" on a.id = h.modelid "+
			"where 1=1 and del_flag=0 and a.id=#{id}")
	Map<String,Object> modelDetail(Long id);
	
	/**
	 * 插入个人项目
	 * @param list
	 */
	@Insert("<script>insert  train_projects (full_name,html_url,name,language,star_count,description,userid,token,createtime) values <foreach collection='list' item='map' separator=','> (#{map.full_name},#{map.html_url},#{map.name},#{map.language},#{map.star_count},#{map.description},#{map.userid},#{map.token},now()) </foreach></script>")
	void insertOwnerProject(List<Map<String,Object>> list);
	/**
	 * 插入公开项目
	 * @param list
	 */
	@Insert("<script>insert  train_projects (full_name,html_url,name,language,star_count,description,userid,token,createtime,gitid,login,avatar_url,node_id) values <foreach collection='list' item='map' separator=','> (#{map.full_name},#{map.html_url},#{map.name},#{map.language},#{map.star_count},#{map.description},#{map.userid},#{map.token},now(),#{map.id},#{map.login},#{map.avatar_url},#{map.node_id}) </foreach></script>")
	void insertopenProject(List<Map<String,Object>> list);
	/**
	 * 插入模型
	 * @param list
	 */
	@Insert("insert into train_model (userid,name,createtime,status,del_flag,detail,type,typename,synopsis,edittime,modelid,token) values (#{userid},#{name},now(),#{status},#{del_flag},#{detail},#{type},#{typename},#{synopsis},now(),#{modelid},#{token})")
	void insertModel(Map<String,Object> params);
	/**
	 * 通过token 查询模型id
	 * @return
	 */
	@Select("select id from train_model where token = #{token}")
	Long getModelidByToken(String token);
	/**
	 * 通过token修改项目模型id
	 * @param token
	 */
	@Update("update train_projects set model_id= #{modelid} where token = #{token}")
	void updateProjectModelidByToken(Map<String,Object> params);
	/**
	 * 根据模型id查询模型是否被使用过
	 * @param modelid
	 * @return
	 */
	@Select("select id from train_use where modelid= #{modelid}")
	Long isUsedByModelId(Long modelid);
	/**
	 * 插入使用数据
	 * @param params
	 */
	@Insert("insert into train_use (userid,modelid,usenum,type,createtime,edittime) values (#{userid},#{modelid},#{usenum},#{type},now(),now())")
	void insertUse(Map<String,Object> params);
	
	/**
	 * 插入使用数据
	 * @param id
	 */
	@Update("update train_use set usenum = usenum+1, edittime = now() where id= #{id}")
	void updateUse(Long id);
	/**
	 * 插入到使用者与模型被使用者关系库
	 * @param id
	 */
	@Insert("insert into train_users (userid,modelid,usersid,createtime) values (#{userid},#{modelid},#{usersid},now())")
	void insertTrainUsers(Map<String,Object> params);
	
	/**
	 * 验证是否是用户的模型
	 * @return
	 */
	@Select("select count(1) from train_model where userid=#{userId} and id=#{modelId} and del_flag=0")
	int isUserModelbyUserIdModelId(Map<String,Object> params) ;
	
	/**
	 * 修改模型信息
	 * @param params
	 */
	@Update("<script>update train_model set <if test='detail!=null'>detail=#{detail}</if> <if test='synopsis!=null'>synopsis=#{synopsis}</if> where id=#{modelId} and userid = #{userId}</script>")
	void updateModelDetail(Map<String,Object> params) ;
	/**
	 * 是否是正在训练中
	 * @param params
	 * @return
	 */
	@Select("select count(1) from train_model where userid=#{userId} and id=#{modelId} and del_flag=0 and status=2")
	int isTraining(Map<String,Object> params);
	
	/**
	 * 删除模型
	 * @param modelid
	 */
	@Update("update train_model set del_flag=1 where id = #{modelid}")
	void deleteModelById(long modelid);
}
