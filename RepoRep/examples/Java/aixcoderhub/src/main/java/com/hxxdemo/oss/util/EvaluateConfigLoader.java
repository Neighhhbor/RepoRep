package com.hxxdemo.oss.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxxdemo.weixinsaomalogin.util.WechatConfigLoader;

public class EvaluateConfigLoader {
	 //日志记录对象
    private static Logger log = LoggerFactory.getLogger(EvaluateConfigLoader.class);
	
    //配置文件路径
    private static String ossPath = "aliyunoss.properties";
    //阿里云oss endpoint
    private static String endpoint;
    //阿里云oss keyid
    private static String accessKeyId;
    //阿里云oss KeySecret
    private static String accessKeySecret;
    //阿里云oss bucketName
    private static String bucketName;
    //阿里云oss fileHost
    private static String fileHost;
    //选择目录
    private static String filepath;
    //选择目录
    private static String filepathWebsite;
    //选择目录
    private static String filepathNews;

	public static String getFilepathWebsite() {
		return filepathWebsite;
	}

	public static void setFilepathWebsite(String filepathWebsite) {
		EvaluateConfigLoader.filepathWebsite = filepathWebsite;
	}

	public static String getFilepathNews() {
		return filepathNews;
	}

	public static void setFilepathNews(String filepathNews) {
		EvaluateConfigLoader.filepathNews = filepathNews;
	}

	public static String getFilepath() {
		return filepath;
	}

	public static void setFilepath(String filepath) {
		EvaluateConfigLoader.filepath = filepath;
	}

	public static String getEndpoint() {
		return endpoint;
	}

	public static void setEndpoint(String endpoint) {
		EvaluateConfigLoader.endpoint = endpoint;
	}

	public static String getAccessKeyId() {
		return accessKeyId;
	}

	public static void setAccessKeyId(String accessKeyId) {
		EvaluateConfigLoader.accessKeyId = accessKeyId;
	}

	public static String getAccessKeySecret() {
		return accessKeySecret;
	}

	public static void setAccessKeySecret(String accessKeySecret) {
		EvaluateConfigLoader.accessKeySecret = accessKeySecret;
	}

	public static String getBucketName() {
		return bucketName;
	}

	public static void setBucketName(String bucketName) {
		EvaluateConfigLoader.bucketName = bucketName;
	}

	public static String getFileHost() {
		return fileHost;
	}

	public static void setFileHost(String fileHost) {
		EvaluateConfigLoader.fileHost = fileHost;
	}

	static {
        // 类初始化后加载配置文件
        InputStream in = WechatConfigLoader.class.getClassLoader()
                .getResourceAsStream(ossPath);
        Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException e) {
            log.error("load wechat setting error,pleace check the file path:"
                    + ossPath);
            log.error(e.toString(), e);
        }
        endpoint = props.getProperty("spring.file.endpoint");
        accessKeyId = props.getProperty("spring.file.keyid");
        accessKeySecret = props.getProperty("spring.file.keysecret");
        bucketName = props.getProperty("spring.file.bucketname");
        fileHost = props.getProperty("spring.file.filehost");
        filepath = props.getProperty("spring.file.filepath");
        filepathWebsite = props.getProperty("spring.file.filepathwebsite");
        filepathNews = props.getProperty("spring.file.filepathnews");
        
        log.debug("load wechat setting success,file path:" + ossPath);
    }
}
