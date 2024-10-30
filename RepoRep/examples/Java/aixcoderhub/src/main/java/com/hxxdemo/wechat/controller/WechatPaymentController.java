package com.hxxdemo.wechat.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.wxpay.sdk.WXPayUtil;
import com.hxxdemo.wechat.service.OrderService;
import com.ibm.icu.text.SimpleDateFormat;

@Controller
@RequestMapping(value = "wxpay")
public class WechatPaymentController {
	
	@Autowired
	private OrderService orderService;
	/**
     * 微信支付回调接口
     * 
     * @param request
     * @param response
     * @return
     */
	@RequestMapping(value = "callback", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public String returnNotify(HttpServletRequest request, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		String resXml = "";
		try {
			InputStream inputStream = request.getInputStream();
			//将InputStream转换成xmlString
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			resXml = sb.toString();
			
			//报文转map
			Map<String, String> params = WXPayUtil.xmlToMap(resXml);
			params.put("result", resXml);
			//保存至订单表
			orderService.saveOrder(params);
			
			HashMap<String, String> paramMap = new HashMap<String,String>();
			paramMap.put("return_code", "SUCCESS");
			paramMap.put("return_msg", "OK");
			String result = WXPayUtil.mapToXml(paramMap);
			return result;
		} catch (Exception e) {
			System.out.println("微信手机支付失败:" + e.getMessage());
			String result = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
			return result;
		}
	}
	/**
	  * 获得唯一订单号
	  */
	 public static String getUniqueOrder() {
		 SimpleDateFormat format = new SimpleDateFormat("YYYYMMddHHmmss");
		 String format2 = format.format(new Date());
		 int hashCodeV = UUID.randomUUID().toString().hashCode();  
		 if(hashCodeV < 0) {
			 //有可能是负数
			 hashCodeV = - hashCodeV;  
		 }
		 return "gxkj"+format2+String.format("%012d", hashCodeV);  
	 }
}
