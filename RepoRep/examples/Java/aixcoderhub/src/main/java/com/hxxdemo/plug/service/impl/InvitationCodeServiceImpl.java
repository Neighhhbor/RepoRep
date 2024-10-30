package com.hxxdemo.plug.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.config.Globals;
import com.hxxdemo.plug.dao.InvitationCodeDao;
import com.hxxdemo.plug.service.InvitationCodeService;
import com.hxxdemo.plug.service.PlugService;
import com.hxxdemo.util.CommonUtil;

@Service("InvitationCodeService")
public class InvitationCodeServiceImpl implements InvitationCodeService {
	
	@Autowired
	private InvitationCodeDao invitationCodeDao;
	
	@Autowired
	private PlugService plugService;
	@Override
	public String getInvitationCode(String telephone) {
		return invitationCodeDao.getInvitationCode(telephone);
	}
	
	@Override
	public void addInvitationCode(String invitationCode ,Long userid) {
		Map<String ,Object> params = new HashMap<String,Object>();
		params.put("invitationCode", invitationCode);
		params.put("userId", userid);
		invitationCodeDao.addInvitationCode(params);
	}
	@Override
	public void addInvitationCodePlus(String invitationCode ,Long userid) {
		Map<String ,Object> params = new HashMap<String,Object>();
		params.put("invitationCode", invitationCode);
		params.put("userId", userid);
//		params.put("num", 1);
		invitationCodeDao.addInvitationCode(params);
		//添加至临时vip
		invitationCodeDao.insertVipShort(params);
		//更新用户vip等级及时间
		params.put("expire_time", Globals.CODEINVITATIONTIMEONEMONTH);
		params.put("viplevel", 2);
		invitationCodeDao.updateUserVipShort(params);
	}
	
	@Override
	public boolean checkInvitationCode(Map<String,Object> params) {
		boolean bool = false;
		int countCode = invitationCodeDao.countCode(params);
		if(countCode == 1) {
			bool = true;
		}else {
			bool = false;
		}
		return bool;
	}
	
	@Override
	public void addBeInvited(Map<String,Object> params) {
		invitationCodeDao.updateInvitationCodeNum(params);
		invitationCodeDao.addBeInvited(params);
		//被邀请人创建邀请码 邀请人数加1
		//创建邀请码
		String invitationCode = CommonUtil.getStringRandom(4);
		//检查验证码是否重复
		invitationCode = this.createInvitationCode(invitationCode);
		this.addInvitationCodePlus(invitationCode ,Long.valueOf(params.get("userId").toString()));
	}
	
	@Override
	public int getInvitationNum(Long userId) {
		List<Integer> list =invitationCodeDao.getInvitationNum(userId);
		if(null == list || list.size() == 0 ) {
			return 0;
		}else {
			return list.get(0);
		}
	}
	
	public void checkVip(Map<String,Object> params) {
		String invitationCode = params.get("invitationCode").toString();
		List<Map<String,Object>> list = plugService.getVipLevel();
		//查询vip等级
		List<Map<String,Object>> invitationCodeList = invitationCodeDao.queryListByInvitationCode(invitationCode);
		int viplevel = Integer.valueOf(invitationCodeList.get(0).get("viplevel").toString()) ;
		int invitationCodeNum = Integer.valueOf(invitationCodeList.get(0).get("num").toString());
		int level = 0 ; 
		int num = 0 ; 
		int isbusiness = 0 ; 
		
		for(int i = 0 ; i < list.size() ; i++ ) {
			level = Integer.valueOf(list.get(i).get("level").toString());
			isbusiness = Integer.valueOf(list.get(i).get("isbusiness").toString());
			if(viplevel == level) {
				break;
			}
		}
		if(isbusiness==0) {
			//非企业版
			for(int i = list.size()-2 ; i >= 0 ; i-- ) {
				num = Integer.valueOf(list.get(i).get("num").toString());
				level = Integer.valueOf(list.get(i).get("level").toString());
				if(invitationCodeNum >= num ) {
					if(viplevel< level && invitationCodeNum == num) {
						//更新vip等级
						params.put("viplevel", level);
						params.put("year", Globals.CODEINVITATIONTIME);
						invitationCodeDao.updateVipLevelByInvitationCode(params);
						//取消临时vip
						invitationCodeDao.cancelVipShort(params);
						break;
					}else if(viplevel == level && level == 2){
						if (invitationCodeNum == num ) {
							//更新用户vip等级及时间
							params.put("expire_time", Globals.CODEINVITATIONTIME);
							params.put("viplevel", level);
							invitationCodeDao.updateUserVipAppend(params);
							//取消临时vip
							invitationCodeDao.cancelVipShort(params);
						}else {
							//更新用户vip等级及时间
							params.put("expire_time", Globals.CODEINVITATIONTIMETHREEMONTH);
							params.put("viplevel", level);
							invitationCodeDao.updateUserVipAppend(params);
						}
						break;
//					}else {
//						//是否是num的倍数
//						if(num !=0 && invitationCodeNum%num == 0) {
//							params.put("viplevel", level);
//							params.put("year", Globals.CODEINVITATIONTIME);
//							invitationCodeDao.updateVipLevelByInvitationCodePlus(params);
//						}
//						break;
					}else if(viplevel < level && level == 2 ){
						params.put("viplevel", level);
						params.put("year", Globals.CODEINVITATIONTIMETHREEMONTH);
						invitationCodeDao.updateVipLevelByInvitationCode(params);
					}
				}
			}
			
		}else {
			//企业版
		}
	}
	public String createInvitationCode(String invitationCode) {
		boolean bool = false;
		Map<String, Object> params  = new HashMap<String,Object>();
		params.put("invitationCode", invitationCode);
		int countCode = invitationCodeDao.countCode(params);
		if(countCode == 1) {
			bool = false;
			invitationCode = CommonUtil.getStringRandom(4);
		}else {
			bool = true;
		}
		if(bool) {
			return invitationCode;
		}else {
			System.out.println("=====邀请码重复了！！=====code："+invitationCode);
			return this.createInvitationCode(invitationCode);
		}
	}
	public String getInvitationCodeByUserUuid(String token) {
		return invitationCodeDao.getInvitationCodeByUserUuid(token);
	}
	
	public List<Long> getUserIdsList(){
		return invitationCodeDao.getUserIdsList();
	}
}
