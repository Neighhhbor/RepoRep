package com.hxxdemo.wechat.util;

import java.beans.XMLDecoder;  
import java.beans.XMLEncoder;  
import java.io.ByteArrayInputStream;  
import java.io.ByteArrayOutputStream;  
import java.io.IOException;  
import java.io.UnsupportedEncodingException;  
import java.util.ArrayList;  
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;  


public class XMLUtil {  
     
    public static String objectXmlEncoder(Object obj) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        // 创建XML文件对象输出类实例  
        XMLEncoder encoder = new XMLEncoder(baos);  
        // 对象序列化输出到XML文件  
        encoder.writeObject(obj);  
        encoder.flush();  
        encoder.close();  

        // 关闭序列化工具  
        try {  
            //关闭流  
            baos.close();  
            //因为我的应用需要经过web传输，所以要对特殊字符做下处理  
            return java.net.URLEncoder.encode(baos.toString("UTF-8"), "UTF-8");  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  

     
    public static List objectXmlDecoder(String objStr) {  
        List objList = new ArrayList();  
        ByteArrayInputStream bis = null;  

        try {  
            bis = new ByteArrayInputStream(objStr.getBytes("UTF-8"));  
        } catch (UnsupportedEncodingException e1) {  
            e1.printStackTrace();  
        }  
        XMLDecoder decoder = new XMLDecoder(bis);  
        Object obj = null;  
        try {  
            while ((obj = decoder.readObject()) != null) {  
                objList.add(obj);  
            }  
        } catch (Exception e) {  
        }  

        try {  
            bis.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        decoder.close();  
        return objList;  
    }  
    /**
     * java 转换成xml
     * @Title: toXml 
     * @Description: TODO 
     * @param obj 对象实例
     * @return String xml字符串
     */
    public static String toXml(Object obj){
        XStream xstream=new XStream();
//            XStream xstream=new XStream(new DomDriver()); //直接用jaxp dom来解释
//            XStream xstream=new XStream(new DomDriver("utf-8")); //指定编码解析器,直接用jaxp dom来解释
        
        ////如果没有这句，xml中的根元素会是<包.类名>；或者说：注解根本就没生效，所以的元素名就是类的属性
//        xstream.processAnnotations(obj.getClass()); //通过注解方式的，一定要有这句话
        return xstream.toXML(obj);
    }
    
    /**
     *  将传入xml文本转换成Java对象
     * @Title: toBean 
     * @Description: TODO 
     * @param xmlStr
     * @param cls  xml对应的class类
     * @return T   xml对应的class类的实例对象
     * 
     * 调用的方法实例：PersonBean person=XmlUtil.toBean(xmlStr, PersonBean.class);
     */
    public static <T> T  toBean(String xmlStr,Class<T> cls){
        //注意：不是new Xstream(); 否则报错：java.lang.NoClassDefFoundError: org/xmlpull/v1/XmlPullParserFactory
        XStream xstream=new XStream(new DomDriver());
//        xstream.processAnnotations(cls);
        T obj=(T)xstream.fromXML(xmlStr);
        return obj;            
    } 

}
