package com.hxxdemo.wechatservice.controller;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.hxxdemo.config.Globals;
import com.hxxdemo.exception.utils.R;
import com.hxxdemo.index.service.QemailService;
import com.hxxdemo.plug.service.PlugService;
import com.hxxdemo.sysLogin.entity.TokenEntity;
import com.hxxdemo.sysLogin.service.TokenService;
import com.hxxdemo.wechatservice.service.CoreService;
import com.hxxdemo.weixinsaomalogin.util.CommonUtil;
import com.hxxdemo.weixinsaomalogin.util.WechatConfigLoader;

@Controller
public class WechatController {
	@Autowired
	private CoreService service;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private PlugService plugService;
	@Autowired
	private QemailService qemailService;

    //gxkj测试公众号
    private static final String app_id = WechatConfigLoader.getGappId();
    private static final String app_secret = WechatConfigLoader.getGappSecret();

    private static final Gson gson = new Gson();


    // 获取access_tocken
    private String getAccessToken() throws Exception{
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + app_id + "&secret=" + app_secret;
        Map<String, Object> accessTokenMap = CommonUtil.httpsRequest(url, "GET", null);
        return accessTokenMap.get("access_token").toString();
    }

    // 通过openid获取用户信息
    private Map<String, Object> getUserInfoByOpenid(String openid) throws Exception {
        String access_tocken = getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + access_tocken + "&openid=" + openid;
        Map<String, Object> map = CommonUtil.httpsRequest(url, "GET", null);
        return map;
    }

    // 生成带参数的二维码，扫描关注微信公众号，自动登录网站
    @RequestMapping(value = "/wechat/qrCode")
    @ResponseBody
    public Map<String,Object> qrCode(String retLanguage) throws Exception {
    	if (null == retLanguage || !retLanguage.equals("en")) {
			retLanguage = "zh";
		}
        String access_token = getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + access_token;
        String eventKey = retLanguage+"gxkj" + com.hxxdemo.util.CommonUtil.GUID();
        String params = "{\"expire_seconds\":60, \"action_name\":\"QR_STR_SCENE\", \"action_info\":{\"scene\":{\"scene_str\":\"" + eventKey + "\"}}}";
        Map<String, Object> resultMap = CommonUtil.httpsRequest(url, "POST", params);
        String qrcodeUrl = "";
        if (resultMap.get("ticket") != null) {
             qrcodeUrl= "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + resultMap.get("ticket");
        }
       service.insertWechatEventKey(retLanguage, null, eventKey);
        return R.ok().put("eventKey", eventKey).put("qrcodeUrl", qrcodeUrl);
    }

