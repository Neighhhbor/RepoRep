package com.hxxdemo.cooperation.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;

import com.hxxdemo.util.BaiduConvertData;
import com.hxxdemo.util.ConversionType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.config.Globals;
import com.hxxdemo.cooperation.service.CooperationService;
import com.hxxdemo.task.dingding.DingdingUtils;
import com.hxxdemo.task.dingding.TextEntity;
import com.taobao.api.ApiException;

@Controller
@RequestMapping("cooperation")
public class CooperationController {
//	private static String webhook = "https://oapi.dingtalk.com/robot/send?access_token=9ca77db67a504fac520175fcc3f4f9caa30ebbad38b48b7a9f45bc40bb3fe5f1";
	private static String webhook = "https://oapi.dingtalk.com/robot/send?access_token=7f4671aa6d6157a4aeb5ef31bfb3eed2d40c1a118e91c30ddce840bc3a121ad0";
	@Autowired
	private CooperationService service;
	@RequestMapping("company")
	@ResponseBody
	public Map<String, Object> enterprise(
			HttpServletRequest request,
			@RequestParam String company,
			@RequestParam String name ,
			@RequestParam String position ,
			@RequestParam String email ,
			@RequestParam String contacts,
			@RequestParam String require,
			String staffNum,
			String logidUrl){
		Map<String,Object> returnMap =  new HashMap<String,Object>();
		String ip = getIpAddress(request);
		Map<String,Object> params =  new HashMap<String,Object>();
		params.put("company",company);
		params.put("position",position);
		params.put("email",email);
		params.put("name",name);
		params.put("contacts", contacts);
		params.put("require", require);
		params.put("ip", ip);
		params.put("staff_num", StringUtils.isNotBlank(staffNum) ? staffNum :"0");
		service.insertCooperation(params);
		TextEntity textEntity = new TextEntity();
		textEntity.setIsAtAll(true);
		textEntity.setContent("有新的企业合作信息，请注意查看！\n"
				+ "姓名："+name+"\n"
				+ "邮箱："+email+"\n"
				+ "职位："+position+"\n"
				+ "公司名称："+company+"\n"
				+ "联系方式："+contacts+"\n"
				+ "需求："+require+"\n"
				);
		if(StringUtils.isNotBlank(logidUrl)){
			// 异步执行
			CompletableFuture.runAsync(new ConversionTask(logidUrl));
		}
		try {
			boolean bool = sendTextMessage(textEntity);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
	}
	public static String getIpAddress(HttpServletRequest request) {		
		String ip = request.getHeader("x-forwarded-for");		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {			
			ip = request.getHeader("Proxy-Client-IP");		
		}		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {			
			ip = request.getHeader("WL-Proxy-Client-IP");		
		}		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {	
			ip = request.getHeader("HTTP_CLIENT_IP");		
		}		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {	
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");		
		}		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {	
			ip = request.getRemoteAddr();		
		}		
		return ip;
	}
	public static boolean sendTextMessage(TextEntity text) throws ApiException {
		return DingdingUtils.sendToDingding(text,webhook);
	}

}

class ConversionTask implements Runnable{

	private String logidUrl;

	public ConversionTask(String logidUrl) {
		this.logidUrl = logidUrl;
	}

	@Override
	public void run() {
		ConversionType cv = new ConversionType();
		cv.setLogidUrl(logidUrl); // 设置落地页url
		cv.setConvertType(3); // 设置转化类型
		List<ConversionType> conversionTypes = new ArrayList<>();
		conversionTypes.add(cv);
		// 重试3次、每次间隔1分钟
		for (int i = 0; i < 3; i++) {
			Boolean sendConvertData = BaiduConvertData.sendConvertData(BaiduConvertData.getToken(), conversionTypes);
			if (sendConvertData) {
				break;
			}
			// 等待1分钟
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
