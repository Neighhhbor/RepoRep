package com.hxxdemo.sms.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxxdemo.weixinsaomalogin.util.WechatConfigLoader;

public class SmsConfigLoader {
	 //日志记录对象
    private static Logger log = LoggerFactory.getLogger(SmsConfigLoader.class);
	
    //配置文件路径
    private static String smsPath = "aliyunsms.properties";
    //阿里云sms id
    private static String accessKeyId;
    //阿里云sms secret
    private static String accessKeySecret;
    //阿里云sms 签名
    private static String signName;
    //阿里云 注册模板中文验证码
    private static String templeteCodeRegister;
    //阿里云 找回密码模板
    private static String templeteCodePassword;
    //阿里云 插件发验证码模板 
    private static String templeteCodePlug;
    //阿里云 插件发验证码模板 
    private static String templeteCodePlugEn;
    //阿里云 绑定微信模板zh
    private static String templeteBindingWechat;
    //阿里云 绑定微信模板en 
    private static String templeteBindingWechatEn;
    //阿里云 绑定微信模板zh
    private static String templeteStautsVerificationCode;
    //阿里云 绑定微信模板en 
    private static String templeteStautsVerificationCodeEn;
  //申请试用通过模板中文
    private static String templeteCodeTrialZh;
    //申请试用通过模板英文
    private static String templeteCodeTrialEn;

	public static String getAccessKeyId() {
		return accessKeyId;
	}
	public static String getTempleteCodePlug() {
		return templeteCodePlug;
	}
	public static void setTempleteCodePlug(String templeteCodePlug) {
		SmsConfigLoader.templeteCodePlug = templeteCodePlug;
	}
	public static String getTempleteCodePlugEn() {
		return templeteCodePlugEn;
	}
	public static void setTempleteCodePlugEn(String templeteCodePlugEn) {
		SmsConfigLoader.templeteCodePlugEn = templeteCodePlugEn;
	}
	public static void setAccessKeyId(String accessKeyId) {
		SmsConfigLoader.accessKeyId = accessKeyId;
	}
	public static String getAccessKeySecret() {
		return accessKeySecret;
	}
	public static void setAccessKeySecret(String accessKeySecret) {
		SmsConfigLoader.accessKeySecret = accessKeySecret;
	}
	public static String getSignName() {
		return signName;
	}
	public static void setSignName(String signName) {
		SmsConfigLoader.signName = signName;
	}
	public static String getTempleteCodeRegister() {
		return templeteCodeRegister;
	}
	public static void setTempleteCodeRegister(String templeteCodeRegister) {
		SmsConfigLoader.templeteCodeRegister = templeteCodeRegister;
	}
	public static String getTempleteCodePassword() {
		return templeteCodePassword;
	}
	public static void setTempleteCodePassword(String templeteCodePassword) {
		SmsConfigLoader.templeteCodePassword = templeteCodePassword;
	}
	
	public static String getTempleteBindingWechat() {
		return templeteBindingWechat;
	}
	public static void setTempleteBindingWechat(String templeteBindingWechat) {
		SmsConfigLoader.templeteBindingWechat = templeteBindingWechat;
	}
	public static String getTempleteBindingWechatEn() {
		return templeteBindingWechatEn;
	}
	public static void setTempleteBindingWechatEn(String templeteBindingWechatEn) {
		SmsConfigLoader.templeteBindingWechatEn = templeteBindingWechatEn;
	}

	public static String getTempleteStautsVerificationCode() {
		return templeteStautsVerificationCode;
	}
	public static void setTempleteStautsVerificationCode(String templeteStautsVerificationCode) {
		SmsConfigLoader.templeteStautsVerificationCode = templeteStautsVerificationCode;
	}
	public static String getTempleteStautsVerificationCodeEn() {
		return templeteStautsVerificationCodeEn;
	}
	public static void setTempleteStautsVerificationCodeEn(String templeteStautsVerificationCodeEn) {
		SmsConfigLoader.templeteStautsVerificationCodeEn = templeteStautsVerificationCodeEn;
	}
	public static String getTempleteCodeTrialZh() {
		return templeteCodeTrialZh;
	}
	public static void setTempleteCodeTrialZh(String templeteCodeTrialZh) {
		SmsConfigLoader.templeteCodeTrialZh = templeteCodeTrialZh;
	}
	public static String getTempleteCodeTrialEn() {
		return templeteCodeTrialEn;
	}
	public static void setTempleteCodeTrialEn(String templeteCodeTrialEn) {
		SmsConfigLoader.templeteCodeTrialEn = templeteCodeTrialEn;
	}

	static {
        // 类初始化后加载配置文件
        InputStream in = WechatConfigLoader.class.getClassLoader()
                .getResourceAsStream(smsPath);
        Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException e) {
            log.error("load wechat setting error,pleace check the file path:"
                    + smsPath);
            log.error(e.toString(), e);
        }
        accessKeyId = props.getProperty("sms.accessKeyId");
        accessKeySecret = props.getProperty("sms.accessKeySecret");
        signName = props.getProperty("sms.signName");
        templeteCodeRegister = props.getProperty("sms.templeteCodeRegister");
        templeteCodePassword = props.getProperty("sms.templeteCodePassword");
        templeteCodePlug = props.getProperty("sms.templeteCodePlug");
        templeteCodePlugEn = props.getProperty("sms.templeteCodePlugEn");
        templeteBindingWechat = props.getProperty("sms.templeteBindingWechat");
        templeteBindingWechatEn = props.getProperty("sms.templeteBindingWechatEn");
        templeteStautsVerificationCode = props.getProperty("sms.templeteStautsVerificationCode");
        templeteStautsVerificationCodeEn = props.getProperty("sms.templeteStautsVerificationCodeEn");
        templeteCodeTrialZh = props.getProperty("sms.templeteCodeTrialZh");
        templeteCodeTrialEn = props.getProperty("sms.templeteCodeTrialEn");
        log.debug("load wechat setting success,file path:" + smsPath);
    }
}
