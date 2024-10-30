package com.hxxdemo.weixinsaomalogin.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.weixinsaomalogin.entity.Menu;
import com.hxxdemo.weixinsaomalogin.entity.WeixinMenu;
import com.hxxdemo.weixinsaomalogin.service.WxAccessTokenService;
import com.hxxdemo.weixinsaomalogin.service.WxMenuService;
import com.hxxdemo.weixinsaomalogin.util.MessageUtil;
import com.hxxdemo.weixinsaomalogin.util.WechatConfigLoader;
import com.hxxdemo.weixinsaomalogin.util.WeiXinUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "/menu")
public class WxMenuController {
	@Autowired
	private WxAccessTokenService wxAccessTokenService;
	@Autowired
	private WxMenuService wxMenuService;
	
	/**
	 * 同步到微信菜单
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/createWxMenu")
	@ResponseBody
	public Map<String,Object> createWxMenu( HttpServletRequest request,HttpServletResponse response) throws IOException {
		Map<String,Object> map = new HashMap<String,Object>();
		int firstLevelNum = Integer.valueOf(WechatConfigLoader.getFirstLevelNum());
		int twoLevelNum = Integer.valueOf(WechatConfigLoader.getTwoLevelNum());
		
		List<Map<String,Object>> firstMenuList = wxMenuService.queryFristLevelMenuList();
		if(null !=firstMenuList && firstMenuList.size()>0) {
			if(firstMenuList.size()>firstLevelNum) {
				map.put("success", false);
				map.put("msg", "最多创建"+firstLevelNum+"个一级菜单！");
			}else {
				boolean bool = true;
				for(int i = 0; i<firstMenuList.size() ; i++) {
					int countnum = Integer.valueOf(firstMenuList.get(i).get("countnum").toString());
					if(countnum>twoLevelNum) {
						bool = false;
						break;
					}
				}
				if(bool) {
					for(int i = 0 ; i < firstMenuList.size(); i++) {
						int countnum = Integer.valueOf(firstMenuList.get(i).get("countnum").toString());
						String event = firstMenuList.get(i).get("type").toString();
						String id = firstMenuList.get(i).get("id").toString();
//						if(event.equals(MessageUtil.EVENT_TYPE_VIEW.toLowerCase())) {
//							firstMenuList.get(i).put("key", id);
//						}
						if(countnum>0 && event.equals(MessageUtil.EVENT_TYPE_CLICK.toLowerCase())) {
							List<Map<String,Object>> twoMenuList = wxMenuService.queryTwoLevelMenuList(id);
							for(int j = 0 ; j< twoMenuList.size() ; j++) {
								twoMenuList.get(j).put("key", twoMenuList.get(j).get("id"));
							}
							firstMenuList.get(i).put("sub_button", twoMenuList);
						}
					}
					Menu menu = new Menu();
					menu.setButton(firstMenuList);
					System.out.println(JSONObject.fromObject(menu).toString());
					
					int rInt = 0 ; 
					rInt = createMenu(JSONObject.fromObject(menu).toString());
					if(rInt>0) {
						map.put("success", true);
						map.put("msg", "同步完成");
					}else {
						map.put("success", false);
						map.put("msg", "同步失败");
					}
				}else {
					map.put("success", false);
					map.put("msg", "二级菜单最多为"+twoLevelNum+"条");
				}
			}
		}else {
			map.put("success", false);
			map.put("msg", "还没有创建菜单，赶紧创建吧！");
		}
		
		return map;
//		int i = createMenu();
	}
	public int createMenu(String user_define_menu) throws IOException {
		
		
//		String user_define_menu = "{" + 
//			"     \"button\":[" + 
//			"     {    " + 
//			"          \"type\":\"click\"," + 
//			"          \"name\":\"今日歌曲\"," + 
//			"          \"key\":\"V1001_TODAY_MUSIC\"" + 
//			"      }," + 
//			"      {" + 
//			"           \"name\":\"菜单\"," + 
//			"           \"sub_button\":[" + 
//			"           {    " + 
//			"               \"type\":\"view\"," + 
//			"               \"name\":\"搜索\"," + 
//			"               \"url\":\"http://www.soso.com/\"" + 
//			"            }," + 
//			"            {" + 
//			"               \"type\":\"click\"," + 
//			"               \"name\":\"赞一下我们\"," + 
//			"               \"key\":\"V1001_GOOD\"" + 
//			"            }]" + 
//			"       }]" + 
//			" }";
		//此处改为自己想要的结构体，替换即可
//		String access_token= getAccess_token();
		String access_token= wxAccessTokenService.getAccessToken(WechatConfigLoader.getGappId(), WechatConfigLoader.getGappSecret(),true).getToken();

		String action = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+access_token;
		try {
			URL url = new URL(action);
			HttpURLConnection http = (HttpURLConnection) url.openConnection(); 
	
			http.setRequestMethod("POST"); 
			http.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
			http.setDoOutput(true); 
			http.setDoInput(true);
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");//连接超时30秒
			System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //读取超时30秒
	
			http.connect();
			OutputStream os= http.getOutputStream(); 
			os.write(user_define_menu.getBytes("UTF-8"));//传入参数 
			os.flush();
			os.close();
	
			InputStream is =http.getInputStream();
			int size =is.available();
			byte[] jsonBytes =new byte[size];
			is.read(jsonBytes);
			String message=new String(jsonBytes,"UTF-8");
			System.out.println(message);
			JSONObject json = JSONObject.fromObject(message);
			if(json.get("errcode").toString().equals("0")) {
				return 1;
			}else {
				return 0;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		} 
	}
	/**
	 * 创建微信菜单
	 * @param name string 菜单名称 必须
	 * @param event string 事件 click、view 必须
	 * @param url string click事件 非必须 、view事件 必须
	 * @param description string 描述 必须
	 * @param sort string 排序（正整数） 非必须
	 * @param level string  级别 1、2 一级菜单 或二级菜单 必须
	 * @param parentid string  级别1 非必须 、级别2 必须
	 * @return
	 */
	@RequestMapping("/insertWxMenu")
	@ResponseBody
	public Map<String,Object> insertWxMenu(String name ,String event,String url,String description,String sort,String level,String parentid){
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == name || "".equals(name)) {
			map.put("success", false);
			map.put("msg", "请输入菜单名称！");
			return map;
		}
		if(null == level || "".equals(level)) {
			map.put("success", false);
			map.put("msg", "请选择菜单级别！");
			return map;
		}
		if(!level.equals("1") && !level.equals("2")) {
			map.put("success", false);
			map.put("msg", "参数请求错误！");
			return map;
		}
		if(level.equals("1")) {
			if(name.length()>8) {
				map.put("success", false);
				map.put("msg", "一级菜单名称不能超过8个字！");
				return map;
			}
		}
		if(level.equals("2")) {
			if(name.length()>20) {
				map.put("success", false);
				map.put("msg", "二级菜单名称不能超过20个字！");
				return map;
			}
		}
		if(null == event || "".equals(event)){
			map.put("success", false);
			map.put("msg", "事件不能为空！");
			return map;
		}
		if(null == sort || "".equals(sort)){
			map.put("success", false);
			map.put("msg", "事件不能为空！");
			return map;
		}
		String regex = "^\\+?[1-9][0-9]*$";
		if(!sort.matches(regex)) {
			map.put("success", false);
			map.put("msg", "排序应为正整数！");
			return map;
		}
		WeixinMenu weixinMenu = new WeixinMenu();
		weixinMenu.setName(name);
		weixinMenu.setEvent(event);
		weixinMenu.setIsapply(MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY);
		weixinMenu.setDescription(description);
		weixinMenu.setSort(Integer.valueOf(sort));
		weixinMenu.setLevel(Integer.valueOf(level));
		if(level.equals("2")) {
			if(null == parentid || "".equals(parentid)) {
				map.put("success", false);
				map.put("msg", "请选择一级菜单！");
				return map;
			}
			if(!parentid.matches(regex)) {
				map.put("success", false);
				map.put("msg", "参数请求错误！");
				return map;
			}
			weixinMenu.setParentid(Long.valueOf(parentid));
		}
		if(event.equals(MessageUtil.EVENT_TYPE_VIEW)) {
			if(null == url || "".equals(url)) {
				map.put("success", false);
				map.put("msg", "跳转地址不能为空！");
				return map;
			}
			regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\\u4E00-\\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";
			if(!url.matches(regex)) {
				map.put("success", false);
				map.put("msg", "请输入网址！");
				return map;
			}
			weixinMenu.setUrl(url);
		}
		//插入微信菜单
		try {
			wxMenuService.insertWxMenu(weixinMenu);
			map.put("success", true);
			map.put("msg", "保存成功！");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "保存失败！");
		}
		return map;
	}
	/**
	 * 创建微信菜单
	 * @param id string 菜单id 必须
	 * @param name string 菜单名称 必须
	 * @param event string 事件 click、view 必须
	 * @param url string click事件 非必须 、view事件 必须
	 * @param description string 描述 必须
	 * @param sort string 排序（正整数） 非必须
	 * @param level string  级别 1、2 一级菜单 或二级菜单 必须
	 * @param parentid string  级别1 非必须 、级别2 必须
	 * @return
	 */
	@RequestMapping("/updateWxMenu")
	@ResponseBody
	public Map<String,Object> updateWxMenu(String id,String name ,String event,String url,String description,String sort,String level,String parentid){
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == name || "".equals(name)) {
			map.put("success", false);
			map.put("msg", "请输入菜单名称！");
			return map;
		}
		if(null == level || "".equals(level)) {
			map.put("success", false);
			map.put("msg", "请选择菜单级别！");
			return map;
		}
		if(!level.equals("1") && !level.equals("2")) {
			map.put("success", false);
			map.put("msg", "参数请求错误！");
			return map;
		}
		if(level.equals("1")) {
			if(name.length()>8) {
				map.put("success", false);
				map.put("msg", "一级菜单名称不能超过8个字！");
				return map;
			}
		}
		if(level.equals("2")) {
			if(name.length()>20) {
				map.put("success", false);
				map.put("msg", "二级菜单名称不能超过20个字！");
				return map;
			}
		}
		if(null == event || "".equals(event)){
			map.put("success", false);
			map.put("msg", "事件不能为空！");
			return map;
		}
		if(null == sort || "".equals(sort)){
			map.put("success", false);
			map.put("msg", "事件不能为空！");
			return map;
		}
		String regex = "^\\+?[1-9][0-9]*$";
		if(!sort.matches(regex)) {
			map.put("success", false);
			map.put("msg", "排序应为正整数！");
			return map;
		}
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		Long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		if(null == parentid || "".equals(parentid.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		
		Long rparentid = 0l;
		try {
			rparentid = Long.valueOf(parentid);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		WeixinMenu weixinMenu = new WeixinMenu();
		weixinMenu.setId(rid);
		weixinMenu.setParentid(rparentid);
		weixinMenu.setName(name);
		weixinMenu.setEvent(event);
		weixinMenu.setIsapply(MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY);
		weixinMenu.setDescription(description);
		weixinMenu.setSort(Integer.valueOf(sort));
		weixinMenu.setLevel(Integer.valueOf(level));
		if(level.equals("2")) {
			if(null == parentid || "".equals(parentid)) {
				map.put("success", false);
				map.put("msg", "请选择一级菜单！");
				return map;
			}
			if(!parentid.matches(regex)) {
				map.put("success", false);
				map.put("msg", "参数请求错误！");
				return map;
			}
			weixinMenu.setParentid(Long.valueOf(parentid));
		}
		if(event.equals(MessageUtil.EVENT_TYPE_VIEW)) {
			if(null == url || "".equals(url)) {
				map.put("success", false);
				map.put("msg", "跳转地址不能为空！");
				return map;
			}
			regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\\u4E00-\\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";
			if(!url.matches(regex)) {
				map.put("success", false);
				map.put("msg", "请输入网址！");
				return map;
			}
			weixinMenu.setUrl(url);
		}
		//插入微信菜单
		try {
			wxMenuService.updateWxMenu(weixinMenu);
			map.put("success", true);
			map.put("msg", "修改成功！");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "修改失败！");
		}
		return map;
	}
	/**
	 * 删除微信菜单  （逻辑删除）
	 * @param id string 菜单id 必须
	 * @return
	 */
	@RequestMapping("/delWxMenu")
	@ResponseBody
	public Map<String,Object> delWxMenu(String id){
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		
		Long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		wxMenuService.delWxMenu(rid);
		map.put("success", true);
		map.put("msg", "删除成功！");
		return map;
	}
	/**
	 * 查询一条微信菜单 
	 * @param id string 菜单id 必须
	 * @return
	 */
	@RequestMapping("/oneWxMenu")
	@ResponseBody
	public Map<String,Object> oneWxMenu(String id){
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		Long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		Map<String,Object> reMap = wxMenuService.oneWxMenu(rid);
		if(null == reMap || reMap.size() ==0) {
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		map.put("success", true);
		map.put("data", reMap);
		return map;
	}
	/**
	 * 是否创建一级菜单
	 * @return
	 */
	@RequestMapping("/isCreateFirstLevelWxMenu")
	@ResponseBody
	public Map<String,Object> isCreateFirstLevelWxMenu(){
		Map<String,Object> map = new HashMap<String,Object>();
		int firstLevelNum = Integer.valueOf(WechatConfigLoader.getFirstLevelNum());
		
		List<Map<String,Object>> firstMenuList = wxMenuService.queryFristLevelMenuList();
		if(null == firstMenuList || firstMenuList.size() < firstLevelNum) {
			map.put("success", true);
			map.put("total", firstMenuList.size());
		}
		if(firstMenuList.size()==firstLevelNum){
			map.put("success", false);
			map.put("msg", "最多创建"+firstLevelNum+"个一级菜单！");
		}else {
			map.put("success", false);
			map.put("msg", "数据库异常，请清理数据库！");
		}
		return map;
	}
	/**
	 * 是否创建二级菜单
	 * @param id
	 * @return
	 */
	@RequestMapping("/isCreateTwoLevelWxMenu")
	@ResponseBody
	public Map<String,Object> isCreateTwoLevelWxMenu(){
		Map<String,Object> map = new HashMap<String,Object>();
		int firstLevelNum = Integer.valueOf(WechatConfigLoader.getFirstLevelNum());
		int twoLevelNum = Integer.valueOf(WechatConfigLoader.getTwoLevelNum());
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> firstMenuList = wxMenuService.queryFristLevelMenuList();
		if(null == firstMenuList || firstMenuList.size() ==0) {
			map.put("success", false);
			map.put("msg", "请先创建一级菜单！");
			return map;
		}else {
			if(firstMenuList.size()>firstLevelNum) {
				map.put("success", false);
				map.put("msg", "数据库异常，请清理数据库！");
				return map;
			}else {
				boolean isException = false;
				boolean bool = false;
				for(int i = 0 ;i < firstMenuList.size(); i++ ) {
					int countnum = Integer.valueOf(firstMenuList.get(i).get("countnum").toString());
					if(countnum>twoLevelNum) {
						isException= true;
						break;
					}
				}
				if(isException) {
					map.put("success", false);
					map.put("msg", "数据库异常，请清理数据库！");
					return map;
				}
				for(int i = 0 ;i < firstMenuList.size(); i++ ) {
					String eventType = firstMenuList.get(i).get("type").toString();
					if(eventType.equals(MessageUtil.EVENT_TYPE_CLICK.toLowerCase())) {
						bool= true;
						break;
					}
				}
				if(bool) {
					for(int i = 0 ;i < firstMenuList.size(); i++ ) {
						String eventType = firstMenuList.get(i).get("type").toString();
						if(eventType.equals(MessageUtil.EVENT_TYPE_CLICK.toLowerCase())) {
							int countnum = Integer.valueOf(firstMenuList.get(i).get("countnum").toString());
							if(countnum<twoLevelNum) {
								list.add(firstMenuList.get(i));
							}
						}
					}
					map.put("success", true);
					map.put("data", list);
				}else {
					map.put("success", false);
					map.put("msg", "请先创建一个事件为click的菜单！");
					return map;
				}
			}
		}
		return map;
	}
	/**
	 * 查询所有菜单列表
	 * @return
	 */
	@RequestMapping("/queryAllWxMenu")
	@ResponseBody
	public Map<String,Object> queryAllWxMenu(){
		Map<String,Object> map = new HashMap<String,Object>();
		List<Map<String,Object>> firstMenuList = wxMenuService.queryFristLevelMenuList();
		
		if(null == firstMenuList || firstMenuList.size()==0) {
			map.put("success", false);
			map.put("msg", "赶紧创建一级菜单吧！");
			return map;
		}else {
			for(int i = 0 ; i < firstMenuList.size() ; i++ ) {
				String eventType = firstMenuList.get(i).get("type").toString();
				if(eventType.equals(MessageUtil.EVENT_TYPE_CLICK.toLowerCase())) {
					List<Map<String,Object>> twoMenuList = wxMenuService.queryTwoLevelMenuList(firstMenuList.get(i).get("id").toString());
					firstMenuList.get(i).put("child", twoMenuList);
					firstMenuList.get(i).put("childnum", twoMenuList.size());
				}else {
					firstMenuList.get(i).put("childnum", 0);
				}
			}
			map.put("success", true);
			map.put("data", firstMenuList);
		}
		return map;
	}
}
