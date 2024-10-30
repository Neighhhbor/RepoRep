package com.hxxdemo.wechat.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.config.Globals;
import com.hxxdemo.wechat.service.AccessTokenService;
import com.hxxdemo.wechat.service.AddressService;
import com.hxxdemo.wechat.service.TicketService;
import com.hxxdemo.wechat.service.WechatUserService;
import com.hxxdemo.wechat.entity.WechatUser;
import com.hxxdemo.wechat.util.WechatUtil;
import com.hxxdemo.weixinsaomalogin.util.WechatConfigLoader;

import jdk.nashorn.internal.objects.Global;

import com.aliyuncs.utils.StringUtils;
import net.sf.json.JSONArray;

import net.sf.json.JSONObject;


@Controller
@RequestMapping(value = "wechat")
public class AccessTokenController {
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AccessTokenController.class);
	
	@Autowired
	private AccessTokenService accessTokenService;
	
	@Autowired
	private TicketService ticketService;
	
	@Autowired
	private WechatUserService wechatUserService;
	
	@Autowired
	private AddressService addressService;
	
	/**
	 * 获取普通access_token
	 * @return
	 */
	@RequestMapping(value ="accessToken")
	@ResponseBody
	public Map<String, Object> accessToken(){
		Map<String, Object> returnMap = new HashMap<String,Object>();
		String access_token= accessTokenService.getAccessToken(WechatConfigLoader.getGappId(), WechatConfigLoader.getGappSecret(),false).getToken();
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", access_token);
		return returnMap;
	}
	
	/**
	 * 通过code获取网页授权accessToken
	 * @param code
	 * @return
	 */
	public JSONObject getUserInfoAccessToken(String code) {
		String url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
				WechatConfigLoader.getGappId(), WechatConfigLoader.getGappSecret(), code);
		JSONObject jsonObject = WechatUtil.httpsRequest(url, "GET", null);
		return jsonObject;
	}
	/**
	 * 获取微信用户信息
	 * @param accessToken
	 * @param openId
	 * @return
	 */
	public JSONObject getUserInfo(String accessToken, String openId) {
		String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId + "&lang=zh_CN";
		JSONObject jsonObject = WechatUtil.httpsRequest(url, "GET", null);
		return jsonObject;
	}
	/**
	 * jssdk获取签名
	 * @param url
	 * @return
	 */
	@RequestMapping("signature")
	@ResponseBody
	public Map<String, Object> signature (String url){
		Map<String, Object> returnMap = new HashMap<String,Object>();
		//查询数据库 ticket
		String ticket = null;
		ticket = ticketService.getTicket();
		if (null == ticket ) {
			ticketService.editExpires_in();
			String access_token = accessTokenService.getAccessToken(WechatConfigLoader.getGappId(), WechatConfigLoader.getGappSecret(),false).getToken();
			String jsapi_ticketURL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
			String requestUrl = jsapi_ticketURL.replace("ACCESS_TOKEN", access_token);
			//得到json对象
			JSONObject jsonObject = WechatUtil.httpsRequest(requestUrl, "GET", null);
			String expires_in = jsonObject.getString("expires_in");
			ticket = jsonObject.getString("ticket");
			ticketService.insertTicket(ticket,expires_in);
			
		}
		String timestamp = create_timestamp();
		String noncestr = create_nonce_str();
		String string1 = "jsapi_ticket=" + ticket + "&noncestr=" + noncestr
		        + "&timestamp=" + timestamp + "&url=" + url;
		String signature = "";
		try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
		returnMap.put("url", url);
		returnMap.put("jsapi_ticket", ticket);
		returnMap.put("appid", WechatConfigLoader.getGappId());
		returnMap.put("nonceStr", noncestr);
		returnMap.put("timestamp", timestamp);
		returnMap.put("signature", signature);
		return returnMap;
	}
	/**
	 * 网页授权回调方法 授权获取微信用户信息
	 * @param code
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("auth")
	@ResponseBody
	public Map<String, Object> auth(String code) throws IOException{
		Map<String, Object> returnMap = new HashMap<String,Object>();
		if (null == code ) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		JSONObject jsonObject = getUserInfoAccessToken(code);//通过这个code获取access_token
        String openId = jsonObject.getString("openid");
        if (StringUtils.isNotEmpty(openId)) {
        	JSONObject json = getUserInfo(jsonObject.getString("access_token"), openId);//使用access_token获取用户信息
        	WechatUser wechatUser = new WechatUser();
        	wechatUser.setCity(json.getString("city"));
        	wechatUser.setCountry(json.getString("country"));
        	wechatUser.setHeadimgurl(json.getString("headimgurl"));
        	wechatUser.setLanguage(json.getString("language"));
        	wechatUser.setNickname(json.getString("nickname"));
        	wechatUser.setOpenid(json.getString("openid"));
        	wechatUser.setProvince(json.getString("province"));
        	wechatUser.setSex(json.getInt("sex"));
        	JSONArray privileges = json.getJSONArray("privilege");
        	String privilege = "";
        	for (int i = 0; i < privileges.size(); i++) {
				if (i==0) {
					privilege = privileges.getString(i);
				}else {
					privilege = ","+privileges.getString(i);
				}
			}
        	wechatUser.setPrivilege(privilege);
        	//更新用户
        	wechatUserService.updateWechatUser(wechatUser);
        	returnMap.put("errorcode", Globals.ERRORCODE0);
        	returnMap.put("errormessage", Globals.ERRORMESSAGE0);
        	returnMap.put("info", openId);
        	return returnMap;
        }else {
        	returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
        }
	}
	
	/**
	 * 创建收件人信息
	 * @param openid
	 * @param username
	 * @param addressee
	 * @param address
	 * @param mobile
	 * @param remark
	 * @return
	 */
	@RequestMapping("createAddress")
	@ResponseBody
	public Map<String, Object> createAddress(String openid,String username,String addressee,String address,String mobile,String remark){
		Map<String, Object> returnMap = new HashMap<String,Object>();
		if (null == openid) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		if(null == mobile || "".equals(mobile) 
				|| null == addressee || "".equals(addressee) 
				|| null == address || "".equals(address)
				|| null == address || "".equals(address)
				|| null == username || "".equals(username)) {
			returnMap.put("errorcode", Globals.ERRORCODE9014);
			returnMap.put("errormessage", Globals.ERRORMESSAGE9014);
		}
		if (wechatUserService.isWechatUser(openid)==0) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		String type = addressService.getAddressType(openid);
		if(null == type) {
			try {
				addressService.createAddress(openid, addressee, address, mobile, remark,username);
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
	}
	/**
	 * 修改收件人信息
	 * @param openid
	 * @param username
	 * @param addressee
	 * @param address
	 * @param mobile
	 * @param remark
	 * @return
	 */
	@RequestMapping(value="/updateAddresseeMessage")
	@ResponseBody
	public Map<String, Object> updateAddresseeMessage(String openid,String username,String addressee,String address,String mobile,String remark) {
		Map<String, Object> returnMap = new HashMap<String,Object>();
		if (null == openid) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		if(null == mobile || "".equals(mobile) 
				|| null == addressee || "".equals(addressee) 
				|| null == address || "".equals(address)
				|| null == address || "".equals(address)
				|| null == username || "".equals(username)) {
			returnMap.put("errorcode", Globals.ERRORCODE9014);
			returnMap.put("errormessage", Globals.ERRORMESSAGE9014);
		}
		if (wechatUserService.isWechatUser(openid)==0) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		String type = addressService.getAddressType(openid);
		if("0".equals(type)) {
			try {
				addressService.updateAddress(openid, addressee, address, mobile, remark,username); 
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
	}
	/**
	 * 获取收件人信息
	 * @param openid
	 * @return
	 */
	@RequestMapping(value="/getAddressMessage")
	@ResponseBody
	public Map<String, Object> getAddresseeMessage(String openid){
		Map<String, Object> returnMap = new HashMap<String,Object>();
		if (null == openid) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		if (wechatUserService.isWechatUser(openid)==0) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		Map<String, Object> param = addressService.getAddressMessage(openid);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", param);
		
		return returnMap;
	}
	/**
	 * 获取收货状态信息
	 * @param openid
	 * @return
	 */
	@RequestMapping(value="/getAddressType")
	@ResponseBody
	public Map<String, Object> getAddressType(String openid){
		Map<String, Object> returnMap = new HashMap<String,Object>();
		if (null == openid) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		if (wechatUserService.isWechatUser(openid)==0) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		Map<String, Object> param = new HashMap<String,Object>();
		String type = addressService.getAddressType(openid);
		if (null == type ) {
			type = "null";
		}
		
//		param.put("isdisplay", true);
//		param.put("type", type);
		returnMap.put("info", type);
		return returnMap;
	}
	
	
	/**
	 * 创建时间戳
	 * @return
	 */
	private static String create_timestamp() {
	    return Long.toString(System.currentTimeMillis() / 1000);
	}
	/**
	 * 创建随机数
	 * @return
	 */
	private static String create_nonce_str() {
	    return UUID.randomUUID().toString();
	}
	/**
	 * 签名
	 * @param hash
	 * @return
	 */
	 private static String byteToHex(final byte[] hash) {
	        Formatter formatter = new Formatter();
	        for (byte b : hash) {
	            formatter.format("%02x", b);
	        }
	        String result = formatter.toString();
	        formatter.close();
	        return result;
	    }

}