    // 检测登录
    @RequestMapping("/wechat/checkEventKey")
    public @ResponseBody Map<String, Object> wechatMpCheckLogin(@RequestParam String eventKey,String loginId){
        // 根据scene_str查询数据库，获取对应记录
    	Map<String,Object> eventMap = service.checkEventKey(eventKey);
    	if (null != eventMap) {
    		Object wechatId = eventMap.get("wechatid");
    		if (null == wechatId) {
    			return R.ok().put("result", 0);
			}
    		String wechatid = eventMap.get("wechatid").toString();
			
			//检查是否绑定手机号或邮箱
			Map<String,Object> user = service.getUserIdByWechatId(wechatid);
			Map<String,Object> info = new HashMap<String, Object>();
			if (null == user) {
				//未绑定
				info.put("isBinding", false);
			}else {
				Long userId = Long.valueOf(user.get("id").toString());
				String uuid = user.get("uuid").toString();
				String username = null == user.get("telephone")? user.get("email").toString():user.get("telephone").toString();
				if (null!=loginId && !loginId.equals("")) {
					//检查是否登录过
					int isLogin = plugService.getPlugLoginId(loginId);
					if (isLogin == 0 ) {
						//添加loginId保存15分钟
						plugService.savePlugLoginId(loginId,username,userId,Globals.LOGINID_EXPIRETIME);
					}
				}
				info.put("isBinding", true);
				String token ="";//用户过期token
				//检查是否有token令牌
				TokenEntity istokenEntity = tokenService.queryPlugToken(userId);
				if(null == istokenEntity) {
					//创建token
					TokenEntity tokenEntity = tokenService.createToken(userId);
					token = tokenEntity.getToken();
				}else {
					token = istokenEntity.getToken();
					Map<String,Object> params = new HashMap<String,Object>();
					params.put("time", Globals.WEBTOKENTIME);
					params.put("token", token);
					tokenService.updatePlugToken(params);
				}
				int level =  plugService.getPlugVipLevel(userId) ;
				info.put("uuid", uuid);
				info.put("token", token);
				info.put("level", level);
				info.put("username", username);
			}
			return R.ok().put("result", 1).put("info", info);
		}else {
			return R.ok().put("result", 2);
		}
    }
    //微信绑定用户（手机号或邮箱）
    @RequestMapping("/wechat/wechatBindingUser")
    @ResponseBody
    public R wechatBindingUser(@RequestParam String username,@RequestParam String eventkey,@RequestParam String code,String loginId) {
    	Map<String,Object> params = new HashMap<>();
    	if (null == code || code.equals("")) {
			return R.error(Globals.ERRORCODE1009, Globals.ERRORCODE1009);
		}
		if (null == username || username.equals("")) {
			return R.error(Globals.ERRORCODE1013,Globals.ERRORMESSAGE1013);
		}
    	params.put("code", code);
    	params.put("username", username);
    	int type = 0;
    	if(username.contains("@")) {
			type = Globals.MSGCODETYPE7;
			params.put("type", type);
			params.put("email", username);
			//验证邮箱
			String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
			if(!match(regex,username)) {
				return R.error(Globals.ERRORCODE1007, Globals.ERRORMESSAGE1007);
			}
		}else {
			type = Globals.MSGCODETYPE6;
			params.put("type", type);
			params.put("email", username);
			if(username.length()!=11) {
				return R.error(Globals.ERRORCODE1014, Globals.ERRORMESSAGE1014);
			}
		}
    	if(code.trim().length()!=4) {
			return R.error(Globals.ERRORCODE1012, Globals.ERRORMESSAGE1012);
		}else {
			params.put("email", username);
			params.put("type", type);
			params.put("code", code);
			String id = qemailService.getEmailIdByMap(params);
			if(null == id) {
				return R.error(Globals.ERRORCODE1012, Globals.ERRORMESSAGE1012);
			}else {
				qemailService.updateIsapplyByCode(params);
			}
		}
    	//验证手机号或邮箱是否绑定了微信
    	Map<String,Object> userWechat = service.getUserWechatByUsername(username);
    	String wechatid = service.getWechatIdByEventKey(eventkey);
    	String isBindWechat = service.getWechatIdByWechatId(wechatid);
    	if(null != isBindWechat) {
    		return R.error(Globals.ERRORCODE1022,Globals.ERRORMESSAGE1022);
    	}
    	if (null == userWechat) {
    		//去新建用户
    		String uuid = UUID.randomUUID().toString().replace("-", "");
    		Map<String,Object> user = new HashMap<>();
    		user.put("wechatid", wechatid);
			user.put("uuid", uuid);
			if (type == Globals.MSGCODETYPE6) {
				//短信
				user.put("telephone", username);
			}else {
				user.put("email", username);
			}
			service.insertUser(user);
		}else {
			if (null != userWechat.get("wechatid")) {
				//已绑定用户
				return R.error(Globals.ERRORCODE1020,Globals.ERRORMESSAGE1020);
			}else{
				//更新用户
				Map<String,Object> user = new HashMap<>();
	    		user.put("wechatid", wechatid);
	    		user.put("id", userWechat.get("id"));
				service.updateUser(user);
			}
		}
    	userWechat = service.getUserWechatByUsername(username);
    	Long userId = Long.valueOf(userWechat.get("id").toString());
		String uuid = userWechat.get("uuid").toString();
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
			TokenEntity tokenEntity = tokenService.createToken(userId);
			token = tokenEntity.getToken();
		}else {
			token = istokenEntity.getToken();
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("time", Globals.WEBTOKENTIME);
			param.put("token", token);
			tokenService.updatePlugToken(param);
		}
		int level =  plugService.getPlugVipLevel(userId) ;
		Map<String,Object> info = new HashMap<String, Object>();
		info.put("uuid", uuid);
		info.put("token", token);
		info.put("level", level);
		info.put("username", username);
    	return R.ok().put("info", info);
    }
    //用户取消绑定微信
    @RequestMapping("/wechat/cancelBindingWechat")
    @ResponseBody
    public R cancelBindingWechat(@RequestParam String token) {
    	if (null == token || token.equals("")) {
    		return R.error(Globals.ERRORCODE7001,Globals.ERRORMESSAGE7001);
		}else {
			TokenEntity tokenEntity = tokenService.queryByToken(token);
			if (null == tokenEntity) {
				return R.error(Globals.ERRORCODE7004,Globals.ERRORMESSAGE7004);
			}else {
				service.cancelBindingWechat(token);
			}
		}
    	return R.ok();
    }

    private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
    }
    // xml转为map
    private Map<String, String> xmlToMap(HttpServletRequest httpServletRequest) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            InputStream inputStream = httpServletRequest.getInputStream();
            SAXReader reader = new SAXReader(); // 读取输入流
            org.dom4j.Document document = reader.read(inputStream);
            Element root = document.getRootElement(); // 得到xml根元素
            List<Element> elementList = root.elements(); // 得到根元素的所有子节点
            // 遍历所有子节点
            for (Element e : elementList)
                map.put(e.getName(), e.getText());
            // 释放资源
            inputStream.close();
            inputStream = null;
            return map;
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }
}