package com.hxxdemo.githubLogin.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxxdemo.weixinsaomalogin.util.WechatConfigLoader;

public class GithubConfigLoader {
	 //日志记录对象
    private static Logger log = LoggerFactory.getLogger(GithubConfigLoader.class);
	
    //配置文件路径
    private static String githubPath = "github.properties";
    //git应用id
    private static String clentId;
    //git应用secret
    private static String clentSecret;
    //git第三方回调地址
    private static String backUrl;
    //git code
    private static String code;
    
    //github创建issue用户id
    private static String userId;
    //github创建issue 用户登录名
    private static String userLogin;
    //github创建issue token令牌
    private static String token;
    //github创建issue 标题模板
    private static String title;
    //github创建issue 内容模板
    private static String content;
    
    
    public static String getClentId() {
		return clentId;
	}


	public static void setClentId(String clentId) {
		GithubConfigLoader.clentId = clentId;
	}


	public static String getClentSecret() {
		return clentSecret;
	}


	public static String getUserId() {
		return userId;
	}


	public static void setUserId(String userId) {
		GithubConfigLoader.userId = userId;
	}


	public static String getUserLogin() {
		return userLogin;
	}


	public static void setUserLogin(String userLogin) {
		GithubConfigLoader.userLogin = userLogin;
	}


	public static String getToken() {
		return token;
	}


	public static void setToken(String token) {
		GithubConfigLoader.token = token;
	}


	public static String getTitle() {
		return title;
	}


	public static void setTitle(String title) {
		GithubConfigLoader.title = title;
	}


	public static String getContent() {
		return content;
	}


	public static void setContent(String content) {
		GithubConfigLoader.content = content;
	}


	public static void setClentSecret(String clentSecret) {
		GithubConfigLoader.clentSecret = clentSecret;
	}


	public static String getBackUrl() {
		return backUrl;
	}


	public static void setBackUrl(String backUrl) {
		GithubConfigLoader.backUrl = backUrl;
	}


	public static String getCode() {
		return code;
	}


	public static void setCode(String code) {
		GithubConfigLoader.code = code;
	}


	static {
        // 类初始化后加载配置文件
        InputStream in = WechatConfigLoader.class.getClassLoader()
                .getResourceAsStream(githubPath);
        Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException e) {
            log.error("load wechat setting error,pleace check the file path:"
                    + githubPath);
            log.error(e.toString(), e);
        }
        clentId = props.getProperty("github.clent_id");
        clentSecret = props.getProperty("github.client_secret");
        backUrl = props.getProperty("github.redirect_uri");
        code = props.getProperty("github.code");
        userId = props.getProperty("github.user_id");
        userLogin = props.getProperty("github.user_login");
        token = props.getProperty("github.user_token");
        title = props.getProperty("github.title");
        content = props.getProperty("github.content");
        log.debug("load wechat setting success,file path:" + githubPath);
    }

}
