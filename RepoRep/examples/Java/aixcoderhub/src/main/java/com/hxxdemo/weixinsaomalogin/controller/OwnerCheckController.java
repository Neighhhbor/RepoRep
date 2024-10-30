package com.hxxdemo.weixinsaomalogin.controller;

import com.hxxdemo.weixinsaomalogin.util.WechatConfigLoader;
import com.hxxdemo.weixinsaomalogin.util.WechatOAuthProcessUtil;
import com.alibaba.fastjson.JSON;
import com.hxxdemo.weixinsaomalogin.dao.WxUserDao;
import com.hxxdemo.weixinsaomalogin.entity.SNSUserInfo;
import com.hxxdemo.weixinsaomalogin.entity.WechatAccessTokenVo;
import com.hxxdemo.weixinsaomalogin.entity.WechatSNSUserInfoVo;
import com.hxxdemo.weixinsaomalogin.service.CoreService;
import com.hxxdemo.weixinsaomalogin.service.WxUserService;
import com.hxxdemo.weixinsaomalogin.util.CheckoutUtil;
import com.hxxdemo.weixinsaomalogin.util.MessageUtil;
import com.hxxdemo.weixinsaomalogin.util.WeiXinUtil;
import com.hxxdemo.weixinsaomalogin.util.WeixinOauth2Token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/index")
public class OwnerCheckController {
	
	@Autowired
	private CoreService coreService;
	/**
	 * 微信消息接收和token验证
	 * @param model
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/ownerCheck")
	public void ownerCheck( HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws IOException {
		boolean isGet = request.getMethod().toLowerCase().equals("get");
		boolean isPost = request.getMethod().toLowerCase().equals("post");
		PrintWriter print;
		if (isGet) {
			// 微信加密签名
			String signature = request.getParameter("signature");
			// 时间戳
			String timestamp = request.getParameter("timestamp");
			// 随机数
			String nonce = request.getParameter("nonce");
			// 随机字符串
			String echostr = request.getParameter("echostr");
			// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
			if (signature != null && CheckoutUtil.checkSignature(signature, timestamp, nonce)) {
				try {
					print = response.getWriter();
					print.write(echostr);
					print.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(isPost){
//			String responseMessage = coreService.processRequest(request);
//			responseMessage=new String(responseMessage.getBytes(), "iso8859-1");
//			out.print(responseMessage);
//			out.flush();
			String message="";
           	message = coreService.processRequest(request);
           	if(null == message) {
           		message = "";
           	}
           	message=new String(message.getBytes(), "iso8859-1");
           	out.print(message);
           	out.flush();
		}
	}
	
//    //返回微信二维码，可供扫描登录
//    @RequestMapping(value = "weixin")
//    @ResponseBody
//    public Map<String,Object> weixin(HttpServletRequest request) throws IOException {
//        Map<String,Object> map = new HashMap<String,Object>();
//        WeiXinUtil wxU = new WeiXinUtil();
//        //http://www.shike.com.cn/index/WeiXinTest是手机用户扫码后会执行的后台方法
//        //www.shike.com.cn此域名要与微信公共平台配置的一致
//        String url="https://open.weixin.qq.com/connect/oauth2/authorize?" +
//                "appid=wx5cae020f59520ad1&" +
//                "redirect_uri=http://weihe.tunnel.echomod.cn/index/WeiXinTest&" +
//                "response_type=code&scope=snsapi_userinfo&state=123#wechat_redirect";
//        //将url转换成短链接，提高扫码速度跟成功率
//        String shorturl=wxU.shortURL(url, wxU.appid, wxU.appSecret);
//        map.put("shorturl", shorturl);
//        return map;
//    }
//
//    int type = 0;
//    String type2 = "";
//    //判断用户是否扫码登录成功，以便于前台页面跳转
//    @RequestMapping(value = "successDL")
//    @ResponseBody
//    public Map<String,Object> successDL(String a,HttpServletRequest req){
//        Map<String,Object> map = new HashMap<String,Object>();
//        try
//        {
//            synchronized (this) {
//                map.put("type", type);
//                type=0;
//            }
//            Thread.sleep(500);
//        }
//        catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        }
//
//        return map;
//    }
//
//    @RequestMapping(value = "type")
//    public void type(int a){
//        type=a;
//    }
//
//    //微信获取用户信息
//    @RequestMapping(value = "WeiXinTest")
//    public ModelAndView WeiXinTest(ModelMap map, HttpServletRequest request, HttpServletResponse response) throws IOException{
//        ModelAndView mav = new ModelAndView();
//        request.setCharacterEncoding("utf-8");
//        response.setCharacterEncoding("utf-8");
//        String code = request.getParameter("code");
//        // 通过code获取access_token
//        WeixinOauth2Token oauth2Token =WeiXinUtil.getOauth2AccessToken(WeiXinUtil.appid, WeiXinUtil.appSecret, code);
//        System.out.println(oauth2Token.toString());
//        String accessToken=oauth2Token.getAccessToken();
//        String openId=oauth2Token.getOpenId();
//        //获取到用户的基本信息
//        SNSUserInfo snsUserInfo=null;
//        if(type==0){
//            snsUserInfo = WeiXinUtil.getSNSUserInfo(accessToken, openId);
//        }
//        if(snsUserInfo!=null){
//            //证明获取到了微信用户基本信息
//            type=1;
//            String id = snsUserInfo.getUnionid();
//            type2=id;//可用作用户id，此值是微信用户的唯一识别
////		   查询库中是否有user
//            /*逻辑写这*/
//            int boolInt = wxUserService.queryWxUserCountByUnionid(id);
//            if(boolInt==0) {
//            	wxUserService.insertWxUser(snsUserInfo);
//            }else {
//            	wxUserService.updateWxUser(snsUserInfo);
//            }
//
//            //给手机端返回页面，成功，此处无法给pc端进行页面跳转
//            mav.setViewName("info");
//        }else{
//            //给手机端返回页面，失败，此处无法给pc端进行页面跳转
//            mav.setViewName("info2");
//        }
//        return mav;
//    }
    
}
