package com.hxxdemo.plug.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.config.Globals;
import com.hxxdemo.plug.service.InvitationCodeService;
import com.hxxdemo.plug.service.PlugService;
import com.hxxdemo.sysLogin.entity.TokenEntity;
import com.hxxdemo.sysLogin.service.TokenService;
import com.hxxdemo.util.CommonUtil;

@Controller
@RequestMapping(value = "invitationCode")
public class InvitationCodeController {
	@Autowired
	private TokenService tokenService;
	@Autowired
	private InvitationCodeService invitationCodeService;
	@Autowired
	private PlugService plugService;
	
	Map<String,Object> returnMap = new HashMap<String,Object>(); 
	
	@RequestMapping(value="/forOwner")
	@ResponseBody
	public Map<String,Object> forOwner(String token,String telephone){
		if(null == telephone) {
			returnMap.put("errorcode", Globals.ERRORCODE1013);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
			return returnMap;
		}
		if(null == token) {
			returnMap.put("errorcode", Globals.ERRORCODE7001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7001);
			return returnMap;
		}
		//验证token令牌是否过期
		TokenEntity tokenEntity = tokenService.queryByToken(token);
		if(null != tokenEntity) {
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("token", token);
			params.put("time", Globals.WEBTOKENTIME);
			tokenService.updatePlugToken(params);
			String invitationCode = invitationCodeService.getInvitationCode(telephone);
			//验证用户是否有邀请码
			Map<String,Object> param = new HashMap<String,Object>();
			Long userid = plugService.getUserId(telephone);
			if(null != invitationCode) {
				returnMap.put("errorcode", Globals.ERRORCODE0);
				returnMap.put("errormessage", Globals.ERRORMESSAGE0);
				int nums = invitationCodeService.getInvitationNum(userid);
				param.put("invitationCode", invitationCode);
				param.put("nums", nums);
				returnMap.put("info", param);
				return returnMap;
			}else {
				//创建邀请码
				invitationCode = CommonUtil.getStringRandom(4);
				//检查验证码是否重复
				invitationCode = invitationCodeService.createInvitationCode(invitationCode);
				invitationCodeService.addInvitationCode(invitationCode ,userid);
				returnMap.put("errorcode", Globals.ERRORCODE0);
				returnMap.put("errormessage", Globals.ERRORMESSAGE0);
				int nums = invitationCodeService.getInvitationNum(userid);
				param.put("invitationCode", invitationCode);
				param.put("nums", nums);
				returnMap.put("info", param);
				
				return returnMap;
			}
		}else {
			returnMap.put("errorcode", Globals.ERRORCODE7004);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7004);
			return returnMap;
		}
		
	}
	@RequestMapping(value="/forOwnerInit")
	@ResponseBody
	public Map<String,Object> forOwnerInit(){
		List<Long> list = invitationCodeService.getUserIdsList();
		for (int i = 0; i < list.size() ; i++) {
			long userid = list.get(i);
			//创建邀请码
			String invitationCode = CommonUtil.getStringRandom(4);
			//检查验证码是否重复
			invitationCode = invitationCodeService.createInvitationCode(invitationCode);
			invitationCodeService.addInvitationCode(invitationCode ,userid);
		}
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
	}

}
