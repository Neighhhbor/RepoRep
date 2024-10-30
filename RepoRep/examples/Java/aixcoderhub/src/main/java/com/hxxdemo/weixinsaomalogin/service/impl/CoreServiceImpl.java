package com.hxxdemo.weixinsaomalogin.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.weixinsaomalogin.entity.Msglog;
import com.hxxdemo.weixinsaomalogin.entity.SNSUserInfo;
import com.hxxdemo.weixinsaomalogin.resp.Article;
import com.hxxdemo.weixinsaomalogin.resp.Image;
import com.hxxdemo.weixinsaomalogin.resp.ImageMessage;
import com.hxxdemo.weixinsaomalogin.resp.NewsMessage;
import com.hxxdemo.weixinsaomalogin.resp.TextMessage;
import com.hxxdemo.weixinsaomalogin.service.CoreService;
import com.hxxdemo.weixinsaomalogin.service.MsglogService;
import com.hxxdemo.weixinsaomalogin.service.ReplyService;
import com.hxxdemo.weixinsaomalogin.service.WxAccessTokenService;
import com.hxxdemo.weixinsaomalogin.service.WxUserService;
import com.hxxdemo.weixinsaomalogin.util.AccessToken;
import com.hxxdemo.weixinsaomalogin.util.CommonUtil;
import com.hxxdemo.weixinsaomalogin.util.MessageUtil;
import com.hxxdemo.weixinsaomalogin.util.WechatConfigLoader;
import com.hxxdemo.weixinsaomalogin.util.WeiXinUtil;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 核心服务类
 */
@Service("coreService")
public class CoreServiceImpl implements CoreService {

    private static Logger log = LoggerFactory.getLogger(CoreServiceImpl.class);

    @Autowired
	private MsglogService msglogService;
    
    @Autowired
   	private ReplyService replyService;
    
    @Autowired
    private WxAccessTokenService wxAccessTokenService;
    
