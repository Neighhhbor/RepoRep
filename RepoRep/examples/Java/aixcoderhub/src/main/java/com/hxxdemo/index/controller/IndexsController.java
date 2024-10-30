package com.hxxdemo.index.controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.config.Globals;
import com.hxxdemo.index.entity.Applyaix;
import com.hxxdemo.index.entity.CompanyUser;
import com.hxxdemo.index.entity.DownloadCount;
import com.hxxdemo.index.entity.Feedback;
import com.hxxdemo.index.entity.User;
import com.hxxdemo.index.service.QemailService;
import com.hxxdemo.index.util.Md5Util;


@Controller
@RequestMapping(value = "/Index")
public class IndexsController {

	@Autowired
    private QemailService qemailService;
	/**
	 * 发送邮件接口地址
	 * @param email 邮箱地址
	 */
	@RequestMapping(value = "sendmail")
    @ResponseBody
    public Map<String,Object> sendmail(HttpServletRequest request, String email) throws ServletException, IOException {
		
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == email || email.equals("")) {
			map.put("errorcode", Globals.ERRORCODE1001);
			map.put("errormessage", Globals.ERRORMESSAGE1001);
		}else {
			String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
			if(match(regex,email)) {
				
				//验证邮件是否已经发送
				//...
				Map<String,Object> param  = new HashMap<String,Object>();
				param.put("email", email);
				param.put("type", Globals.MSGCODETYPE1);
				int codeis = qemailService.countEmailByName(param);
				if(codeis>0) {
					map.put("errorcode", Globals.ERRORCODE1010);
					map.put("errormessage", Globals.ERRORMESSAGE1010);
				}else {
					String randcode = (int)(Math.random()*(999999-100000+1)+100000)+"";
					Map<String,Object> params = new HashMap<String,Object>();
					params.put("isapply", "0");
					params.put("code", randcode);
					params.put("email", email);
					params.put("type", Globals.MSGCODETYPE1);
					params.put("time", Globals.CODEEMAILTIME);
					//插入数据到数据库
					qemailService.insertEmail(params);
					Boolean bool =  false;
					bool = qemailService.send("试用验证码", "您的验证码是 :"+randcode, email);
					if(bool) {
						map.put("errorcode", Globals.ERRORCODE0);
						map.put("errormessage", Globals.ERRORMESSAGE0);
					}else {
						map.put("errorcode", Globals.ERRORCODE1011);
						map.put("errormessage", Globals.ERRORMESSAGE1011);
					}
				}
			}else {
				map.put("errorcode", Globals.ERRORCODE1007);
				map.put("errormessage", Globals.ERRORMESSAGE1007);
			}
		}
		return map;
	}
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	/**
	 * 获取邀请码接口地址
	 * @param email 邮箱地址
	 * @param name 姓名
	 * @param codemsg 验证码
	 * @param product 产品id
	 * @param action 用途
	 */
	@RequestMapping(value = "applycode")
    @ResponseBody
    public Map<String,Object> applycode(HttpServletRequest request, String email,String name,String codemsg,String product,String action) throws ServletException, IOException {
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == product || "".equals(product)) {
			product= "1";
		}
		if(null == name || "".equals(name)) {
			map.put("errorcode", Globals.ERRORCODE9001);
			map.put("errormessage",Globals.ERRORMESSAGE9001);
			return map;
		}
		if(null == action || "".equals(action)) {
			map.put("errorcode", Globals.ERRORCODE9002);
			map.put("errormessage",Globals.ERRORMESSAGE9002);
			return map;
		}
		if(null == codemsg || "".equals(codemsg)) {
			map.put("errorcode", Globals.ERRORCODE1009);
			map.put("errormessage",Globals.ERRORMESSAGE1009);
			return map;
		}
		String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		if(!match(regex,email)) {
			map.put("errorcode", Globals.ERRORCODE1007);
			map.put("errormessage", Globals.ERRORMESSAGE1007);
			return map;
		}
		//验证码时效
		String id =  "" ;
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("email", email);
		params.put("code", codemsg);
		params.put("type", Globals.MSGCODETYPE1);
		id = qemailService.getEmailIdByMap(params);
		if(null == id || "".equals(id)) {
			map.put("errorcode", Globals.ERRORCODE1012);
			map.put("errormessage",Globals.ERRORMESSAGE1012);
			return map;
		}else {
			//改变isapply状态
			qemailService.updateIsapply(id);
		}
		//查看是否有用户申请过
		List<User> listUser = new ArrayList<User>(); 
		params.put("name", name);
		listUser = qemailService.queryUserByEmail(params);
		if(null == listUser || listUser.size() == 0) {
			//插入user表
			User user  = new User();
			user.setName(name);
			user.setEmail(email);
			user.setDelstatus(0);
			qemailService.inserUser(user);
			listUser = qemailService.queryUserByEmail(params);
		}
		//插入到applyaix
		Applyaix applyaix = new Applyaix();
		applyaix.setUserid(listUser.get(0).getId());
		applyaix.setProduct(product);
		applyaix.setAction(Integer.valueOf(action));
		qemailService.insertApplyaix(applyaix);
		map.put("errorcode", Globals.ERRORCODE0);
		map.put("errormessage", "申请成功");
		return map;
	}
	/**
	 * 申请VIP接口地址
	 * @param name 公司名称
	 * @param context 联系方式
	 * @param context_name 联系人名称
	 * @param action 用途
	 */
	@RequestMapping(value = "companytrial")
    @ResponseBody
    public Map<String,Object> companytrial(HttpServletRequest request, String name,String context,String context_name,String action) throws ServletException, IOException {
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == name || "".equals(name)) {
			map.put("errorcode", Globals.ERRORCODE9006);
			map.put("errormessage",Globals.ERRORMESSAGE9006);
			return map;
		}
		if(null == context || "".equals(context)) {
			map.put("errorcode", Globals.ERRORCODE9007);
			map.put("errormessage",Globals.ERRORMESSAGE9007);
			return map;
		}
		if(null == context_name || "".equals(context_name)) {
			map.put("errorcode", Globals.ERRORCODE9008);
			map.put("errormessage",Globals.ERRORMESSAGE9008);
			return map;
		}
		if(null == action || "".equals(action)) {
			map.put("errorcode", Globals.ERRORCODE9009);
			map.put("errormessage",Globals.ERRORMESSAGE9009);
			return map;
		}
		CompanyUser companyUser = new CompanyUser();
		companyUser.setAction(action);
		companyUser.setName(name);
		companyUser.setContext(context);
		companyUser.setContext_name(context_name);
		qemailService.insertCompanyUser(companyUser);
		map.put("errorcode", Globals.ERRORCODE0);
		map.put("errormessage", "我们已经收到您的申请试用请求");
		return map;
	}
	/**
	 * 使用反馈接口地址
	 * @param email 邮箱
	 * @param aix_ver 插件版本
	 * @param content 意见
	 * @param development 专业领域
	 * @param profession 开发环境
	 */
	@RequestMapping(value = "feedbackadd")
    @ResponseBody
    public Map<String,Object> feedbackadd(HttpServletRequest request, String email,String aix_ver,String content,String development,String profession) throws ServletException, IOException {
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == email || "".equals(email)) {
			map.put("errorcode", Globals.ERRORCODE1001);
			map.put("errormessage",Globals.ERRORMESSAGE1001);
			return map;
		}
		if(null == aix_ver || "".equals(aix_ver)) {
			map.put("errorcode", Globals.ERRORCODE9004);
			map.put("errormessage",Globals.ERRORMESSAGE9004);
			return map;
		}
		if(null == content || "".equals(content)) {
			map.put("errorcode", Globals.ERRORCODE9005);
			map.put("errormessage",Globals.ERRORMESSAGE9005);
			return map;
		}
		String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		if(!match(regex,email)) {
			map.put("errorcode", Globals.ERRORCODE1007);
			map.put("errormessage",Globals.ERRORMESSAGE1007);
			return map;
		}
		Feedback feedback = new Feedback();
		feedback.setEmail(email);
		feedback.setAix_ver(aix_ver);
		feedback.setContent(content);
		feedback.setProfession(profession);
		feedback.setDevelopment(development);
		qemailService.insertFeedback(feedback);
		map.put("errorcode", Globals.ERRORCODE0);
		map.put("errormessage", "反馈成功");
		return map;
	}
	
	/**
	 * 接收code接口地址
	 * @param email 邮箱地址
	 * @param codemsg 邮箱验证码
	 * @param applycode 邀请码
	 * @throws NoSuchAlgorithmException 
	 */
	@RequestMapping(value = "chkmailcode")
    @ResponseBody
    public Map<String,Object> chkmailcode(HttpServletRequest request, String email,String codemsg,String applycodemsg) throws ServletException, IOException, NoSuchAlgorithmException {
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == email || "".equals(email)) {
			map.put("errorcode", Globals.ERRORCODE1001);
			map.put("errormessage",Globals.ERRORMESSAGE1001);
			return map;
		}
		if(null == codemsg || "".equals(codemsg)) {
			map.put("errorcode", Globals.ERRORCODE1009);
			map.put("errormessage",Globals.ERRORMESSAGE1009);
			return map;
		}
		if(null == applycodemsg || "".equals(applycodemsg)) {
			map.put("errorcode", Globals.ERRORCODE9010);
			map.put("errormessage",Globals.ERRORMESSAGE9010);
			return map;
		}
		String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		if(!match(regex,email)) {
			map.put("errorcode", Globals.ERRORCODE1007);
			map.put("errormessage",Globals.ERRORMESSAGE1007);
			return map;
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("email", email);
		params.put("code", codemsg);
		params.put("applycode", applycodemsg);
		params.put("type", Globals.MSGCODETYPE1);
		String mailid = "";
		List<Map<String,Object>>  applyList = new ArrayList<Map<String,Object>>();
		mailid = qemailService.getEmailIdByMap(params);
		applyList = qemailService.getApplyIdByMap(params);
		if(null !=mailid && !"".equals(mailid) && null != applyList && applyList.size()!=0) {
			qemailService.updateIsapply(mailid);
			qemailService.updateApplyIsapply(applyList.get(0).get("appid").toString());
		}else {
			if(null !=mailid && !"".equals(mailid)) {
				map.put("errorcode", Globals.ERRORCODE9011);
				map.put("errormessage",Globals.ERRORMESSAGE9011);
				return map;
			}else {
				map.put("errorcode", Globals.ERRORCODE1012);
				map.put("errormessage",Globals.ERRORMESSAGE1012);
				return map;
			}
		}
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        list = qemailService.queryApplyaixById(applyList.get(0).get("appid").toString());
        String randStr = (int)(Math.random()*(999999-100000+1)+1000)+list.get(0).get("action").toString()+new Date();
        String downloadcode = Md5Util.md5(randStr);
        DownloadCount  downloadCount = new DownloadCount();
        downloadCount.setUserid(Long.valueOf(list.get(0).get("userid").toString()));
        downloadCount.setDownnum(0);
        downloadCount.setProduct(Integer.valueOf(list.get(0).get("product").toString()));
        downloadCount.setDowloadcode(downloadcode);
        qemailService.insertDownLoadCount(downloadCount);
        
		Map<String,Object> codeMap = new HashMap<String,Object>();
		codeMap.put("code", downloadcode);
        map.put("info",  codeMap);
        map.put("errorcode", Globals.ERRORCODE0);
        map.put("errormessage", "验证成功");
		return map;
	}
	/**
	 * @param code
	 */
	@RequestMapping(value = "downloadfile")
    @ResponseBody
    public Map<String,Object> downloadfile(HttpServletRequest request, String code,HttpServletResponse response) throws ServletException, IOException, NoSuchAlgorithmException {
		
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == code || "".equals(code)) {
			map.put("errorcode", Globals.ERRORCODE9003);
			map.put("errorcode", Globals.ERRORMESSAGE9003);
			return map;
		}
		String downloadId = qemailService.queryDownLoadIdByMd5Code(code);
		
		if(null != downloadId && !downloadId.equals("")) {
			//下载加1
			qemailService.updateDownloadNumByCode(code);
//			String filePath = "d:/survey.jar";
			String filePath = "/var/www/html/download/dev/plugin-0.4.x.zip";
			File file = new File(filePath);
			if(!file.exists()) {
				map.put("errorcode", Globals.ERRORCODE9013);
				map.put("errorcode", Globals.ERRORMESSAGE9013);
				return map;
			}else {
				download(filePath,request,response);
			}
		}else {
			map.put("errorcode", Globals.ERRORCODE9012);
			map.put("errorcode", Globals.ERRORMESSAGE9012);
			return map;
		}
		
		return map;
	}
	public static void download(String filePath,HttpServletRequest request, HttpServletResponse response) {
		try {
			request.setCharacterEncoding("UTF-8");
			String name = "aixcoder试用版.zip";
			//第一步：设置响应类型
			response.setContentType("application/force-download");//应用程序强制下载
			//第二读取文件
			String path = filePath;
			InputStream in = new FileInputStream(path);
			//设置响应头，对文件进行url编码
			name = URLEncoder.encode(name, "UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename="+name);   
			response.setContentLength(in.available());
			
			//第三步：老套路，开始copy
			OutputStream out = response.getOutputStream();
			byte[] b = new byte[1024];
			int len = 0;
			while((len = in.read(b))!=-1){
			  out.write(b, 0, len);
			}
			out.flush();
			out.close();
			in.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
	

