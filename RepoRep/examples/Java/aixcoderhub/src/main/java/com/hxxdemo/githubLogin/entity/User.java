package com.hxxdemo.githubLogin.entity;

import com.hxxdemo.githubLogin.util.JsonUtil;

/**
 * Created by Ji Jianhong on 17/9/25.
 */
public class User {

    /**
     * 主键
     */
    private Long id;
    /**
     * 登录名
     */
    private String login;

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

}
