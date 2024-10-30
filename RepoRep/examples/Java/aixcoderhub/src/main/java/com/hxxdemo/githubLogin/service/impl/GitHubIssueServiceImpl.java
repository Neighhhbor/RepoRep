package com.hxxdemo.githubLogin.service.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import com.hxxdemo.githubLogin.entity.GitHubIssue;
import com.hxxdemo.githubLogin.service.GitHubIssueService;
import com.hxxdemo.githubLogin.util.JsonUtil;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Service("GitHubIssueService")
public class GitHubIssueServiceImpl implements GitHubIssueService {

    public GitHubIssue createIssue(String token, GitHubIssue issue) throws Exception {
        if (StringUtils.isBlank(token)) {
            throw new RuntimeException("token不能为空");
        }
        String repo = issue.getRepo();
        if (StringUtils.isBlank(repo)) {
            throw new RuntimeException("repo不能为空");
        }
        if (StringUtils.isBlank(issue.getTitle())) {
            throw new RuntimeException("title不能为空");
        }

        String url = "https://api.github.com/repos/:repo/issues".replace(":repo", repo);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost req = new HttpPost(url);
        req.setHeader("Authorization", "token " + token);

        Map param = new HashMap();
        param.put("title", issue.getTitle());
        param.put("body", issue.getBody());

        HttpEntity reqEntity = new StringEntity(JsonUtil.toJson(param), ContentType.APPLICATION_JSON);
        req.setEntity(reqEntity);
        CloseableHttpResponse res = client.execute(req);

        if (res.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
            HttpEntity resEntity = res.getEntity();
            InputStream inputStream = resEntity.getContent();
            String content = IOUtils.toString(inputStream, Charset.forName("UTF-8"));

            issue = JsonUtil.toObject(content, GitHubIssue.class);
            issue.setRepo(repo);
            return issue;
        } else {
            throw new RuntimeException("请求失败");
        }
    }

}
