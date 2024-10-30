package com.hxxdemo.wechatservice.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.wechatservice.service.CoreService;
import com.hxxdemo.wechatservice.util.SignUtil;

import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("wechatCon")
public class WXSController {
	@Autowired
	private CoreService service;

	@RequestMapping(value = "wechat",method = RequestMethod.GET)
	@ResponseBody
	public void get(HttpServletRequest request,HttpServletResponse response) {
//		System.out.println("get");
		// 微信加密签名
		String signature = request.getParameter("signature");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		// 随机字符串
		String echostr = request.getParameter("echostr");
  System.out.println(signature);
  System.out.println(timestamp);
  System.out.println(nonce);
  System.out.println(echostr);
        PrintWriter out = null;  
        try {  
            out = response.getWriter();  
            // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，否则接入失败  
            if (SignUtil.checkSignature(signature, timestamp, nonce)) {  
                out.print(echostr);  
            } 
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            out.close();  
            out = null;  
        }  
	}
	@RequestMapping(value = "wechat",method = RequestMethod.POST)
	@ResponseBody
	public void post(HttpServletRequest request,HttpServletResponse response) {
//		System.out.println("post");
		PrintWriter out = null;
		try {
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();
			String message="";
	       	message = service.processRequest(request);
	       	if(null == message) {
	       		message = "";
	       	}
	       	message=new String(message.getBytes(), "UTF-8");
	       	out.print(message);
	       	out.flush();
		}  catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();  
            out = null;  
        }  
		
	}
}
