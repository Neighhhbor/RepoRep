package com.hxxdemo.total.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.githubLogin.util.CommonUtil;
import com.hxxdemo.total.service.TotalTimesService;

@Controller
@RequestMapping(value = "/total")
public class TotalTimesController {

	@Autowired
	private TotalTimesService totalTimesService;
	@RequestMapping(value = "/click")
	@ResponseBody
	public void click(String model,HttpServletRequest request){
		if(null == model | "".equals(model)) {
			
		}else {
			//获取id
			String ip = CommonUtil.getIpAddr(request);
			System.out.println(ip);
			//查询当天是否包含此ip
			Map<String,Object> params = new HashMap<String,Object>();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			params.put("create_time", df.format(new Date()));
			params.put("ip", ip);
			params.put("model", model);
			int isIp = totalTimesService.isIp(params);
			//不包含
			if(isIp == 0) {
				//模型计数
				totalTimesService.initCountModel(params);
			}
			totalTimesService.insertIp(params);
			//插入数据库
		}
		
	}
}
