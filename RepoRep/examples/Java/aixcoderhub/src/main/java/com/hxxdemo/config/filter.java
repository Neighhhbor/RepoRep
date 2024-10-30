package com.hxxdemo.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.hxxdemo.pager.entity.Token;
import com.hxxdemo.pager.service.QuestionService;

@Component
@ServletComponentScan
public class filter  implements Filter{
	@Autowired
    private QuestionService questionService;
	
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    	 HttpServletResponse response = (HttpServletResponse) servletResponse;
         HttpServletRequest request = (HttpServletRequest) servletRequest;
         /**
          * 支持跨域
          */
//         response.setHeader("Access-Control-Allow-Origin", "*");           
//         response.setHeader("Access-Control-Allow-Methods", "GET,POST");
//         response.addHeader("Access-Control-Max-Age", "1");
         
         //只允许post请求
         if (	!request.getMethod().equals("POST") 
        		 && !request.getRequestURI().equals("/wechatCon/wechat") 
        		 && !request.getRequestURI().equals("/aixcoderinstall/wechatCon/wechat")
        		 && !request.getRequestURI().equals("/plug/saveBuyerOrder")
        		 && !request.getRequestURI().equals("/aixcoderinstall/plug/saveBuyerOrder")
        		 && !request.getRequestURI().equals("/plug/checkBuyerToken")
        		 && !request.getRequestURI().equals("/aixcoderinstall/plug/checkBuyerToken")
        		 && !request.getRequestURI().equals("/getVersion")
        		 && !request.getRequestURI().equals("/aixcoderinstall/getVersion")
        		 ) {
			return;
         }
         request.setCharacterEncoding("UTF-8");
         response.setCharacterEncoding("UTF-8");
         List<String> uriList = new ArrayList<String>() {{
//        	add("/plug/userLogin"); 
//        	add("/plug/userRegister"); 
//        	add("/plug/retrievePassword"); 
//        	add("/plug/resetPassword"); 
//        	add("/personalSetting/password"); 
//        	add("/register/registerUser"); 
//        	add("/login/userLogin"); 
//        	add("/account/setPassword"); 
//        	add("/account/resetPassword"); 
//        	
//        	add("/aixcoderinstall/plug/userLogin"); 
//        	add("/aixcoderinstall/plug/userRegister"); 
//        	add("/aixcoderinstall/plug/retrievePassword"); 
//        	add("/aixcoderinstall/plug/resetPassword"); 
//        	add("/aixcoderinstall/personalSetting/password"); 
//        	add("/aixcoderinstall/register/registerUser"); 
//        	add("/aixcoderinstall/login/userLogin"); 
//        	add("/aixcoderinstall/account/setPassword"); 
//        	add("/aixcoderinstall/account/resetPassword"); 
         }};
         if (uriList.contains(request.getRequestURI())) {
        	 String password = request.getParameter("password");
        	 if (null != password && !password.equals("")) {
        		 String repassword = request.getParameter("repassword");
        		 ChangeRequestWrapper changeRequestWrapper = new ChangeRequestWrapper((HttpServletRequest) servletRequest);
        		 // 获取所有参数集合
        	        Map<String, String[]> parameterMap = new HashMap<>(changeRequestWrapper.getParameterMap());
        	        // 修改集合中的某个参数
        	        parameterMap.put("password", new String[]{BtoaEncode.decrypt(password)});
        	        if (null != repassword && !repassword.equals("")) {
        	        	parameterMap.put("repassword", new String[]{BtoaEncode.decrypt(repassword)});
        	        }
        	        // 将集合存到自定义HttpServletRequestWrapper
        	        changeRequestWrapper.setParameterMap(parameterMap);
        	        // 替换原本的request
        	        filterChain.doFilter(changeRequestWrapper, servletResponse);
        	 }
         }else {
        	 if("/paper/submission".equals(request.getRequestURI())) {
     		 	String token = request.getParameter("token");
     		 	if(null != token && !"".equals(token)) {
     		 		Token tokenEntity = new Token();
     		 		tokenEntity.setId(token);
     		 		List<Map<String,Object>> tokenList = questionService.getTokenById(tokenEntity);
     		 		if(null == tokenList || tokenList.size()==0) {
//     		 			filterChain.doFilter(servletRequest, servletResponse);
     		 		}
     		 	}
              }else {
//             	 filterChain.doFilter(servletRequest, servletResponse);
              }
         	 String[] language = request.getParameterMap().get("retLanguage");
         	 
         	 if (null != language && language.length > 0 && language[0].equals("en")) {
         		 //处理英文提示
         		 ResponseWrapper wrapperResponse = new ResponseWrapper((HttpServletResponse) response,request);
                  // 这里只拦截返回，直接让请求过去，如果在请求前有处理，可以在这里处理
                  filterChain.doFilter(request, wrapperResponse);
                  byte[] content = wrapperResponse.getContent();//获取返回值
                  // 判断是否有值
                  if (content.length > 0) {
                      // 这里是返回的内容
                      String str = new String(content, "UTF-8");
                      String body1 =  JSONObject.toJSON(str).toString();
                      JSONObject json = JSONObject.parseObject(body1);
                      Globals.retValueHandler(json);
                      OutputStream out = response.getOutputStream();
                      out.write(json.toJSONString().getBytes());
                      out.flush();
                  }
         	 }else {
         		 filterChain.doFilter(servletRequest, servletResponse);
         	 }
         }
    }
    @Override
    public void destroy() {

    }
    /**
	 * 可逆的的加密解密方法；两次是解密，一次是加密
	 * @param inStr
	 * @return
	 */
	public static String convertMD5(String inStr){

		char[] a = inStr.toCharArray();
		for (int i = 0; i < a.length; i++){
			a[i] = (char) (a[i] ^ 't');
		}
		String s = new String(a);
		return s;

	}
}
