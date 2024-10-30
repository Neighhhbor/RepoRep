package com.hxxdemo.weixinsaomalogin.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.weixinsaomalogin.dao.WxUserDao;
import com.hxxdemo.weixinsaomalogin.entity.SNSUserInfo;
import com.hxxdemo.weixinsaomalogin.service.WxUserService;

@Service("WxUserService")
public class WxUserServiceImpl implements WxUserService {
	@Autowired
	private WxUserDao wxUserDao;
	@Override
	public void insertWxUser(SNSUserInfo snsUserInfo) {
		wxUserDao.insertWxUser(snsUserInfo);
	}
	@Override
	public int queryWxUserCountByUnionid(String unionid) {
		return wxUserDao.queryWxUserCountByUnionid(unionid);
	}
	/**
	 * 根据openid 查询微信用户
	 * @param openid
	 * @return int
	 */
	@Override
	public int queryWxUserCountByOpenid(String openid) {
		return wxUserDao.queryWxUserCountByOpenid(openid);
	}
	/**
	 * 修改微信用户
	 * @param snsUserInfo
	 */
	public void updateWxUser(SNSUserInfo snsUserInfo) {
		wxUserDao.updateWxUser(snsUserInfo);
	}
	@Override
	public void wxUserCancelFollow(SNSUserInfo snsUserInfo) {
		wxUserDao.wxUserCancelFollow(snsUserInfo);
	}
	@Override
	public void wxUserAllCancelFollow() {
		wxUserDao.wxUserAllCancelFollow();
	}
	@Override
	public void updateWxUserRemark(SNSUserInfo snsUserInfo) {
		wxUserDao.updateWxUserRemark(snsUserInfo);
	}
	@Override
	public List<Map<String, Object>> queryWxUserList(Map<String, Object> params) {
		return wxUserDao.queryWxUserList(params);
	}
	@Override
	public int countWxUserList(Map<String, Object> params) {
		return wxUserDao.countWxUserList(params);
	}
	@Override
	public Map<String, Object> oneWxUserInfo(Long id) {
		return wxUserDao.oneWxUserInfo(id);
	}
}
