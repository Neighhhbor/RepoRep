package com.hxxdemo.weixinsaomalogin.service;

import java.util.List;
import java.util.Map;

import com.hxxdemo.weixinsaomalogin.entity.WeixinMenu;

public interface WxMenuService {

	/**
	 * 查询一级菜单
	 * @return
	 */
	List<Map<String,Object>> queryFristLevelMenuList();
	
	/**
	 * 查询二级菜单
	 * @param id
	 * @return
	 */
	List<Map<String,Object>> queryTwoLevelMenuList(String id);
	
	/**
	 * 插入微信菜单
	 * @param weixinMenu
	 */
	void insertWxMenu(WeixinMenu weixinMenu);
	/**
	 * 修改微信菜单
	 * @param weixinMenu
	 */
	void updateWxMenu(WeixinMenu weixinMenu);
	/**
	 * 删除微信菜单
	 * @param weixinMenu
	 */
	void delWxMenu(Long id);
	/**
	 * 查询单个微信菜单
	 * @param weixinMenu
	 */
	Map<String,Object> oneWxMenu(Long rid);
	
	
}
