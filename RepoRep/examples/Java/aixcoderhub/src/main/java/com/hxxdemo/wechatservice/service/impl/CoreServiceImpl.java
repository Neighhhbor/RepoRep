package com.hxxdemo.wechatservice.service.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.config.EnGlobals;
import com.hxxdemo.config.Globals;
import com.hxxdemo.wechatservice.dao.CoreDao;
import com.hxxdemo.wechatservice.service.CoreService;
import com.hxxdemo.wechatservice.util.MessageUtil;

@Service("CoreService")
public class CoreServiceImpl implements CoreService{
	
	@Autowired
	private CoreDao dao;

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
            //事件类型
            String eventType = requestMap.get("Event");
            //事件密钥
            String EventKey = requestMap.get("EventKey");
            //票据
            String Ticket = requestMap.get("Ticket");
            String msg = "";
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
            	if (null != Ticket && !"".equals(Ticket) ) {
            		//保存EventKey+FromUserName+language 用来校验是否登录
            		//关注
            		if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
        				msg = Globals.SCAN_SUBSCRIBE;
//        				qrscene_zhgxkj130FFB75_9BFD_4227_99E7_839DBC0F3916
        				EventKey = EventKey.substring(EventKey.indexOf("gxkj")-2, EventKey.length());
            			respMessage = MessageUtil.initTextNew(toUserName, fromUserName, msg);
					}
            		//已关注
                    if ( eventType.equals("SCAN")) {
						msg = Globals.SCAN_SCAN;
//						zhgxkj130FFB75_9BFD_4227_99E7_839DBC0F3916
                    	respMessage = MessageUtil.initTextNew(toUserName, fromUserName, msg);
        			}
                    dao.updateWechatEventKey(fromUserName, EventKey);
            		
    			}
            	
                //取消关注
                if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
					System.out.println("取消关注");
//					dao.unsubscribe(fromUserName);
				}
			}
        }catch (Exception e) {
        	
		}
        return respMessage;
    }
    public void insertWechatEventKey(String language,String wechatid,String eventKey) {
    	dao.insertWechatEventKey(language, wechatid, eventKey);
    }
    
    public Map<String, Object> checkEventKey(String eventkey){
    	return dao.checkEventKey(eventkey);
    }
    
    public void expireEventKeyDisplay(String eventkey) {
    	dao.expireEventKeyDisplay(eventkey);
    }
    
    public Map<String, Object> getUserIdByWechatId(String wechatid) {
    	return dao .getUserIdByWechatId(wechatid);
    }
    
    public String getWechatIdByEventKey(String eventkey) {
    	return dao.getWechatIdByEventKey(eventkey);
    }
    
    public Map<String,Object>  getUserWechatByUsername(String username) {
    	return dao.getUserWechatByUsername(username);
    }
    public void insertUser(Map<String, Object> params) {
    	dao.insertUser(params);
    }
    public void updateUser(Map<String, Object> params) {
    	dao.updateUser(params);
    }
    public void cancelBindingWechat(String token) {
    	dao.cancelBindingWechat(token);
    }
    public 	String getWechatIdByWechatId(String wechatid) {
    	return dao.getWechatIdByWechatId(wechatid);
    }
}
