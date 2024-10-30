package com.hxxdemo.weixinsaomalogin.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hxxdemo.weixinsaomalogin.resp.Article;
import com.hxxdemo.weixinsaomalogin.resp.ImageMessage;
import com.hxxdemo.weixinsaomalogin.resp.NewsMessage;
import com.hxxdemo.weixinsaomalogin.resp.TextMessage;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * 消息工具类
 * 
 * @author lvhuina
 * @date 2016-01-15
 */
public class MessageUtil {
	
	/**
	 * 是否是未命中消息 0 否 1 是
	 */
	public static final int RESP_MESSAGE_STATUS_ISNotMISFORTUNE=0;
	public static final int RESP_MESSAGE_STATUS_ISMISFORTUNE=1;
	
	/**
	 * 是否使用 0 否 1 是
	 */
	public static final int RESP_MESSAGE_STATUS_ISNOTAPPLY = 0;
	public static final int RESP_MESSAGE_STATUS_ISAPPLY = 1;

	/**
	 * 是否开启未命中 0 否 1 是
	 */
	public static final int RESP_MESSAGE_STATUS_ISNOTOPEN=0;
	public static final int RESP_MESSAGE_STATUS_ISOPEN=1;
	/**
	 * 标记状态 0 未标记  1 标记
	 */
	public static final int RESP_MESSAGE_STATUS_SIGN_ZERO = 0;
	public static final int RESP_MESSAGE_STATUS_SIGN_ONE = 1;
	/**
	 * 返回消息类型：文本
	 */
	public static final String RESP_MESSAGE_TYPE_TEXT = "text";
	/**
	 * 返回消息类型：图文
	 */
	public static final String RESP_MESSAGE_TYPE_IMAGE = "image";

	/**
	 * 返回消息类型：音乐
	 */
	public static final String RESP_MESSAGE_TYPE_MUSIC = "music";

	/**
	 * 返回消息类型：图文
	 */
	public static final String RESP_MESSAGE_TYPE_NEWS = "news";

	/**
	 * 请求消息类型：文本
	 */
	public static final String REQ_MESSAGE_TYPE_TEXT = "text";

	/**
	 * 请求消息类型：图片
	 */
	public static final String REQ_MESSAGE_TYPE_IMAGE = "image";

	/**
	 * 请求消息类型：链接
	 */
	public static final String REQ_MESSAGE_TYPE_LINK = "link";

	/**
	 * 请求消息类型：地理位置
	 */
	public static final String REQ_MESSAGE_TYPE_LOCATION = "location";

	/**
	 * 请求消息类型：音频
	 */
	public static final String REQ_MESSAGE_TYPE_VOICE = "voice";

	/**
	 * 请求消息类型：推送
	 */
	public static final String REQ_MESSAGE_TYPE_EVENT = "event";

	/**
	 * 事件类型：subscribe(订阅)
	 */
	public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";

	/**
	 * 事件类型：unsubscribe(取消订阅)
	 */
	public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";

	/**
	 * 事件类型：CLICK(自定义菜单点击事件)
	 */
	public static final String EVENT_TYPE_CLICK = "CLICK";
	
	/**
	 * 事件类型：VIEW(自定义菜单点击事件)
	 */
	public static final String EVENT_TYPE_VIEW = "VIEW";

	/**
	 * 解析微信发来的请求（XML）
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
		// 将解析结果存储在HashMap中
		Map<String, String> map = new HashMap<String, String>();

		// 从request中取得输入流
		InputStream inputStream = request.getInputStream();
		// 读取输入流
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputStream);
		// 得到xml根元素
		Element root = document.getRootElement();
		// 得到根元素的所有子节点
		List<Element> elementList = root.elements();

		// 遍历所有子节点
		for (Element e : elementList)
			map.put(e.getName(), e.getText());

		// 释放资源
		inputStream.close();
		inputStream = null;

		return map;
	}

	
	
	

	

	

	/**
	 * 扩展xstream，使其支持CDATA块
	 * 
	 * @date 2016-01-15
	 */
	private static XStream xstream = new XStream(new XppDriver() {
		public HierarchicalStreamWriter createWriter(Writer out) {
			return new PrettyPrintWriter(out) {
				// 对所有xml节点的转换都增加CDATA标记
				boolean cdata = true;

				@SuppressWarnings("unchecked")
				public void startNode(String name, Class clazz) {
//					super.startNode(name, clazz);
					super.startNode(name);
				}

				protected void writeText(QuickWriter writer, String text) {
					if (cdata) {
						writer.write("<![CDATA[");
						writer.write(text);
						writer.write("]]>");
					} else {
						writer.write(text);
					}
				}
			};
		}
	});
	
	
	

	
	
	
	
