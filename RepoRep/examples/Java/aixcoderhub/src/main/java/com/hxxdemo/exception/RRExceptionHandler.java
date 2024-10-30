/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.power.io
 *
 * 版权所有，侵权必究！
 */

package com.hxxdemo.exception;

import com.alibaba.fastjson.JSON;
import com.hxxdemo.exception.utils.R;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 异常处理器
 * 
 */
@Component
public class RRExceptionHandler implements HandlerExceptionResolver {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		R r = new R();
		try {
			response.setContentType("application/json;charset=utf-8");
			response.setCharacterEncoding("utf-8");
			
			if (ex instanceof RRException) {
				r.put("errorcode", ((RRException) ex).getCode());
				r.put("errormessage", ((RRException) ex).getMessage());
			}else if(ex instanceof DuplicateKeyException){
				r = R.error("数据库中已存在该记录");
			}else if(ex instanceof ClientAbortException) {
				logger.error("已知异常：java.io.IOException: Connection reset by peer");
			}else{
				r = R.error();
			}
			
			//记录异常日志
//			logger.error(ex.getMessage(), ex);
			
			String json = JSON.toJSONString(r);
			response.getWriter().print(json);
		} catch (Exception e) {
			logger.error("RRExceptionHandler 异常处理失败", e);
		}finally {
			logger.error("RRExceptionHandler：",ex);
		}
		return new ModelAndView();
	}
}
