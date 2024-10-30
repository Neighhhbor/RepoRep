package com.hxxdemo.plug.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
@Mapper
public interface PlugDao {

	/**
	 * 保存插件用户
	 * @param params
	 */
	@Insert("<script>insert into sys_user (<if test='email!=null'>email</if><if test='telephone!=null'>telephone</if>,uuid,createtime <if test='viplevel!=null'> ,viplevel </if> "
			+ "<if test='source!=null'> ,source </if> <if test='expire_time!=null'> ,expire_time </if>) "
			+ "values (<if test='email!=null'>#{email}</if><if test='telephone!=null'>#{telephone}</if>,#{uuid},now()"
			+ "<if test='viplevel!=null'> ,#{viplevel} </if> <if test='source!=null'> ,#{source} </if> <if test='expire_time!=null'> ,now()+${expire_time} </if> )</script>")
	void insertPlugUser(Map<String,Object> params);
	/**
	 * 保存插件用户password
	 * @param params
	 */
	@Insert("<script>insert into sys_user (<if test='email!=null'>email</if><if test='telephone!=null'>telephone</if>,password,uuid,createtime) values (<if test='email!=null'>#{email}</if><if test='telephone!=null'>#{telephone}</if>,md5(#{password}),#{uuid},now())</script>")
	void insertPlugUserPassword(Map<String,Object> params);
	/**
	 * 通过电话号码获取uuid
	 * @param telephone
	 * @return
	 */
	@Select("select uuid from sys_user where telephone=#{telephone} or email = #{telephone}")
	String getUUID(String telephone);
	/**
	 * 通过电话好吗获取用户id
	 * @param telephone
	 * @return
	 */
	@Select("select id from sys_user where telephone=#{telephone} or email = #{telephone}")
	Long getUserId(String telephone);
	
	/**
	 * 通过uuid查询是否是有效的
	 * @param uuid
	 * @return
	 */
	@Select("select count(1) from  sys_user where uuid =#{uuid}")
	int countUserByUUID(String uuid);
	/**
	 * 是否绑定了
	 * @return
	 */
	@Select("select count(1) from plug_binding where uuid = #{uuid} and macid = #{macid}")
	int countBinding(Map<String,Object> params);
	
	/**
	 * 绑定macid uuid
	 * @param params
	 */
	@Select("insert into plug_binding (uuid,macid,system,version) values (#{uuid},#{macid},#{system},#{version})")
	void insertBinding(Map<String,Object> params) ;
	/**
	 * 获取vip等级
	 * @return
	 */
	@Select("select * from viplevel order by level ")
	public List<Map<String,Object>> getVipLevel();
	/**
	 * 获取插件vip等级
	 * @return
	 */
	@Select("select viplevel from sys_user where id = #{userId} ")
	public List<Integer> getPlugVipLevel(Long userId);
	
	/**
	 * 是否是企业版
	 * @param userId
	 * @return
	 */
	@Select("SELECT isbusiness FROM viplevel  WHERE level= (SELECT viplevel level from sys_user where id = #{userId})")
	public List<Integer> isBusiness(Long userId);
	/**
	 * 获取vip模型列表 
	 * @param params
	 * @return
	 */
	@Select("<script>SELECT * FROM `vip_privilege` WHERE channel_id = (SELECT id  channel_id from channel where channelnumber = #{channelnumber} ) "
			+ "and  vip_id in (SELECT id vip_id from viplevel where level <if test='isbusiness!=null'> = </if> <if test='isbusiness==null'> &lt;= </if> "
			+ "(SELECT viplevel from sys_user where id = #{userId}))  ORDER BY  vip_id,model,remarks </script>")
	List<Map<String,Object>> getvipModels(Map<String,Object> params);
	
	/**
	 * 是否是注册用户
	 * @param username
	 * @return
	 */
	@Select("select count(1) from sys_user where telephone=#{username} or email=#{username}")
	int isRegister(String username);
	/**
	 * 通过用户名和密码检查用户
	 * @param username
	 * @param password
	 * @return
	 */
	@Select("select id from sys_user where (telephone=#{username} or email=#{username}) and password = md5(#{password})")
	Long checkUserByUsernamePassword(Map<String,Object> params);
	
	/**
	 * 修改密码
	 * @param username
	 * @param password
	 */
	@Update("update sys_user set password=md5(#{password}) where telephone=#{username} or email =#{username} ")
	void updateUserPassword(Map<String,Object> params);
	/**
	 * 获取用户名称
	 * @param token
	 * @return
	 */
	@Select("SELECT case WHEN telephone is null then email WHEN email is null  then telephone END as username  FROM sys_user WHERE id = (SELECT user_id FROM tb_utiltoken WHERE token =#{token})")
	String getUserNameByToken(String token);
	
