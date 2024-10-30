package com.hxxdemo.weixinsaomalogin;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping(value = "/error")
public class error {

	//返回微信二维码，可供扫描登录
    @RequestMapping(value = "401")
    @ResponseBody
    public ModelAndView error401(ModelMap map, HttpServletRequest request, HttpServletResponse response) throws IOException{
  	  	ModelAndView mav = new ModelAndView();
  	  	mav.setViewName("401");
  	  	return mav;
    }
  //返回微信二维码，可供扫描登录
    @RequestMapping(value = "404")
    @ResponseBody
    public ModelAndView error404(ModelMap map, HttpServletRequest request, HttpServletResponse response) throws IOException{
  	  	ModelAndView mav = new ModelAndView();
  	  	mav.setViewName("404");
  	  	return mav;
    }
    //返回微信二维码，可供扫描登录
    @RequestMapping(value = "500")
    @ResponseBody
    public ModelAndView error500(ModelMap map, HttpServletRequest request, HttpServletResponse response) throws IOException{
    	ModelAndView mav = new ModelAndView();
    	mav.setViewName("500");
    	return mav;
    }
}
