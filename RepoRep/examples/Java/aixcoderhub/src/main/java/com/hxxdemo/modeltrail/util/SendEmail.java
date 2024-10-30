package com.hxxdemo.modeltrail.util;

import java.util.Properties;
import java.io.UnsupportedEncodingException;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.hxxdemo.sysLogin.util.EmailConfigLoader;

public class SendEmail {
	private static final String ALIDM_SMTP_HOST = "smtpdm.aliyun.com";
 	private static final String ALIDM_SMTP_PORT = "465";//或"80"
 	
 	//阿里云邮箱账号密码
	private static String user = EmailConfigLoader.getUser();
	private static String password = EmailConfigLoader.getPassword();
	
	public static Boolean sendEmail(String title,String code,String email) {
    	// 配置发送邮件的环境属性
        final Properties props = new Properties();
        // 表示SMTP发送邮件，需要进行身份验证
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", ALIDM_SMTP_HOST);
        props.put("mail.smtp.port", ALIDM_SMTP_PORT);
        //25被封  转用 465 start
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.port", ALIDM_SMTP_PORT);
        //25被封  转用 465 end
        // 发件人的账号，填写控制台配置的发信地址,比如xxx@xxx.com
        props.put("mail.user", user);
        // 访问SMTP服务时需要提供的密码(在控制台选择发信地址进行设置)
        props.put("mail.password", password);
        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
     // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        MimeMessage message = new MimeMessage(mailSession){
        };
        try {
			// 设置发件人邮件地址和名称。填写控制台配置的发信地址,比如xxx@xxx.com。和上面的mail.user保持一致。名称用户可以自定义填写。
			InternetAddress from = new InternetAddress(user, "aiXcoder");
			message.setFrom(from);
			//可选。设置回信地址
//        Address[] a = new Address[1];
//        a[0] = new InternetAddress("");
//        message.setReplyTo(a);
			// 设置收件人邮件地址，比如yyy@yyy.com
			InternetAddress to = new InternetAddress(email);
			message.setRecipient(MimeMessage.RecipientType.TO, to);
			// 设置邮件标题
			message.setSubject(title);
			// 设置邮件的内容体
			message.setContent(code, "text/html;charset=UTF-8");
			// 发送邮件
			Transport.send(message);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
        return true;
	}
}
