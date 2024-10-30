package com.hxxdemo.sysLogin;

import java.util.HashMap;
import java.util.Map;

import com.hxxdemo.sysLogin.entity.User;

public class Singleton {

	public Map<String,User> map = new HashMap<String,User>() ;
	public Map<String,Long> tokenMap = new HashMap<>();
	public Map<Long,String> moreTokenMap = new HashMap<>();
	private Singleton(){
    }
    private static volatile Singleton instance = null;
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized(Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
	public Map<String, User> getMap() {
		return map;
	}
	public void setMap(String token,User user) {
		this.map.put(token, user);
	}
	public Map<String,Long> getTokenMap(){
		return tokenMap;
	}
	public void setTokenMap(String token,Long time){
		this.tokenMap.put(token, time);
	}
	public Map<Long,String> getMoreTokenMap(){
		return moreTokenMap;
	}
	public void setMoreTokenMap(Long token,String time){
		this.moreTokenMap.put(token, time);
	}
	public void cleanTokenMap() {
		this.tokenMap = new HashMap<>();
	}
    
}