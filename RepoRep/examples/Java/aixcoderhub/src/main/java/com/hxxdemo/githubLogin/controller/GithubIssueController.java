package com.hxxdemo.githubLogin.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.githubLogin.entity.User;
import com.hxxdemo.githubLogin.entity.GitHubIssue;
import com.hxxdemo.githubLogin.service.GitHubIssueService;
import com.hxxdemo.githubLogin.util.GithubConfigLoader;

@Controller
@RequestMapping(value="issue")
public class GithubIssueController {

	@Autowired
	private GitHubIssueService gitHubIssueService;
	
	@RequestMapping(value="createIssue")
	@ResponseBody
	public Map<String,Object> createIssue() throws Exception{
		Map<String,Object> returnMap = new HashMap<String,Object>();
		
		
		User user = new User();
		user.setId(Long.valueOf(GithubConfigLoader.getUserId()));
		user.setLogin(GithubConfigLoader.getUserLogin());
		for (int i = 0; i < 10; i++) {
			GitHubIssue issue =  new GitHubIssue();
			issue.setUser(user);
			issue.setTitle(GithubConfigLoader.getTitle());
			issue.setBody("username"+GithubConfigLoader.getContent());
			issue.setRepo("userfull_name");
			gitHubIssueService.createIssue(GithubConfigLoader.getToken(), issue);
		}
		
		
		return returnMap;
		
	}

}
