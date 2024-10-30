package com.hxxdemo.model.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hxxdemo.config.Globals;
import com.hxxdemo.githubLogin.entity.GitHubIssue;
import com.hxxdemo.githubLogin.service.GitHubIssueService;
import com.hxxdemo.githubLogin.util.GithubConfigLoader;
import com.hxxdemo.model.service.ModelService;
import com.hxxdemo.sysLogin.Singleton;
import com.hxxdemo.sysLogin.entity.TokenEntity;
import com.hxxdemo.sysLogin.entity.User;
import com.hxxdemo.sysLogin.service.RegisterService;
import com.hxxdemo.sysLogin.service.TokenService;
import com.hxxdemo.vote.service.VoteService;

@Controller
@RequestMapping(value="/model")
public class ModelController {

	@Autowired
	private ModelService modelService;
	@Autowired
	private RegisterService registerService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private GitHubIssueService gitHubIssueService;
	@Autowired
	private VoteService voteService;
	/**
	 * 
	 * @param request
	 * @param page
	 * @param rows
	 * @param status
	 * @param id
	 * @return
	 */
	@RequestMapping(value="modelList")
	@ResponseBody
	public Map<String,Object> modelList(Integer page,Integer rows,Long id,Integer status,String name){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> params = new HashMap<String,Object>();
		if(null == page) {
			page=1;
		}
		if(null == rows) {
			rows=3;
		}
		page= (page-1)*rows;
		params.put("page", page);
		params.put("rows", rows);
		if(null == status) {
			status = Globals.MODELTRAINSTATUS0;
		}
		if(null != name && !"".equals(name)) {
			params.put("name", name);
		}
		params.put("status", status);
		if(null != id) {
			int isUser = registerService.isUserById(id);
			if(isUser == 0) {
				returnMap.put("errorcode", Globals.ERRORCODE4001);
				returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
				return returnMap;
			}
			params.put("userid", id);
		}
		List<Map<String,Object>> modelList = new ArrayList<Map<String,Object>>();
		modelList = modelService.ModelList(params);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", modelList);
		return returnMap;
	}
	
