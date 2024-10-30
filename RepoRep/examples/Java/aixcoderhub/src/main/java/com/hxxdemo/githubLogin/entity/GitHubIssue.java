package com.hxxdemo.githubLogin.entity;

import com.hxxdemo.githubLogin.util.JsonUtil;

/**
 * Created by Ji Jianhong on 17/9/24.
 */
public class GitHubIssue {
    /**
     * 所属仓库
     */
    private String repo;
    /**
     * 主键
     */
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String body;
    /**
     * 状态
     */
    private String state;
    /**
     * 编号
     */
    private Long number;
    /**
     * 提出人
     */
    private User user;

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
