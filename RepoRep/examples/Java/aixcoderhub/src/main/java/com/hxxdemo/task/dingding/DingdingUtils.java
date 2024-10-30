package com.hxxdemo.task.dingding;


import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;

public class DingdingUtils {

    /**
     * 发送钉钉消息
     * @param jsonString 消息内容
     * @param webhook 钉钉自定义机器人webhook
     * @return
     * @throws ApiException 
     */
    public static boolean sendToDingding(TextEntity entity,String webhook) throws ApiException {
    	DingTalkClient client = new DefaultDingTalkClient(webhook);
    	OapiRobotSendRequest request = new OapiRobotSendRequest();
    	request.setMsgtype(entity.getMsgType());
    	OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
    	text.setContent(entity.getContent());
    	request.setText(text);
    	OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
    	at.setAtMobiles(entity.getAtMobiles());
    	at.setIsAtAll(entity.getIsAtAll().toString());
    	request.setAt(at);

    	OapiRobotSendResponse response = client.execute(request);
		return response.isSuccess();
    }
}
