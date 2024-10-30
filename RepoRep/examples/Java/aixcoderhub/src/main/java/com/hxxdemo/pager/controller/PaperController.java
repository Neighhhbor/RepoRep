package com.hxxdemo.pager.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.pager.entity.Answer;
import com.hxxdemo.pager.entity.Paper;
import com.hxxdemo.pager.entity.Question;
import com.hxxdemo.pager.entity.Token;
import com.hxxdemo.pager.service.QuestionService;
import com.ibm.icu.text.DecimalFormat;

import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "/paper")
public class PaperController {

	@Autowired
    private QuestionService questionService;
	/**
	 * @param id 根据问卷id 查询问题
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "question")
    @ResponseBody
    public Map<String,Object> question(HttpServletRequest request,String id) throws ServletException, IOException {
		Map<String,Object> map = new HashMap<String,Object>();
		List<Paper> paper =  questionService.queryPaperById(id);
		if(null != paper) {
			List<Map<String,Object>> list = questionService.queryQuestionListByPaperId(id);
			if(null != list){
				String ids  = "";
				for (int i = 0; i < list.size(); i++) {
					ids = list.get(i).get("id").toString();
					List<Map<String,Object>> Clist = questionService.queryQuestionById(ids);
					list.get(i).put("clist", Clist);
				}
				map.put("success", true);
				map.put("msg", "请求成功！");
				map.put("token", java.util.UUID.randomUUID().toString());
				map.put("list", list);
			}
		}else {
			map.put("success", false);
			map.put("msg", "请求失败！");
		}
		return map;
		
//		
//		
//		response.addHeader("Access-Control-Allow-Origin", "http://www.aixcoder.com");  
//		response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");  
//	    response.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-requested-with, sid, mycustom, smuser");  
//		response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
//		response.setCharacterEncoding("UTF-8");
//	    response.setContentType("text/html;charset=UTF-8");
//		List<Paper> paper =  questionService.queryPaperById(id);
//		Map<String,Object> map = new HashMap<String,Object>();
//		if(null != paper) {
//			List<Map<String,Object>> list = questionService.queryQuestionListByPaperId(id);
//			if(null != list){
//				String ids  = "";
//				for (int i = 0; i < list.size(); i++) {
//					ids = list.get(i).get("id").toString();
//					List<Map<String,Object>> Clist = questionService.queryQuestionById(ids);
//					list.get(i).put("clist", Clist);
//				}
//				map.put("list", list);
//			}
//		}
//		JSONArray jsonArray = JSONArray.fromObject(map);
//	    String result = jsonArray.toString();
//
//	    //前端传过来的回调函数名称
//	    String callback = request.getParameter("theFunction");
//	    //用回调函数名称包裹返回数据，这样，返回数据就作为回调函数的参数传回去了
//	    result = callback + "(" + result + ")";
//
//	    response.getWriter().write(result);
    }
	@RequestMapping(value = "submission")
    @ResponseBody
    /**
     * @param id 问卷id
     * @param answer 回答的问题答案
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public Map<String,Object> submission(HttpServletRequest request, String id,String answer,String token) throws ServletException, IOException {
		
		System.out.println("============"+token);
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == answer || "".equals(answer) || null == id || "".equals(id) || null == token || "".equals(token)) {
			map.put("success", false);
			map.put("msg", "提交信息错误！");
		}
		List<Paper> paper =  questionService.queryPaperById(id);
		if(null == paper){
			map.put("success", false);
			map.put("msg", "提交信息错误！");
		}else {
			Token tokenEntity = new Token();
			tokenEntity.setId(token);
			List<Map<String,Object>> tokenList = questionService.getTokenById(tokenEntity);
			if(null == tokenList || tokenList.size() == 0) {
				questionService.insertToken(tokenEntity);
				Answer answerEntity = new Answer();
				answerEntity.setId(java.util.UUID.randomUUID().toString());
				answerEntity.setPaperid(id);
				answerEntity.setAnswer(answer);
				questionService.insertAnswer(answerEntity);
				map.put("success", true);
				map.put("msg", "提交成功！");
			}else {
				map.put("success", false);
				map.put("msg", "提交信息错误！");
			}
		}
		return map;
		
//		response.addHeader("Access-Control-Allow-Origin", "http://www.aixcoder.com");  
//		response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");  
//	    response.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-requested-with, sid, mycustom, smuser");  
//	    response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
//	    response.setCharacterEncoding("UTF-8");
//	    response.setContentType("text/html;charset=UTF-8");
//	    
//	    String callback = request.getParameter("theFunction");
//	    Map<String,Object> map = new HashMap<String,Object>();
//	    String answer = request.getParameter("answer");
//	    if(null == answer) {
//	    	map.put("fail", "提交信息错误！");
//		    JSONArray jsonArray = JSONArray.fromObject(map);
//		    String result = jsonArray.toString();
//		    //前端传过来的回调函数名称
//		    //用回调函数名称包裹返回数据，这样，返回数据就作为回调函数的参数传回去了
//		    result = callback + "(" + result + ")";
//		    response.getWriter().write(result);
//	    }else {
//	    	String id = request.getParameter("id");
//	    	if(null == id) {
//	    		map.put("success", false);
//	    		map.put("msg", "提交信息错误！");
//	    		JSONArray jsonArray = JSONArray.fromObject(map);
//	    		String result = jsonArray.toString();
//	    		//用回调函数名称包裹返回数据，这样，返回数据就作为回调函数的参数传回去了
//	    		result = callback + "(" + result + ")";
//	    		response.getWriter().write(result);
//	    	}else {
//	    		List<Paper> paper =  questionService.queryPaperById(id);
//	    		if(null == paper){
//	    			map.put("success", false);
//	    			map.put("msg", "提交信息错误！");
//		    		JSONArray jsonArray = JSONArray.fromObject(map);
//		    		String result = jsonArray.toString();
//		    		//用回调函数名称包裹返回数据，这样，返回数据就作为回调函数的参数传回去了
//		    		result = callback + "(" + result + ")";
//		    		response.getWriter().write(result);
//	    		}else {
//	    			Answer answerEntity = new Answer();
//	    			answerEntity.setId(java.util.UUID.randomUUID().toString().toString());
//	    			answerEntity.setPaperid(id);
//	    			answerEntity.setAnswer(answer);
//	    			questionService.insertAnswer(answerEntity);
//	    			map.put("success", true);
//	    			map.put("msg", "提交成功！");
//		    		JSONArray jsonArray = JSONArray.fromObject(map);
//		    		String result = jsonArray.toString();
//		    		//用回调函数名称包裹返回数据，这样，返回数据就作为回调函数的参数传回去了
//		    		result = callback + "(" + result + ")";
//		    		response.getWriter().write(result);
//	    		}
//	    	}
//	    }
	}
	@RequestMapping(value = "paper")
    @ResponseBody
    /**
     * 问卷list
     * @param name 问卷名称
     * @param int rows 每页显示行
     * @param page 页码
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public Map<String,Object> paper(HttpServletRequest request, Integer rows,Integer page,String name) throws ServletException, IOException {
//		response.setCharacterEncoding("UTF-8");
//	    response.setContentType("text/html;charset=UTF-8");
		//初始化参数行数
		if(null == rows || "".equals(rows)) {
			rows =  10 ;
		}
		//初始化参数页码
		if(null == page || "".equals(page)) {
			page =  1 ;
		}
		Map<String ,Object> params = new HashMap<String,Object>();
		params.put("rows", rows);
		params.put("page", (page-1)*rows);
		params.put("name", name);
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		list = questionService.queryPaperList(params);
		
		Map<String,Object> map = new HashMap<String,Object>();
		int total = 0 ; 
		total = questionService.countPaperList(params);
		map.put("total", total);
		map.put("success", true);
		map.put("list", list);
		return map;
//		JSONArray jsonArray = JSONArray.fromObject(list);
//	    String result = jsonArray.toString();
//
//	    //前端传过来的回调函数名称
//	    String callback = request.getParameter("theFunction");
//	    //用回调函数名称包裹返回数据，这样，返回数据就作为回调函数的参数传回去了
//	    result = callback + "(" + result + ")";
//
//	    response.getWriter().write(result);
	}
	@RequestMapping(value = "answer")
    @ResponseBody
    /**
     * @param id 问卷id
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public Map<String,Object> answer(HttpServletRequest request, Integer rows,Integer page,String id) throws ServletException, IOException {
//		response.setCharacterEncoding("UTF-8");
//	    response.setContentType("text/html;charset=UTF-8");
		//初始化参数行数
		if(null == rows || "".equals(rows)) {
			rows =  10 ;
		}
		//初始化参数页码
		if(null == page || "".equals(page)) {
			page =  1 ;
		}
		Map<String ,Object> params = new HashMap<String,Object>();
		params.put("rows", rows);
		params.put("page", (page-1)*rows);
		params.put("paperid", id);
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		list = questionService.queryAnswerList(params);
		Map<String,Object> map = new HashMap<String,Object>();
		int total = 0 ; 
		total = questionService.countAnswerList(params);
		map.put("total", total);
		map.put("success", true);
		map.put("list", list);
		return map;
//	    String result = jsonArray.toString();
//
//	    //前端传过来的回调函数名称
//	    String callback = request.getParameter("theFunction");
//	    //用回调函数名称包裹返回数据，这样，返回数据就作为回调函数的参数传回去了
//	    result = callback + "(" + result + ")";
//
//	    response.getWriter().write(result);
	}
	@RequestMapping(value = "oneAnswer")
    @ResponseBody
    /**
     * @param id 问卷id
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public Map<String,Object> oneAnswer(HttpServletRequest request, String id) throws ServletException, IOException {
//		response.setCharacterEncoding("UTF-8");
//	    response.setContentType("text/html;charset=UTF-8");
		//问卷名称
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == id ||   "".equals(id)) {
			map.put("success", false);
			map.put("msg", "请求错误！");
			return map;
		}
		
		Map<String ,Object> params = new HashMap<String,Object>();
		params.put("id", id);
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> plist = new ArrayList<Map<String,Object>>();
		list = questionService.queryAnswerOne(params);
		if(null == list || list.size() ==0 ) {
			map.put("success", false);
			map.put("msg", "请求错误！");
			return map;
		}else {
			String papaerid = list.get(0).get("paperid").toString();
			plist = questionService.queryQuestionListByPaperId(papaerid);
			String ids  = "";
			String answer= list.get(0).get("answer").toString();
			String answers[] = answer.split(";");
			
			for (int i = 0; i < plist.size(); i++) {
				ids = plist.get(i).get("id").toString();
				List<Map<String,Object>> Clist = questionService.queryQuestionById(ids);
				if(plist.get(i).get("type").toString().equals("2") || plist.get(i).get("type").toString().equals("3")) {
					if(null != Clist && Clist.size()>0) {
						for(int j = 0 ; j < Clist.size() ; j++ ) {
							String aid = Clist.get(j).get("pid").toString();
							for(int k = 0 ; k < answers.length ;k++) {
								String panswer = answers[k].split(":")[0];
								if(aid.equals(panswer)) {
									String canswer = answers[k].split(":")[1];
									String canswers[] = canswer.split(",");
									for (int l = 0; l < canswers.length; l++) {
										if(null == Clist.get(j).get("bool")) {
											Clist.get(j).put("bool", false);
										}
										if(canswers[l].equals(Clist.get(j).get("id").toString())) {
											Clist.get(j).put("bool", true);
										}else {
											if(null != Clist.get(j).get("flag") && Clist.get(j).get("flag").toString().equals("1")) {
												if(canswers[l].contains("&")) {
													Clist.get(j).put("bool", true);
													Clist.get(j).put("result", canswers[l].split("&")[1].toString());
												}
											}
										}
									}
								}
							}
						}
					}
				}else {
					plist.get(i).put("result", "");
					for(int m = 0 ; m < answers.length ; m++) {
						String panswer = answers[m].split(":")[0];
						if(ids.equals(panswer)) {
							plist.get(i).put("result", answers[m].split(":")[1]);
						}
					}
				}
				plist.get(i).put("clist", Clist);
			}
			
		}
		map.put("success", true);
		map.put("list", plist);
		return map;
//		JSONArray jsonArray = JSONArray.fromObject(list);
//	    String result = jsonArray.toString();
//
//	    //前端传过来的回调函数名称
//	    String callback = request.getParameter("theFunction");
//	    //用回调函数名称包裹返回数据，这样，返回数据就作为回调函数的参数传回去了
//	    result = callback + "(" + result + ")";
//
//	    response.getWriter().write(result);
	}
	@RequestMapping(value = "allAnswer")
    @ResponseBody
	public Map<String,Object> allAnswer(HttpServletRequest request, String id) throws ServletException, IOException {
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == id || "".equals(id)) {
			map.put("success", false);
			map.put("msg", "请求错误！");
			return map;
		}
		List<Map<String,Object>> list = questionService.queryQuestionListByPaperId(id);
		if(null != list){
			String ids  = "";
			for (int i = 0; i < list.size(); i++) {
				ids = list.get(i).get("id").toString();
				List<Map<String,Object>> Clist = questionService.queryQuestionById(ids);
				list.get(i).put("clist", Clist);
			}
		}
		List<Map<String,Object>> alist = questionService.queryAllAnswer(id);
		for(int i = 0 ;i <list.size(); i++) {
			if(list.get(i).get("type").toString().equals("2") || list.get(i).get("type").toString().equals("3")) {
				String qid  = list.get(i).get("id").toString();
				for(int j = 0 ; j < alist.size() ; j++ ) {
					String answer = alist.get(j).get("answer").toString();
					String answers[] = answer.split(";");
					for(int k = 0 ; k < answers.length ; k++) {
						String panswer = answers[k].split(":")[0];
						if(panswer.equals(qid)) {
							List<Map<String,Object>> clist = (List<Map<String,Object>>)list.get(i).get("clist");
							String canswer = answers[k].split(":")[1];
							String canswers[] = canswer.split(",");
							for(int m = 0 ; m < clist.size() ; m++ ) {
								String cid = clist.get(m).get("id").toString();
								if(null == clist.get(m).get("num")) {
									clist.get(m).put("num", 0);
								}
								for(int n = 0 ; n < canswers.length ; n++) {
									String aid = canswers[n];
									if(cid.equals(aid)){
										int num = Integer.parseInt(clist.get(m).get("num").toString());
										clist.get(m).put("num", num+1);
									}else {
										if(aid.contains("&")) {
											int num = Integer.parseInt(clist.get(m).get("num").toString());
											clist.get(m).put("num", num+1);
										}
									}
								}
							}
							
							list.get(i).put("clist", clist);
						}
					}
				}
			}
		}
		for(int i = 0 ; i < list.size() ; i++) {
			if(list.get(i).get("type").toString().equals("2") || list.get(i).get("type").toString().equals("3")) {
				List<Map<String,Object>> clist = (List<Map<String,Object>>)list.get(i).get("clist");
				int total = 0 ;
				for(int j = 0 ; j < clist.size(); j++) {
					if(null == clist.get(j).get("num")) {
						total = 0;
					}else {
						total += Integer.parseInt(clist.get(j).get("num").toString());
					}
				}
				DecimalFormat df = new DecimalFormat("0.00");//格式化小数  
				for(int j = 0 ; j < clist.size(); j++) {
					if(null == clist.get(j).get("num")) {
						clist.get(j).put("percent", "0.00%");
					}else {
						int topnum = Integer.parseInt(clist.get(j).get("num").toString())*100;
						String percent = df.format((float)topnum/total)+"%";//返回的是String类型
						clist.get(j).put("percent", percent);
					}
				}
				list.get(i).put("clist", clist);
				System.out.println(total);
			}
		}
		map.put("success", true);
		map.put("list", list);
		return map;
	}
	
}
