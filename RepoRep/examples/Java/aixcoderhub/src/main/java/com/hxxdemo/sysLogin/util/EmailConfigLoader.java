package com.hxxdemo.sysLogin.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxxdemo.weixinsaomalogin.util.WechatConfigLoader;

public class EmailConfigLoader {
	 //日志记录对象
    private static Logger log = LoggerFactory.getLogger(EmailConfigLoader.class);
	
    //配置文件路径
    private static String emailPath = "email.properties";
    
    private static String from ;
    private static String account ;
    private static String pass ;
    private static String theme ;
    private static String hubfrom ;
    private static String hubaccount ;
    private static String hubpass ;
    private static String hubtheme ;
    
    //阿里云邮箱账号和密码
    private static String user;
    private static String password;
    
    
    public static String getHubfrom() {
		return hubfrom;
	}

	public static void setHubfrom(String hubfrom) {
		EmailConfigLoader.hubfrom = hubfrom;
	}

	public static String getHubaccount() {
		return hubaccount;
	}

	public static void setHubaccount(String hubaccount) {
		EmailConfigLoader.hubaccount = hubaccount;
	}

	public static String getHubpass() {
		return hubpass;
	}

	public static void setHubpass(String hubpass) {
		EmailConfigLoader.hubpass = hubpass;
	}

	public static String getHubtheme() {
		return hubtheme;
	}

	public static void setHubtheme(String hubtheme) {
		EmailConfigLoader.hubtheme = hubtheme;
	}


	private static String activateUrl ;
	public static String getFrom() {
		return from;
	}

	public static void setFrom(String from) {
		EmailConfigLoader.from = from;
	}

	public static String getAccount() {
		return account;
	}

	public static void setAccount(String account) {
		EmailConfigLoader.account = account;
	}

	public static String getPass() {
		return pass;
	}

	public static void setPass(String pass) {
		EmailConfigLoader.pass = pass;
	}

	public static String getTheme() {
		return theme;
	}

	public static void setTheme(String theme) {
		EmailConfigLoader.theme = theme;
	}
	
	public static String getActivateUrl() {
		return activateUrl;
	}
	
	public static void setActivateUrl(String activateUrl) {
		EmailConfigLoader.activateUrl = activateUrl;
	}


	public static String getUser() {
		return user;
	}

	public static void setUser(String user) {
		EmailConfigLoader.user = user;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		EmailConfigLoader.password = password;
	}


	static {
        // 类初始化后加载配置文件
        InputStream in = WechatConfigLoader.class.getClassLoader()
                .getResourceAsStream(emailPath);
        Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException e) {
            log.error("load wechat setting error,pleace check the file path:"
                    + emailPath);
            log.error(e.toString(), e);
        }
        from = props.getProperty("email.from");
        account = props.getProperty("email.account");
        pass = props.getProperty("email.pass");
        theme = props.getProperty("email.theme");
        hubfrom = props.getProperty("email.aixcoderhub.from");
        hubaccount = props.getProperty("email.aixcoderhub.account");
        hubpass = props.getProperty("email.aixcoderhub.pass");
        hubtheme = props.getProperty("email.aixcoderhub.theme");
        activateUrl = props.getProperty("email.activateUrl");
        user = props.getProperty("email.aliyun.user");
        password = props.getProperty("email.aliyun.password");
        
        log.debug("load wechat setting success,file path:" + emailPath);
    }
}
