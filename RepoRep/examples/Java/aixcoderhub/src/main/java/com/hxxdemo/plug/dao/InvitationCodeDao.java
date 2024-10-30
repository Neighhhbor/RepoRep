package com.hxxdemo.plug.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface InvitationCodeDao {
	
	@Select("select invitation_code from invitationforowner where userid = (select id from sys_user where telephone = #{telephone} or email= #{telephone}) limit 0 ,1")
	public String getInvitationCode(String telephone);
	
	@Insert("<script> insert into invitationforowner (invitation_code,userid,create_time<if test='null!=num'>,num</if>) values (#{invitationCode},#{userId},now()<if test='null!=num'>,#{num}</if>) </script>")
	public void addInvitationCode(Map<String,Object> params);
	
	
	@Select("select count(1) num from invitationforowner where invitation_code = #{invitationCode}")
	public int countCode(Map<String,Object> params);	
	
	
	@Insert("insert into beinvited (userid,invitation_code,create_time) values (#{userId},#{invitationCode},now())")
	public void addBeInvited(Map<String,Object> params) ;
	
	@Update("update invitationforowner set num= num +1 where invitation_code =#{invitationCode}")
	public void updateInvitationCodeNum(Map<String,Object> params) ;
	
	@Select("select num  from invitationforowner where userId = #{userId}")
	public List<Integer> getInvitationNum(Long userId);
	
	@Select("select a.*,b.viplevel from invitationforowner a   LEFT JOIN sys_user b  ON  a.userid = b.id  where a.invitation_code = #{invitationCode}")
	public List<Map<String,Object>> queryListByInvitationCode(String invitationCode);
	
	@Update("update sys_user set viplevel = #{viplevel} ,expire_time = now()+ ${year} where id = (select userid id from invitationforowner where invitation_code = #{invitationCode} )")
	public void updateVipLevelByInvitationCode(Map<String,Object> params );
	
	@Update("update sys_user set viplevel = #{viplevel} ,expire_time = expire_time+ ${year} where id = (select userid id from invitationforowner where invitation_code = #{invitationCode} )")
	public void updateVipLevelByInvitationCodePlus(Map<String,Object> params );
	
	@Select("SELECT invitation_code  FROM invitationforowner where userid =(SELECT id FROM sys_user where uuid = #{token}) ")
	public String getInvitationCodeByUserUuid(String token);
	
	@Select("SELECT c.userid   from  (SELECT a.userid  ,b.invitation_code FROM beinvited a  LEFT JOIN invitationforowner b on a.userid = b.userid) c where c.invitation_code is null ")
	public List<Long> getUserIdsList();
	
	@Insert("insert into vip_short (userid) values (#{userId})")
	public void insertVipShort(Map<String,Object> params);
	
	@Update("update sys_user set viplevel=#{viplevel},expire_time=now()+${expire_time} where id=#{userId}")
	public void updateUserVipShort(Map<String,Object> params);
	
	@Update("update vip_short set isapply=0 where userid= (select userid  from invitationforowner where invitation_code = #{invitationCode} )")
	void cancelVipShort(Map<String, Object> params);
	
	@Update("update sys_user set viplevel=#{viplevel},expire_time=expire_time+${expire_time} where id= (select userid id from invitationforowner where invitation_code = #{invitationCode} )")
	void updateUserVipAppend(Map<String, Object> params);
	
}
