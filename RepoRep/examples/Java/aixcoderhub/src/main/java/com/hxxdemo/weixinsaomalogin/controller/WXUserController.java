package com.hxxdemo.weixinsaomalogin.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.weixinsaomalogin.entity.SNSUserInfo;
import com.hxxdemo.weixinsaomalogin.service.WxAccessTokenService;
import com.hxxdemo.weixinsaomalogin.service.WxUserService;
import com.hxxdemo.weixinsaomalogin.util.AccessToken;
import com.hxxdemo.weixinsaomalogin.util.CommonUtil;
import com.hxxdemo.weixinsaomalogin.util.WechatConfigLoader;
import com.hxxdemo.weixinsaomalogin.util.WeiXinUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "/wechat")
public class WXUserController {

	@Autowired
	private WxUserService wxUserService;
	@Autowired
	private WxAccessTokenService wxAccessTokenService;
	
	/**
	 * 更新关注用户
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/updateUser")
	@ResponseBody
	public Map<String,Object> updateUser(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = new HashMap<String,Object>();
		AccessToken token = wxAccessTokenService.getAccessToken(WechatConfigLoader.getGappId(), WechatConfigLoader.getGappSecret(),false);
		
		//获取关注用户列表 没有next_openid参数 可以拉取10000个用户 有next_openid 表示拉取后面的用户
//		String url = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN";
//		String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=owsPU04YcMbmsLFY5kDPw0lMHjRo";
		String url = WeiXinUtil.users_url;
		url = url.replace("ACCESS_TOKEN", token.getToken());
		url = url.replaceAll("OPENID", WechatConfigLoader.getGappId());
		//得到json对象
		JSONObject jsonObject = CommonUtil.httpsRequest(url, "GET", null);
		int total = jsonObject.getInt("total");
		int count = jsonObject.getInt("count");
		String next_openid = jsonObject.getString("next_openid");
		String accessToken = token.getToken();
		if(total >0) {
			//全部取消关注
			wxUserService.wxUserAllCancelFollow();
			List<String> openids = (List<String>)jsonObject.getJSONObject("data").get("openid");
			updateWXUsers(total, count, next_openid, openids, url, accessToken);
			map.put("success", true);
			map.put("msg", "更新完毕！");
		}else {
			map.put("success", false);
			map.put("msg", "还没有关注者哦……");
		}
		return map;
	}
	public void updateWXUsers(int total,int count,String next_openid,List<String> openids,String url,String accessToken) {
		for(int i = 0 ; i < openids.size() ; i++) {
//			url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
			url = WeiXinUtil.userinfo_url;
			url = url.replace("ACCESS_TOKEN", accessToken);
			url = url.replaceAll("OPENID", openids.get(i));
			//得到json对象
			JSONObject jsonUser = CommonUtil.httpsRequest(url, "GET", null);
			
			SNSUserInfo snsUserInfo= new SNSUserInfo();
			snsUserInfo.setOpenId(jsonUser.getString("openid"));
			snsUserInfo.setNickname(jsonUser.getString("nickname"));
			snsUserInfo.setSex(jsonUser.getInt("sex"));
			snsUserInfo.setCountry(jsonUser.getString("country"));
			snsUserInfo.setProvince(jsonUser.getString("province"));
			snsUserInfo.setCity(jsonUser.getString("city"));
			snsUserInfo.setHeadImgUrl(jsonUser.getString("headimgurl"));
			snsUserInfo.setUnionid(jsonUser.getString("unionid"));
			snsUserInfo.setRemark(jsonUser.getString("remark"));
			snsUserInfo.setIsfollow(1);
			snsUserInfo.setSubscribetime(jsonUser.getString("subscribe_time"));
			//验证用户是否存在
			int ubool = wxUserService.queryWxUserCountByUnionid(snsUserInfo.getUnionid());
			if(ubool >0) {
				//执行update
				wxUserService.updateWxUser(snsUserInfo);
			}else{
				//执行insert
				wxUserService.insertWxUser(snsUserInfo);
			}
		}
		if(total>count) {
			url = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID";
			url = url.replace("NEXT_OPENID", next_openid);
			//得到json对象
			JSONObject jsonObject = CommonUtil.httpsRequest(url, "GET", null);
			count += jsonObject.getInt("count");
			next_openid = jsonObject.getString("next_openid");
			openids = (List<String>)jsonObject.getJSONObject("data").get("openid");
			updateWXUsers(total, count, next_openid, openids, url, accessToken);
		}
	}
	/**
	 * 修改备注名
	 * @param openid string 公众号openid 必须
	 * @param remark string 备注名 必须
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/updateWxUserRemark")
	@ResponseBody
	public Map<String,Object> updateWxUserRemark(String openid ,String remark) throws IOException{
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == openid || "".equals(openid)) {
			map.put("success", false);
			map.put("msg", "关注者openid不能为空！");
			return map;
		}
		if(null == remark || "".equals(remark)) {
			map.put("success", false);
			map.put("msg", "备注名不能为空！");
			return map;
		}
		JSONObject json = new JSONObject();
		json.put("openid", openid);
		json.put("remark", remark);
		if(editRemark(json.toString())>0) {
			//存入数据库 修改用户信息
			SNSUserInfo snsUserInfo = new SNSUserInfo();
			snsUserInfo.setOpenId(openid);
			snsUserInfo.setRemark(remark);
			wxUserService.updateWxUserRemark(snsUserInfo);
			map.put("success", true);
			map.put("msg", "修改成功");
		}else {
			map.put("success", false);
			map.put("msg", "修改失败");
		}
		return map;
	}
	public int editRemark(String openidRemarkJson) throws IOException {
		String access_token= wxAccessTokenService.getAccessToken(WechatConfigLoader.getGappId(), WechatConfigLoader.getGappSecret(),false).getToken();
		String action = "https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token="+access_token;
		try {
			URL url = new URL(action);
			HttpURLConnection http = (HttpURLConnection) url.openConnection(); 
	
			http.setRequestMethod("POST"); 
			http.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
			http.setDoOutput(true); 
			http.setDoInput(true);
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");//连接超时30秒
			System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //读取超时30秒
	
			http.connect();
			OutputStream os= http.getOutputStream(); 
			os.write(openidRemarkJson.getBytes("UTF-8"));//传入参数 
			os.flush();
			os.close();
	
			InputStream is =http.getInputStream();
			int size =is.available();
			byte[] jsonBytes =new byte[size];
			is.read(jsonBytes);
			String message=new String(jsonBytes,"UTF-8");
			System.out.println(message);
			JSONObject json = JSONObject.fromObject(message);
			if(json.get("errcode").toString().equals("0")) {
				return 1;
			}else {
				return 0;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		} 
	}
	/**
	 * 查询微信公众号关注者分页列表
	 * @param rows int 行数 非必须
	 * @param page int 页数 非必须
	 * @param nickname string 昵称 或者 备注名 非必须
	 * @return
	 */
	@RequestMapping("/queryWxUserList")
	@ResponseBody
	public Map<String,Object> queryWxUserList(Integer rows,Integer page,String nickname){
		Map<String,Object> map = new HashMap<String,Object>();
		//初始化参数行数
		if(null == rows || "".equals(rows.toString())) {
			rows =  10 ;
		}
		//初始化参数页码
		if(null == page || "".equals(page.toString())) {
			page =  1 ;
		}
		Map<String ,Object> params = new HashMap<String,Object>();
		params.put("rows", rows);
		params.put("page", (page-1)*rows);
		if(null != nickname && !"".equals(nickname)) {
			params.put("nickname", nickname);
		}
		List<Map<String,Object>> list = wxUserService.queryWxUserList(params);
		int count = wxUserService.countWxUserList(params);
		map.put("success", true);
		map.put("count", count);
		map.put("data", list);
		return map;
	} 
	/**
	 * 查询微信公众号关注者分页列表
	 * @param id string 用户id
	 * @return
	 */
	@RequestMapping("/oneWxUserInfo")
	@ResponseBody 
	public Map<String,Object> oneWxUserInfo(String id){
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == id || "".equals(id)) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
		}
		Long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		Map<String,Object> reMap = wxUserService.oneWxUserInfo(rid);
		if(null == reMap || reMap.size() ==0) {
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		map.put("success", true);
		map.put("data", reMap);
		return map;
	}
}