	/** 
     * 从html中抽取出历史上的今天信息 
     *  
     * @param html 
     * @return 
     */  
    public static String extract(String html) {  
        StringBuffer buffer = null;  
        // 日期标签：区分是昨天还是今天  
        String dateTag = getMonthDay(0);  
  
        Pattern p = Pattern.compile("(.*)(<div class=\"listren\">)(.*?)(</div>)(.*)");  
        Matcher m = p.matcher(html);  
        if (m.matches()) {  
            buffer = new StringBuffer();  
            if (m.group(3).contains(getMonthDay(-1)))  
                dateTag = getMonthDay(-1);  
  
            // 拼装标题  
            buffer.append("≡≡").append(" 历史上的  ").append(dateTag).append("≡≡").append("\n\n");  
  
            // 抽取需要的数据  
            for (String info : m.group(3).split("  ")) {  
            	 info = info.replace(dateTag, "").replace("（图）", "").replaceAll("</?[^>]+>", "").replace("&nbsp;", "").trim();  
                // 在每行末尾追加2个换行符  
                if (!"".equals(info)) {  
                    buffer.append(info).append("\n\n");  
                }  
            }  
        }  
        // 将buffer最后两个换行符移除并返回  
        return (null == buffer) ? null : buffer.substring(0, buffer.lastIndexOf("\n\n"));  
    }  
  
    /** 
     * 获取前/后n天日期(M月d日) 
     *  
     * @return 
     */  
    public static String getMonthDay(int diff) {  
        DateFormat df = new SimpleDateFormat("M月d日");  
        Calendar c = Calendar.getInstance();  
        c.add(Calendar.DAY_OF_YEAR, diff);  
        return df.format(c.getTime());  
    }  
  
  
  
    
    /**
     * 获取城市天气预报代码 
     * @param args
     */
    
    public static  String getWeather(String cityName){
    	
    	
    	return "w";
    }
    /**
     * 文本消息对象转换成xml
     *
     * @param textMessage 文本消息对象
     * @return xml
     */
    public static String textMessageToXml(TextMessage textMessage) {
        xstream.alias("xml", textMessage.getClass());
        return xstream.toXML(textMessage);
    }
    /**
     * 图文消息对象转换成xml
     *
     * @param newsMessage 图文消息对象
     * @return xml
     */
    public static String newsMessageToXml(NewsMessage newsMessage) {
        xstream.alias("xml", newsMessage.getClass());
        xstream.alias("item", new Article().getClass());
        return xstream.toXML(newsMessage);
    }
    /**
     * 图文消息对象转换成xml
     *
     * @param newsMessage 图片消息对象
     * @return xml
     */
    public static String imageMessageToXml(ImageMessage imageMessage) {
    	xstream.alias("xml", imageMessage.getClass());
    	return xstream.toXML(imageMessage);
    }
    /**
     * xml转为map
     * @param request
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public static Map<String, String> xmlToMap(HttpServletRequest request ) throws DocumentException, IOException
    {
        Map<String,String> map = new HashMap<String, String>();

        SAXReader reader = new SAXReader();

        InputStream ins = request.getInputStream();
        Document doc = reader.read(ins);

        Element root = doc.getRootElement();
        List<Element> list = root.elements();
        for (Element e : list) {
            map.put(e.getName(), e.getText());
        }
        ins.close();
        return map;
    }
    
    public static String initText(String toUserName, String fromUserName, String content){
        TextMessage text = new TextMessage();
        text.setFromUserName(toUserName);
        text.setToUserName(fromUserName);
        text.setMsgType(REQ_MESSAGE_TYPE_TEXT);
        text.setCreateTime(new Date().getTime());
        text.setContent(content);
        return textMessageToXml(text);
    }
    public static void downloadPicture(String imageUrl,String path) {
        URL url = null;
        try {
            url = new URL(imageUrl);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
 
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
 
            byte[] buffer = new byte[1024];
            int length;
 
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