    @Autowired
    private WxUserService wxUserService;
    /**
     * 处理微信发来的请求（包括事件的推送）
     *
     * @param request
     * @return
     */
    public  String processRequest(HttpServletRequest request) {

        String respMessage = null;
        try {
            // 默认返回的文本消息内容
            String respContent = "请求处理异常，请稍候尝试！";
            // xml请求解析
            Map<String, String> requestMap = MessageUtil.parseXml(request);
            // 发送方帐号（open_id）
            String fromUserName = requestMap.get("FromUserName");
            // 公众帐号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");
            //创建消息时间
            String CreateTime = requestMap.get("CreateTime");
            // 回复文本消息
            TextMessage textMessage = new TextMessage();
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(new Date().getTime());
            textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
            textMessage.setFuncFlag(0);


            // 创建图文消息
            NewsMessage newsMessage = new NewsMessage();
            newsMessage.setToUserName(fromUserName);
            newsMessage.setFromUserName(toUserName);
            newsMessage.setCreateTime(new Date().getTime());
            newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
            newsMessage.setFuncFlag(0);

            // 创建图片消息
            ImageMessage imageMessage = new ImageMessage();
            imageMessage.setToUserName(fromUserName);
            imageMessage.setFromUserName(toUserName);
            imageMessage.setCreateTime(new Date().getTime());
            imageMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_IMAGE);
            List<Article> articleList = new ArrayList<Article>();
            // 接收文本消息内容
            String content = requestMap.get("Content");
            // 自动回复文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
            	//消息记录开始
            	insertFromUserMsgLog(content, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null,null);
            	//消息记录结束
            	CreateTime = (Integer.valueOf(CreateTime)+1)+"";
                //如果用户发送表情，则回复同样表情。
                if (isQqFace(content)) {
                	//判断是否开启未命中
                	int isopend = replyService.countReplyConfigIsopen();
                	if(isopend == 0) {
                		return respMessage;
                	}
                	//调用未命中回复
                	List<Map<String,Object>> list = replyService.getMisfortune();
                	if(null != list && list.size() > 0) {
                		//单位（分钟）回复一次后 到这个时间后才能回去下一条 未命中不回复
                		String opentime = replyService.queryReplyConfigTime();
                		Msglog msglog = new Msglog();
                		msglog.setFromUserName(toUserName);
                		msglog.setToUserName(fromUserName);
                		msglog.setOpentime(opentime);
                		int isMisfortune = msglogService.countMisfortuneTimeNum(msglog);
                		//没有向用户发送过未命中消息
                		if(isMisfortune == 0 ) {
                			//随机数 1~list.size长度的 随机数
                			int randNum =(int)(Math.random()*(list.size()-1+1)+1)-1;
                			String msgtype = list.get(randNum).get("msgtype").toString();
                    		String msg = list.get(randNum).get("msg").toString();
                    		JSONObject msgjson = JSONObject.fromObject(msg);
                    		respContent = msgjson.getString("title");
                            textMessage.setContent(respContent);
                            // 将文本消息对象转换成xml字符串
                            respMessage = MessageUtil.textMessageToXml(textMessage);
                            insertToUserMsgLog(list.get(randNum).get("msg").toString(), toUserName, msgtype, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
                		}
                	}
//                	//表情 努力建设中
//                	List<Map<String,Object>> list = msglogService.getMsgReplayByKeyword("other");
//                	String msgtype = list.get(0).get("msgtype").toString();
//            		String msg = list.get(0).get("msg").toString();
//            		JSONObject msgjson = JSONObject.fromObject(msg);
//                    respContent = msgjson.getString("title");
//                    textMessage.setContent(respContent);
//                    // 将文本消息对象转换成xml字符串
//                    respMessage = MessageUtil.textMessageToXml(textMessage);
//                    insertToUserMsgLog(respContent, toUserName, msgtype, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
                } else {
                	//查询关键字
                	List<Map<String,Object>> replayList = replyService.getMsgReplayByKeyword(content);
                	if(null !=replayList && replayList.size()>0) {
                		String msgtype = replayList.get(0).get("msgtype").toString();
                		String msg = replayList.get(0).get("msg").toString();
                		JSONObject msgjson = JSONObject.fromObject(msg);
                		if(msgtype.equals(MessageUtil.RESP_MESSAGE_TYPE_TEXT)){
                			respContent = msgjson.getString("title");
                			textMessage.setContent(respContent);
                			respMessage = MessageUtil.textMessageToXml(textMessage);
                			insertToUserMsgLog(replayList.get(0).get("msg").toString(), toUserName, msgtype, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
                		}else if(msgtype.equals(MessageUtil.RESP_MESSAGE_TYPE_NEWS)){
                			if(replayList.size()==1) {
                				Article article = new Article();
                				article.setTitle(msgjson.getString("title"));
                				// 图文消息中可以使用QQ表情、符号表情
                				article.setDescription(msgjson.getString("describes"));
                				// 将图片置为空
                				article.setPicUrl(msgjson.getString("picurl"));
                				article.setUrl(msgjson.getString("url"));
                				articleList.add(article);
                				newsMessage.setArticleCount(articleList.size());
                				newsMessage.setArticles(articleList);
                				respMessage = MessageUtil.newsMessageToXml(newsMessage);
                				insertToUserMsgLog(replayList.get(0).get("msg").toString(), toUserName, msgtype, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
                			}else {
                    			for(int i = 0 ; i < replayList.size(); i++) {
                    				msg = replayList.get(i).get("msg").toString();
                            		msgjson = JSONObject.fromObject(msg);
                    				Article article = new Article();
                    				article.setTitle(msgjson.getString("title"));
                    				// 图文消息中可以使用QQ表情、符号表情
                    				article.setDescription("");
                    				// 将图片置为空
                    				article.setPicUrl(msgjson.getString("picurl"));
                    				article.setUrl(msgjson.getString("url"));
                    				articleList.add(article);
                    				newsMessage.setArticleCount(articleList.size());
                    				insertToUserMsgLog(msg, toUserName, msgtype, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
                    			}
                    			newsMessage.setArticles(articleList);
                    			respMessage = MessageUtil.newsMessageToXml(newsMessage);
                			}
                		}else if(msgtype.equals(MessageUtil.RESP_MESSAGE_TYPE_IMAGE)){
                			Image image = new Image();
                			imageMessage.setImage(image);
                			respContent = msgjson.getString("media_id");
                			image.setMediaId(respContent);
                			respMessage = MessageUtil.imageMessageToXml(imageMessage);
                			insertToUserMsgLog(replayList.get(0).get("msg").toString(), toUserName, msgtype, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
                		}
                		
                	}else {
//                		默认回复
//                		list = msglogService.getMsgReplayByKeyword("0");
//                		String msgtype = list.get(0).get("msgtype").toString();
//                		String msg = list.get(0).get("msg").toString();
//                		JSONObject msgjson = JSONObject.fromObject(msg);
//                		if(msgtype.equals("text")){
//                			String msgarr[] = msgjson.getString("title").split("&");
//                			StringBuffer buffer = new StringBuffer();
//                			for(int i = 0 ; i < msgarr.length ; i++){
//                				if(msgarr[i].equals("")) {
//                					buffer.append("\n");
//                				}else {
//                					buffer.append(msgarr[i]).append("\n");
//                				}
//                			}
//                			respContent = String.valueOf(buffer);
//                			textMessage.setContent(respContent);
//                			respMessage = MessageUtil.textMessageToXml(textMessage);
//                			insertToUserMsgLog(respContent, toUserName, msgtype, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
                		//判断是否开启未命中
                    	int isopend = replyService.countReplyConfigIsopen();
                    	if(isopend == 0) {
                    		return respMessage;
                    	}
                		//调用未命中回复
                    	List<Map<String,Object>> list = replyService.getMisfortune();
                    	if(null != list && list.size() > 0) {
                    		//单位（分钟）回复一次后 到这个时间后才能回去下一条 未命中不回复
                    		String opentime = replyService.queryReplyConfigTime();
                    		Msglog msglog = new Msglog();
                    		msglog.setFromUserName(toUserName);
                    		msglog.setToUserName(fromUserName);
                    		msglog.setOpentime(opentime);
                    		int isMisfortune = msglogService.countMisfortuneTimeNum(msglog);
                    		//没有向用户发送过未命中消息
                    		if(isMisfortune == 0 ) {
                    			//随机数 1~list.size长度的 随机数
                    			int randNum =(int)(Math.random()*(list.size()-1+1)+1)-1;
                    			String msgtype = list.get(randNum).get("msgtype").toString();
                        		String msg = list.get(randNum).get("msg").toString();
                        		JSONObject msgjson = JSONObject.fromObject(msg);
                        		respContent = msgjson.getString("title");
                                textMessage.setContent(respContent);
                                // 将文本消息对象转换成xml字符串
                                respMessage = MessageUtil.textMessageToXml(textMessage);
                                insertToUserMsgLog(msg, toUserName, msgtype, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
                    		}
                		}
                	}
                    /**
                	//回复固定消息
                    switch (content) {

                        case "1": {
                            StringBuffer buffer = new StringBuffer();
                            buffer.append("您好，我是小8，请回复数字选择服务：").append("\n\n");
                            buffer.append("11 可查看测试单图文").append("\n");
                            buffer.append("12  可测试多图文发送").append("\n");
                            buffer.append("13  可测试网址").append("\n");

                            buffer.append("或者您可以尝试发送表情").append("\n\n");
                            buffer.append("回复“1”显示此帮助菜单").append("\n");
                            respContent = String.valueOf(buffer);
                            textMessage.setContent(respContent);
                            respMessage = MessageUtil.textMessageToXml(textMessage);
                            break;
                        }
                        case "11": {
                            //测试单图文回复
                            Article article = new Article();
                            article.setTitle("微信公众帐号开发教程Java版");
                            // 图文消息中可以使用QQ表情、符号表情
                            article.setDescription("这是测试有没有换行\n\n如果有空行就代表换行成功\n\n点击图文可以跳转到百度首页");
                            // 将图片置为空
                            article.setPicUrl("http://www.sinaimg.cn/dy/slidenews/31_img/2016_38/28380_733695_698372.jpg");
                            article.setUrl("http://www.baidu.com");
                            articleList.add(article);
                            newsMessage.setArticleCount(articleList.size());
                            newsMessage.setArticles(articleList);
                            respMessage = MessageUtil.newsMessageToXml(newsMessage);
                            break;
                        }
                        case "12": {
                            //多图文发送
                            Article article1 = new Article();
                            article1.setTitle("紧急通知，不要捡这种钱！湛江都已经传疯了！\n");
                            article1.setDescription("");
                            article1.setPicUrl("http://www.sinaimg.cn/dy/slidenews/31_img/2016_38/28380_733695_698372.jpg");
                            article1.setUrl("http://mp.	.qq.com/s?__biz=MjM5Njc2OTI4NQ==&mid=2650924309&idx=1&sn=8bb6ae54d6396c1faa9182a96f30b225&chksm=bd117e7f8a66f769dc886d38ca2d4e4e675c55e6a5e01e768b383f5859e09384e485da7bed98&scene=4#wechat_redirect");

                            Article article2 = new Article();
                            article2.setTitle("湛江谁有这种女儿，请给我来一打！");
                            article2.setDescription("");
                            article2.setPicUrl("http://www.sinaimg.cn/dy/slidenews/31_img/2016_38/28380_733695_698372.jpg");
                            article2.setUrl("http://mp.weixin.qq.com/s?__biz=MjM5Njc2OTI4NQ==&mid=2650924309&idx=2&sn=d7ffc840c7e6d91b0a1c886b16797ee9&chksm=bd117e7f8a66f7698d094c2771a1114853b97dab9c172897c3f9f982eacb6619fba5e6675ea3&scene=4#wechat_redirect");

                            Article article3 = new Article();
                            article3.setTitle("以上图片我就随意放了");
                            article3.setDescription("");
                            article3.setPicUrl("http://www.sinaimg.cn/dy/slidenews/31_img/2016_38/28380_733695_698372.jpg");
                            article3.setUrl("http://mp.weixin.qq.com/s?__biz=MjM5Njc2OTI4NQ==&mid=2650924309&idx=3&sn=63e13fe558ff0d564c0da313b7bdfce0&chksm=bd117e7f8a66f7693a26853dc65c3e9ef9495235ef6ed6c7796f1b63abf1df599aaf9b33aafa&scene=4#wechat_redirect");

                            articleList.add(article1);
                            articleList.add(article2);
                            articleList.add(article3);
                            newsMessage.setArticleCount(articleList.size());
                            newsMessage.setArticles(articleList);
                            respMessage = MessageUtil.newsMessageToXml(newsMessage);
                            break;
                        }

                        case "13": {
                            //测试网址回复
                            respContent = "<a href=\"http://www.baidu.com\">百度主页</a>";
                            textMessage.setContent(respContent);
                            // 将文本消息对象转换成xml字符串
                            respMessage = MessageUtil.textMessageToXml(textMessage);
                            break;
                        }

                        default: {
                            respContent = "（这是里面的）很抱歉，现在小8暂时无法提供此功能给您使用。\n\n回复“1”显示帮助信息";
                            textMessage.setContent(respContent);
                            // 将文本消息对象转换成xml字符串
                            respMessage = MessageUtil.textMessageToXml(textMessage);
                        }
                    }*/
                }
            }
	        // 图片消息
	        else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
	//            respContent = "您发送的是图片消息！";
	        	String PicUrl = requestMap.get("PicUrl");
	        	String MediaId = requestMap.get("MediaId");
	        	String uuid = UUID.randomUUID().toString();
	        	String path = request.getSession().getServletContext().getRealPath("/")+"picurl/" ;
	        	File eFile = new File(path);
			    if(!eFile.exists()) {
			    	eFile.mkdirs();
			    }
			    path +=  uuid + ".jpg";
	        	String picurl = WechatConfigLoader.getServerAddress() + "picurl/" + uuid + ".jpg";
	        	MessageUtil.downloadPicture(PicUrl, path);
	        	insertFromUserMsgLog(MediaId, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime, null,picurl);
//	        	List<Map<String,Object>> list = msglogService.getMsgReplayByKeyword("other");
//	        	String msgtype = list.get(0).get("msgtype").toString();
//	    		String msg = list.get(0).get("msg").toString();
//	    		JSONObject msgjson = JSONObject.fromObject(msg);
//	            respContent = msgjson.getString("title");
//	            textMessage.setContent(respContent);
//	            // 将文本消息对象转换成xml字符串
//	            respMessage = MessageUtil.textMessageToXml(textMessage);
//	            insertToUserMsgLog(respContent, toUserName, msgtype, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
	        	//判断是否开启未命中
            	int isopend = replyService.countReplyConfigIsopen();
            	if(isopend == 0) {
            		return respMessage;
            	}
	        	//调用未命中回复
            	List<Map<String,Object>> list = replyService.getMisfortune();
            	if(null != list && list.size() > 0) {
            		//单位（分钟）回复一次后 到这个时间后才能回去下一条 未命中不回复
            		String opentime = replyService.queryReplyConfigTime();
            		Msglog msglog = new Msglog();
            		msglog.setFromUserName(toUserName);
            		msglog.setToUserName(fromUserName);
            		msglog.setOpentime(opentime);
            		int isMisfortune = msglogService.countMisfortuneTimeNum(msglog);
            		//没有向用户发送过未命中消息
            		if(isMisfortune == 0 ) {
            			//随机数 1~list.size长度的 随机数
            			int randNum =(int)(Math.random()*(list.size()-1+1)+1)-1;
            			String msgtype = list.get(randNum).get("msgtype").toString();
                		String msg = list.get(randNum).get("msg").toString();
                		JSONObject msgjson = JSONObject.fromObject(msg);
                		respContent = msgjson.getString("title");
                        textMessage.setContent(respContent);
                        // 将文本消息对象转换成xml字符串
                        respMessage = MessageUtil.textMessageToXml(textMessage);
                        insertToUserMsgLog(msg, toUserName, msgtype, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
            		}
            	}
	        }
	        // 地理位置消息
	        else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
	//            respContent = "您发送的是地理位置消息！";
//	        	List<Map<String,Object>> list = msglogService.getMsgReplayByKeyword("other");
//	        	String msgtype = list.get(0).get("msgtype").toString();
//	    		String msg = list.get(0).get("msg").toString();
//	    		JSONObject msgjson = JSONObject.fromObject(msg);
//	            respContent = msgjson.getString("title");
//	            textMessage.setContent(respContent);
//	            // 将文本消息对象转换成xml字符串
//	            respMessage = MessageUtil.textMessageToXml(textMessage);
//	            insertToUserMsgLog(respContent, toUserName, msgtype, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
	        	//判断是否开启未命中
            	int isopend = replyService.countReplyConfigIsopen();
            	if(isopend == 0) {
            		return respMessage;
            	}
	        	//调用未命中回复
            	List<Map<String,Object>> list = replyService.getMisfortune();
            	if(null != list && list.size() > 0) {
            		//单位（分钟）回复一次后 到这个时间后才能回去下一条 未命中不回复
            		String opentime = replyService.queryReplyConfigTime();
            		Msglog msglog = new Msglog();
            		msglog.setFromUserName(toUserName);
            		msglog.setToUserName(fromUserName);
            		msglog.setOpentime(opentime);
            		int isMisfortune = msglogService.countMisfortuneTimeNum(msglog);
            		//没有向用户发送过未命中消息
            		if(isMisfortune == 0 ) {
            			//随机数 1~list.size长度的 随机数
            			int randNum =(int)(Math.random()*(list.size()-1+1)+1)-1;
            			String msgtype = list.get(randNum).get("msgtype").toString();
                		String msg = list.get(randNum).get("msg").toString();
                		JSONObject msgjson = JSONObject.fromObject(msg);
                		respContent = msgjson.getString("title");
                        textMessage.setContent(respContent);
                        // 将文本消息对象转换成xml字符串
                        respMessage = MessageUtil.textMessageToXml(textMessage);
                        insertToUserMsgLog(msg, toUserName, msgtype, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
            		}
            	}
	        }
	        // 链接消息
	        else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
	//            respContent = "您发送的是链接消息！";
//	        	List<Map<String,Object>> list = msglogService.getMsgReplayByKeyword("other");
//	        	String msgtype = list.get(0).get("msgtype").toString();
//	    		String msg = list.get(0).get("msg").toString();
//	    		JSONObject msgjson = JSONObject.fromObject(msg);
//	            respContent = msgjson.getString("title");
//	            textMessage.setContent(respContent);
//	            // 将文本消息对象转换成xml字符串
//	            respMessage = MessageUtil.textMessageToXml(textMessage);
//	            insertToUserMsgLog(respContent, toUserName, msgtype, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
	        	//判断是否开启未命中
            	int isopend = replyService.countReplyConfigIsopen();
            	if(isopend == 0) {
            		return respMessage;
            	}
	        	//调用未命中回复
            	List<Map<String,Object>> list = replyService.getMisfortune();
            	if(null != list && list.size() > 0) {
            		//单位（分钟）回复一次后 到这个时间后才能回去下一条 未命中不回复
            		String opentime = replyService.queryReplyConfigTime();
            		Msglog msglog = new Msglog();
            		msglog.setFromUserName(toUserName);
            		msglog.setToUserName(fromUserName);
            		msglog.setOpentime(opentime);
            		int isMisfortune = msglogService.countMisfortuneTimeNum(msglog);
            		//没有向用户发送过未命中消息
            		if(isMisfortune == 0 ) {
            			//随机数 1~list.size长度的 随机数
            			int randNum =(int)(Math.random()*(list.size()-1+1)+1)-1;
            			String msgtype = list.get(randNum).get("msgtype").toString();
                		String msg = list.get(randNum).get("msg").toString();
                		JSONObject msgjson = JSONObject.fromObject(msg);
                		respContent = msgjson.getString("title");
                        textMessage.setContent(respContent);
                        // 将文本消息对象转换成xml字符串
                        respMessage = MessageUtil.textMessageToXml(textMessage);
                        insertToUserMsgLog(msg, toUserName, msgtype, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
            		}
            	}
	
	        }
	        // 音频消息
	        else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
	        	content =  requestMap.get("Recognition");
	        	insertFromUserMsgLog(content, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime, null,null);
	//            respContent = "您发送的是音频消息！";
//	        	List<Map<String,Object>> list = msglogService.getMsgReplayByKeyword("other");
//	        	String msgtype = list.get(0).get("msgtype").toString();
//	    		String msg = list.get(0).get("msg").toString();
//	    		JSONObject msgjson = JSONObject.fromObject(msg);
//	            respContent = msgjson.getString("title");
//	            textMessage.setContent(respContent);
//	            // 将文本消息对象转换成xml字符串
//	            respMessage = MessageUtil.textMessageToXml(textMessage);
//	            insertToUserMsgLog(respContent, toUserName, msgtype, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
	        	//判断是否开启未命中
            	int isopend = replyService.countReplyConfigIsopen();
            	if(isopend == 0) {
            		return respMessage;
            	}
	        	//调用未命中回复
            	List<Map<String,Object>> list = replyService.getMisfortune();
            	if(null != list && list.size() > 0) {
            		//单位（分钟）回复一次后 到这个时间后才能回去下一条 未命中不回复
            		String opentime = replyService.queryReplyConfigTime();
            		Msglog msglog = new Msglog();
            		msglog.setFromUserName(toUserName);
            		msglog.setToUserName(fromUserName);
            		msglog.setOpentime(opentime);
            		int isMisfortune = msglogService.countMisfortuneTimeNum(msglog);
            		//没有向用户发送过未命中消息
            		if(isMisfortune == 0 ) {
            			//随机数 1~list.size长度的 随机数
            			int randNum =(int)(Math.random()*(list.size()-1+1)+1)-1;
            			String msgtype = list.get(randNum).get("msgtype").toString();
                		String msg = list.get(randNum).get("msg").toString();
                		JSONObject msgjson = JSONObject.fromObject(msg);
                		respContent = msgjson.getString("title");
                        textMessage.setContent(respContent);
                        // 将文本消息对象转换成xml字符串
                        respMessage = MessageUtil.textMessageToXml(textMessage);
                        insertToUserMsgLog(msg, toUserName, msgtype, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
            		}
            	}
	        }else if(MessageUtil.REQ_MESSAGE_TYPE_EVENT.equals(msgType)){
	            String eventType = requestMap.get("Event");
	            String eventKey = requestMap.get("EventKey");
	            System.out.println();
	            //若是关注事件  subscribe
	            if(MessageUtil.EVENT_TYPE_SUBSCRIBE.equals(eventType)){
	            	
	            	//更新和插入数据库
	            	updateWxUser(fromUserName, 1);
	            	String mycontent = "感谢您关注aiXcoder";
	            	Map<String,Object> params = new HashMap<String,Object>();
	        		Map<String,Object> reMap = new HashMap<String,Object>();
	        		
	        		
	        		params.put("msgtype", MessageUtil.RESP_MESSAGE_TYPE_TEXT);
	        		//统计关注时文本回复配置
	        		int count = replyService.countFollowConfig(params);
	        		if(count==0) {
	        			params.put("isapply", MessageUtil.RESP_MESSAGE_STATUS_ISNOTAPPLY);
	        			replyService.insertFollowConfig(params);
	        		}else if(count > 1) {
	        			params.put("isapply", null);
	        			replyService.delFollowConfig(params);
	        			params.put("isapply", MessageUtil.RESP_MESSAGE_STATUS_ISNOTAPPLY);
	        			replyService.insertFollowConfig(params);
	        		}else if(count == 1) {
	        			reMap = replyService.oneFollowConfig(params);
	        		}
	        		if(null != reMap && reMap.size()>0) {
	        			if(Integer.valueOf(reMap.get("isapply").toString()) == MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY) {
	        				//查询关注时回复文本信息
	        				Map<String ,Object> param = new HashMap<String,Object>();
	        				param.put("rows", 10);
	        				param.put("page", 0);
	        				param.put("ismisfortune", MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
	        				param.put("msgtype", MessageUtil.RESP_MESSAGE_TYPE_TEXT);
	        				List<Map<String,Object>> list = replyService.queryTextFollowReplyList(param);
	        				if(null != list && list.size()>0) {
	        					int randNum =(int)(Math.random()*(list.size()-1+1)+1)-1;
	        					msgType = list.get(randNum).get("msgtype").toString();
	                    		String msg = list.get(randNum).get("msg").toString();
	                    		JSONObject msgjson = JSONObject.fromObject(msg);
	                    		mycontent = msgjson.getString("title");
	                    		respMessage = MessageUtil.initText(toUserName, fromUserName, mycontent);
	        	                insertToUserMsgLog(msg, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,eventType);
	        				}
	        			}else {
		        			//统计关注时图文回复配置
		        			params.put("msgtype", MessageUtil.RESP_MESSAGE_TYPE_NEWS);
		        			count = replyService.countFollowConfig(params);
			        		if(count==0) {
			        			params.put("isapply", MessageUtil.RESP_MESSAGE_STATUS_ISNOTAPPLY);
			        			replyService.insertFollowConfig(params);
			        		}else if(count > 1) {
			        			params.put("isapply", null);
			        			replyService.delFollowConfig(params);
			        			params.put("isapply", MessageUtil.RESP_MESSAGE_STATUS_ISNOTAPPLY);
			        			replyService.insertFollowConfig(params);
			        		}else if(count == 1) {
			        			reMap = replyService.oneFollowConfig(params);
			        		}
			        		if(null != reMap && reMap.size()>0) {
			        			if(Integer.valueOf(reMap.get("isapply").toString()) == MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY) {
			        				Map<String ,Object> param = new HashMap<String,Object>();
			        				param.put("rows", 8);
			        				param.put("page", 0);
			        				param.put("ismisfortune", MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
			        				param.put("msgtype", MessageUtil.RESP_MESSAGE_TYPE_NEWS);
			        				List<Map<String,Object>> list = replyService.queryNewsFollowReplyList(param);
			        				msgType = list.get(0).get("msgtype").toString();
			        				String msg = list.get(0).get("msg").toString();
			        				JSONObject msgjson = JSONObject.fromObject(msg);
			        				if(null != list && list.size()>0) {
			        					if(list.size()==1) {
			                        		Article article = new Article();
			                				article.setTitle(msgjson.getString("title"));
			                				// 图文消息中可以使用QQ表情、符号表情
			                				article.setDescription(msgjson.getString("describes"));
			                				// 将图片置为空
			                				article.setPicUrl(msgjson.getString("picurl"));
			                				article.setUrl(msgjson.getString("url"));
			                				articleList.add(article);
			                				newsMessage.setArticleCount(articleList.size());
			                				newsMessage.setArticles(articleList);
			                				respMessage = MessageUtil.newsMessageToXml(newsMessage);
			                				insertToUserMsgLog(msg, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
			        					}else {
			        						for(int i = 0 ; i < list.size(); i++) {
			                    				msg = list.get(i).get("msg").toString();
			                            		msgjson = JSONObject.fromObject(msg);
			                    				Article article = new Article();
			                    				article.setTitle(msgjson.getString("title"));
			                    				// 图文消息中可以使用QQ表情、符号表情
			                    				article.setDescription("");
			                    				// 将图片置为空
			                    				article.setPicUrl(msgjson.getString("picurl"));
			                    				article.setUrl(msgjson.getString("url"));
			                    				articleList.add(article);
			                    				newsMessage.setArticleCount(articleList.size());
			                    				insertToUserMsgLog(msg, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
			                    			}
			        						newsMessage.setArticles(articleList);
			                    			respMessage = MessageUtil.newsMessageToXml(newsMessage);
			        					}
			        				}
			        			}else {
			        				//统计关注时图片回复配置
				        			params.put("msgtype", MessageUtil.RESP_MESSAGE_TYPE_IMAGE);
				        			count = replyService.countFollowConfig(params);
					        		if(count==0) {
					        			params.put("isapply", MessageUtil.RESP_MESSAGE_STATUS_ISNOTAPPLY);
					        			replyService.insertFollowConfig(params);
					        		}else if(count > 1) {
					        			params.put("isapply", null);
					        			replyService.delFollowConfig(params);
					        			params.put("isapply", MessageUtil.RESP_MESSAGE_STATUS_ISNOTAPPLY);
					        			replyService.insertFollowConfig(params);
					        		}else if(count == 1) {
					        			reMap = replyService.oneFollowConfig(params);
					        		}
					        		if(Integer.valueOf(reMap.get("isapply").toString()) == MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY) {
					        			Map<String ,Object> param = new HashMap<String,Object>();
				        				param.put("rows", 10);
				        				param.put("page", 0);
				        				param.put("ismisfortune", MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
				        				param.put("msgtype", MessageUtil.RESP_MESSAGE_TYPE_IMAGE);
				        				List<Map<String,Object>> list = replyService.queryImageFollowReplyList(param);
				        				msgType = list.get(0).get("msgtype").toString();
				        				if(null != list && list.size()>0) {
				        					int randNum =(int)(Math.random()*(list.size()-1+1)+1)-1;
				        					msgType = list.get(randNum).get("msgtype").toString();
				                    		String msg = list.get(randNum).get("msg").toString();
				                    		JSONObject msgjson = JSONObject.fromObject(msg);
				                    		Image image = new Image();
				                			imageMessage.setImage(image);
				                			mycontent = msgjson.getString("media_id");
				                			image.setMediaId(mycontent);
				                			respMessage = MessageUtil.imageMessageToXml(imageMessage);
				        	                insertToUserMsgLog(msg, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,eventType);
				        				}
				        			}else {
				        				respMessage = MessageUtil.initText(toUserName, fromUserName, mycontent);
				        				insertToUserMsgLog(mycontent, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,eventType);
				        			}
		        				}
			        		}else {
			        			respMessage = MessageUtil.initText(toUserName, fromUserName, mycontent);
			        			insertToUserMsgLog(mycontent, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,eventType);
	        				}
	        			}
	        		}else {
	        			//统计关注时图文回复配置
	        			params.put("msgtype", MessageUtil.RESP_MESSAGE_TYPE_NEWS);
	        			count = replyService.countFollowConfig(params);
		        		if(count==0) {
		        			params.put("isapply", MessageUtil.RESP_MESSAGE_STATUS_ISNOTAPPLY);
		        			replyService.insertFollowConfig(params);
		        		}else if(count > 1) {
		        			params.put("isapply", null);
		        			replyService.delFollowConfig(params);
		        			params.put("isapply", MessageUtil.RESP_MESSAGE_STATUS_ISNOTAPPLY);
		        			replyService.insertFollowConfig(params);
		        		}else if(count == 1) {
		        			reMap = replyService.oneFollowConfig(params);
		        		}
		        		if(null != reMap && reMap.size()>0) {
		        			if(Integer.valueOf(reMap.get("isapply").toString()) == MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY) {
		        				Map<String ,Object> param = new HashMap<String,Object>();
		        				param.put("rows", 8);
		        				param.put("page", 0);
		        				param.put("ismisfortune", MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
		        				param.put("msgtype", MessageUtil.RESP_MESSAGE_TYPE_NEWS);
		        				List<Map<String,Object>> list = replyService.queryNewsFollowReplyList(param);
		        				msgType = list.get(0).get("msgtype").toString();
		        				String msg = list.get(0).get("msg").toString();
		        				JSONObject msgjson = JSONObject.fromObject(msg);
		        				if(null != list && list.size()>0) {
		        					if(list.size()==1) {
		                        		Article article = new Article();
		                				article.setTitle(msgjson.getString("title"));
		                				// 图文消息中可以使用QQ表情、符号表情
		                				article.setDescription(msgjson.getString("describes"));
		                				// 将图片置为空
		                				article.setPicUrl(msgjson.getString("picurl"));
		                				article.setUrl(msgjson.getString("url"));
		                				articleList.add(article);
		                				newsMessage.setArticleCount(articleList.size());
		                				newsMessage.setArticles(articleList);
		                				respMessage = MessageUtil.newsMessageToXml(newsMessage);
		                				insertToUserMsgLog(msg, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
		        					}else {
		        						
		        						for(int i = 0 ; i < list.size(); i++) {
		                    				msg = list.get(i).get("msg").toString();
		                            		msgjson = JSONObject.fromObject(msg);
		                    				Article article = new Article();
		                    				article.setTitle(msgjson.getString("title"));
		                    				// 图文消息中可以使用QQ表情、符号表情
		                    				article.setDescription("");
		                    				// 将图片置为空
		                    				article.setPicUrl(msgjson.getString("picurl"));
		                    				article.setUrl(msgjson.getString("url"));
		                    				articleList.add(article);
		                    				newsMessage.setArticleCount(articleList.size());
		                    				insertToUserMsgLog(msg, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
		                    			}
		        						newsMessage.setArticles(articleList);
		                    			respMessage = MessageUtil.newsMessageToXml(newsMessage);
		        					}
		        				}
		        			}else {
	        					respMessage = MessageUtil.initText(toUserName, fromUserName, mycontent);
	        	                insertToUserMsgLog(mycontent, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,eventType);
	        				}
		        		}else {
		        			//统计关注时图片回复配置
		        			params.put("msgtype", MessageUtil.RESP_MESSAGE_TYPE_IMAGE);
		        			count = replyService.countFollowConfig(params);
			        		if(count==0) {
			        			params.put("isapply", MessageUtil.RESP_MESSAGE_STATUS_ISNOTAPPLY);
			        			replyService.insertFollowConfig(params);
			        		}else if(count > 1) {
			        			params.put("isapply", null);
			        			replyService.delFollowConfig(params);
			        			params.put("isapply", MessageUtil.RESP_MESSAGE_STATUS_ISNOTAPPLY);
			        			replyService.insertFollowConfig(params);
			        		}else if(count == 1) {
			        			reMap = replyService.oneFollowConfig(params);
			        		}
			        		if(Integer.valueOf(reMap.get("isapply").toString()) == MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY) {
			        			Map<String ,Object> param = new HashMap<String,Object>();
		        				param.put("rows", 10);
		        				param.put("page", 0);
		        				param.put("ismisfortune", MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
		        				param.put("msgtype", MessageUtil.RESP_MESSAGE_TYPE_IMAGE);
		        				List<Map<String,Object>> list = replyService.queryImageFollowReplyList(param);
		        				msgType = list.get(0).get("msgtype").toString();
		        				if(null != list && list.size()>0) {
		        					int randNum =(int)(Math.random()*(list.size()-1+1)+1)-1;
		        					msgType = list.get(randNum).get("msgtype").toString();
		                    		String msg = list.get(randNum).get("msg").toString();
		                    		JSONObject msgjson = JSONObject.fromObject(msg);
		                    		Image image = new Image();
		                			imageMessage.setImage(image);
		                			mycontent = msgjson.getString("media_id");
		                			image.setMediaId(mycontent);
		                			respMessage = MessageUtil.imageMessageToXml(imageMessage);
		        	                insertToUserMsgLog(msg, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,eventType);
		        				}
		        			}else {
		        				respMessage = MessageUtil.initText(toUserName, fromUserName, mycontent);
		        				insertToUserMsgLog(mycontent, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,eventType);
		        			}
        				}
	        		}
	                
	            }else if(MessageUtil.EVENT_TYPE_UNSUBSCRIBE.equals(eventType)){
	            	//更新数据库
	            	updateWxUser(fromUserName, 0);
	            	//取消关注了
	            	String mycontent = "您取消了关注";
//	            	respMessage = MessageUtil.initText(toUserName, fromUserName, mycontent);
	            	insertToUserMsgLog(mycontent, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,eventType);
	            } else if(MessageUtil.EVENT_TYPE_CLICK.equals(eventType)){
	            	//click
	            	insertFromUserMsgLog(eventKey, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime, eventType,null);
	            	System.out.println(eventKey);
	            	String mycontent = "拼命建设中……";
	            	//查询消息回复消息
	            	List<Map<String,Object>> list = replyService.queryClickReplyList(Long.valueOf(eventKey));
	            	if(null != list && list.size()>0) {
	            		msgType = list.get(0).get("msgtype").toString();
	            		String msg = list.get(0).get("msg").toString();
	            		JSONObject msgjson = JSONObject.fromObject(msg);
	            		if(list.size()==1) {
	            			if(msgType.equals(MessageUtil.RESP_MESSAGE_TYPE_TEXT)) {
	            				//文本
                    			respContent = msgjson.getString("title");
                    			textMessage.setContent(respContent);
                    			respMessage = MessageUtil.textMessageToXml(textMessage);
                    			insertToUserMsgLog(msg, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
	            			}else if(msgType.equals(MessageUtil.RESP_MESSAGE_TYPE_IMAGE)) {
	            				//图片
	            				Image image = new Image();
	                			imageMessage.setImage(image);
	                			mycontent = msgjson.getString("media_id");
	                			image.setMediaId(mycontent);
	                			respMessage = MessageUtil.imageMessageToXml(imageMessage);
	        	                insertToUserMsgLog(msg, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,eventType);
	            			}else if(msgType.equals(MessageUtil.RESP_MESSAGE_TYPE_NEWS)) {
	            				//单图文
	            				Article article = new Article();
                				article.setTitle(msgjson.getString("title"));
                				// 图文消息中可以使用QQ表情、符号表情
                				article.setDescription(msgjson.getString("describes"));
                				// 将图片置为空
                				article.setPicUrl(msgjson.getString("picurl"));
                				article.setUrl(msgjson.getString("url"));
                				articleList.add(article);
                				newsMessage.setArticleCount(articleList.size());
                				newsMessage.setArticles(articleList);
                				respMessage = MessageUtil.newsMessageToXml(newsMessage);
                				insertToUserMsgLog(msg, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
	            			}
	            		}else if(list.size()<=8) {
	            			//回复图文
	            			for(int i = 0 ; i < list.size(); i++) {
                				msg = list.get(i).get("msg").toString();
                        		msgjson = JSONObject.fromObject(msg);
                				Article article = new Article();
                				article.setTitle(msgjson.getString("title"));
                				// 图文消息中可以使用QQ表情、符号表情
                				article.setDescription("");
                				// 将图片置为空
                				article.setPicUrl(msgjson.getString("picurl"));
                				article.setUrl(msgjson.getString("url"));
                				articleList.add(article);
                				newsMessage.setArticleCount(articleList.size());
                				insertToUserMsgLog(msg, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
                			}
    						newsMessage.setArticles(articleList);
                			respMessage = MessageUtil.newsMessageToXml(newsMessage);
	            		}
	            	}else {
	            		respMessage = MessageUtil.initText(toUserName, fromUserName, mycontent);
	            		insertToUserMsgLog(mycontent, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime,null);
	            	}
	            	
	            }else  if(MessageUtil.EVENT_TYPE_VIEW.equals(eventType)){
	            	insertFromUserMsgLog(eventKey, toUserName, msgType, MessageUtil.RESP_MESSAGE_STATUS_SIGN_ZERO, fromUserName, CreateTime, eventType,null);
	            }
	        } 
    	} catch (Exception e) {
    		e.printStackTrace();
    		return respMessage;
    	}

    	return respMessage;
    }
    
    public void updateWxUser(String fromUserName,int isFollow) {
    	SNSUserInfo snsUserInfo= new SNSUserInfo();
    	if(isFollow == 1 ) {
    		String url = WeiXinUtil.userinfo_url;
    		AccessToken token = wxAccessTokenService.getAccessToken(WechatConfigLoader.getGappId(), WechatConfigLoader.getGappSecret(),false);
    		url = url.replace("ACCESS_TOKEN", token.getToken());
    		url = url.replaceAll("OPENID", fromUserName);
    		//得到json对象
    		JSONObject jsonUser = CommonUtil.httpsRequest(url, "GET", null);
    		
    		snsUserInfo.setOpenId(jsonUser.getString("openid"));
    		snsUserInfo.setNickname(jsonUser.getString("nickname"));
    		snsUserInfo.setSex(jsonUser.getInt("sex"));
    		snsUserInfo.setCountry(jsonUser.getString("country"));
    		snsUserInfo.setProvince(jsonUser.getString("province"));
    		snsUserInfo.setCity(jsonUser.getString("city"));
    		snsUserInfo.setHeadImgUrl(jsonUser.getString("headimgurl"));
    		snsUserInfo.setUnionid(jsonUser.getString("unionid"));
    		snsUserInfo.setRemark(jsonUser.getString("remark"));
    		snsUserInfo.setIsfollow(isFollow);
    		snsUserInfo.setSubscribetime(jsonUser.getString("subscribe_time"));
    		//通过unionid验证用户是否存在
    		int ubool = wxUserService.queryWxUserCountByUnionid(snsUserInfo.getUnionid());
    		if(ubool >0) {
    			//执行update
    			wxUserService.updateWxUser(snsUserInfo);
    		}else{
    			//执行insert
    			wxUserService.insertWxUser(snsUserInfo);
    		}
    	}else {
    		//验证用户是否存在
    		int ubool = wxUserService.queryWxUserCountByOpenid(fromUserName);
    		if(ubool >0) {
    			snsUserInfo.setIsfollow(isFollow);
    			snsUserInfo.setOpenId(fromUserName);
    			wxUserService.wxUserCancelFollow(snsUserInfo);
    		}
    	}
    }
    
	/**
	 * 判断是否是QQ表情
	 *
	 * @param content
	 * @return
	 */
	public static boolean isQqFace(String content) {
        boolean result = false;

        // 判断QQ表情的正则表达式
        String qqfaceRegex = "/::\\)|/::~|/::B|/::\\||/:8-\\)|/::<|/::$|/::X|/::Z|/::'\\(|/::-\\||/::@|/::P|/::D|/::O|/::\\(|/::\\+|/:--b|/::Q|/::T|/:,@P|/:,@-D|/::d|/:,@o|/::g|/:\\|-\\)|/::!|/::L|/::>|/::,@|/:,@f|/::-S|/:\\?|/:,@x|/:,@@|/::8|/:,@!|/:!!!|/:xx|/:bye|/:wipe|/:dig|/:handclap|/:&-\\(|/:B-\\)|/:<@|/:@>|/::-O|/:>-\\||/:P-\\(|/::'\\||/:X-\\)|/::\\*|/:@x|/:8\\*|/:pd|/:<W>|/:beer|/:basketb|/:oo|/:coffee|/:eat|/:pig|/:rose|/:fade|/:showlove|/:heart|/:break|/:cake|/:li|/:bome|/:kn|/:footb|/:ladybug|/:shit|/:moon|/:sun|/:gift|/:hug|/:strong|/:weak|/:share|/:v|/:@\\)|/:jj|/:@@|/:bad|/:lvu|/:no|/:ok|/:love|/:<L>|/:jump|/:shake|/:<O>|/:circle|/:kotow|/:turn|/:skip|/:oY|/:#-0|/:hiphot|/:kiss|/:<&|/:&>";
        Pattern p = Pattern.compile(qqfaceRegex);
        Matcher m = p.matcher(content);
        if (m.matches()) {
        	result = true;
        }
        return result;
    }

	public void insertFromUserMsgLog(String content,String toUserName,String msgType,int status,String fromUserName,String CreateTime,String event,String PicUrl) {
		Msglog msglog = new Msglog();
    	msglog.setContent(content);
    	msglog.setFromUserName(fromUserName);
    	msglog.setMsgType(msgType);
    	msglog.setStatus(status);
    	msglog.setToUserName(toUserName);
    	msglog.setCreateTime(CreateTime);
    	if(null !=event) {
    		msglog.setEvent(event);
    	}
    	if(null !=PicUrl) {
    		msglog.setPicUrl(PicUrl); 
    	}
    	msglogService.insertMsgLog(msglog);
	}
	public void insertToUserMsgLog(String content,String toUserName,String msgType,int status,String fromUserName,String CreateTime,String event) {
		Msglog msglog = new Msglog();
    	msglog.setContent(content);
    	msglog.setFromUserName(toUserName);
    	msglog.setMsgType(msgType);
    	msglog.setStatus(status);
    	msglog.setToUserName(fromUserName);
    	msglog.setCreateTime(CreateTime);
    	if(null !=event) {
    		msglog.setEvent(event);
    	}
    	msglogService.insertMsgLog(msglog);
	}

	public JSONObject getReplayJson(String keyword) {
		List<Map<String,Object>> list = replyService.getMsgReplayByKeyword(keyword);
    	String msgtype = list.get(0).get("msgtype").toString();
		String msg = list.get(0).get("msg").toString();
		JSONObject msgjson = JSONObject.fromObject(msg);
		return msgjson;
	}
}