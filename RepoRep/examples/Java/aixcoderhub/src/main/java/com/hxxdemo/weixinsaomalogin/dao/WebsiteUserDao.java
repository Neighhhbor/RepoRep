package com.hxxdemo.weixinsaomalogin.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hxxdemo.weixinsaomalogin.entity.WechatSNSUserInfoVo;
@Mapper
public interface WebsiteUserDao {

	/**
	 * 插入网站授权微信用户
	 * @param wechatSNSUserInfoVo
	 */
	@Insert("insert into weixin_user (openid,nickname,sex,country,province,city,headimgurl,unionid,createtime,edittime) values (#{openId},#{nickname},#{sex},#{country},#{province},#{city},#{headImgUrl},#{unionid},now(),now())")
	void insertWebsiteUser(WechatSNSUserInfoVo wechatSNSUserInfoVo);
	
	/**
	 * 修改网站授权微信用户
	 * @param wechatSNSUserInfoVo
	 */
	@Update("update weixin_user set openid=#{openId},nickname=#{nickname},sex=#{sex},country=#{country},province=#{province},city=#{city},headimgurl=#{headImgUrl},unionid=#{unionid},edittime=now() where unionid = #{unionid}")
	void updateWebsiteUser(WechatSNSUserInfoVo wechatSNSUserInfoVo);
	
	/**
	 * 检查用户是否存在
	 * @param openid
	 * @return 0 不存在 1 存在
	 */
	@Select("select count(1) from weixin_user where unionid = #{unionid}")
	int checkWebsiteUser(WechatSNSUserInfoVo wechatSNSUserInfoVo);
	
	/**
	 * 修改备注名称
	 * @param openid
	 * @param remark
	 */
	@Update("update weixin_user set remark=#{remark} where openid=#{openId}")
	void editWebsiteUserRemark(WechatSNSUserInfoVo wechatSNSUserInfoVo);
	
}
