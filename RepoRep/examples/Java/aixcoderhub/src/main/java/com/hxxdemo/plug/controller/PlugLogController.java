package com.hxxdemo.plug.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hxxdemo.config.Globals;
import com.hxxdemo.exception.utils.R;
import com.hxxdemo.oss.AliyunOSSUtil;
import com.hxxdemo.plug.service.PlugService;
import com.hxxdemo.task.dingding.DingdingUtils;
import com.hxxdemo.task.dingding.TextEntity;
import com.hxxdemo.util.CommonUtil;
import com.taobao.api.ApiException;

@Controller
public class PlugLogController {

	private static final Logger log = LoggerFactory.getLogger(PlugLogController.class);
	@Autowired
	private PlugService service;
	@RequestMapping(value = "reportIssue2",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> reportIssue2(@RequestParam String contact,@RequestParam String comment,@RequestParam(required = false) MultipartFile log,HttpServletRequest request){
		String logUrl = "";
		if (null != log) {
			String path = System.getProperty("user.dir")+"/";
			
			String fileName = log.getOriginalFilename();
			String suffixName = fileName.substring(fileName.lastIndexOf("."));
			String logFileName= UUID.randomUUID().toString()+suffixName;
			File logFile = new File(path + logFileName);
			try {
				log.transferTo(logFile);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			logUrl = AliyunOSSUtil.upload(logFile);
			logFile.delete();
		}
		Map<String, Object> map = new HashMap<String,Object>();
//		map.put("zip", logUrl);
//		map.put("contact", contact);
//		map.put("comment", comment);
		String realIP = CommonUtil.getIpAddress(request);
		Map<String, Object> params = new HashMap<String,Object>();
		String emojiToStr = resolveToByteFromEmoji(comment);
		params.put("contact", contact);
		params.put("comment", emojiToStr);
		params.put("ip", realIP);
		params.put("log_url", logUrl);
		service.savePlugLog(params);
		TextEntity textEntity = new TextEntity();
		textEntity.setIsAtAll(false);
		String content = "";
		if (null == log) {
			content = "有新的插件反馈信息，\n"
					+ "联系方式："+contact+"，\n"
					+ "反馈内容:"+comment;
		}else {
			content = "有新的插件反馈信息，\n"
					+ "联系方式："+contact+"，\n"
					+ "反馈内容:"+comment+",\n"
					+ "日志地址:"+logUrl;
		}
		textEntity.setContent(content);
//		textEntity.setContent("这是一个卸载反馈测试！");
		try {
			boolean bool = sendTextMessage(textEntity);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("errorcode", Globals.ERRORCODE0);
        map.put("errormessage", Globals.ERRORMESSAGE0);
		return map;
	}
	/**
     * 将str中的emoji表情转为byte数组
     *
     * @param str
     * @return
     */
    public static String resolveToByteFromEmoji(String str) {
        if(str != null && str != ""){
            Pattern pattern = Pattern
                    .compile("[^(\u2E80-\u9FFF\\w\\s`~!@#\\$%\\^&\\*\\(\\)_+-？（）——=\\[\\]{}\\|;。，、《》”：；“！……’:'\"<,>\\.?/\\\\*)]");
            Matcher matcher = pattern.matcher(str);
            StringBuffer sb2 = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb2, resolveToByte(matcher.group(0)));
            }
            matcher.appendTail(sb2);
            return sb2.toString();
        }
        return str;
    }

    /**
     * 将str中的byte数组类型的emoji表情转为正常显示的emoji表情
     *
     * @param str
     * @return
     */
    public static String resolveToEmojiFromByte(String str) {
        if(str != null && str != ""){
            Pattern pattern2 = Pattern.compile("<:([[-]\\d*[,]]+):>");
            Matcher matcher2 = pattern2.matcher(str);
            StringBuffer sb3 = new StringBuffer();
            while (matcher2.find()) {
                matcher2.appendReplacement(sb3, resolveToEmoji(matcher2.group(0)));
            }
            matcher2.appendTail(sb3);
            return sb3.toString();
        }
        return str;
    }

    private static String resolveToByte(String str) {
        byte[] b = str.getBytes();
        StringBuffer sb = new StringBuffer();
        sb.append("<:");
        for (int i = 0; i < b.length; i++) {
            if (i < b.length - 1) {
                sb.append(Byte.valueOf(b[i]).toString() + ",");
            } else {
                sb.append(Byte.valueOf(b[i]).toString());
            }
        }
        sb.append(":>");
        return sb.toString();
    }

    private static String resolveToEmoji(String str) {
        str = str.replaceAll("<:", "").replaceAll(":>", "");
        String[] s = str.split(",");
        byte[] b = new byte[s.length];
        for (int i = 0; i < s.length; i++) {
            b[i] = Byte.valueOf(s[i]);
        }
        return new String(b);
    }
	@RequestMapping(value = "reportIssue",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> reportIssue(@RequestParam String contact,@RequestParam String comment,@RequestParam String log,
			HttpServletRequest request){
		
		Map<String, Object> map = new HashMap<String,Object>();
		
		//获取根目录
		String path = System.getProperty("user.dir")+"/";
		String fileName= UUID.randomUUID().toString()+".txt";
		writeToFileWithCommonsIO(log,path,fileName);
		File file = new File(path+fileName);
		String url = AliyunOSSUtil.upload(file);
		String realIP = CommonUtil.getIpAddress(request);
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("contact", contact);
		params.put("comment", comment);
		params.put("ip", realIP);
		params.put("log_url", url);
		service.savePlugLog(params);
		file.delete();
		TextEntity textEntity = new TextEntity();
		textEntity.setIsAtAll(false);
		textEntity.setContent("有新的插件反馈信息，\n"
				+ "联系方式："+contact+"，\n"
				+ "反馈内容:"+comment+",\n"
				+ "日志地址:"+url);
//		textEntity.setContent("这是一个卸载反馈测试！");
		try {
			boolean bool = sendTextMessage(textEntity);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("errorcode", Globals.ERRORCODE0);
        map.put("errormessage", Globals.ERRORMESSAGE0);
		return map;
	}
	
	
	
//	public static void main(String[] args) {
//		String log = "log";
//		String fileName = UUID.randomUUID().toString()+".log";
//		String path = "c:/file/";
//		writeToFileWithCommonsIO(log,path,fileName);
//		
//	}
	public void writeToFileWithCommonsIO(String data,String path,String fileName){
	    try {
	        byte[] sourceBytes = data.getBytes("UTF-8");
	        if(null!=sourceBytes){
	            FileUtils.writeByteArrayToFile( new File(path+fileName), sourceBytes,false);//这里的false代表写入的文件是从头开始重新写入，或者理解为清空文件内容后重新写；若为true,则是接着原本文件内容的结尾开始写
	        }
	    } catch (UnsupportedEncodingException e) {
	        // do something
	    } catch (IOException e){
	        // do something
	    } finally {
	        // do something
	    }
	}
	private static String webhook = "https://oapi.dingtalk.com/robot/send?access_token=c90228ed212fda5cdb65918556940d027912867ebd290c56435ac9aea70acd08";
	public static boolean sendTextMessage(TextEntity text) throws ApiException {
		return DingdingUtils.sendToDingding(text,webhook);
	}
	
	@RequestMapping(value = "issue/feedback",method = RequestMethod.POST)
	@ResponseBody
	public R feedback(@RequestParam String describtion,@RequestParam String contact,MultipartFile[] files){
		if (null == describtion || "".equals(describtion)) {
			return R.error(Globals.ERRORCODE9007,Globals.ERRORMESSAGE9007);
		}
		if (null == contact || "".equals(contact)) {
			return R.error(Globals.ERRORCODE9007,Globals.ERRORMESSAGE9007);
		}
		String urls = "";
		if (null != files && files.length > 0) {
			try {
				String img[] = { "bmp", "jpg", "jpeg", "png", "gif","mp4"};
				for (int i = 0; i < files.length; i++) {
					MultipartFile file = files[i];
					String filename = file.getOriginalFilename();
	                // 获取文件后缀名并转化为写，用于后续比较
	                String fileType = filename.substring(filename.lastIndexOf(".") + 1, filename.length()).toLowerCase();
	                boolean bool =false;
	                // 创建图片类型数组
	                for (int j = 0; j < img.length; j++){
	                	if (img[j].equals(fileType)) {
	                		bool = true;
	                	}
	                }
	                if(!bool) {
	                	return R.error(Globals.ERRORCODE3003,Globals.ERRORMESSAGE3003);
	                }
	                if(!"".equals(filename.trim())){
	                    File newFile = new File(filename);
	                    FileOutputStream os = new FileOutputStream(newFile);
	                    os.write(file.getBytes());
	                    os.close();
	                    file.transferTo(newFile);
	                    //上传到OSS
	                    String url = AliyunOSSUtil.upload(newFile);
	                    newFile.delete();
	                    urls += url+";";
	                }
				}
			} catch (Exception e) {
				log.error(e.getMessage());
	            return R.error(Globals.ERRORCODE3002,Globals.ERRORMESSAGE3002);
			}
		}
//		保存到数据库
		Map<String,Object> params = new HashMap<>();
		params.put("describtion", describtion);
		params.put("contact", contact);
		params.put("urls", urls);
		service.issueFeedback(params);
		return R.ok();
	}

}