	/**
	 * 是否是临时vip
	 * @param userId
	 * @return
	 */
	@Select("select count(1) from vip_short where userid=#{userId} and isapply=1")
	int isVipShort(Long userId);
	/**
	 * 获取过期时间
	 * @param userId
	 * @return
	 */
	@Select("select DATE_FORMAT(expire_time,'%Y-%m-%d %T') expire_time from sys_user where id=#{userId}")
	String getExpireTime(Long userId);
	
	@Select("select count(1) from user_machine where machineid = #{machineID} and userid = #{userid} and isdel = 0")
	int countMachineID(@Param("machineID")String machineID,@Param("userid")Long userid);
	
	@Select("select count(1) from user_machine where machineid = #{machineID} and realip= #{realIP} and userid = #{userid} and isdel = 0")
	int countMachineIDIP(@Param("machineID")String machineID,@Param("realIP")String realIP,@Param("userid")Long userid);
	
	@Select("select count(1) from user_machine where userid=userid and isdel = 0") 
	int countNumUserid(Long userid);
	
	@Insert("insert into user_machine (machineid,realip,userid) values (#{machineID},#{realIP},#{userid})")
	void insertMachineID(@Param("machineID")String machineID,@Param("realIP")String realIP,@Param("userid")Long userid);
	
	@Update("update user_machine set isdel = 1 where machineid= #{machineID} and userid =#{userid} and isdel = 0")
	void delMachineid(@Param("machineID")String machineID,@Param("userid")Long userid);
	
	@Insert("insert into user_machine_code (machineid,realip,userid,code,create_time,edit_time) values (#{machineID},#{realIP},#{userid},#{code},now(),now())")
	void inserMachineCode(@Param("machineID")String machineID,@Param("realIP")String realIP,@Param("userid")Long userid,@Param("code")String code);
	
	@Select("select count(1) from user_machine_code where code =#{code} and isdel = 0 ")
	int countCode(String code);
	
	@Update("update user_machine_code set isdel = 1 where code =#{code}")
	void delMachineCode(String code);
	
	@Select("insert into user_machine (machineid, realip,userid) select machineid, realip,userid from user_machine_code where code =#{code}")
	void insertMachineByCode(String code);
	
	@Insert("insert into plug_log (contact,comment,log_url,ip,create_time) values "
			+ "(#{contact},#{comment},#{log_url},#{ip},now())")
	void savePlugLog(Map<String, Object> params);
	
	@Insert("insert into smstoken (username,token,expire_time) values "
			+ "(#{email},#{smstoken},now() + INTERVAL 2 minute )")
	void savePlugSmsCode(Map<String, Object> params);
	
	@Update("update smstoken set expire_time=now() where id = #{tokenid}")
	void expireSmsCode(Map<String, Object> params);
	
	@Select("select id from smstoken where username = #{email} and expire_time >now()")
	Integer getSmsCodeId(Map<String, Object> params);
	
	@Select("select username from smstoken where token =#{smstoken}")
	String getSmsToken(Map<String, Object> params);
	
	@Select("select count(1) from login_log where loginId=#{loginId} and expire_time>now()")
	int getPlugLoginId(@Param(value = "loginId") String loginId);
	
	@Insert("insert into login_log (loginid,username,userid,expire_time,create_time) values("
			+ "#{loginId},#{userName},#{userId},now() + interval ${expire} minute ,now())")
	void savePlugLoginId(@Param(value = "loginId") String loginId,
			@Param(value = "userName") String userName,@Param(value = "userId") Long userId,
			@Param(value = "expire") int expire);
	
	@Select("select * from login_log where loginId=#{loginId} and expire_time>now()")
	Map<String, Object> getPlugLoginByIdName(@Param(value = "loginId") String loginId);
	
	@Select("SELECT type from modeltrail where  userid = (SELECT user_id FROM `tb_plug_token` where token = #{token})")
	Integer remoteCheckAuth(@Param(value = "token") String proToken);
	
	@Update("update modeltrail set type = 0 where  userid = (SELECT user_id FROM `tb_plug_token` where token = #{token})")
	int remoteCheckCloseAuth(@Param(value = "token") String proToken);
	
	@Insert("insert into issue_feedback (urls ,describtion,contact,create_time) values (#{urls},#{describtion},#{contact},now())")
	void issueFeedback(Map<String, Object> params);
	
}
