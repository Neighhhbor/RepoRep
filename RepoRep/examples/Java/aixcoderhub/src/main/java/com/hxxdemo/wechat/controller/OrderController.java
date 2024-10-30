package com.hxxdemo.wechat.controller;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.wxpay.sdk.WXPayUtil;
import com.hxxdemo.config.Globals;
import com.hxxdemo.plug.service.PlugService;
import com.hxxdemo.sysLogin.entity.TokenEntity;
import com.hxxdemo.sysLogin.service.TokenService;
import com.hxxdemo.wechat.service.OrderService;
import com.hxxdemo.wechat.util.HttpUtil;
import com.hxxdemo.weixinsaomalogin.util.WechatConfigLoader;

@Controller
@RequestMapping("order")
public class OrderController {
	
	@Autowired
	private PlugService plugService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private OrderService orderService;
	/**
	 * 获取价格列表
	 * @return
	 */
	@RequestMapping("getPrice")
    @ResponseBody
    public Map<String, Object> getPrice(){
		Map<String, Object> returnMap = new HashMap<String,Object>();
		List<Map<String, Object>> list = orderService.getPrice();
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", list);
		return returnMap;
	}
	/**
	 * 校验订单是否支付成功
	 * @param productid
	 * @return
	 */
	@RequestMapping("checkOrder")
	@ResponseBody
	public Map<String, Object> checkOrder(@RequestParam String productid){
		Map<String, Object> returnMap = new HashMap<String,Object>();
		boolean bool = false;
		int index = orderService.countOrder(productid);
		if (index > 0) {
			bool = true;
		}
		returnMap.put("errorcode", Globals.ERRORCODE0);
		returnMap.put("errormessage", Globals.ERRORMESSAGE0);
		returnMap.put("info", bool);
		return returnMap;
	}
	/**
	 * 创建预支付订单
	 * @param token
	 * @param username
	 * @param priceid
	 * @param request
	 * @return
	 * @throws Exception
	 */
    @RequestMapping("userOrder")
    @ResponseBody
    public Map<String, Object> userOrder(@RequestParam String token,@RequestParam String username,@RequestParam Long priceid,  HttpServletRequest request) throws Exception {
    	//校验token
    	Map<String, Object> returnMap = new HashMap<String,Object>();
    	long userId = 0;
		String uuid = "";//用户唯一标识
		//验证是否存在该用户
		uuid = plugService.getUUID(username);
		if(null == uuid || "".equals(uuid)) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		userId = plugService.getUserId(username);
		String url = request.getRequestURL().toString();
		//检查是否有token令牌
		TokenEntity istokenEntity = tokenService.queryPlugToken(userId);
		if(null==istokenEntity) {
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}else {
			if(!istokenEntity.getToken().equals(token)) {
				returnMap.put("errorcode", Globals.ERRORCODE4001);
				returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
				return returnMap;
			}else {
				//是否包含价格id
				boolean isPrice = orderService.isPrice(priceid);
				if(isPrice) {
					//创建productid
					String productid = orderService.createProductid(userId,priceid);
					int price = orderService.getPriceByPriceid(priceid);
					String orderSubject = orderService.getOrderSubject(priceid);
					String params = getXmlData(productid,price,orderSubject);
					String xmlResult = HttpUtil.sendPost(WechatConfigLoader.getPurl(), params,false);
					Map<String ,String > xmlToMap = WXPayUtil.xmlToMap(xmlResult);
					xmlToMap.put("productid", productid);
					returnMap.put("info", xmlToMap);
					returnMap.put("errorcode", Globals.ERRORCODE0);
					returnMap.put("errormessage", Globals.ERRORMESSAGE0);
				}else {
					returnMap.put("errorcode", Globals.ERRORCODE4001);
					returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
					return returnMap;
				}
			}
		}
        return returnMap;
    }
    /**
     * 已完成订单列表
     * @param token
     * @param username
     * @return
     * @throws Exception
     */
    @RequestMapping("ownerOrderList")
    @ResponseBody
    public Map<String, Object> ownerOrderList(@RequestParam String token,@RequestParam String username,Integer page,Integer rows) throws Exception {
    	if(null == page) {
			page=1;
		}
		if(null == rows) {
			rows=30;
		}
    	//校验token
    	Map<String, Object> returnMap = new HashMap<String,Object>();
    	long userId = 0;
		String uuid = "";//用户唯一标识
		//验证是否存在该用户
		uuid = plugService.getUUID(username);
		if(null == uuid || "".equals(uuid)) {
			returnMap.put("errorcode", Globals.ERRORCODE4001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
			return returnMap;
		}
		userId = plugService.getUserId(username);
		//检查是否有token令牌
		TokenEntity istokenEntity = tokenService.queryPlugToken(userId);
		if(null==istokenEntity) {
			returnMap.put("errorcode", Globals.ERRORCODE2001);
			returnMap.put("errormessage", Globals.ERRORMESSAGE2001);
			return returnMap;
		}else {
			if(!istokenEntity.getToken().equals(token)) {
				returnMap.put("errorcode", Globals.ERRORCODE4001);
				returnMap.put("errormessage", Globals.ERRORMESSAGE4001);
				return returnMap;
			}else {
				Map<String, Object> params = new HashMap<String,Object>();
				params.put("userid", userId);
				params.put("page", (page-1)*rows);
				params.put("rows", rows);
				List<Map<String, Object>> list = orderService.getOrderList(params);
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("page", page);
				map.put("list", list);
				map.put("totalnum", orderService.countOrderList(params));
				returnMap.put("info", map);
				returnMap.put("errorcode", Globals.ERRORCODE0);
				returnMap.put("errormessage", Globals.ERRORMESSAGE0);
				return returnMap;
			}
		}
    }
    /**
     * MD5加密
     * @param strObj
     * @return
     * @throws Exception
     */
    public static String MD5(String strObj) throws Exception{
		MessageDigest md = MessageDigest.getInstance("MD5");
		
		return byteToString(md.digest(strObj.getBytes()));
	}
	 // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }
 // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }
    // 全局数组
    private static final String[] strDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
    public static String getXmlData(String productid,int price,String orderSubject	) throws Exception {
		int totalCount = price;//金额 （分）
		String domain = WechatConfigLoader.getPcallback();
		HashMap<String, String> paramMap = new HashMap<String,String>(); 
		paramMap.put("trade_type", "NATIVE"); //交易类型
		paramMap.put("spbill_create_ip",localIp()); //本机的Ip
		paramMap.put("product_id", productid); // 商户根据自己业务传递的参数 必填
		paramMap.put("body", orderSubject);         //描述
		paramMap.put("out_trade_no", productid); //商户 后台的贸易单号
		paramMap.put("total_fee", "" + totalCount); //金额必须为整数  单位为分
		paramMap.put("notify_url", domain); //支付成功后，回调地址     
		paramMap.put("appid", WechatConfigLoader.getPappid()); //appid
		paramMap.put("mch_id", WechatConfigLoader.getPmchid()); //商户号      
		paramMap.put("nonce_str", UUID.randomUUID().toString().replace("-", ""));  //随机数  
		paramMap.put("sign",getSignature(paramMap));//根据微信签名规则，生成签名 
		String xmlData = WXPayUtil.mapToXml(paramMap);//把参数转换成XML数据格式
		return xmlData;
	}
    private static String localIp(){
        String ip = null;
        Enumeration allNetInterfaces;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();            
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                List<InterfaceAddress> InterfaceAddress = netInterface.getInterfaceAddresses();
                for (InterfaceAddress add : InterfaceAddress) {
                    InetAddress Ip = add.getAddress();
                    if (Ip != null && Ip instanceof Inet4Address) {
                        ip = Ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block        
//            logger.warn("获取本机Ip失败:异常信息:"+e.getMessage());
        }
        return ip;
    }
    /**
	   * 生成签名
	   *
	   * @param inputParams 需要的参数, 注意不要传入sign字段
	   * @return 签名之后的结果
	 * @throws Exception 
	   */
	  public static String getSignature(Map<String, String> inputParams) throws Exception {
	    Map<String, String> params = new TreeMap<>(inputParams);
	    StringBuilder sb = new StringBuilder();

	    Set<String> keys = params.keySet();
	    List<String> keyList = new ArrayList<>();
	    keyList.addAll(keys);
	    Collections.sort(keyList);
	    for (String k : keyList) {
	      sb.append(k);
	      sb.append("=");
	      sb.append(params.get(k));
	      sb.append("&");
	    }
	    sb.append("key=");
	    sb.append(WechatConfigLoader.getPkey());
	    String sign = MD5(sb.toString()).toUpperCase();
	    return sign;
	  }
}