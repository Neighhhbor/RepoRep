package com.hxxdemo.weixinsaomalogin.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.weixinsaomalogin.dao.WxMenuDao;
import com.hxxdemo.weixinsaomalogin.entity.WeixinMenu;
import com.hxxdemo.weixinsaomalogin.service.WxMenuService;
@Service
public class WxMenuServiceImpl implements WxMenuService {

	@Autowired
	private WxMenuDao wxMenuDao;

	@Override
	public List<Map<String, Object>> queryFristLevelMenuList() {
		return wxMenuDao.queryFristLevelMenuList();
	}

	@Override
	public List<Map<String, Object>> queryTwoLevelMenuList(String id) {
		return wxMenuDao.queryTwoLevelMenuList(id);
	}

	@Override
	public void insertWxMenu(WeixinMenu weixinMenu) {
		wxMenuDao.insertWxMenu(weixinMenu);
	}

	@Override
	public void updateWxMenu(WeixinMenu weixinMenu) {
		wxMenuDao.updateWxMenu(weixinMenu);
	}

	@Override
	public void delWxMenu(Long id) {
		wxMenuDao.delWxMenu(id);
	}

	@Override
	public Map<String,Object> oneWxMenu(Long id) {
		return wxMenuDao.oneWxMenu(id);
	}

}