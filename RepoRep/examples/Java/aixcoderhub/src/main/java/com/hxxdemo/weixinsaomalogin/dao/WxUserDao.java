package com.hxxdemo.weixinsaomalogin.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hxxdemo.weixinsaomalogin.entity.SNSUserInfo;

@Mapper
public interface WxUserDao {
	
	/**
	 * 插入微信用户
	 * @param snsUserInfo
	 */
	@Insert("insert into weixin_user (gopenid,nickname,sex,country,province,city,headimgurl,unionid,remark,createtime,edittime,isfollow,subscribetime) values (#{openId},#{nickname},#{sex},#{country},#{province},#{city},#{headImgUrl},#{unionid},NULL,now(),now(),#{isfollow},#{subscribetime}) ")
	void insertWxUser(SNSUserInfo snsUserInfo);
	/**
	 * 修改微信用户
	 * @param snsUserInfo
	 */
	@Update("update weixin_user set gopenid=#{openId},nickname=#{nickname},sex=#{sex},country=#{country},province=#{province},city=#{city},headimgurl=#{headImgUrl},unionid=#{unionid},remark=#{remark},edittime=now(),isfollow=#{isfollow},subscribetime=#{subscribetime} where unionid=#{unionid}")
	void updateWxUser(SNSUserInfo snsUserInfo);
	
	/**
	 * 根据unionid 查询微信用户
	 * @param unionid
	 * @return int
	 */
	@Select("select count(1) from weixin_user where unionid = #{unionid}")
	int queryWxUserCountByUnionid(String unionid);
	
	/**
	 * 根据openid 查询微信用户
	 * @param openid
	 * @return int
	 */
	@Select("select count(1) from weixin_user where gopenid = #{gopenid}")
	int queryWxUserCountByOpenid(String gopenid);
	
	/**
	 * 微信用户取消关注
	 * @param snsUserInfo
	 */
	@Update("update weixin_user set isfollow = #{isfollow} ,  privilegelist=null where gopenid=#{openId}")
	void wxUserCancelFollow(SNSUserInfo snsUserInfo);
	
	/**
	 * 设置订阅号关注者取消关注
	 * @param snsUserInfo
	 */
	@Update("update weixin_user set isfollow = 0 where isfollow =1")
	void wxUserAllCancelFollow();
	
	/**
	 * 修改微信用户备注
	 * @param snsUserInfo
	 */
	@Update("update weixin_user set remark=#{remark}  where gopenid=#{openId}")
	void updateWxUserRemark(SNSUserInfo snsUserInfo);
	
	/**
	 * 查询微信公众号关注者分页列表
	 * @param params
	 * @return
	 */
	@Select("<script>select * from weixin_user where 1=1 and gopenid is not null <if test='nickname!=null and nickname!=null'> and (nickname like  concat('%', #{nickname}, '%') or remark like  concat('%', #{nickname}, '%') )</if> limit #{page},#{rows}</script>")
	List<Map<String,Object>> queryWxUserList(Map<String,Object> params);
	/**
	 * 统计微信公众号关注者分页列表
	 * @param params
	 * @return
	 */
	@Select("<script>select count(1) from weixin_user where 1=1 and gopenid is not null <if test='nickname!=null and nickname!=null'> and (nickname like  concat('%', #{nickname}, '%') or remark like  concat('%', #{nickname}, '%') )</if> </script>")
	int countWxUserList(Map<String,Object> params);
	
	/**
	 * 查询一条微信用户信息
	 * @param id
	 * @return
	 */
	@Select("select * from weixin_user where id=#{id}")
	Map<String ,Object> oneWxUserInfo(Long id);
}
