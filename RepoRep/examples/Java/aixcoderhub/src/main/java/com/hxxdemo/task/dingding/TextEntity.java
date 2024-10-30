package com.hxxdemo.task.dingding;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

public class TextEntity {

	private String msgType;

    // 显示内容
    private String content;

    // 是否at所有人
    private Boolean isAtAll;

    // 被@人的手机号(在content里添加@人的手机号)
    private List<String> atMobiles;

    public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
    public String getMsgType() {
        return "text";
    }
    public void setContent(String content) {
    	this.content = content;
    }
    public String getContent() {
		return content;
	}
    public void setIsAtAll(Boolean isAtAll) {
    	this.isAtAll = isAtAll;
    }
	public Boolean getIsAtAll() {
		return isAtAll;
	}
	public void setAtMobiles(List<String> atMobiles) {
		this.atMobiles = atMobiles;
	}
	public List<String> getAtMobiles() {
		return atMobiles;
	}

	public String getJSONObjectString() {
        // text类型
        JSONObject content = new JSONObject();
        content.put("content", this.getContent());
        
        // at some body
        JSONObject atMobile = new JSONObject();
        if(this.getAtMobiles().size() > 0){
            List<String> mobiles = new ArrayList<String>();
            for (int i=0;i<this.getAtMobiles().size();i++){
                mobiles.add(this.getAtMobiles().get(i));
            }
            if(mobiles.size()>0){
                atMobile.put("atMobiles", mobiles);
            }
            atMobile.put("isAtAll", this.getIsAtAll());
        }

        JSONObject json = new JSONObject();
        json.put("msgtype", this.getMsgType());
        json.put("text", content);
        json.put("at", atMobile);
        return json.toJSONString();
    }
}
