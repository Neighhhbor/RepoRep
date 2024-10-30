package com.hxxdemo.index.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.hxxdemo.index.dao.QemailDao;
import com.hxxdemo.index.entity.Applyaix;
import com.hxxdemo.index.entity.CompanyUser;
import com.hxxdemo.index.entity.DownloadCount;
import com.hxxdemo.index.entity.Feedback;
import com.hxxdemo.index.entity.User;
import com.hxxdemo.index.service.QemailService;
import com.hxxdemo.sysLogin.util.EmailConfigLoader;
import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.web.multipart.MultipartFile;

@Service("QemailService")
public class QemailServiceImpl implements QemailService {
	@Autowired
	private QemailDao qemailDao;
	private static String protocol = "smtp";
	private static String host = "smtp.exmail.qq.com";
	private static String port = "465";
	private static String from = EmailConfigLoader.getFrom();
	private static String account = EmailConfigLoader.getAccount();
	private static String pass = EmailConfigLoader.getPass();
	private static String theme = EmailConfigLoader.getTheme();
	private static String hubfrom = EmailConfigLoader.getHubfrom();
	private static String hubaccount = EmailConfigLoader.getHubaccount();
	private static String hubpass = EmailConfigLoader.getHubpass();
	private static String hubtheme = EmailConfigLoader.getHubtheme();

	//阿里云邮箱账号密码
	private static String user = EmailConfigLoader.getUser();
	private static String password = EmailConfigLoader.getPassword();
	
	
	private static final String ALIDM_SMTP_HOST = "smtpdm.aliyun.com";
 	private static final String ALIDM_SMTP_PORT = "465";//或"80"
	
	//用户名密码验证，需要实现抽象类Authenticator的抽象方法PasswordAuthentication
    static class MyAuthenricator extends Authenticator {
        String u = null;
        String p = null;

        public MyAuthenricator(String u, String p) {
            this.u = u;
            this.p = p;
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(u, p);
        }
    }
    public Boolean sendhub(String title,String code,String email) {
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

	public Boolean sendhubExpire(String title,String code,String email) {
		Properties prop = new Properties();
        //协议
        prop.setProperty("mail.transport.protocol", protocol);
        //服务器
        prop.setProperty("mail.smtp.host", host);
        //端口
        prop.setProperty("mail.smtp.port", port);
        //使用smtp身份验证
        prop.setProperty("mail.smtp.auth", "true");
        //使用SSL，企业邮箱必需！
        //开启安全协议
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.socketFactory", sf);
        Session session = Session.getInstance(prop, new MyAuthenricator(hubaccount, hubpass));
        session.setDebug(false);
        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress(hubfrom, hubtheme));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            mimeMessage.setSubject(title);
            mimeMessage.setSentDate(new Date());
            mimeMessage.setText(code);
            mimeMessage.saveChanges();
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
	}
	public Boolean send(String title,String code,String email) {
		Properties prop = new Properties();
		//协议
		prop.setProperty("mail.transport.protocol", protocol);
		//服务器
		prop.setProperty("mail.smtp.host", host);
		//端口
		prop.setProperty("mail.smtp.port", port);
		//使用smtp身份验证
		prop.setProperty("mail.smtp.auth", "true");
		//使用SSL，企业邮箱必需！
		//开启安全协议
		MailSSLSocketFactory sf = null;
		try {
			sf = new MailSSLSocketFactory();
			sf.setTrustAllHosts(true);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		prop.put("mail.smtp.ssl.enable", "true");
		prop.put("mail.smtp.ssl.socketFactory", sf);
		Session session = Session.getInstance(prop, new MyAuthenricator(account, pass));
		session.setDebug(false);
		MimeMessage mimeMessage = new MimeMessage(session);
		try {
			mimeMessage.setFrom(new InternetAddress(from, theme));
			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			mimeMessage.setSubject(title);
			mimeMessage.setSentDate(new Date());
			mimeMessage.setText(code);
			mimeMessage.saveChanges();
			Transport.send(mimeMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	@Override
	public Boolean sendWithAttach(String title,String content,String email, MultipartFile[] files) {
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
//			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			// 设置发件人
			helper.setFrom(from);

			// 设置收件人
			helper.setTo(new InternetAddress(email));

			// 设置邮件标题
			helper.setSubject(title);

			// 设置邮件内容
			helper.setText(content, false);

			// 绑定附件
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					InputStreamSource source = new ByteArrayResource(file.getBytes());
					helper.addAttachment(file.getOriginalFilename(), source);
				}
			}

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
		} catch (Exception e) {
			e.printStackTrace();
            return false;
		}
		return true;
	}

	public int countEmailByName(Map<String,Object> param) {
		return qemailDao.countEmailByName(param);
	}
	
	public void insertEmail(Map<String,Object> params) {
		qemailDao.insertEmail(params);
	}
	
	public String getEmailIdByMap(Map<String,Object> params) {
		return qemailDao.getEmailIdByMap(params);
	}
	
	public void updateIsapply(String id) {
		qemailDao.updateIsapply(id);
	}
	public void updateIsapplyByCode(Map<String,Object> params) {
		qemailDao.updateIsapplyByCode(params);
	}
	
	public List<User> queryUserByEmail(Map<String,Object> params){
		return qemailDao.queryUserByEmail(params);
	}
	public void  inserUser(User user) {
		qemailDao.inserUser(user);
	}
	
	public void insertApplyaix(Applyaix applyaix) {
		qemailDao.insertApplyaix(applyaix);
	}
	
	public void insertCompanyUser(CompanyUser companyUser) {
		qemailDao.insertCompanyUser(companyUser);
	}
	public void insertFeedback(Feedback feedback) {
		qemailDao.insertFeedback(feedback);
	}
	public List<Map<String,Object>> getApplyIdByMap(Map<String,Object> params) {
		return qemailDao.getApplyIdByMap(params);
	}
	public void updateApplyIsapply(String id) {
		qemailDao.updateApplyIsapply(id);
	}
	public List<Map<String,Object>> queryApplyaixById(String applyid){
		return qemailDao.queryApplyaixById(applyid);
	}
	public void insertDownLoadCount(DownloadCount downLoadCount) {
		qemailDao.insertDownLoadCount(downLoadCount);
	}
	public String queryDownLoadIdByMd5Code(String code) {
		return qemailDao.queryDownLoadIdByMd5Code(code);
	}
	
	public void updateDownloadNumByCode(String code) {
		qemailDao.updateDownloadNumByCode(code);
	}
}