	/**
	 * 
	 * @param request
	 * @param page
	 * @param rows
	 * @param status
	 * @return
	 */
	@RequestMapping(value="ownerModelList")
	@ResponseBody
	public Map<String,Object> ownerModelList(HttpServletRequest request,Integer page,Integer rows,Integer status,String name,String token){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> params = new HashMap<String,Object>();
		if(null == token || "".equals(token)) {
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
		//验证token是否过期
		TokenEntity tokenEntity = tokenService.queryByToken(token);
		if(null == tokenEntity) {
			Singleton.getInstance().getMap().remove(token);
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
		if(null == page) {
			page=1;
		}
		if(null == rows) {
			rows=30;
		}
		User user = Singleton.getInstance().getMap().get(token);
		if(null == user ) {
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
		params.put("userid", user.getId());
		page= (page-1)*rows;
		params.put("page", page);
		params.put("rows", rows);
		if(null != name && !"".equals(name)) {
			params.put("name", name);
		}
		if(null == status) {
			status = Globals.MODELTRAINSTATUS0;
		}
		params.put("status", status);
		List<Map<String,Object>> modelList = new ArrayList<Map<String,Object>>();
		modelList = modelService.ModelList(params);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", modelList);
		return returnMap;
	}
	@RequestMapping(value="modelDetail")
	@ResponseBody
	public Map<String,Object> modelDetail(Long id){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == id) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		Map<String,Object> modelMap = new HashMap<String,Object>();
		modelMap = modelService.modelDetail(id);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", modelMap==null?"":modelMap);
		return returnMap;
	}
	@RequestMapping(value="createModel")
	@ResponseBody
	public Map<String,Object> createModel(HttpServletRequest request, String name,String ownerProjects,String openProjects,Integer type,String typename,String detail,String synopsis,Long modelid,Long userid,String token,boolean issend){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == token || "".equals(token)) {
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
		//验证token是否过期
		TokenEntity tokenEntity = tokenService.queryByToken(token);
		if(null == tokenEntity) {
			Singleton.getInstance().getMap().remove(token);
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
		if(null == name || "".equals(name)) {
			returnMap.put("errorcode", Globals.ERRORCODE5003);
			returnMap.put("errormessage", Globals.ERRORMESSAGE5003);
			return returnMap;
		}
		if(null == userid) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		int isUser = registerService.isUserById(userid);
		if(isUser == 0) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		if(null == type) {
			returnMap.put("errorcode", Globals.ERRORCODE5004);
			returnMap.put("errormessage", Globals.ERRORMESSAGE5004);
			return returnMap;
		}
		if((null == ownerProjects || "".equals(ownerProjects) || "[]".equals(ownerProjects)) && (null == openProjects || "".equals(openProjects) || "[]".equals(openProjects))) {
			returnMap.put("errorcode", Globals.ERRORCODE5005);
			returnMap.put("errormessage", Globals.ERRORMESSAGE5005);
			return returnMap;
		}
		if(null == modelid) {
			returnMap.put("errorcode", Globals.ERRORCODE5006);
			returnMap.put("errormessage", Globals.ERRORMESSAGE5006);
			return returnMap;
		}
//		if(null == synopsis || "".equals(synopsis)) {
//			returnMap.put("errorcode", Globals.ERRORCODE5007);
//			returnMap.put("errormessage", Globals.ERRORMESSAGE5007);
//			return returnMap;
//		}
//		if(null == detail || "".equals(detail)) {
//			returnMap.put("errorcode", Globals.ERRORCODE5008);
//			returnMap.put("errormessage", Globals.ERRORMESSAGE5008);
//			return returnMap;
//		}
		String tokenid = UUID.randomUUID().toString();
		User user = Singleton.getInstance().getMap().get(token);
		if(null == user ) {
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
		if(null != ownerProjects && !"".equals(ownerProjects) && !"[]".equals(ownerProjects)) {
			try {
				JSONArray jsonProject = (JSONArray)JSON.parse(ownerProjects);
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				for(int i = 0 ; i < jsonProject.size() ; i++) {
					String jstr = jsonProject.getString(i);
					Map<String,Object> map = (Map)JSON.parse(jstr); 
					map.put("userid", user.getId());
					map.put("token", tokenid);
					list.add(map);
				}
				modelService.insertOwnerProject(list);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				returnMap.put("errorcode", Globals.ERRORCODE4001);
				returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
				return returnMap;
			}  
		}
		if(null != openProjects && !"".equals(openProjects) && !"[]".equals(openProjects)) {
			try {
				JSONArray jsonProject = (JSONArray)JSON.parse(openProjects);
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				for(int i = 0 ; i < jsonProject.size() ; i++) {
					String jstr = jsonProject.getString(i);
					Map<String,Object> map = (Map)JSON.parse(jstr); 
					map.put("userid", user.getId());
					map.put("token", tokenid);
					list.add(map);
				}
				modelService.insertopenProject(list);
				
				if(issend) {
					com.hxxdemo.githubLogin.entity.User githubUser = new com.hxxdemo.githubLogin.entity.User();
					githubUser.setId(Long.valueOf(GithubConfigLoader.getUserId()));
					githubUser.setLogin(GithubConfigLoader.getUserLogin());
					for (int i = 0; i < list.size() ; i++) {
//						if(!list.get(i).get("login").toString().equals(user.getLogin())) {
							GitHubIssue issue =  new GitHubIssue();
							issue.setUser(githubUser);
							issue.setTitle(GithubConfigLoader.getTitle());
							issue.setBody(user.getLogin()+GithubConfigLoader.getContent());
							issue.setRepo(list.get(i).get("full_name").toString());
							gitHubIssueService.createIssue(GithubConfigLoader.getToken(), issue);
//						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				returnMap.put("errorcode", Globals.ERRORCODE4001);
				returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
				return returnMap;
			}  
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userid", user.getId());
		params.put("name", name);
		params.put("status", Globals.MODELTRAINSTATUS0);
		params.put("del_flag", Globals.ISDEL0);
		params.put("detail", detail);
		params.put("type", type);
		params.put("typename", typename);
		params.put("synopsis", synopsis);
		params.put("modelid", modelid);
		params.put("token", tokenid);
		modelService.insertModel(params);
		//新创建模型id
		Long newModelId = modelService.getModelidByToken(tokenid);
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("token", tokenid);
		param.put("modelid", newModelId);
		modelService.updateProjectModelidByToken(param);
		//初始化本用户使用模型
		Map<String,Object> initUseMap = new HashMap<String,Object>();
		initUseMap.put("userid", userid);
		initUseMap.put("modelid", newModelId);
		initUseMap.put("usenum", 0);
		initUseMap.put("type", type);
		modelService.insertUse(initUseMap);
		//初始化本用户投票模型
		Map<String,Object> initVoteMap = new HashMap<String,Object>();
		initVoteMap.put("userid", userid);
		initVoteMap.put("votesnum", 0);
		initVoteMap.put("id", newModelId);
		initVoteMap.put("type", type);
		voteService.addVote(initVoteMap);
		
		//根据模型id查询模型是否被使用过
		Long isUsedId = modelService.isUsedByModelId(modelid);
		if(null == isUsedId)  {
			//插入使用者关联库
			Map<String,Object> useMap = new HashMap<String,Object>();
			useMap.put("userid", userid);
			useMap.put("modelid", modelid);
			useMap.put("usenum", 1);
			useMap.put("type", type);
			modelService.insertUse(useMap);
		}else {
			modelService.updateUse(isUsedId);
		}
		//插入到使用者与模型被使用者关系库
		Map<String,Object> usersMap = new HashMap<String,Object>();
		usersMap.put("userid", userid);
		usersMap.put("modelid", modelid);
		usersMap.put("usersid", user.getId());
		modelService.insertTrainUsers(usersMap);
		
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
	}
	@RequestMapping(value="updateModel")
	@ResponseBody
//	public Map<String,Object> updateModel(long modelid,String token,String detail,String synopsis,String language){
		public Map<String,Object> updateModel(long modelid,String token,String detail,String synopsis){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == token || "".equals(token)) {
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
		//验证token是否过期
		TokenEntity tokenEntity = tokenService.queryByToken(token);
		if(null == tokenEntity) {
			Singleton.getInstance().getMap().remove(token);
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
//		if((null == detail || "".equals(detail)) && (null == synopsis || "".equals(synopsis)) && (null == language || "".equals(language))) {
		if((null == detail || "".equals(detail)) && (null == synopsis || "".equals(synopsis))) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		User user = Singleton.getInstance().getMap().get(token);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userId", user.getId());
		params.put("modelId", modelid);
		int isUserModel = modelService.isUserModelbyUserIdModelId(params);
		if(isUserModel>0) {
			params.put("detail", detail);
			params.put("synopsis", synopsis);
//			params.put("language", language);
			modelService.updateModelDetail(params);
			returnMap.put("errorcode", Globals.ERRORCODE0);
			returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		}else {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		return returnMap;
	}
	
	@RequestMapping(value="deleteModel")
	@ResponseBody
	public Map<String,Object> deleteModel(long modelid,String token){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(null == token || "".equals(token)) {
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
		//验证token是否过期
		TokenEntity tokenEntity = tokenService.queryByToken(token);
		if(null == tokenEntity) {
			Singleton.getInstance().getMap().remove(token);
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
		User user = Singleton.getInstance().getMap().get(token);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userId", user.getId());
		params.put("modelId", modelid);
		int isUserModel = modelService.isUserModelbyUserIdModelId(params);
		if(isUserModel>0) {
			//查询状态是否是正在训练
			params.put("status", 2);
			int isTraining = modelService.isTraining(params);
			if(isTraining>0) {
				returnMap.put("errorcode", Globals.ERRORCODE5010);
				returnMap.put("errormessage", Globals.ERRORMESSAGE5010);
				return returnMap;
			}else {
				//删除模型
				modelService.deleteModelById(modelid);
				returnMap.put("errorcode", Globals.ERRORCODE0);
				returnMap.put("errormessage", Globals.ERRORMESSAGE0);
			}
			
		}else {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		return returnMap;
	}
}
