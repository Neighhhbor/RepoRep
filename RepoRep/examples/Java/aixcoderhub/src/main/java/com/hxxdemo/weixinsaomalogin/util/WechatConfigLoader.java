package com.hxxdemo.weixinsaomalogin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 类名: WechatConfigLoader  * 描述: 微信初始化配置信息 * 开发人员： weining  * 创建时间：  2017/5/5  */
public class WechatConfigLoader {
    //日志记录对象
    private static Logger log = LoggerFactory.getLogger(WechatConfigLoader.class);

    //配置文件路径
    private static String wechatPath = "wechat.properties";
    //开发平台应用唯一标识
    private static String appId;
    //开放平台应用密钥
    private static String appSecret;
    //微信第三方回调地址
    private static String backUrl;
    //微信公众号唯一标识
    private static String gappId;
    //微信公众号密钥
    private static String gappSecret;
    //最大一级菜单个数
    private static String firstLevelNum;
    //最大二级菜单个数
    private static String twoLevelNum;
    //服务器地址
    private static String serverAddress;
    //公众号原始id
    private static String originalId;
    
    //微信服务号id
    private static String pappid;
    //商户id
    private static String pmchid;
    //商户key
    private static String pkey;
    //生成订单url
    private static String purl;
    //支付成功回调地址
    private static String pcallback;
    
    
    public static String getOriginalId() {
		return originalId;
	}

	public static void setOriginalId(String originalId) {
		WechatConfigLoader.originalId = originalId;
	}

	public static String getServerAddress() {
		return serverAddress;
	}

	public static void setServerAddress(String serverAddress) {
		WechatConfigLoader.serverAddress = serverAddress;
	}

	public static String getFirstLevelNum() {
		return firstLevelNum;
	}

	public static void setFirstLevelNum(String firstLevelNum) {
		WechatConfigLoader.firstLevelNum = firstLevelNum;
	}

	public static String getTwoLevelNum() {
		return twoLevelNum;
	}

	public static void setTwoLevelNum(String twoLevelNum) {
		WechatConfigLoader.twoLevelNum = twoLevelNum;
	}

	public static String getGappId() {
		return gappId;
	}

	public static void setGappId(String gappId) {
		WechatConfigLoader.gappId = gappId;
	}

	public static String getGappSecret() {
		return gappSecret;
	}

	public static void setGappSecret(String gappSecret) {
		WechatConfigLoader.gappSecret = gappSecret;
	}
	public static String getPappid() {
		return pappid;
	}

	public static String getPmchid() {
		return pmchid;
	}

	public static void setPmchid(String pmchid) {
		WechatConfigLoader.pmchid = pmchid;
	}

	public static String getPkey() {
		return pkey;
	}

	public static void setPkey(String pkey) {
		WechatConfigLoader.pkey = pkey;
	}

	public static String getPurl() {
		return purl;
	}

	public static void setPurl(String purl) {
		WechatConfigLoader.purl = purl;
	}

	public static String getPcallback() {
		return pcallback;
	}

	public static void setPcallback(String pcallback) {
		WechatConfigLoader.pcallback = pcallback;
	}

	public static void setPappid(String pappid) {
		WechatConfigLoader.pappid = pappid;
	}



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
        backUrl = props.getProperty("wechat.backUrl");
        gappId = props.getProperty("wechat.gappId");
        gappSecret = props.getProperty("wechat.gappSecret");
        firstLevelNum = props.getProperty("wechat.maxFirstLevelNum");
        twoLevelNum = props.getProperty("wechat.maxTwoLevelNum");
        originalId = props.getProperty("wechat.originalId");
        serverAddress = props.getProperty("server.address");
        pappid = props.getProperty("wechat.pay.appid");
        pmchid = props.getProperty("wechat.pay.mchid");
        pkey = props.getProperty("wechat.pay.key");
        purl = props.getProperty("wechat.pay.url");
        pcallback = props.getProperty("wechat.pay.callback");
        log.debug("load wechat setting success,file path:" + wechatPath);
    }

    public static String getAppId() {
        return appId;
    }

    public static String getAppSecret() {
        return appSecret;
    }

    public static String getBackUrl() {
        return backUrl;
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

    public static void setBackUrl(String backUrl) {
        WechatConfigLoader.backUrl = backUrl;
    }
}