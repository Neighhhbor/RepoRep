package com.hxxdemo.wechat.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class WechatConfigLoader {
    //日志记录对象
    private static Logger log = LoggerFactory.getLogger(WechatConfigLoader.class);

    //配置文件路径
    private static String wechatPath = "wechat.properties";
    //开发平台应用唯一标识
    private static String appId;
    //开放平台应用密钥
    private static String appSecret;
    
	
	static {
        // 类初始化后加载配置文件
        InputStream in = WechatConfigLoader.class.getClassLoader()
                .getResourceAsStream(wechatPath);
        Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException e) {
            log.error("load wechat setting error,pleace check the file path:"
                    + wechatPath);
            log.error(e.toString(), e);
        }
        appId = props.getProperty("wechat.appId");
        appSecret = props.getProperty("wechat.appSecret");
        log.debug("load wechat setting success,file path:" + wechatPath);
    }

    public static String getAppId() {
        return appId;
    }

    public static String getAppSecret() {
        return appSecret;
    }


    public static void setWechatPath(String wechatPath) {
        WechatConfigLoader.wechatPath = wechatPath;
    }

    public static String getWechatPath() {
        return wechatPath;
    }

    public static void setAppId(String appId) {
        WechatConfigLoader.appId = appId;
    }

    public static void setAppSecret(String appSecret) {
        WechatConfigLoader.appSecret = appSecret;
    }

}