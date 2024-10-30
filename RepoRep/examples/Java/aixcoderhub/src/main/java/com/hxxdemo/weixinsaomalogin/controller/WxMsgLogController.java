package com.hxxdemo.weixinsaomalogin.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.weixinsaomalogin.resp.TextMessage;
import com.hxxdemo.weixinsaomalogin.service.MsglogService;
import com.hxxdemo.weixinsaomalogin.util.MessageUtil;
import com.hxxdemo.weixinsaomalogin.util.WechatConfigLoader;

@Controller
@RequestMapping(value = "/msgLog")
public class WxMsgLogController {

	@Autowired
	private MsglogService msglogService;
	
	/**
	 * 查询日志列表
	 * @param rows int 行数 非必须
	 * @param page int 页码 非必须
	 * @param title string 回复内容 非必须 
	 * @param nickname string 昵称或备注名 非必须 
	 * @param openid string 订阅号openid 必须
	 */
	@RequestMapping("/queryMsgLogList")
	@ResponseBody
	public Map<String,Object> queryMsgLogList(Integer rows,Integer page,String title ,String nickname){
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
		if(null != title && !"".equals(title)) {
			params.put("title", title);
		}
		if(null != nickname && !"".equals(nickname)) {
			params.put("nickname", nickname);
		}
		String originalId = WechatConfigLoader.getOriginalId();
		params.put("originalId", originalId);
		map.put("success", true);
		List<Map<String,Object>> list = msglogService.queryMsgLogList(params);
		int count = msglogService.countMsgLogList(params);
		map.put("success", true);
		map.put("data", list);
		map.put("count", count);
		return map;
	}
}
