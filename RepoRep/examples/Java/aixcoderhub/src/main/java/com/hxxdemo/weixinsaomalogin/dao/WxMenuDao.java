package com.hxxdemo.weixinsaomalogin.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hxxdemo.weixinsaomalogin.entity.WeixinMenu;
@Mapper
public interface WxMenuDao {

	/**
	 * 查询一级菜单
	 * @return
	 */
	@Select("select name ,id,event type,url,(SELECT COUNT(1) FROM weixin_menu WHERE parentid = a.id AND isapply = 1 AND level=2 ) countnum ,description FROM weixin_menu a  WHERE level =1 and isapply=1")
	List<Map<String,Object>> queryFristLevelMenuList();
	
	/**
	 * 查询二级菜单
	 * @param id
	 * @return
	 */
	@Select("select name ,id,event type,url,description,parentid FROM weixin_menu WHERE parentid = #{id} and isapply =1 and level=2")
	List<Map<String,Object>> queryTwoLevelMenuList(String id);
	/**
	 * 插入微信菜单
	 * @param weixinMenu
	 */
	@Insert("<script>insert into weixin_menu (name ,event,description,sort,isapply,level<if test='parentid!=null and parentid!=null'> ,parentid</if><if test='url!=null and url!=null'> ,url</if>) values(#{name},#{event},#{description},#{sort},#{isapply},#{level}<if test='parentid!=null and parentid!=null'> ,#{parentid}</if><if test='url!=null and url!=null'> ,#{url}</if>)</script>")
	void insertWxMenu(WeixinMenu weixinMenu);
	
	/**
	 * 修改微信菜单
	 * @param weixinMenu
	 */
	@Update("update weixin_menu set name=#{name},event=#{event},description=#{description},sort=#{sort},isapply=#{isapply},level=#{level} <if test='parentid!=null and parentid!=null'> ,parentid=#{parentid}</if><if test='url!=null and url!=null'> ,url=#{url}</if> where id=#{id} and isapply=#{isapply}")
	void updateWxMenu(WeixinMenu weixinMenu);
	/**
	 * 删除微信菜单
	 * @param weixinMenu
	 */
	@Update("update weixin_menu set isapply=0 where id =#{id} and isapply=1")
	void delWxMenu(Long id);
	/**
	 * 查询单个微信菜单
	 * @param weixinMenu
	 */
	@Select("select * from weixin_menu where id =#{id} and isapply=1")
	Map<String,Object> oneWxMenu(Long id);
}
