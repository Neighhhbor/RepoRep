package com.hxxdemo.vote.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.config.Globals;
import com.hxxdemo.model.service.ModelService;
import com.hxxdemo.sysLogin.Singleton;
import com.hxxdemo.sysLogin.entity.TokenEntity;
import com.hxxdemo.sysLogin.entity.User;
import com.hxxdemo.sysLogin.service.TokenService;
import com.hxxdemo.vote.service.VoteService;

@Controller
@RequestMapping(value="/vote")
public class VoteController {

	@Autowired
	private ModelService modelService;
	@Autowired
	private VoteService voteService;
	@Autowired
	private TokenService tokenService;
	
	
	@RequestMapping(value="/voteModel")
	@ResponseBody
	public Map<String,Object> voteModel(HttpServletRequest request ,Long modelId,String token){
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
		if(null == user ) {
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}
		Map<String,Object> modelMap = new HashMap<String,Object>();
		modelMap = modelService.modelDetail(modelId);
		if(null == modelMap) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		modelMap.put("voterid", user.getId());
		//查看进当天是否投票了
		int isVoter = voteService.isVoter(modelMap);
		if(isVoter == 1 ) {
			returnMap.put("errorcode", Globals.ERRORCODE5009);
			returnMap.put("errormessage", Globals.ERRORMESSAGE5009);
			return returnMap;
		}
		//查询投票总数
		int countVote = voteService.countIsVote(modelId);
		if(countVote==0) {
			//插入到投票总数
			modelMap.put("votesnum", 1);
			voteService.addVote(modelMap);
		}else {
			//修改投票总数
			voteService.editVote(modelId);
		}
		//插入投票人表
		voteService.addVoter(modelMap);
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		return returnMap;
	}

}
