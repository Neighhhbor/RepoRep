package com.hxxdemo.plug.controller;

import com.alibaba.fastjson.JSONObject;
import com.hxxdemo.baidu.service.BaiduService;
import com.hxxdemo.config.BtoaEncode;
import com.hxxdemo.config.Globals;
import com.hxxdemo.exception.utils.R;
import com.hxxdemo.index.service.QemailService;
import com.hxxdemo.plug.service.BuyerOrderService;
import com.hxxdemo.plug.service.InvitationCodeService;
import com.hxxdemo.plug.service.PlugService;
import com.hxxdemo.sysLogin.Singleton;
import com.hxxdemo.sysLogin.entity.TokenEntity;
import com.hxxdemo.sysLogin.service.TokenService;
import com.hxxdemo.sysLogin.service.impl.TokenServiceImpl;
import com.hxxdemo.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping(value = "plug")
public class PlugLoginController {
	@Autowired
	private QemailService qemailService;
	@Autowired
	private PlugService plugService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private InvitationCodeService invitationCodeService;
	@Autowired
	private BaiduService baiduService;
	@Autowired
	private BuyerOrderService buyerService;
	
	
	@RequestMapping(value = "/webLogin")
	@ResponseBody
	public Map<String,Object> webLogin(HttpServletRequest request,HttpServletResponse response ,String telephone,String smsCode,String invitationCode
			,String loginId){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		//是否是发送邮箱验证码
    	boolean isSendEmail = false;
    	int type= 0;
		if(null == telephone) {
    		returnMap.put("errorcode", Globals.ERRORCODE1013);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
    		return returnMap;
    	}else {
    		if(telephone.contains("@")) {
    			isSendEmail = true;
    			type = Globals.MSGCODETYPE5;
    			//验证邮箱
    			if(null == telephone || telephone.equals("")) {
    				returnMap.put("errorcode", Globals.ERRORCODE1001);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE1001);
    				return returnMap;
    			}else {
    				String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    				if(!match(regex,telephone)) {
    					returnMap.put("errorcode", Globals.ERRORCODE1007);
    					returnMap.put("errormessage", Globals.ERRORMESSAGE1007);
    					return returnMap;
    				}
    			}
    		}else {
    			type = Globals.MSGCODETYPE4;
    			if(telephone.length()!=11) {
    				returnMap.put("errorcode", Globals.ERRORCODE1014);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE1014);
    				return returnMap;
    			}else {
    				if(!CommonUtil.isTelephone(telephone)) {
    					returnMap.put("errorcode", Globals.ERRORCODE1015);
    					returnMap.put("errormessage", Globals.ERRORMESSAGE1015);
    					return returnMap;
    				}
    			}
    		}
    	}
		//验证手机验证码
		if(null == smsCode || smsCode.equals("")) {
			returnMap.put("errorcode", Globals.ERRORCODE1009);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1009);
			return returnMap;
		}else {
			if(smsCode.trim().length()!=4) {
				returnMap.put("errorcode", Globals.ERRORCODE1012);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1012);
				return returnMap;
			}else {
				Map<String,Object> params = new HashMap<String,Object>();
				params.put("email", telephone);
				params.put("type", type);
				params.put("code", smsCode);
				String id = qemailService.getEmailIdByMap(params);
				if(null == id) {
					returnMap.put("errorcode", Globals.ERRORCODE1012);
					returnMap.put("errormessage", Globals.ERRORMESSAGE1012);
					return returnMap;
				}else {
					long userId = 0;
					String uuid = "";//用户唯一标识
					String token ="";//用户过期token
					//验证是否存在该用户
					uuid = plugService.getUUID(telephone);
					boolean bool = false;
					if(null == uuid || "".equals(uuid)) {
						params.remove("email");
						//不存在
						uuid = UUID.randomUUID().toString().replace("-", "");
						if(isSendEmail) {
							params.put("email", telephone);
							//取消邮箱注册
//							returnMap.put("errorcode", Globals.ERRORCODE9017);
//							returnMap.put("errormessage", Globals.ERRORMESSAGE9017);
//							return returnMap; 
						}else {
							params.put("telephone", telephone);
						}
						params.put("uuid",uuid);
						//新建用户时是否有邀请码
						if(null != invitationCode && !invitationCode.equals("")) {
							//判断是否是百度效率云
							
							if(invitationCode.equals("百度效率云")) {
								String source = "baidu";
								int isopen = baiduService.getSwitch(source);
								if(isopen == 1) {
									//设置来源
									params.put("source", source);
									//设置过期时间 三个月
									params.put("expire_time", Globals.BAIDUXIAOLVYUN);
									//设置为专业版 该状态为2
									params.put("viplevel", 2);
									plugService.insertPlugUser(params);
								}else {
									returnMap.put("errorcode", Globals.ERRORCODE9015);
									returnMap.put("errormessage", Globals.ERRORMESSAGE9015);
									return returnMap;
								}
							}else {
								params.put("invitationCode", invitationCode.toUpperCase());
								bool = invitationCodeService.checkInvitationCode(params);
								if(bool) {
									plugService.insertPlugUser(params);
								}else {
									//邀请码错误
									returnMap.put("errorcode", Globals.ERRORCODE2003);
									returnMap.put("errormessage", Globals.ERRORMESSAGE2003);
									return returnMap;
								}
							}
							
						}else {
							plugService.insertPlugUser(params);
						}
						
					}
					userId = plugService.getUserId(telephone);
					if(bool) {
						params.put("userId", userId);
						//添加邀请码邀请人关系
						invitationCodeService.addBeInvited(params);
						//检查更新用户vip等级
						invitationCodeService.checkVip(params);
					}
					if (null!=loginId && !loginId.equals("")) {
						//检查是否登录过
						int isLogin = plugService.getPlugLoginId(loginId);
						if (isLogin == 0 ) {
							//添加loginId保存15分钟
							plugService.savePlugLoginId(loginId,telephone,userId,Globals.LOGINID_EXPIRETIME);
						}
					}
					//检查是否有token令牌
					TokenEntity istokenEntity = tokenService.queryPlugToken(userId);
					if(null == istokenEntity) {
						String tokenUuid = UUID.randomUUID().toString().replace("-", "");
						if (null == Singleton.getInstance().getMoreTokenMap().get(userId)) {
							Singleton.getInstance().getMoreTokenMap().put(userId, tokenUuid);
						}else {
							return R.error(Globals.ERRORCODE20011, Globals.ERRORMESSAGE20011);
						}
						if (!tokenUuid.equals(Singleton.getInstance().getMoreTokenMap().get(userId))) {
							return R.error(Globals.ERRORCODE20011, Globals.ERRORMESSAGE20011);
						}
						//创建token
						TokenEntity tokenEntity = tokenService.createToken(userId);
						token = tokenEntity.getToken();
						Singleton.getInstance().getMoreTokenMap().remove(userId);
					}else {
						//2019-07-18 16:37:00 取消更新token
//						String newToken = UUID.randomUUID().toString().replace("-", "");
						token = istokenEntity.getToken();
						params.put("time", Globals.WEBTOKENTIME);
						params.put("token", token);
//						params.put("newToken", newToken);
						tokenService.updatePlugToken(params);
//						token = newToken;
					}
					int level =  plugService.getPlugVipLevel(userId) ;
					//设置验证码不可用
					qemailService.updateIsapply(id);
					Map<String,Object> map  = new HashMap<String,Object>();
					map.put("uuid", uuid);
					map.put("token", token);
					map.put("level", level);
					map.put("username", telephone);
					returnMap.put("errorcode", Globals.ERRORCODE0);
					returnMap.put("errormessage", Globals.ERRORMESSAGE0);
					returnMap.put("info", map);
					
					return returnMap;
				}
			}
		}
	}
	@RequestMapping(value = "/login")
	@ResponseBody
	public Map<String,Object> login(String telephone,String smsCode,String invitationCode
			,String loginId){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		//是否是发送邮箱验证码
    	boolean isSendEmail = false;
    	int type= 0;
		if(null == telephone) {
    		returnMap.put("errorcode", Globals.ERRORCODE1013);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
    		return returnMap;
    	}else {
    		if(telephone.contains("@")) {
    			isSendEmail = true;
    			type = Globals.MSGCODETYPE5;
    			//验证邮箱
    			if(null == telephone || telephone.equals("")) {
    				returnMap.put("errorcode", Globals.ERRORCODE1001);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE1001);
    				return returnMap;
    			}else {
    				String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    				if(!match(regex,telephone)) {
    					returnMap.put("errorcode", Globals.ERRORCODE1007);
    					returnMap.put("errormessage", Globals.ERRORMESSAGE1007);
    					return returnMap;
    				}
    			}
    		}else {
    			type = Globals.MSGCODETYPE4;
    			if(telephone.length()!=11) {
    				returnMap.put("errorcode", Globals.ERRORCODE1014);
    				returnMap.put("errormessage", Globals.ERRORMESSAGE1014);
    				return returnMap;
    			}else {
    				if(!CommonUtil.isTelephone(telephone)) {
    					returnMap.put("errorcode", Globals.ERRORCODE1015);
    					returnMap.put("errormessage", Globals.ERRORMESSAGE1015);
    					return returnMap;
    				}
    			}
    		}
    	}
		//验证手机验证码
		if(null == smsCode || smsCode.equals("")) {
			returnMap.put("errorcode", Globals.ERRORCODE1009);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1009);
			return returnMap;
		}else {
			if(smsCode.trim().length()!=4) {
				returnMap.put("errorcode", Globals.ERRORCODE1012);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1012);
				return returnMap;
			}else {
				Map<String,Object> params = new HashMap<String,Object>();
				params.put("email", telephone);
				params.put("type", type);
				params.put("code", smsCode);
				String id = qemailService.getEmailIdByMap(params);
				if(null == id) {
					returnMap.put("errorcode", Globals.ERRORCODE1012);
					returnMap.put("errormessage", Globals.ERRORMESSAGE1012);
					return returnMap;
				}else {
					long userId = 0;
					String uuid = "";//用户唯一标识
					String token ="";//用户过期token
					//验证是否存在该用户
					uuid = plugService.getUUID(telephone);
					boolean bool = false;
					if(null == uuid || "".equals(uuid)) {
						params.remove("email");
						//不存在
						uuid = UUID.randomUUID().toString().replace("-", "");
						if(isSendEmail) {
							params.put("email", telephone);
							//取消邮箱注册
//							returnMap.put("errorcode", Globals.ERRORCODE9017);
//							returnMap.put("errormessage", Globals.ERRORMESSAGE9017);
//							return returnMap; 
						}else {
							params.put("telephone", telephone);
						}
						params.put("uuid",uuid);
						//新建用户时是否有邀请码
						if(null != invitationCode && !invitationCode.equals("")) {
							//判断是否是百度效率云
							
							if(invitationCode.equals("百度效率云")) {
								String source = "baidu";
								int isopen = baiduService.getSwitch(source);
								if(isopen == 1) {
									//设置来源
									params.put("source", source);
									//设置过期时间 三个月
									params.put("expire_time", Globals.BAIDUXIAOLVYUN);
									//设置为专业版 该状态为2
									params.put("viplevel", 2);
									plugService.insertPlugUser(params);
								}else {
									returnMap.put("errorcode", Globals.ERRORCODE9015);
									returnMap.put("errormessage", Globals.ERRORMESSAGE9015);
									return returnMap;
								}
							}else {
								params.put("invitationCode", invitationCode.toUpperCase());
								bool = invitationCodeService.checkInvitationCode(params);
								if(bool) {
									plugService.insertPlugUser(params);
								}else {
									//邀请码错误
									returnMap.put("errorcode", Globals.ERRORCODE2003);
									returnMap.put("errormessage", Globals.ERRORMESSAGE2003);
									return returnMap;
								}
							}
							
						}else {
							plugService.insertPlugUser(params);
						}
						
					}
					userId = plugService.getUserId(telephone);
					if(bool) {
						params.put("userId", userId);
						//添加邀请码邀请人关系
						invitationCodeService.addBeInvited(params);
						//检查更新用户vip等级
						invitationCodeService.checkVip(params);
					}
					if (null!=loginId && !loginId.equals("")) {
						//检查是否登录过
						int isLogin = plugService.getPlugLoginId(loginId);
						if (isLogin == 0 ) {
							//添加loginId保存15分钟
							plugService.savePlugLoginId(loginId,telephone,userId,Globals.LOGINID_EXPIRETIME);
						}
					}
					//检查是否有token令牌
					TokenEntity istokenEntity = tokenService.queryPlugTokenInstall(userId);
					if(null == istokenEntity) {
						//创建token
						TokenEntity tokenEntity = tokenService.createTokenInstall(userId);
						token = tokenEntity.getToken();
					}else {
						//2019-07-12 14:30:00 取消更新token
//						String newToken = UUID.randomUUID().toString().replace("-", "");//更新token
						token = istokenEntity.getToken();
						params.put("time", Globals.PLUGTOKENTIME);
						params.put("token", token);
//						params.put("newToken", newToken);//更新token
						tokenService.updatePlugTokenInstall(params);
//						token = newToken;//更新token
					}
					int level =  plugService.getPlugVipLevel(userId) ;
					//设置验证码不可用
					qemailService.updateIsapply(id);
					Map<String,Object> map  = new HashMap<String,Object>();
					map.put("uuid", uuid);
					map.put("token", token);
					map.put("level", level);
					map.put("username", telephone);
					returnMap.put("errorcode", Globals.ERRORCODE0);
					returnMap.put("errormessage", Globals.ERRORMESSAGE0);
					returnMap.put("info", map);
					
					return returnMap;
				}
			}
		}
	}
	@RequestMapping(value = "/binding")
	@ResponseBody
	public Map<String,Object> binding(String token,String uuid,String macid,String system,String version){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == token || "".equals(token)) {
			returnMap.put("errorcode", Globals.ERRORCODE7001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7001);
			return returnMap;
		}
		if(null == uuid || "".equals(uuid)) {
			returnMap.put("errorcode", Globals.ERRORCODE7002);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7002);
			return returnMap;
		}
		if(null == macid || "".equals(macid)) {
			returnMap.put("errorcode", Globals.ERRORCODE7003);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7003);
			return returnMap;
		}
		if(null == system || "".equals(system)) {
			returnMap.put("errorcodde", Globals.ERRORCODE7006);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7006);
			return returnMap;
		}
		if(null == version || "".equals(version)) {
			returnMap.put("errorcodde", Globals.ERRORCODE7007);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7007);
			return returnMap;
		}
		//验证token令牌是否过期
		TokenEntity tokenEntity = tokenService.queryByTokenInstall(token);
		if(null == tokenEntity) {
			returnMap.put("errorcode", Globals.ERRORCODE7004);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7004);
			return returnMap;
		}
		//验证是否是有效的uuid
		int isapplyUUID = plugService.countUserByUUID(uuid);
		if(isapplyUUID == 0) {
			returnMap.put("errorcode", Globals.ERRORCODE7005);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7005);
			return returnMap;
		}
		//查看是否绑定了
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("macid", macid);
		params.put("uuid", uuid);
		int isBinding = plugService.countBinding(params);
		if(isBinding == 0 ) {
			params.put("system", system);
			params.put("version", version);
			plugService.insertBinding(params);
		}
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
	}
	@RequestMapping(value = "/checkToken")
	@ResponseBody
	public Map<String,Object> checkToken(String token){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == token || "".equals(token)) {
			returnMap.put("errorcode", Globals.ERRORCODE7001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7001);
			return returnMap;
		}
		//验证token令牌是否过期
		TokenEntity tokenEntity = tokenService.queryByTokenInstall(token);
		boolean bool = false;
		if(null != tokenEntity) {
			bool = true;
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("token", token);
			params.put("time", Globals.PLUGTOKENTIME);
			tokenService.updatePlugTokenInstall(params);
		}
		if(bool) {
			returnMap.put("username", plugService.getUserNameByToken(token));
			int retlevel = plugService.getPlugVipLevel(tokenEntity.getUserId()) ;
			returnMap.put("level", retlevel);
		}
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", bool);
		return returnMap;
	}
	
	@RequestMapping(value = "/checkBuyerToken")
	@ResponseBody
	public Map<String,Object> checkBuyerToken(String token){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == token || "".equals(token)) {
			returnMap.put("errorcode", Globals.ERRORCODE7001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7001);
			return returnMap;
		}
		//验证token令牌是否过期
		TokenEntity tokenEntity = tokenService.queryByToken(token);
		boolean bool = false;
		if(null != tokenEntity) {
			bool = true;
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("token", token);
			params.put("time", Globals.WEBTOKENTIME);
			tokenService.updatePlugToken(params);
		}
		if(bool) {
			returnMap.put("username", plugService.getUserNameByToken(token));
			int retlevel = plugService.getPlugVipLevel(tokenEntity.getUserId()) ;
			returnMap.put("level", retlevel);
		}
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", bool);
		return returnMap;
	}
	@RequestMapping(value = "/webCheckToken")
	@ResponseBody
	public Map<String,Object> webCheckToken(String token){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == token || "".equals(token)) {
			returnMap.put("errorcode", Globals.ERRORCODE7001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7001);
			return returnMap;
		}
		//验证token令牌是否过期
		TokenEntity tokenEntity = tokenService.queryByToken(token);
		boolean bool = false;
		if(null != tokenEntity) {
			bool = true;
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("token", token);
			params.put("time", Globals.WEBTOKENTIME);
			tokenService.updatePlugToken(params);
		}
		if(bool) {
			returnMap.put("username", plugService.getUserNameByToken(token));
			int retlevel = plugService.getPlugVipLevel(tokenEntity.getUserId()) ;
			returnMap.put("level", retlevel);
		}
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", bool);
		return returnMap;
	}
	
	@RequestMapping(value = "/getPlugUserInfo")
	@ResponseBody
	public Map<String,Object> getPlugUserInfo(String telephone,String token){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		
		if(null == telephone || "".equals(telephone)) {
			returnMap.put("errorcode", Globals.ERRORCODE1013);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
			return returnMap;
		}
		if(null == token || "".equals(token)) {
			returnMap.put("errorcode", Globals.ERRORCODE7001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7001);
			return returnMap;
		}
		long userId = 0;
		String uuid = "";//用户唯一标识
		//验证是否存在该用户
		uuid = plugService.getUUID(telephone);
		if(null == uuid || "".equals(uuid)) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		userId = plugService.getUserId(telephone);
		//检查是否有token令牌
		TokenEntity istokenEntity = tokenService.queryPlugToken(userId);
		if(null==istokenEntity) {
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}else {
			if(!istokenEntity.getToken().equals(token)) {
				returnMap.put("errorcode", Globals.ERRORCODE4001);
				returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
				return returnMap;
			}else {
				Map<String, Object> retMap  = new HashMap<String,Object>();
				int retlevel = plugService.getPlugVipLevel(userId) ;
				returnMap.put("errorcode", Globals.ERRORCODE0);
				returnMap.put("errormessage", Globals.ERRORMESSAGE0);
//				returnMap.put("info", retlevel);
				retMap.put("level", retlevel);
				//是否是临时vip
				if(retlevel == 2 ){
					boolean bool = plugService.isVipShort(userId);
					String expire_time = plugService.getExpireTime(userId);
					retMap.put("expire_time", expire_time);
					if(bool) {
						retMap.put("bool", false);
					}else {
						retMap.put("bool", true);
					}
				}else {
					retMap.put("bool", false);
				}
				returnMap.put("info", retMap);
			}
		}
		return returnMap;
	}
	/**
	 * 获取vip模型
	 * @param telephone
	 * @param token
	 * @param channelnumber
	 * @return
	 */
	@RequestMapping(value="/getVipModels")
	@ResponseBody
	public Map<String,Object>  getVipModels (String telephone ,String token,String channelnumber){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == telephone || "".equals(telephone)) {
			returnMap.put("errorcode", Globals.ERRORCODE1013);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
			return returnMap;
		}
		if(null == token || "".equals(token)) {
			returnMap.put("errorcode", Globals.ERRORCODE7001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7001);
			return returnMap;
		}
		if(null == channelnumber || "".equals(channelnumber)) {
			returnMap.put("errorcode", Globals.ERRORCODE7008);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7008);
			return returnMap;
		}
		long userId = 0;
		String uuid = "";//用户唯一标识
		//验证是否存在该用户
		uuid = plugService.getUUID(telephone);
		if(null == uuid || "".equals(uuid)) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		userId = plugService.getUserId(telephone);
		//检查是否有token令牌
		TokenEntity istokenEntity = tokenService.queryPlugToken(userId);
		if(null==istokenEntity) {
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}else {
			//是否是企业版
			boolean isbusiness = plugService.isBusiness(userId);
			//是
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("userId", userId);
			params.put("channelnumber", channelnumber);
			if(isbusiness) {
				params.put("isbusiness", true);
			}
			List<Map<String,Object>> list = plugService.getvipModels(params);
			returnMap.put("errorcode", Globals.ERRORCODE0);
			returnMap.put("errormessage", Globals.ERRORMESSAGE0);
			returnMap.put("info", list);
		}
		return returnMap;
	}
	
	private static boolean match(String regex, String str) {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(str);
			return matcher.matches();
	}
	@RequestMapping(value = "/userLogin")
	@ResponseBody
	public Map<String,Object> userLogin(HttpServletRequest request,HttpServletResponse response,String username,String password,String machineID
			,String loginId){
		boolean flag = false;
		String code = "";
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == username || "".equals(username.trim())) {
			returnMap.put("errorcode", Globals.ERRORCODE1013);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
    		return returnMap;
		}
		if(null == password || "".equals(password.trim())) {
			returnMap.put("errorcode", Globals.ERRORCODE1002);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1002);
    		return returnMap;
		}
		password = BtoaEncode.decrypt(password);
		//通过用户名和密码查询用户
		Long userId = plugService.checkUserByUsernamePassword(username,"123456"); 
		if (null != userId ) {
			returnMap.put("errorcode", Globals.ERRORCODE4004);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4004);
			return returnMap;
		}
		
		userId = plugService.checkUserByUsernamePassword(username,password);
		if(null != userId && userId > 0 ) {
			if (null!=loginId && !loginId.equals("")) {
				//检查是否登录过
				int isLogin = plugService.getPlugLoginId(loginId);
				if (isLogin == 0 ) {
					//添加loginId保存15分钟
					plugService.savePlugLoginId(loginId,username,userId,Globals.LOGINID_EXPIRETIME);
				}
			}
			String token ="";//用户过期token
			//检查是否有token令牌
			TokenEntity istokenEntity = tokenService.queryPlugToken(userId);
			if(null == istokenEntity) {
				//创建token
				try {
					TokenEntity tokenEntity = tokenService.createToken(userId);
					token = tokenEntity.getToken();
				} catch (Exception e) {
					istokenEntity = tokenService.queryPlugToken(userId);
					token = istokenEntity.getToken();
				}
			}else {
				token = istokenEntity.getToken();
				Map<String,Object> params = new HashMap<String,Object>();
				params.put("time", Globals.WEBTOKENTIME);
				params.put("token", token);
				tokenService.updatePlugToken(params);
			}
			//校验machineID
			if(null != machineID && !"".equals(machineID)) {
				String realIP = CommonUtil.getIpAddress(request);
				code = CommonUtil.GUID();
				flag = plugService.checkMachineID(machineID, realIP,userId,code);
				if(flag) {
					code = "";
				}
			}
			int level =  plugService.getPlugVipLevel(userId) ;
			Map<String,Object> map  = new HashMap<String,Object>();
			String uuid = plugService.getUUID(username);
			map.put("uuid", uuid);
			map.put("token", token);
			map.put("level", level);
			map.put("username", username);
			map.put("flag", flag);
			map.put("code", code);
			returnMap.put("errorcode", Globals.ERRORCODE0);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
    		returnMap.put("info", map);
		}else {
			returnMap.put("errorcode", Globals.ERRORCODE2004);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE2004);
    		return returnMap;
		}
		
		return returnMap;
	}
	@RequestMapping(value = "/userRegister")
	@ResponseBody
	public Map<String,Object> userRegister(HttpServletRequest request,HttpServletResponse response ,String username,String password,String repassword,String smsCode,String invitationCode){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == username || "".equals(username.trim())) {
			returnMap.put("errorcode", Globals.ERRORCODE1013);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
    		return returnMap;
		}
		if(null == password || "".equals(password.trim())) {
			returnMap.put("errorcode", Globals.ERRORCODE1002);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1002);
    		return returnMap;
		}
		if(null == repassword || "".equals(repassword.trim())) {
			returnMap.put("errorcode", Globals.ERRORCODE1004);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1004);
    		return returnMap;
		}
		password = BtoaEncode.decrypt(password);
		repassword = BtoaEncode.decrypt(repassword);
		if(null == smsCode || "".equals(smsCode.trim())) {
			returnMap.put("errorcode", Globals.ERRORCODE1009);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1009);
    		return returnMap;
		}
		//是否是发送邮箱验证码
    	boolean isSendEmail = false;
    	int type= 0;
		//验证是手机号还是邮箱
		if(username.contains("@")) {
			isSendEmail = true;
			type = Globals.MSGCODETYPE5;
			//验证邮箱
			String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
			if(!match(regex,username)) {
				returnMap.put("errorcode", Globals.ERRORCODE1007);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1007);
				return returnMap;
			}
		}else {
			type = Globals.MSGCODETYPE4;
			if(username.length()!=11) {
				returnMap.put("errorcode", Globals.ERRORCODE1014);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1014);
				return returnMap;
			}else {
				if(!CommonUtil.isTelephone(username)) {
					returnMap.put("errorcode", Globals.ERRORCODE1015);
					returnMap.put("errormessage", Globals.ERRORMESSAGE1015);
					return returnMap;
				}
			}
		}
		//验证用户是否注册过
		int isRegister = plugService.isRegister(username);
		if(isRegister == 1 ) {
			returnMap.put("errorcode", Globals.ERRORCODE1016);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1016);
			return returnMap;
		}
		//验证密码
		if(password.trim().length() == password.length()) {
			if(password.trim().length() < 8 || password.trim().length() > 16) {
				returnMap.put("errorcode", Globals.ERRORCODE1003);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1003);
				return returnMap;
			}
			if(null == repassword || "".equals(repassword)) {
				returnMap.put("errorcode", Globals.ERRORCODE1004);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1004);
				return returnMap;
			}
			if(!password.equals(repassword)) {
				returnMap.put("errorcode", Globals.ERRORCODE1005);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1005);
				return returnMap;
			}
		}else {
			returnMap.put("errorcode", Globals.ERRORCODE1006);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1006);
			return returnMap;
		}
		
		Map<String,Object> params = new HashMap<String,Object>();
		//验证手机验证码
		if(smsCode.trim().length()!=4) {
			returnMap.put("errorcode", Globals.ERRORCODE1012);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1012);
			return returnMap;
		}else {
			params.put("email", username);
			params.put("type", type);
			params.put("code", smsCode);
			String id = qemailService.getEmailIdByMap(params);
			if(null == id) {
				returnMap.put("errorcode", Globals.ERRORCODE1012);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1012);
				return returnMap;
			}
		}
		boolean bool = false;
		String uuid = UUID.randomUUID().toString().replace("-", "");
		params.put("password", password);
		params.put("uuid", uuid);
		params.remove("email"); 
		if(isSendEmail) {
			params.put("email", username);
			//取消邮箱注册
			//设置验证码过期
//			qemailService.updateIsapplyByCode(params);
//			returnMap.put("errorcode", Globals.ERRORCODE9017);
//			returnMap.put("errormessage", Globals.ERRORMESSAGE9017);
//			return returnMap; 
		}else {
			params.put("telephone", username);
		}
		//设置验证码过期
		qemailService.updateIsapplyByCode(params);
		//验证邀请码
		if(invitationCode==null || "".equals(invitationCode)) {
			//没有邀请码
			plugService.insertPlugUserPassword(params);
		}else {
			//有邀请码
			//百度效率云
			if(invitationCode.equals("百度效率云")) {
				String source = "baidu";
				int isopen = baiduService.getSwitch(source);
				if(isopen == 1) {
					//设置来源
					params.put("source", source);
					//设置过期时间 三个月
					params.put("expire_time", Globals.BAIDUXIAOLVYUN);
					//设置为专业版 该状态为2
					params.put("viplevel", 2);
					plugService.insertPlugUser(params);
				}else {
					returnMap.put("errorcode", Globals.ERRORCODE9015);
					returnMap.put("errormessage", Globals.ERRORMESSAGE9015);
					return returnMap;
				}
			}else {
				params.put("invitationCode", invitationCode);
				bool = invitationCodeService.checkInvitationCode(params);
				if(bool) {
					plugService.insertPlugUserPassword(params);
					Long userId = plugService.getUserId(username);
					params.put("userId", userId);
					//添加邀请码邀请人关系
					invitationCodeService.addBeInvited(params);
					//检查更新用户vip等级
					invitationCodeService.checkVip(params);
				}else {
					//邀请码错误
					returnMap.put("errorcode", Globals.ERRORCODE2003);
					returnMap.put("errormessage", Globals.ERRORMESSAGE2003);
					return returnMap;
				}
			}
		}
		
		//注册完成直接登录
		Long userId = plugService.checkUserByUsernamePassword(username,password);
		//保存token
		String token ="";//用户过期token
		//检查是否有token令牌
		TokenEntity istokenEntity = tokenService.queryPlugToken(userId);
		if(null == istokenEntity) {
			//创建token
			TokenEntity tokenEntity = tokenService.createToken(userId);
			token = tokenEntity.getToken();
		}else {
			token = istokenEntity.getToken();
			params.put("time", Globals.WEBTOKENTIME);
			params.put("token", token);
			tokenService.updatePlugToken(params);
		}
		int level =  plugService.getPlugVipLevel(userId) ;
		Map<String,Object> map  = new HashMap<String,Object>();
		map.put("uuid", uuid);
		map.put("token", token);
		map.put("level", level);
		map.put("username", username);
		returnMap.put("info", map);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		
		
		return returnMap;
	}
	@RequestMapping(value = "/retrievePassword")
	@ResponseBody
	public Map<String,Object> retrievePassoword(String username,String password,String repassword,String smsCode){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == username || "".equals(username.trim())) {
			returnMap.put("errorcode", Globals.ERRORCODE1013);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
    		return returnMap;
		}
		if(null == password || "".equals(password.trim())) {
			returnMap.put("errorcode", Globals.ERRORCODE1002);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1002);
    		return returnMap;
		}
		if(null == repassword || "".equals(repassword.trim())) {
			returnMap.put("errorcode", Globals.ERRORCODE1004);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1004);
    		return returnMap;
		}
		password = BtoaEncode.decrypt(password);
		repassword = BtoaEncode.decrypt(repassword);
		if(null == smsCode || "".equals(smsCode.trim())) {
			returnMap.put("errorcode", Globals.ERRORCODE1009);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1009);
    		return returnMap;
		}
		//是否是发送邮箱验证码
    	boolean isSendEmail = false;
    	int type= 0;
		//验证是手机号还是邮箱
		if(username.contains("@")) {
			isSendEmail = true;
			type = Globals.MSGCODETYPE9;
			//验证邮箱
			String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
			if(!match(regex,username)) {
				returnMap.put("errorcode", Globals.ERRORCODE1007);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1007);
				return returnMap;
			}
		}else {
			type = Globals.MSGCODETYPE8;
			if(username.length()!=11) {
				returnMap.put("errorcode", Globals.ERRORCODE1014);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1014);
				return returnMap;
			}else {
				if(!CommonUtil.isTelephone(username)) {
					returnMap.put("errorcode", Globals.ERRORCODE1015);
					returnMap.put("errormessage", Globals.ERRORMESSAGE1015);
					return returnMap;
				}
			}
		}
		//验证用户是否注册过
		int isRegister = plugService.isRegister(username);
		if(isRegister < 1 ) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		//验证密码
		if(password.trim().length() == password.length()) {
			if(password.trim().length() < 8 || password.trim().length() > 16) {
				returnMap.put("errorcode", Globals.ERRORCODE1003);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1003);
				return returnMap;
			}
			if(null == repassword || "".equals(repassword)) {
				returnMap.put("errorcode", Globals.ERRORCODE1004);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1004);
				return returnMap;
			}
			if(!password.equals(repassword)) {
				returnMap.put("errorcode", Globals.ERRORCODE1005);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1005);
				return returnMap;
			}
		}else {
			returnMap.put("errorcode", Globals.ERRORCODE1006);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1006);
			return returnMap;
		}
		Map<String,Object> params = new HashMap<String,Object>();
		//验证手机验证码
		if(smsCode.trim().length()!=4) {
			returnMap.put("errorcode", Globals.ERRORCODE1012);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1012);
			return returnMap;
		}else {
			params.put("email", username);
			params.put("type", type);
			params.put("code", smsCode);
			String id = qemailService.getEmailIdByMap(params);
			if(null == id) {
				returnMap.put("errorcode", Globals.ERRORCODE1012);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1012);
				return returnMap;
			}
		}
		//设置验证码过期
		qemailService.updateIsapplyByCode(params);
		plugService.updateUserPassword(username ,password);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
	}
	@RequestMapping(value = "/checkSmsCode")
	@ResponseBody
	public Map<String,Object> retrievePassoword(String username ,String smsCode){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == username || "".equals(username.trim())) {
			returnMap.put("errorcode", Globals.ERRORCODE1013);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
    		return returnMap;
		}
		if(null == smsCode || "".equals(smsCode.trim())) {
			returnMap.put("errorcode", Globals.ERRORCODE1009);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1009);
    		return returnMap;
		}
		//是否是发送邮箱验证码
    	boolean isSendEmail = false;
    	int type= 0;
		//验证是手机号还是邮箱
		if(username.contains("@")) {
			isSendEmail = true;
			type = Globals.MSGCODETYPE9;
			//验证邮箱
			String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
			if(!match(regex,username)) {
				returnMap.put("errorcode", Globals.ERRORCODE1007);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1007);
				return returnMap;
			}
		}else {
			type = Globals.MSGCODETYPE8;
			if(username.length()!=11) {
				returnMap.put("errorcode", Globals.ERRORCODE1014);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1014);
				return returnMap;
			}else {
				if(!CommonUtil.isTelephone(username)) {
					returnMap.put("errorcode", Globals.ERRORCODE1015);
					returnMap.put("errormessage", Globals.ERRORMESSAGE1015);
					return returnMap;
				}
			}
		}
		//验证用户是否注册过
		int isRegister = plugService.isRegister(username);
		if(isRegister < 1 ) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		Map<String,Object> params = new HashMap<String,Object>();
		//验证手机验证码
		if(smsCode.trim().length()!=4) {
			returnMap.put("errorcode", Globals.ERRORCODE1012);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1012);
			return returnMap;
		}else {
			params.put("email", username);
			params.put("type", type);
			params.put("code", smsCode);
			String id = qemailService.getEmailIdByMap(params);
			if(null == id) {
				returnMap.put("errorcode", Globals.ERRORCODE1012);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1012);
				return returnMap;
			}else {
				//设置验证码过期
				qemailService.updateIsapplyByCode(params);
				String smsToken = UUID.randomUUID().toString().replace("-", "");
				params.put("smstoken", smsToken);
				plugService.expireSmsCode(params);
				plugService.savePlugSmsCode(params);
				returnMap.put("smsToken", smsToken);
				returnMap.put("errorcode", Globals.ERRORCODE0);
				returnMap.put("errormessage", Globals.ERRORMESSAGE0);
				return returnMap;
			}
		}
	}
	@RequestMapping(value = "/resetPassword")
	@ResponseBody
	public Map<String,Object> resetPassword(String password,String repassword,String smsToken){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if (null== smsToken) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		if(null == password || "".equals(password.trim())) {
			returnMap.put("errorcode", Globals.ERRORCODE1002);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1002);
    		return returnMap;
		}
		if(null == repassword || "".equals(repassword.trim())) {
			returnMap.put("errorcode", Globals.ERRORCODE1004);
    		returnMap.put("errormessage", Globals.ERRORMESSAGE1004);
    		return returnMap;
		}
		password = BtoaEncode.decrypt(password);
		repassword = BtoaEncode.decrypt(repassword);
    	Map<String,Object> params = new HashMap<String,Object>();
    	params.put("smstoken", smsToken);
    	String username = plugService.getSmsToken(params);
    	
    	if (null == username) {
    		returnMap.put("errorcode", Globals.ERRORCODE1012);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1012);
			return returnMap;
		}
    	params.put("email", username);
		//验证密码
		if(password.trim().length() == password.length()) {
			if(password.trim().length() < 8 || password.trim().length() > 16) {
				returnMap.put("errorcode", Globals.ERRORCODE1003);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1003);
				return returnMap;
			}
			if(null == repassword || "".equals(repassword)) {
				returnMap.put("errorcode", Globals.ERRORCODE1004);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1004);
				return returnMap;
			}
			if(!password.equals(repassword)) {
				returnMap.put("errorcode", Globals.ERRORCODE1005);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1005);
				return returnMap;
			}
		}else {
			returnMap.put("errorcode", Globals.ERRORCODE1006);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1006);
			return returnMap;
		}
		//设置验证码token过期
		plugService.expireSmsCode(params);
		plugService.updateUserPassword(username ,password);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
	}
	@RequestMapping("logOut")
	@ResponseBody
	public Map<String,Object> logOut(String token) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		tokenService.expireTokenByToken(token);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
	}
	@RequestMapping(value = "/checkCode")
	@ResponseBody
	public Map<String,Object> checkCode(String code){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == code || "".equals(code)) {
			returnMap.put("errorcode", Globals.ERRORCODE7009);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7009);
			return returnMap;
		}
		boolean bool = false;
		bool = plugService.checkCode(code);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", bool);
		return returnMap;
	}
	@RequestMapping(value = "/handMachineCode")
	@ResponseBody
	public Map<String,Object> handMachineCode(String code,String isagree){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == code || "".equals(code)) {
			returnMap.put("errorcode", Globals.ERRORCODE7009);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7009);
			return returnMap;
		}
		if(null == isagree || "".equals(isagree)) {
			isagree = "0";
			//不同意
			plugService.agreeNo(code);
		}else {
			if(isagree.equals("1")) {
				//同意 
				plugService.agreeYes(code);
			}else {
				//不同意
				plugService.agreeNo(code);
			}
		}
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
	}
	
	
	@RequestMapping("saveBuyerOrder")
	@ResponseBody
	public Map<String, Object> saveBuyerOrder(@RequestParam String token,@RequestParam String orderNo){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("token", token);
		Object userid = tokenService.getIdByToken(token);
		if (null==userid) {
			returnMap.put("errorcode", Globals.ERRORCODE7004);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7004);
		}else {
			params.put("orderNo", orderNo);
			params.put("userId", userid);
			buyerService.saveBuyOrder(params);
			returnMap.put("errorcode", Globals.ERRORCODE0);
			returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		}
		return returnMap;
	}
	@RequestMapping("orderList")
	@ResponseBody
	public Map<String, Object> orderList(@RequestParam String token,@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer rows,
			HttpServletRequest request){
		Map<String, Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> params = new HashMap<String,Object>();
		if (null == token || token.equals("")) {
			returnMap.put("errorcode", Globals.ERRORCODE7001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7001);
			return returnMap;
		}else {
			TokenEntity tokenEntity = tokenService.queryByToken(token);
			if (null == tokenEntity) {
				returnMap.put("errorcode", Globals.ERRORCODE7004);
				returnMap.put("errormessage", Globals.ERRORMESSAGE7004);
				return returnMap;
			}else {
				Object userid = tokenService.getIdByToken(token);
				params.put("userId", userid);
			}
		}
		params.put("p", (page-1)*rows);
		params.put("r", rows);
		List<Map<String, Object>> userOrderList = buyerService.userOrderList(params);
		int count = buyerService.userOrderCount(params);
		if (null!= userOrderList && userOrderList.size()>0) {
			params.put("list", userOrderList);
			List<Map<String, Object>> list =  buyerService.orderList(params);
			String value = buyerService.getKeyValue();
			JSONObject json = JSONObject.parseObject(value);
			String retLanguage = request.getParameter("retLanguage");
			for (int i = 0; i < list.size(); i++) {
//				list.get(i).put("price", json.getJSONObject(getPayWay(list.get(i).get("paytype").toString())).get(list.get(i).get("payplan").toString()));
				list.get(i).put("subject", getSubject(list.get(i).get("subject"),retLanguage));
				list.get(i).put("payplan", getPayPlan(list.get(i).get("payplan").toString(),retLanguage));
				list.get(i).put("paytype", getPayType(list.get(i).get("paytype").toString(),retLanguage));
				list.get(i).put("front_url", list.get(i).get("front_url").toString()+"?out_trade_no="+list.get(i).get("out_trade_no").toString()+"&token="+token);
			}
			returnMap.put("errorcode", Globals.ERRORCODE0);
			returnMap.put("errormessage", Globals.ERRORMESSAGE0);
			returnMap.put("list", list);
			returnMap.put("count", count);
			returnMap.put("page", page);
		}else {
			returnMap.put("list", null);
		}
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
	}
	public Object getSubject(Object subject ,String retLanguage) {
		if (null != retLanguage && retLanguage.equals("en")) {
			return "Professional Edition Purchasing/Extending";
		}else {
			return subject;
		}
	}
	public String getPayPlan(String payPlan,String retLanguage) {
		if (payPlan.equals("1")) {
			if (null != retLanguage && retLanguage.equals("en")) {
				return "One Month";
			}
			return "一个月";
		}else if (payPlan.equals("2")) {
			if (null != retLanguage && retLanguage.equals("en")) {
				return "Half a Year";
			}
			return "半年";
		}else if (payPlan.equals("3")) {
			if (null != retLanguage && retLanguage.equals("en")) {
				return "One Year";
			}
			return "一年";
		}
		return "一个月";
	}
	public String getPayWay(String payType) {
		if (payType.equals("1")) {
			return "zfb";
		}else if (payType.equals("2")) {
			return "wx";
		}
		return "zfb";
	}
	
	public String getPayType(String payType,String retLanguage) {
		String ret = "支付宝";
		if (null != retLanguage && retLanguage.equals("en")) {
			ret = "Alipay";
		}
		if (payType.equals("1")) {
			if (null != retLanguage && retLanguage.equals("en")) {
				ret = "Alipay";
			}
			return ret;
		}else if (payType.equals("2")) {
			ret = "微信";
			if (null != retLanguage && retLanguage.equals("en")) {
				ret = "WeChat";
			}
			return ret;
		}
		return ret;
	}
	@RequestMapping("checkOrder")
	@ResponseBody
	public Map<String, Object> checkOrder(@RequestParam String token,@RequestParam String orderNo){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("token", token);
		Object userid = tokenService.getIdByToken(token);
		if (null==userid) {
			returnMap.put("code", Globals.ERRORCODE7004);
			returnMap.put("msg", Globals.ERRORMESSAGE7004);
		}else {
			params.put("orderNo", orderNo);
			params.put("userId", userid);
			int count = buyerService.checkOrder(params);
			if (count == 0 ) {
				returnMap.put("code", Globals.ERRORCODE7010);
				returnMap.put("msg", Globals.ERRORMESSAGE7010);
				return returnMap;
			}
			returnMap.put("code", Globals.ERRORCODE0);
			returnMap.put("msg", Globals.ERRORMESSAGE0);
		}
		return returnMap;
	}
	@RequestMapping("checkPlugLoginId")
	@ResponseBody
	public R checkPlugLoginId(@RequestParam String loginId) {
		//查询loginId内容
		Map<String,Object> map = plugService.getPlugLoginByIdName(loginId);
		if (null == map) {
			return R.error(Globals.ERRORCODE2001,Globals.ERRORMESSAGE2001);
		}
		Long userId = Long.valueOf(map.get("userid").toString());
		TokenEntity tokenEntity = tokenService.queryPlugTokenByUserId(userId);
		if (null == tokenEntity) {
			tokenEntity = tokenService.createPlugLoginToken(userId);
		}
		Map<String, Object> params = new HashMap<>();
		params.put("username", map.get("username"));
		params.put("token", tokenEntity.getToken());
		
		return R.ok().put("data", params);
	}
	@RequestMapping("extendExpireToken")
	@ResponseBody
	public R extendExpireToken(@RequestParam String token,@RequestParam String username,@RequestParam(defaultValue = "15") int time ) {
		Long historyTime = Singleton.getInstance().getTokenMap().get(token);
		if (null == historyTime ) {
			Singleton.getInstance().setTokenMap(token, System.currentTimeMillis());
		}else {
			Long nowTime = System.currentTimeMillis();
			if (nowTime - historyTime < 5 * 60 * 1000) { // 5分钟缓存
				return R.ok();
			}else {
				Singleton.getInstance().setTokenMap(token, System.currentTimeMillis());
			}
		}
		Long aixcoderTime = Singleton.getInstance().getTokenMap().get("aixcoderToken");
		if (null == aixcoderTime ) {
			Singleton.getInstance().setTokenMap("aixcoderToken", System.currentTimeMillis());
		}else {
			Long nowTime = System.currentTimeMillis();
			if (nowTime - aixcoderTime > 24 * 60 * 60 * 1000) { //超过一天清除一下map缓存
				Singleton.getInstance().cleanTokenMap();
			}
		}
		Long userId = plugService.getUserId(username);
		if (null == userId) {
			return R.error(Globals.ERRORCODE2001,Globals.ERRORMESSAGE2001);
		}
		TokenEntity tokenEntity = tokenService.queryPlugTokenByUserId(userId);
		if (null == tokenEntity) {
			return R.error(Globals.ERRORCODE2001,Globals.ERRORMESSAGE2001);
		}else {
			if (tokenEntity.getToken().equals(token)) {
				Date now = new Date();
				tokenEntity.setUpdateTime(now);
				tokenEntity.setExpireTime(new Date(now.getTime()+TokenServiceImpl.TIME * time));
				tokenService.updatePlugTokenEntity(tokenEntity);
				return R.ok();
			}else {
				return R.error(Globals.ERRORCODE2001,Globals.ERRORMESSAGE2001);
			}
		}
	}
	@RequestMapping("remoteCheckAuth")
	@ResponseBody
	public R remoteCheckAuth(@RequestParam String proToken){
		//获取远程权限
		Integer num = plugService.remoteCheckAuth(proToken);
		if (null != num &&  num == 1) {
			plugService.remoteCheckCloseAuth(proToken);
			return R.ok().put("allowed", true);
		}else {
			return R.ok().put("allowed", false);
		}
		
	}

	@RequestMapping("logoff")
	@ResponseBody
	public Map<String, Object> logoff(@RequestParam String username,@RequestParam String code,@RequestParam String token,HttpServletRequest request) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		//是否是发送邮箱验证码
		boolean isSendEmail = false;
		int type= 0;
		if(null == username) {
			returnMap.put("errorcode", Globals.ERRORCODE1013);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
			return returnMap;
		}else {
			if(username.contains("@")) {
				isSendEmail = true;
				type = Globals.MSGCODETYPE9;
				//验证邮箱
				if(null == username || username.equals("")) {
					returnMap.put("errorcode", Globals.ERRORCODE1001);
					returnMap.put("errormessage", Globals.ERRORMESSAGE1001);
					return returnMap;
				}else {
					String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
					if(!match(regex,username)) {
						returnMap.put("errorcode", Globals.ERRORCODE1007);
						returnMap.put("errormessage", Globals.ERRORMESSAGE1007);
						return returnMap;
					}
				}
			}else {
				type = Globals.MSGCODETYPE8;
				if(username.length()!=11) {
					returnMap.put("errorcode", Globals.ERRORCODE1014);
					returnMap.put("errormessage", Globals.ERRORMESSAGE1014);
					return returnMap;
				}else {
					if(!com.hxxdemo.githubLogin.util.CommonUtil.isTelephone(username)) {
						returnMap.put("errorcode", Globals.ERRORCODE1015);
						returnMap.put("errormessage", Globals.ERRORMESSAGE1015);
						return returnMap;
					}
				}
			}
		}
		//验证手机验证码
		if(null == code || code.equals("")) {
			returnMap.put("errorcode", Globals.ERRORCODE1009);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1009);
			return returnMap;
		}else {
			if(code.trim().length()!=4) {
				returnMap.put("errorcode", Globals.ERRORCODE1012);
				returnMap.put("errormessage", Globals.ERRORMESSAGE1012);
				return returnMap;
			}else {
				Map<String,Object> params = new HashMap<String,Object>();
//				params.put("isapply", "0");
				params.put("code", code);
				params.put("email", username);
				params.put("type", type);
				String id = qemailService.getEmailIdByMap(params);
				if(null == id) {
					returnMap.put("errorcode", Globals.ERRORCODE1012);
					returnMap.put("errormessage", Globals.ERRORMESSAGE1012);
					return returnMap;
				}else {
					if (null == token || token.equals("")) {
						returnMap.put("errorcode", Globals.ERRORCODE7001);
						returnMap.put("errormessage", Globals.ERRORMESSAGE7001);
						return returnMap;
					} else {
						TokenEntity tokenEntity = tokenService.queryByToken(token);
						if (null == tokenEntity) {
							returnMap.put("errorcode", Globals.ERRORCODE7004);
							returnMap.put("errormessage", Globals.ERRORMESSAGE7004);
							return returnMap;
						} else {
							Object userid = tokenService.getIdByToken(token);
							try {
								plugService.logOff(userid);
								tokenService.expireToken(Long.parseLong(userid.toString()));
								returnMap.put("errorcode", Globals.ERRORCODE0);
								returnMap.put("errormessage", Globals.ERRORMESSAGE0);
								return  returnMap;
							} catch (Exception e) {
								e.printStackTrace();
								returnMap.put("errorcode", Globals.ERRORCODE7004);
								returnMap.put("errormessage", Globals.ERRORMESSAGE7004);
								return returnMap;
							}
						}
					}
				}
			}
		}
	}
}
