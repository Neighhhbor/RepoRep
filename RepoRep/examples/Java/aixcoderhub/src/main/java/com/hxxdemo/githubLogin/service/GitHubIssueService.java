package com.hxxdemo.githubLogin.service;

import java.util.List;
import java.util.Map;

import com.hxxdemo.githubLogin.entity.GitHubIssue;

/**
 * Created by Ji Jianhong on 17/9/24.
 */
public interface GitHubIssueService {


    /**
     * 创建问题
     *
     * @param token 令牌
     * @param issue 问题
     * @return
     * @throws Exception
     */
    GitHubIssue createIssue(String token, GitHubIssue issue) throws Exception;

}
