package com.hxxdemo.modeltrail.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aliyuncs.exceptions.ClientException;
import com.hxxdemo.modeltrail.dao.ModelTrailDao;
import com.hxxdemo.modeltrail.service.ModelTrailService;
import com.hxxdemo.modeltrail.util.SendEmail;
import com.hxxdemo.sms.util.SendSms;
import com.hxxdemo.sms.util.SmsConfigLoader;

@Service("ModelTrailService")
public class ModelTrailServiceImpl implements ModelTrailService {

	@Autowired
	ModelTrailDao dao;
	
	@Override
	public Integer getUserStatus(String token) {
		return dao.getUserStatus(token);
	}
	
	@Override
	public Integer getWebUserStatus(String token) {
		return dao.getWebUserStatus(token);
	}
	
	private static String aixcoderUrl = "https://aixcoder.com";
	public static String zhEmail = "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'><html>	<head>		<title>			aiXcoder试用		</title>	</head>	<style>	</style>	<body style='margin:0;'>		<div class='trialdown' style=\"width:100%;height:100vh;background:linear-gradient(180deg,#1732A6 0%,#02083C 100%);padding-top:4%;\">			<div style=\"background: url('https://file.aixcoder.com/logs/2022-04-18/8c049e9755b14d399e8b4efb8c140c2c.png') top center no-repeat;background-size:contain;margin:0 auto;font-family:PingFangSC-Regular,PingFang SC;text-align:center;width:360px;\">				<img style=\"width:120px;padding:40px 0 20px;\" src='https://file.aixcoder.com/img/2020/logo2.png' alt='' class='trialdown-logo'>				<h3 style=\"font-size:26px;font-weight:600;color:#474747;line-height:34px;margin:0;\">					<b>恭喜您，可以免费使用aiXcoder云端服务了。</b>				</h3>	 			<h5 style=\"font-size:15px;font-weight:500;color:#666666;line-height:36px;margin:0;padding:10px 0 8px;\">					快来尝鲜体验吧				</h5> 	<div>				<a href='"+aixcoderUrl+"/#/trial'><img style=\"width:126px;\" src='https://file.aixcoder.com/aixcoderweb/image/button_cloud_guide.png'				/></a></div> 								<p style=\"font-size:14px;font-weight:400;color:#AAAAAA;line-height:36px;margin:5px 0px 10px;\">					试用期限为3个月，到期后可再次申请				</p>				<div style=\"width:85px;height:85px;padding:2px;background:#FFFFFF;border-radius:11px;margin:10px auto 5px;overflow: hidden;\">					<img style=\"width:100%;height:100%;\" src='https://file.aixcoder.com/logs/2022-04-15/f0dcfc0b4352422e982c3ce5ac40aa3f.png'>				</div>				<p style=\"font-size:12px;font-weight:400;color:#AAAAAA;line-height:22px;margin:5px;color:#ABBDFF;\">扫描二维码，加入aiXcoder用户群</p>				<img style=\"width:80px;margin-top: 5px;padding-bottom: 80px;\" src='https://file.aixcoder.com/logs/2022-04-15/fc29e43ba31c4d028e0286938a077741.png'				/>			</div>		</div>	</body></html>";
	public static String enEmail = "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'><html>	<head>		<title>			aiXcoder trial		</title>	</head>	<style>	</style>	<body style='margin:0;'>		<div class='trialdown' style=\"width:100%;height:100vh;background:linear-gradient(180deg,#1732A6 0%,#02083C 100%);padding-top:4%;\">			<div style=\"background: url('https://file.aixcoder.com/logs/2022-04-18/8c049e9755b14d399e8b4efb8c140c2c.png') top center no-repeat;background-size:contain;margin:0 auto;font-family:PingFangSC-Regular,PingFang SC;text-align:center;width:360px;\">				<img style=\"width:120px;padding:40px 0 20px;\" src='https://file.aixcoder.com/img/2020/logo2.png' alt='' class='trialdown-logo'>			<h3 style=\"font-size:16px;font-weight:600;color:#474747;line-height:20px;margin:0;\">	Congratulations on starting your </h3><h3 style=\"font-size:16px;font-weight:600;color:#474747;line-height:20px;margin:0;\">journey with aiXcoder Cloud Service. 	</h3>				<h5 style=\"font-size:16px;font-weight:500;color:#666666;line-height:36px;margin:0;padding:15px 0 8px;\">					Try it for free			</h5>				<div>				<a href='"+aixcoderUrl+"/en/#/trial' ><img style=\"width:140px;\" src='https://file.aixcoder.com/aixcoderweb/image/button_cloud_guide_en.png' /></a></div>					<p style=\"font-size:12px;font-weight:400;color:#AAAAAA;line-height:12px;margin-top:20px;\">					Your free trial period is 3 months.</p> <p style=\"font-size:12px;font-weight:400;color:#AAAAAA;line-height:12px;margin-top:-8px;\">     After expired, you can apply again.	</p>				<div style=\"width:85px;height:85px;padding:2px;background:#FFFFFF;border-radius:11px;margin:10px auto 5px;overflow: hidden;\">					<img style=\"width:100%;height:100%;\" src='https://file.aixcoder.com/logs/2022-04-15/f0dcfc0b4352422e982c3ce5ac40aa3f.png'>				</div>				<p style=\"font-size:12px;font-weight:400;color:#AAAAAA;line-height:22px;margin:5px;color:#ABBDFF;\">Scan the QR code, join us</p>				<img style=\"width:80px;margin-top: 5px;padding-bottom: 100px;\" src='https://file.aixcoder.com/logs/2022-04-15/fc29e43ba31c4d028e0286938a077741.png'				/>			</div>		</div>	</body></html>";
	public static String zhEmailTitle = "您申请的aiXcoder云端服务试用权限已通过";
	public static String enEmailTitle = "Your free trial application of aiXcoder Cloud Service is approved";
	private Integer month = 12;
	
