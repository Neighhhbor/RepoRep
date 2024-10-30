package com.hxxdemo.plug.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.config.Globals;
import com.hxxdemo.plug.service.AddresseeService;
import com.hxxdemo.plug.service.PlugService;
import com.hxxdemo.sysLogin.entity.TokenEntity;
import com.hxxdemo.sysLogin.service.TokenService;

@Controller
@RequestMapping(value="address")
public class AddressController {
	@Autowired
	private TokenService tokenService;
	@Autowired
	private PlugService plugService;
	@Autowired
	private AddresseeService addresseeService;
	
	@RequestMapping(value="/createAddress")
	@ResponseBody
	public Map<String,Object> createAddress(String token,String telephone,String mobile ,String addressee,String address ,String remark){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == token || token.equals("")) {
			returnMap.put("errorcode", Globals.ERRORCODE7001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7001);
			return returnMap;
		}
		if(null == telephone || telephone.equals(""))  {
			returnMap.put("errorcode", Globals.ERRORCODE1013);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
			return returnMap;
		}
		if(null == mobile || "".equals(mobile) 
				|| null == addressee || "".equals(addressee) 
				|| null == address || "".equals(address)
				|| null == remark || "".equals(remark)) {
			returnMap.put("errorcode", Globals.ERRORCODE9014);
			returnMap.put("errormessage", Globals.ERRORMESSAGE9014);
		}
		//验证token令牌是否过期
		TokenEntity tokenEntity = tokenService.queryByToken(token);
		if(null != tokenEntity) {
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
					int level = plugService.getPlugVipLevel(userId) ;
					boolean bool = plugService.isVipShort(userId);
					//vip等级是否足够 临时vip不能添加
					if (level >= 2 && !bool) {
						//是否有该信息了
						String type = addresseeService.getAddresseeType(userId);
						if(null == type) {
							try {
								addresseeService.createAddressee(userId, addressee, address, mobile, remark);
								returnMap.put("errorcode", Globals.ERRORCODE0);
								returnMap.put("errormessage", Globals.ERRORMESSAGE0);
								return returnMap;
							} catch (Exception e) {
								returnMap.put("errorcode", Globals.ERRORCODE4001);
								returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
								return returnMap;
							}
						}else {
							returnMap.put("errorcode", Globals.ERRORCODE4001);
							returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
							return returnMap;
						}
					}else {
						returnMap.put("errorcode", Globals.ERRORCODE4001);
						returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
						return returnMap;
					}
				}
			}
		}else {
			returnMap.put("errorcode", Globals.ERRORCODE7004);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7004);
			return returnMap;
		}
	}
	/**
	 * 获取收货状态信息
	 * @param token
	 * @param telephone
	 * @return
	 */
	@RequestMapping(value="/getAddressType")
	@ResponseBody
	public Map<String, Object> getAddressType(String token ,String telephone){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == token || token.equals("")) {
			returnMap.put("errorcode", Globals.ERRORCODE7001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7001);
			return returnMap;
		}
		if(null == telephone || telephone.equals(""))  {
			returnMap.put("errorcode", Globals.ERRORCODE1013);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
			return returnMap;
		}
		//验证token令牌是否过期
		TokenEntity tokenEntity = tokenService.queryByToken(token);
		if(null != tokenEntity) {
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
					returnMap.put("errorcode", Globals.ERRORCODE0);
					returnMap.put("errormessage", Globals.ERRORMESSAGE0);
					Map<String, Object> param = new HashMap<String,Object>();
					int level = plugService.getPlugVipLevel(userId) ;
					if (level >= 2) {
						param.put("isdisplay", true);
						//
						String type = addresseeService.getAddresseeType(userId);
						if (null == type ) {
							type = "null";
						}
						
						param.put("type", type);
						returnMap.put("info", param);
						return returnMap;
					}else {
						param.put("isdisplay", false);
						returnMap.put("info", param);
						return returnMap;
					}
				}
			}
		}else {
			returnMap.put("errorcode", Globals.ERRORCODE7004);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7004);
			return returnMap;
		}
	}
	/**
	 * 获取收件人信息
	 * @param token
	 * @param telephone
	 * @return
	 */
	@RequestMapping(value="/getAddressMessage")
	@ResponseBody
	public Map<String, Object> getAddresseeMessage(String token,String telephone){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == token || token.equals("")) {
			returnMap.put("errorcode", Globals.ERRORCODE7001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7001);
			return returnMap;
		}
		if(null == telephone || telephone.equals(""))  {
			returnMap.put("errorcode", Globals.ERRORCODE1013);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
			return returnMap;
		}
		//验证token令牌是否过期
		TokenEntity tokenEntity = tokenService.queryByToken(token);
		if(null != tokenEntity) {
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
					Map<String, Object> param = addresseeService.getAddresseeMessage(userId);
					returnMap.put("errorcode", Globals.ERRORCODE0);
					returnMap.put("errormessage", Globals.ERRORMESSAGE0);
					returnMap.put("info", param);
					return returnMap;
				}
			}
		}else {
			returnMap.put("errorcode", Globals.ERRORCODE7004);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7004);
			return returnMap;
		}
	}
	/**
	 * 修改收件人信息
	 * @param token
	 * @param telephone
	 * @return
	 */
	@RequestMapping(value="/updateAddresseeMessage")
	@ResponseBody
	public Map<String, Object> updateAddresseeMessage(String token,String telephone,String mobile ,String addressee,String address ,String remark){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == token || token.equals("")) {
			returnMap.put("errorcode", Globals.ERRORCODE7001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7001);
			return returnMap;
		}
		if(null == telephone || telephone.equals(""))  {
			returnMap.put("errorcode", Globals.ERRORCODE1013);
			returnMap.put("errormessage", Globals.ERRORMESSAGE1013);
			return returnMap;
		}
		if(null == mobile || "".equals(mobile) 
				|| null == addressee || "".equals(addressee) 
				|| null == address || "".equals(address)
				|| null == remark || "".equals(remark)) {
			returnMap.put("errorcode", Globals.ERRORCODE9014);
			returnMap.put("errormessage", Globals.ERRORMESSAGE9014);
		}
		//验证token令牌是否过期
		TokenEntity tokenEntity = tokenService.queryByToken(token);
		if(null != tokenEntity) {
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
					int level = plugService.getPlugVipLevel(userId) ;
					//vip等级是否足够
					if (level >= 2) {
						//是否有该信息了
						String type = addresseeService.getAddresseeType(userId);
						if("0".equals(type)) {
							try {
								addresseeService.updateAddressee(userId, addressee, address, mobile, remark); 
								returnMap.put("errorcode", Globals.ERRORCODE0);
								returnMap.put("errormessage", Globals.ERRORMESSAGE0);
								return returnMap;
							} catch (Exception e) {
								e.printStackTrace();
								returnMap.put("errorcode", Globals.ERRORCODE4001);
								returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
								return returnMap;
							}
						}else {
							returnMap.put("errorcode", Globals.ERRORCODE4001);
							returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
							return returnMap;
						}
					}else {
						returnMap.put("errorcode", Globals.ERRORCODE4001);
						returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
						return returnMap;
					}
				}
			}
		}else {
			returnMap.put("errorcode", Globals.ERRORCODE7004);
			returnMap.put("errormessage", Globals.ERRORMESSAGE7004);
			return returnMap;
		}
	}
}
