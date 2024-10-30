package com.hxxdemo.wechat.util;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import net.sf.json.JSONObject;

public class WechatUtil {
	
	private static Logger log = LoggerFactory.getLogger(WechatUtil.class);
	private final static String access_token_url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	
	public static AccessToken getAccessToken(String appid, String appSecret) {
		
		AccessToken accesstoken=new AccessToken();
		//替换真实appid和appsecret
		String requestUrl = access_token_url.replace("APPID", appid).replace("APPSECRET", appSecret);
		//得到json对象
//		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
		JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);
		
		//将得到的json对象的属性值，存到accesstoken中
		accesstoken.setToken(jsonObject.getString("access_token"));
		accesstoken.setExpiresIn(jsonObject.getInt("expires_in"));
		return accesstoken;
	} 
	public static JSONObject httpsRequest(String requestUrl,String requestMethod,String outputStr){
		JSONObject jsonObject=null;
		try {
			//创建SSLContext对象 并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			
			//设置请求方式
			conn.setRequestMethod(requestMethod);
			
			//当outputStr 不为null时 向输出流写数据
			if(null!=outputStr){
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			
			
			// 读取返回数据
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new  InputStreamReader(inputStream,"utf-8");
			BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while((str=bufferedReader.readLine())!=null){
				buffer.append(str);
			}
			
			//释放资源
			bufferedReader.close();
			inputStreamReader.close();
			inputStream=null;
			conn.disconnect();
			jsonObject=JSONObject.fromObject(buffer.toString());
			
			
		} catch (Exception e) {
           log.error("异常了。。。。。。");
			e.printStackTrace();
		}
		
		return jsonObject;
	}
}