	@Override
	public void saveTrail(Long userId,String retLanguage) {
		dao.saveTrail(userId,retLanguage);
		//直接通过
		Long id = dao.getModelTrialId(userId);
		dao.approved(id,month);
		
//		Map<String,Object> user = dao.getUserNameByUserId(userId);
//		String username = "";
//		if (null == user.get("telephone")) {
//			username = user.get("email").toString();
//		}else {
//			username = user.get("telephone").toString();
//		}
//		if (username.contains("@")) {
//			if (retLanguage.equals("zh")) {
//				SendEmail.sendEmail(zhEmailTitle, zhEmail, username);
//			}else {
//				SendEmail.sendEmail(enEmailTitle, enEmail, username);
//			}
//		}else{
//			if (retLanguage.equals("zh")) {
//				String templeteCode = SmsConfigLoader.getTempleteCodeTrialZh();
//				try {
//					SendSms.sendSms(username, "", templeteCode);
//				} catch (ClientException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}else {
//				String templeteCode = SmsConfigLoader.getTempleteCodeTrialEn();
//				try {
//					SendSms.sendSms(username, "", templeteCode);
//				} catch (ClientException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
	
	}

	@Override
	public Long checkUserLogin( String token) {
		return dao.checkUserLogin( token);
	}
	
	@Override
	public Long checkPlugUserLogin( String token) {
		return dao.checkPlugUserLogin( token);
	}

	@Override
	public String getUserEmail(String token) {
		
		return dao.getUserEmail(token);
	}

	@Override
	public Integer getWebUserExpireStatus(Long userId) {
		return dao.getWebUserExpireStatus(userId);
	}
	
	@Override
	public void editTrial(Long userId, String retLanguage) {
		dao.editTrial(userId, retLanguage);
		//直接通过
		Long id = dao.getModelTrialId(userId);
		dao.approved(id,month);
	}
	
	@Override
	public String getUserName(Long userId) {
		return dao.getUserName(userId);
	}
}
