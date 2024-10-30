package com.hxxdemo.weixinsaomalogin.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hxxdemo.weixinsaomalogin.entity.Reply;
import com.hxxdemo.weixinsaomalogin.service.ReplyService;
import com.hxxdemo.weixinsaomalogin.util.MessageUtil;
import com.hxxdemo.weixinsaomalogin.util.WechatConfigLoader;

import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "/reply")
public class ReplyController {
	
	@Autowired
	private ReplyService replyService;
	/**
	 * 关键字文本回复消息
	 * @param title string 回复内容 必须
	 * @param msgtype string 类型 text 必须
	 * @param keyword string 关键字 必须
	 * @return
	 */
	@RequestMapping("/textSubmit")
	@ResponseBody
	public Map<String,Object> textSubmit(String title,String msgtype,String keyword){
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == msgtype || "".equals(msgtype)) {
			map.put("success", false);
			map.put("msg", "类型不能为空！");
			return map;
		}
		if(!msgtype.equals(MessageUtil.RESP_MESSAGE_TYPE_TEXT)) {
			map.put("success", false);
			map.put("msg", "类型参数错误！");
			return map;
		}
		if(null == keyword || "".equals(keyword.trim())) {
			map.put("success", false);
			map.put("msg", "关键字不能为空！");
			return map;
		}
		if(null == title || "".equals(title.trim())) {
			map.put("success", false);
			map.put("msg", "回复消息不能为空！");
			return map;
		}
		int countKeyword = replyService.countReplyTextByKeyword(keyword);
		if(countKeyword>0) {
			map.put("success", false);
			map.put("msg", "换个关键字试试！");
			return map;
		}
		JSONObject json = new JSONObject();
		json.put("title", title);
		Reply reply = new Reply();
		reply.setMsgtype(msgtype);
		reply.setKeyword(keyword);
		reply.setIsapply(MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY);
		reply.setMsg(json.toString());
		replyService.insertTextReply(reply);
		map.put("success", true);
		map.put("msg", "文本消息创建成功！");
		return map;
	}
	/**
	 * 关键字图文回复消息
	 * @param file file文件 图片jpg、png两种任选其一 必须
	 * @param msgtype string 类型 image 必须
	 * @param url string 跳转地址 必须
	 * @param title string 主标题 必须
	 * @param keyword string 关键字 必须 
	 * @param describe string 描述 必须
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/imgTextSubmit")
	@ResponseBody
	public Map<String,Object> upload(HttpServletRequest request,@RequestParam("file") MultipartFile file,String msgtype,String url,String title,String keyword,String describe) throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		//jpg .jpeg .gif .png .bmp
		String imgTypes[] = {".jpg",".png"};
		
		if (file.isEmpty()) {
			map.put("success", false);
			map.put("msg", "请上传图片！");
			return map;
		}
		if(null == msgtype || "".equals(msgtype)) {
			map.put("success", false);
			map.put("msg", "类型不能为空！");
			return map;
		}
		if(!msgtype.equals(MessageUtil.RESP_MESSAGE_TYPE_IMAGE)) {
			map.put("success", false);
			map.put("msg", "类型参数错误！");
			return map;
		}
		if(null == title || "".equals(title.trim())) {
			map.put("success", false);
			map.put("msg", "标题不能为空！");
			return map;
		}
		if(null == keyword || "".equals(keyword.trim())) {
			map.put("success", false);
			map.put("msg", "关键字不能为空！");
			return map;
		}
		if(null == url || "".equals(url.trim())) {
			map.put("success", false);
			map.put("msg", "文章地址不能为空！");
			return map;
		}
		if(null == describe || "".equals(describe.trim())) {
			map.put("success", false);
			map.put("msg", "描述不能为空！");
			return map;
		}
		int countKeyword = replyService.countReplyImgTextByKeyword(keyword);
		if(countKeyword>0) {
			map.put("success", false);
			map.put("msg", "换个关键字试试！");
			return map;
		}
		//取得当前上传图片的名称    
		String myFileName = file.getOriginalFilename(); 
		//后缀名
		String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		boolean isImage = false;
		suffix = suffix.toLowerCase();
		for(int i = 0 ; i < imgTypes.length ; i++) {
			if(suffix.equals(imgTypes[i])) {
				isImage = true;
				break;
			}
		}
		if(!isImage) {
			map.put("success", false);
			map.put("msg", "请选择图片上传！");
			return map;
		}
		//图片名称
		String fileName = String.valueOf(System.currentTimeMillis())+suffix;
		try {
			if(myFileName.trim() !=""){ 
				String basePath = request.getSession().getServletContext().getRealPath("/")+"imgtext";
				File dir = new File(basePath);
			    if (!dir.exists()) {
			    	dir.mkdir();
			    }
			  //定义上传路径    
			    String path =  basePath + "/" + fileName;   
			    File localFile = new File(path);    
			    try {
					file.transferTo(localFile);
					String serverAdress = WechatConfigLoader.getServerAddress();
					String picurl = serverAdress +"imgtext/"+fileName;
					JSONObject json = new JSONObject();
					json.put("title", title);
					json.put("describes", describe);
					json.put("picurl", picurl);
					json.put("url", url);
					Reply reply = new Reply();
					reply.setMsgtype(msgtype);
					reply.setKeyword(keyword);
					reply.setIsapply(MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY);
					reply.setMsg(json.toString());
					replyService.insertImageReply(reply);
					map.put("success", true);
					map.put("msg", "保存成功！");
					return map;
				} catch (Exception e) {
					map.put("success", false);
					map.put("msg", "上传失败！");
					return map;
				}   
			}
		} catch (Exception e) {
			map.put("success", false);
			map.put("msg", "上传失败！");
			return map;
		}
		return map;
	}
	
	/**
	 * 未命中 文本回复
	 * @param title string 回复内容 必须
	 * @return
	 */
	@RequestMapping("/misfortuneSubmit")
	@ResponseBody
	public Map<String,Object> misfortuneSubmit(String title){
		Map<String,Object> map = new HashMap<String,Object>();
		
		if(null == title || "".equals(title.trim())) {
			map.put("success", false);
			map.put("msg", "回复消息不能为空！");
			return map;
		}
		String msgtype = MessageUtil.RESP_MESSAGE_TYPE_TEXT;
		
		JSONObject json = new JSONObject();
		json.put("title", title);
		Reply reply = new Reply();
		reply.setMsg(json.toString());
		reply.setMsgtype(msgtype);
		reply.setIsapply(MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY);
		reply.setIsmisfortune(MessageUtil.RESP_MESSAGE_STATUS_ISMISFORTUNE);
		//插入未命中消息
		replyService.insertMisfortuneReply(reply);
		map.put("success", true);
		map.put("msg", "添加成功");
		return map;
	}
	/**
	 * 未命中消息 分页列表
	 * @param rows int 行数 非必须
	 * @param page int 页码 非必须
	 * @param title string 回复内容 非必须
	 * @return
	 */
	@RequestMapping("/queryMisfortuneList")
	@ResponseBody
	public Map<String,Object> queryMisfortuneList(Integer rows,Integer page,String title) {
		Map<String,Object> map = new HashMap<String,Object>();
		//初始化参数行数
		if(null == rows || "".equals(rows.toString())) {
			rows =  10 ;
		}
		//初始化参数页码
		if(null == page || "".equals(page.toString())) {
			page =  1 ;
		}
		Map<String ,Object> params = new HashMap<String,Object>();
		params.put("rows", rows);
		params.put("page", (page-1)*rows);
		if(null != title && !"".equals(title)) {
			params.put("title", title);
		}
		params.put("ismisfortune", MessageUtil.RESP_MESSAGE_STATUS_ISMISFORTUNE);
		List<Map<String,Object>> list = replyService.queryMisfortuneList(params);
		if(null != list && list.size()>0) {
			for(int i = 0 ; i < list.size() ; i++ ) {
				list.get(i).put("msg", JSONObject.fromObject(list.get(i).get("msg")).get("title"));
			}
		}
		int count = replyService.countMisfortuneList(params);
		map.put("success", true);
		map.put("data", list);
		map.put("count", count);
		return map;
	}
	/**
	 * 修改未命中回复消息
	 * @param title string 回复内容 必须
	 * @param id string 未命中消息id 必须
	 * @return
	 */
	@RequestMapping("/updateMisfortune")
	@ResponseBody
	public Map<String,Object> updateMisfortune(String title ,String id) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == title || "".equals(title.trim())) {
			map.put("success", false);
			map.put("msg", "回复消息不能为空！");
			return map;
		}
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		
		long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		Reply reply = new Reply();
		reply.setId(rid);
		JSONObject json = new JSONObject();
		json.put("title", title);
		reply.setMsg(json.toString());
		reply.setMsgtype(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
		reply.setIsmisfortune(MessageUtil.RESP_MESSAGE_STATUS_ISMISFORTUNE);
		//根据id修改未命中消息
		replyService.updateMisfortune(reply);
		map.put("success",true);
		map.put("msg", "修改成功！");
		return map;
	}
	
	/**
	 * 查询一条 未命中消息
	 * @param id string 未命中消息id 必须
	 * @return
	 */
	@RequestMapping("/oneMisfortune")
	@ResponseBody
	public Map<String,Object> oneMisfortune(String id) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		
		long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		Map<String ,Object> reMap =  replyService.oneMisfortune(rid);
		if(null == reMap || reMap.size()==0) {
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		reMap.put("msg", JSONObject.fromObject(reMap.get("msg")).get("title"));
		map.put("success", true);
		map.put("data", reMap);
		return map;
	}
	/**
	 * 删除未命中消息 （逻辑删除）
	 * @param id string 未命中消息id 必须
	 * @return
	 */
	@RequestMapping("/delMisfortune")
	@ResponseBody
	public Map<String,Object> delMisfortune(String id) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		
		long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		Reply reply = new Reply();
		reply.setId(rid);
		reply.setMsgtype(MessageUtil.RESP_MESSAGE_TYPE_IMAGE);
		reply.setIsapply(MessageUtil.RESP_MESSAGE_STATUS_ISNOTAPPLY);
		reply.setIsmisfortune(MessageUtil.RESP_MESSAGE_STATUS_ISMISFORTUNE);
		replyService.delMisfortune(reply);
		map.put("success", true);
		map.put("msg", "删除成功！");
		return map;
	}
	
	/**
	 * 查询未命中消息配置
	 * @return
	 */
	@RequestMapping("/queryReplyConfig")
	@ResponseBody
	public Map<String,Object> queryReplyConfig() {
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> reMap = new HashMap<String,Object>();
		int count = replyService.countReplyConfig();
		if(count==0) {
			map.put("success", false);
			map.put("msg", "赶紧配置未命中配置吧！");
			return map;
		} else if(count==1) {
			//查询开启未命中配置
			reMap = replyService.queryReplyConfig();
			map.put("success", true);
			map.put("data", reMap);
		} else {
			//重置未命中配置
			replyService.resetReplyConfg();
			map.put("success", false);
			map.put("msg", "配置已经重置，赶紧配置未命中配置吧！");
			return map;
		}
		return map;
	}
	/**
	 * 创建未命中消息配置
	 * @param isopen string 是否开启 0 否 1 是 必须
	 * @param opentime string 开启时间（正整数） 必须
	 * @return
	 */
	@RequestMapping("/insertReplyConfig")
	@ResponseBody
	public Map<String,Object> insertReplyConfig(String isopen,String opentime) {
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> param = new HashMap<String,Object>();
		if(null == isopen || "".equals(isopen.trim())) {
			map.put("success", false);
			map.put("msg", "是否启用未命中状态不能为空！");
			return map;
		}
		if(!isopen.equals(MessageUtil.RESP_MESSAGE_STATUS_ISOPEN+"") && !isopen.equals(MessageUtil.RESP_MESSAGE_STATUS_ISNOTOPEN+"")) {
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		if(null == opentime || "".equals(opentime.trim())) {
			map.put("success", false);
			map.put("msg", "未命中时间不能为空！");
			return map;
		}
		String regex = "^\\+?[1-9][0-9]*$";
		if(!opentime.matches(regex)) {
			map.put("success", false);
			map.put("msg", "未命中时间应为正整数！");
			return map;
		}
		int count = replyService.countReplyConfig();
		if(count==0) {
			param.put("isopen", isopen);
			param.put("opentime", opentime);
			replyService.insertReplyConfig(param);
			map.put("success", true);
			map.put("msg", "添加成功！");
		}else {
			map.put("success", false);
			map.put("msg", "请求错误，已经有未命中配置了！");
		}
		return map;
	}
	/**
	 * 开启或关闭配置
	 * @param isopen string 是否开启 0 否 1 是 必须
	 * @param opentime string 开启时间（正整数） 必须
	 * @param id string id 配置id 必须
	 * @return
	 */
	@RequestMapping("/updateReplyConfig")
	@ResponseBody
	public Map<String,Object> updateReplyConfig(String isopen,String opentime,String id ) {
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> param = new HashMap<String,Object>();
		if(null == isopen || "".equals(isopen.trim())) {
			map.put("success", false);
			map.put("msg", "是否启用未命中状态不能为空！");
			return map;
		}
		if(!isopen.equals(MessageUtil.RESP_MESSAGE_STATUS_ISOPEN+"") && !isopen.equals(MessageUtil.RESP_MESSAGE_STATUS_ISNOTOPEN+"")) {
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		if(null == opentime || "".equals(opentime.trim())) {
			map.put("success", false);
			map.put("msg", "未命中时间不能为空！");
			return map;
		}
		String regex = "^\\+?[1-9][0-9]*$";
		if(!opentime.matches(regex)) {
			map.put("success", false);
			map.put("msg", "未命中时间应为正整数！");
			return map;
		}
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		
		int rid = 0;
		try {
			rid = Integer.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		param.put("id", rid);
		param.put("isopen", isopen);
		param.put("opentime", opentime);
		replyService.updateReplyConfg(param);
		map.put("success", true);
		map.put("msg", "修改成功！");
		return map;
	}
	/**
	 * 关键字文本回复消息 分页列表
	 * @param rows int 行数 非必须
	 * @param page int 页码 非必须
	 * @param title string 回复内容 非必须
	 * @return
	 */
	@RequestMapping("/queryReplyTextList")
	@ResponseBody
	public Map<String,Object> queryReplyTextList(Integer rows,Integer page,String title) {
		Map<String,Object> map = new HashMap<String,Object>();
		//初始化参数行数
		if(null == rows || "".equals(rows.toString())) {
			rows =  10 ;
		}
		//初始化参数页码
		if(null == page || "".equals(page.toString())) {
			page =  1 ;
		}
		
		Map<String ,Object> params = new HashMap<String,Object>();
		params.put("rows", rows);
		params.put("page", (page-1)*rows);
		if(null!=title && !"".equals(title)) {
			params.put("title", title);
		}
		params.put("msgtype", MessageUtil.RESP_MESSAGE_TYPE_TEXT);
		params.put("ismisfortune", MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
		List<Map<String,Object>> list = replyService.getReplyTextList(params);
		if(null !=list && list.size()>0) {
			for(int i = 0 ; i < list.size() ; i++ ) {
				list.get(i).put("msg", JSONObject.fromObject(list.get(i).get("msg")).get("title"));
			}
		}
		int count = replyService.countReplyTextList(params);
		map.put("success", true);
		map.put("count", count);
		map.put("data", list);
		return map;
	}
	/**
	 * 查询一条关键字文本回复消息
	 * @param id string 回复消息id 必须
	 * @return
	 */
	@RequestMapping("/oneReplyText")
	@ResponseBody
	public Map<String,Object> oneReplyText(String id) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		
		long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		Map<String ,Object> reMap =  replyService.oneReplyText(rid);
		if(null == reMap || reMap.size()==0) {
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		reMap.put("msg", JSONObject.fromObject(reMap.get("msg")).get("title"));
		map.put("success", true);
		map.put("data", reMap);
		return map;
	}
	/**
	 * 修改关键字文本回复消息
	 * @param id string 回复消息id 必须
	 * @param title string 回复消息内容 必须
	 * @param keyword string 关键字 必须
	 * @return
	 */
	@RequestMapping("/updateReplyText")
	@ResponseBody
	public Map<String,Object> updateReplyText(String id,String title,String keyword) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == title || "".equals(title.trim())) {
			map.put("success", false);
			map.put("msg", "回复消息不能为空！");
			return map;
		}
		if(null == keyword || "".equals(keyword.trim())) {
			map.put("success", false);
			map.put("msg", "回复消息不能为空！");
			return map;
		}
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		int countKeyword = replyService.countReplyTextByKeyword(keyword);
		if(countKeyword>0) {
			map.put("success", false);
			map.put("msg", "换个关键字试试！");
			return map;
		}
		Reply reply = new Reply();
		reply.setId(rid);
		JSONObject json = new JSONObject();
		json.put("title", title);
		reply.setMsg(json.toString());
		reply.setKeyword(keyword);
		reply.setMsgtype(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
		reply.setIsmisfortune(MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
		//根据id修改文本消息
		replyService.updateReplyText(reply);
		map.put("success",true);
		map.put("msg", "修改成功！");
		return map;
	}
	/**
	 * 删除关键字文本回复消息（逻辑删除）
	 * @param id string 回复消息id 必须
	 * @return
	 */
	@RequestMapping("/delReplyText")
	@ResponseBody
	public Map<String,Object> delReplyText(String id) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		
		long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		Reply reply = new Reply();
		reply.setId(rid);
		reply.setMsgtype(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
		reply.setIsapply(MessageUtil.RESP_MESSAGE_STATUS_ISNOTAPPLY);
		reply.setIsmisfortune(MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
		replyService.delReplyText(reply);
		map.put("success", true);
		map.put("msg", "删除成功！");
		return map;
	}
	/**
	 * 关键字图文回复消息 分页列表
	 * @param rows int 行数 非必须
	 * @param page int 页码 非必须
	 * @param title string 回复内容 非必须
	 * @return
	 */
	@RequestMapping("/queryReplyImgTextList")
	@ResponseBody
	public Map<String,Object> queryReplyImgTextList(Integer rows,Integer page,String title) {
		Map<String,Object> map = new HashMap<String,Object>();
		//初始化参数行数
		if(null == rows || "".equals(rows.toString())) {
			rows =  10 ;
		}
		//初始化参数页码
		if(null == page || "".equals(page.toString())) {
			page =  1 ;
		}
		
		Map<String ,Object> params = new HashMap<String,Object>();
		params.put("rows", rows);
		params.put("page", (page-1)*rows);
		if(null!=title && !"".equals(title)) {
			params.put("title", title);
		}
		params.put("msgtype", MessageUtil.RESP_MESSAGE_TYPE_IMAGE);
		params.put("ismisfortune", MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
		List<Map<String,Object>> list = replyService.queryReplyImgTextList(params);
		if(null !=list && list.size()>0) {
			for(int i = 0 ; i < list.size() ; i++ ) {
				JSONObject json = JSONObject.fromObject(list.get(i).get("msg"));
				list.get(i).put("picurl", json.get("picurl"));
				list.get(i).put("url", json.get("url"));
				list.get(i).put("describes", json.get("describes"));
				list.get(i).put("msg", json.get("title"));
			}
		}
		int count = replyService.countReplyImgTextList(params);
		map.put("success", true);
		map.put("count", count);
		map.put("data", list);
		return map;
	}
	/**
	 * 查询一条关键字图文回复消息
	 * @param id string 回复消息id 必须
	 * @return
	 */
	@RequestMapping("/oneReplyImgText")
	@ResponseBody
	public Map<String,Object> oneReplyImgText(String id) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		
		long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		Map<String ,Object> reMap =  replyService.oneReplyImgText(rid);
		if(null == reMap || reMap.size()==0) {
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		JSONObject json = JSONObject.fromObject(reMap.get("msg"));
		reMap.put("describes", json.get("describes"));
		reMap.put("picurl", json.get("picurl"));
		reMap.put("url", json.get("url"));
		reMap.put("msg", json.get("title"));
		map.put("success", true);
		map.put("data", reMap);
		return map;
	}
	/**
	 * 删除关键字图文回复消息 （逻辑删除）
	 * @param id string 回复消息id
	 * @return
	 */
	@RequestMapping("/delReplyImgText")
	@ResponseBody
	public Map<String,Object> delReplyImgText(String id) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		
		long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		Reply reply = new Reply();
		reply.setId(rid);
		reply.setMsgtype(MessageUtil.RESP_MESSAGE_TYPE_IMAGE);
		reply.setIsapply(MessageUtil.RESP_MESSAGE_STATUS_ISNOTAPPLY);
		reply.setIsmisfortune(MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
		replyService.delReplyImgText(reply);
		map.put("success", true);
		map.put("msg", "删除成功！");
		return map;
	}
	/**
	 * 修改关键字图文回复消息  
	 * @param file file文件 图片 jpg 、png 两种任选其一 非必须  
	 * @param msgtype string 类型 image 必须
	 * @param url string 跳转地址 必须
	 * @param title string 标题内容 必须
	 * @param keyword string 关键字 必须
	 * @param describe string 描述内容 必须
	 * @param picurl string 图片地址 非必须（上传图片 picurl非必须 不上传图片 picurl 必须）
	 * @param id string 回复消息id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/updateReplyImgText")
	@ResponseBody
	public Map<String,Object> updateReplyImgText(HttpServletRequest request,@RequestParam("file") MultipartFile file,String msgtype,String url,String title,String keyword,String describe,String picurl,String id) throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		//jpg .jpeg .gif .png .bmp
		String imgTypes[] = {".jpg",".png"};
		
		if(null == msgtype || "".equals(msgtype)) {
			map.put("success", false);
			map.put("msg", "类型不能为空！");
			return map;
		}
		if( !msgtype.equals(MessageUtil.RESP_MESSAGE_TYPE_IMAGE)) {
			map.put("success", false);
			map.put("msg", "类型参数错误！");
			return map;
		}
		if(null == title || "".equals(title.trim())) {
			map.put("success", false);
			map.put("msg", "标题不能为空！");
			return map;
		}
		if(null == keyword || "".equals(keyword.trim())) {
			map.put("success", false);
			map.put("msg", "关键字不能为空！");
			return map;
		}
		if(null == url || "".equals(url.trim())) {
			map.put("success", false);
			map.put("msg", "文章地址不能为空！");
			return map;
		}
		if(null == describe || "".equals(describe.trim())) {
			map.put("success", false);
			map.put("msg", "描述不能为空！");
			return map;
		}
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		
		long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		int countKeyword = replyService.countReplyImgTextByKeyword(keyword);
		if(countKeyword>0) {
			map.put("success", false);
			map.put("msg", "换个关键字试试！");
			return map;
		}
		if (file.isEmpty()) {
			if(null == picurl || "".equals(picurl.trim())) {
				map.put("success", false);
				map.put("msg", "图片地址不能为空！");
				return map;
			}
			Reply reply = new Reply();
			reply.setMsgtype(msgtype);
			reply.setId(rid);
			reply.setIsapply(MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY);
			reply.setIsmisfortune(MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
			reply.setKeyword(keyword);
			JSONObject json = new JSONObject();
			json.put("title", title);
			json.put("describes", describe);
			json.put("picurl", picurl);
			json.put("url", url);
			reply.setMsg(json.toString());
			replyService.updateReplyImgText(reply);
			map.put("success", true);
			map.put("msg", "修改成功！");
			return map;
		}else {
			//取得当前上传图片的名称    
			String myFileName = file.getOriginalFilename(); 
			//后缀名
			String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
			boolean isImage = false;
			suffix = suffix.toLowerCase();
			for(int i = 0 ; i < imgTypes.length ; i++) {
				if(suffix.equals(imgTypes[i])) {
					isImage = true;
					break;
				}
			}
			if(!isImage) {
				map.put("success", false);
				map.put("msg", "请选择图片上传！");
				return map;
			}
			//图片名称
			String fileName = String.valueOf(System.currentTimeMillis())+suffix;
			try {
				if(myFileName.trim() !=""){ 
					String basePath = request.getSession().getServletContext().getRealPath("/")+"imgtext";
					File dir = new File(basePath);
				    if (!dir.exists()) {
				    	dir.mkdir();
				    }
				  //定义上传路径    
				    String path =  basePath + "/" + fileName;   
				    File localFile = new File(path);    
				    try {
						file.transferTo(localFile);
						String serverAdress = WechatConfigLoader.getServerAddress();
						picurl = serverAdress +"imgtext/"+fileName;
						JSONObject json = new JSONObject();
						json.put("title", title);
						json.put("describes", describe);
						json.put("picurl", picurl);
						json.put("url", url);
						Reply reply = new Reply();
						reply.setId(rid);
						reply.setMsgtype(msgtype);
						reply.setKeyword(keyword);
						reply.setIsapply(MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY);
						reply.setIsmisfortune(MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
						reply.setMsg(json.toString());
						replyService.updateReplyImgText(reply);
						map.put("success", true);
						map.put("msg", "修改成功！");
						return map;
					} catch (Exception e) {
						map.put("success", false);
						map.put("msg", "上传失败！");
						return map;
					}   
				}
			} catch (Exception e) {
				map.put("success", false);
				map.put("msg", "上传失败！");
				return map;
			}
		}
		return map;
	}
	/**
	 * 初始化关注时文本消息配置
	 * @return
	 */
	@RequestMapping("/initTextFollowConfig")
	@ResponseBody
	public Map<String,Object> textFollowConfig(){
		Map<String,Object> map = new HashMap<String,Object>();
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
		map.put("success", true);
		map.put("data", reMap);
		return map;
	}
	/**
	 *  初始化关注时图文消息配置
	 * @return
	 */
	@RequestMapping("/initImageFollowConfig")
	@ResponseBody
	public Map<String,Object> imageFollowConfig(){
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> params = new HashMap<String,Object>();
		Map<String,Object> reMap = new HashMap<String,Object>();
		params.put("msgtype", MessageUtil.RESP_MESSAGE_TYPE_IMAGE);
		//统计关注时文本回复配置
		int count = replyService.countFollowConfig(params);
		if(count==0) {
			replyService.insertFollowConfig(params);
		}else if(count > 1) {
			params.put("isapply", null);
			replyService.delFollowConfig(params);
			params.put("isapply", MessageUtil.RESP_MESSAGE_STATUS_ISNOTAPPLY);
			replyService.insertFollowConfig(params);
		}else if(count == 1) {
			reMap = replyService.oneFollowConfig(params);
		}
		map.put("success", true);
		map.put("data", reMap);
		return map;
	}
	/**
	 * 是否开启关注时文本回复消息
	 * @param isapply int 是否使用  0 否 1 是 必须
	 * @return
	 */
	@RequestMapping("/updateTextFollowConfig")
	@ResponseBody
	public Map<String,Object> updateTextFollowConfig(Integer isapply){
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> params = new HashMap<String,Object>();
		if(null == isapply || "".equals(isapply.toString())) {
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		if(isapply == MessageUtil.RESP_MESSAGE_STATUS_ISNOTAPPLY || isapply == MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY) {
			params.put("msgtype", MessageUtil.RESP_MESSAGE_TYPE_TEXT);
			params.put("isapply", isapply);
			replyService.updateFollowConfig(params);
			map.put("success", true);
			map.put("msg", "修改成功！");
		}else {
			map.put("success", false);
			map.put("msg", "请求参数错误！");
		}
		return map;
	}
	/**
	 * 是否开启关注时图文回复消息
	 * @param isapply int 是否使用  0 否 1 是 必须
	 * @return
	 */
	@RequestMapping("/updateImageFollowConfig")
	@ResponseBody
	public Map<String,Object> updateImageFollowConfig(Integer isapply){
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> params = new HashMap<String,Object>();
		if(isapply == MessageUtil.RESP_MESSAGE_STATUS_ISNOTAPPLY || isapply == MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY) {
			params.put("msgtype", MessageUtil.RESP_MESSAGE_TYPE_IMAGE);
			params.put("isapply", isapply);
			replyService.updateFollowConfig(params);
		}else {
			map.put("success", false);
			map.put("msg", "请求参数错误！");
		}
		map.put("success", true);
		map.put("msg", "修改成功！");
		return map;
	}
	/**
	 * 关注时文本回复消息 分页列表
	 * @param rows int 行数 非必须
	 * @param page int 页码 非必须
	 * @param title string 回复内容 非必须
	 * @return
	 */
	@RequestMapping("/queryTextFollowReplyList")
	@ResponseBody
	public Map<String,Object> queryTextFollowReplyList(Integer rows,Integer page,String title){
		Map<String,Object> map = new HashMap<String,Object>();
		//初始化参数行数
		if(null == rows || "".equals(rows.toString())) {
			rows =  10 ;
		}
		//初始化参数页码
		if(null == page || "".equals(page.toString())) {
			page =  1 ;
		}
		Map<String ,Object> params = new HashMap<String,Object>();
		params.put("rows", rows);
		params.put("page", (page-1)*rows);
		if(null != title && !"".equals(title)) {
			params.put("title", title);
		}
		params.put("ismisfortune", MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
		params.put("msgtype", MessageUtil.RESP_MESSAGE_TYPE_TEXT);
		List<Map<String,Object>> list = replyService.queryTextFollowReplyList(params);
		if(null != list && list.size()>0) {
			for(int i = 0 ; i < list.size() ; i++ ) {
				list.get(i).put("msg", JSONObject.fromObject(list.get(i).get("msg")).get("title"));
			}
		}
		int count = replyService.countTextFollowReplyList(params);
		map.put("success", true);
		map.put("data", list);
		map.put("count", count);
		return map;
	}
	/**
	 * 添加关注时文本回复消息
	 * @param title string 回复内容 必须 
	 * @param msgtype string 类型 text 必须
	 * @return
	 */
	@RequestMapping("/insertTextFollowReply")
	@ResponseBody
	public Map<String,Object> insertTextFollowReply(String title,String msgtype){
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == msgtype || "".equals(msgtype)) {
			map.put("success", false);
			map.put("msg", "类型不能为空！");
			return map;
		}
		if(!msgtype.equals(MessageUtil.RESP_MESSAGE_TYPE_TEXT)) {
			map.put("success", false);
			map.put("msg", "类型参数错误！");
			return map;
		}
		if(null == title || "".equals(title.trim())) {
			map.put("success", false);
			map.put("msg", "回复消息不能为空！");
			return map;
		}
		JSONObject json = new JSONObject();
		json.put("title", title);
		Reply reply = new Reply();
		reply.setMsgtype(msgtype);
		reply.setKeyword(null);
		reply.setIsapply(MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY);
		reply.setMsg(json.toString());
		replyService.insertTextReply(reply);
		map.put("success", true);
		map.put("msg", "文本消息创建成功！");
		return map;
	}
	/**
	 * 修改关注时文本回复消息
	 * @param title string 回复内容 必须 
	 * @param msgtype string 类型 text 必须
	 * @param id string 消息回复id
	 * @return
	 */
	@RequestMapping("/updateTextFollowReply")
	@ResponseBody
	public Map<String,Object> updateTextFollowReply(String title,String msgtype,String id){
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == msgtype || "".equals(msgtype)) {
			map.put("success", false);
			map.put("msg", "类型不能为空！");
			return map;
		}
		if(!msgtype.equals(MessageUtil.RESP_MESSAGE_TYPE_TEXT)) {
			map.put("success", false);
			map.put("msg", "类型参数错误！");
			return map;
		}
		if(null == title || "".equals(title.trim())) {
			map.put("success", false);
			map.put("msg", "回复消息不能为空！");
			return map;
		}
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		
		long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		JSONObject json = new JSONObject();
		json.put("title", title);
		Reply reply = new Reply();
		reply.setMsgtype(msgtype);
		reply.setKeyword(null);
		reply.setIsapply(MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY);
		reply.setIsmisfortune(MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
		reply.setMsg(json.toString());
		reply.setId(rid);
		replyService.updateTextFollowReply(reply);
		map.put("success", true);
		map.put("msg", "修改成功！");
		return map;
	}
	/**
	 * 删除关注时文本回复消息 （逻辑删除）
	 * @param id string 消息回复id
	 * @return
	 */
	@RequestMapping("/delTextFollowReply")
	@ResponseBody
	public Map<String,Object> delTextFollowReply(String id){
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		
		long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		Reply reply = new Reply();
		reply.setMsgtype(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
		reply.setIsapply(MessageUtil.RESP_MESSAGE_STATUS_ISNOTAPPLY);
		reply.setIsmisfortune(MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
		reply.setId(rid);
		replyService.delTextFollowReply(reply);
		map.put("success", true);
		map.put("msg", "删除成功！");
		return map;
	}
	
	/**
	 * 查询一条关注时文本回复消息 
	 * @param id string 消息回复id
	 * @return
	 */
	@RequestMapping("/oneTextFollowReply")
	@ResponseBody
	public Map<String,Object> oneTextFollowReply(String id){
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		
		long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		Map<String ,Object> reMap =  replyService.oneTextFollowReply(rid);
		if(null == reMap || reMap.size()==0 ) {
			map.put("success", false);
			map.put("msg", "请求参数错误！");
		}else {
			reMap.put("msg", JSONObject.fromObject(reMap.get("msg")).get("title"));
			map.put("success", true);
			map.put("data", reMap);
		}
		return map;
	}
	/**
	 * 关注时图文回复消息 分页列表
	 * @param rows int 行数 非必须
	 * @param page int 页码 非必须
	 * @param title string 回复内容 非必须
	 * @return
	 */
	@RequestMapping("/queryImageFollowReplyList")
	@ResponseBody
	public Map<String,Object> queryImageFollowReplyList(Integer rows,Integer page,String title){
		Map<String,Object> map = new HashMap<String,Object>();
		//初始化参数行数
		if(null == rows || "".equals(rows.toString())) {
			rows =  10 ;
		}
		//初始化参数页码
		if(null == page || "".equals(page.toString())) {
			page =  1 ;
		}
		Map<String ,Object> params = new HashMap<String,Object>();
		params.put("rows", rows);
		params.put("page", (page-1)*rows);
		if(null != title && !"".equals(title)) {
			params.put("title", title);
		}
		params.put("ismisfortune", MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
		params.put("msgtype", MessageUtil.RESP_MESSAGE_TYPE_IMAGE);
		List<Map<String,Object>> list = replyService.queryImageFollowReplyList(params);
		if(null != list && list.size()>0) {
			for(int i = 0 ; i < list.size() ; i++ ) {
				JSONObject json = JSONObject.fromObject(list.get(i).get("msg"));
				list.get(i).put("picurl", json.get("picurl"));
				list.get(i).put("url", json.get("url"));
				list.get(i).put("describes", json.get("describes"));
				list.get(i).put("msg", json.get("title"));
			}
		}
		int count = replyService.countImageFollowReplyList(params);
		map.put("success", true);
		map.put("data", list);
		map.put("count", count);
		return map;
	}
	/**
	 * 添加关注时图文回复消息
	 * @param file file文件 图片 jpg、png 任选其一  必须
	 * @param msgtype string 类型 image 必须 
	 * @param url string 跳转地址 必须
	 * @param title string 主标题 必须
	 * @param describe string 描述 必须
	 * @return
	 */
	@RequestMapping("/insertImageFollowReplyList")
	@ResponseBody
	public Map<String,Object> insertImageFollowReplyList(HttpServletRequest request,@RequestParam("file") MultipartFile file,String msgtype,String url,String title,String describe){
		Map<String,Object> map = new HashMap<String,Object>();
		//jpg .jpeg .gif .png .bmp
		String imgTypes[] = {".jpg",".png"};
		
		if (file.isEmpty()) {
			map.put("success", false);
			map.put("msg", "请上传图片！");
			return map;
		}
		if(null == msgtype || "".equals(msgtype)) {
			map.put("success", false);
			map.put("msg", "类型不能为空！");
			return map;
		}
		if(!msgtype.equals(MessageUtil.RESP_MESSAGE_TYPE_IMAGE)) {
			map.put("success", false);
			map.put("msg", "类型参数错误！");
			return map;
		}
		if(null == title || "".equals(title.trim())) {
			map.put("success", false);
			map.put("msg", "标题不能为空！");
			return map;
		}
		if(null == url || "".equals(url.trim())) {
			map.put("success", false);
			map.put("msg", "文章地址不能为空！");
			return map;
		}
		if(null == describe || "".equals(describe.trim())) {
			map.put("success", false);
			map.put("msg", "描述不能为空！");
			return map;
		}
		//取得当前上传图片的名称    
		String myFileName = file.getOriginalFilename(); 
		//后缀名
		String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		boolean isImage = false;
		suffix = suffix.toLowerCase();
		for(int i = 0 ; i < imgTypes.length ; i++) {
			if(suffix.equals(imgTypes[i])) {
				isImage = true;
				break;
			}
		}
		if(!isImage) {
			map.put("success", false);
			map.put("msg", "请选择图片上传！");
			return map;
		}
		//图片名称
		String fileName = String.valueOf(System.currentTimeMillis())+suffix;
		try {
			if(myFileName.trim() !=""){ 
				String basePath = request.getSession().getServletContext().getRealPath("/")+"imgtext";
				File dir = new File(basePath);
			    if (!dir.exists()) {
			    	dir.mkdir();
			    }
			  //定义上传路径    
			    String path =  basePath + "/" + fileName;   
			    File localFile = new File(path);    
			    try {
					file.transferTo(localFile);
					String serverAdress = WechatConfigLoader.getServerAddress();
					String picurl = serverAdress +"imgtext/"+fileName;
					JSONObject json = new JSONObject();
					json.put("title", title);
					json.put("describes", describe);
					json.put("picurl", picurl);
					json.put("url", url);
					Reply reply = new Reply();
					reply.setMsgtype(msgtype);
					reply.setKeyword(null);
					reply.setIsapply(MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY);
					reply.setMsg(json.toString());
					replyService.insertImageReply(reply);
					map.put("success", true);
					map.put("msg", "保存成功！");
					return map;
				} catch (Exception e) {
					map.put("success", false);
					map.put("msg", "上传失败！");
					return map;
				}   
			}
		} catch (Exception e) {
			map.put("success", false);
			map.put("msg", "上传失败！");
			return map;
		}
		return map;
	}
	/**
	 * 修改关注时图文回复消息
	 * @param file file文件 图片 jpg、png 任选其一  非必须
	 * @param msgtype string 类型 image 必须 
	 * @param url string 跳转地址 必须
	 * @param title string 主标题 必须
	 * @param describe string 描述 必须
	 * @param picurl string 图片地址 非必须（上传图片 picurl非必须 不上传图片 picurl 必须） 
	 * @param id string 回复消息id
	 * @return
	 */
	@RequestMapping("/updateImageFollowReplyList")
	@ResponseBody
	public Map<String,Object> updateImageFollowReplyList(HttpServletRequest request,@RequestParam("file") MultipartFile file,String msgtype,String url,String title,String describe,String picurl,String id){
		Map<String,Object> map = new HashMap<String,Object>();
		//jpg .jpeg .gif .png .bmp
		String imgTypes[] = {".jpg",".png"};
		
		if(null == msgtype || "".equals(msgtype)) {
			map.put("success", false);
			map.put("msg", "类型不能为空！");
			return map;
		}
		if( !msgtype.equals(MessageUtil.RESP_MESSAGE_TYPE_IMAGE)) {
			map.put("success", false);
			map.put("msg", "类型参数错误！");
			return map;
		}
		if(null == title || "".equals(title.trim())) {
			map.put("success", false);
			map.put("msg", "标题不能为空！");
			return map;
		}
		if(null == url || "".equals(url.trim())) {
			map.put("success", false);
			map.put("msg", "文章地址不能为空！");
			return map;
		}
		if(null == describe || "".equals(describe.trim())) {
			map.put("success", false);
			map.put("msg", "描述不能为空！");
			return map;
		}
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		
		long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		if (file.isEmpty()) {
			if(null == picurl || "".equals(picurl.trim())) {
				map.put("success", false);
				map.put("msg", "图片地址不能为空！");
				return map;
			}
			Reply reply = new Reply();
			reply.setMsgtype(msgtype);
			reply.setId(rid);
			reply.setIsapply(MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY);
			reply.setIsmisfortune(MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
			reply.setKeyword(null);
			JSONObject json = new JSONObject();
			json.put("title", title);
			json.put("describes", describe);
			json.put("picurl", picurl);
			json.put("url", url);
			reply.setMsg(json.toString());
			replyService.updateImageFollowReply(reply);
			map.put("success", true);
			map.put("msg", "修改成功！");
			return map;
		}else {
			//取得当前上传图片的名称    
			String myFileName = file.getOriginalFilename(); 
			//后缀名
			String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
			boolean isImage = false;
			suffix = suffix.toLowerCase();
			for(int i = 0 ; i < imgTypes.length ; i++) {
				if(suffix.equals(imgTypes[i])) {
					isImage = true;
					break;
				}
			}
			if(!isImage) {
				map.put("success", false);
				map.put("msg", "请选择图片上传！");
				return map;
			}
			//图片名称
			String fileName = String.valueOf(System.currentTimeMillis())+suffix;
			try {
				if(myFileName.trim() !=""){ 
					String basePath = request.getSession().getServletContext().getRealPath("/")+"imgtext";
					File dir = new File(basePath);
				    if (!dir.exists()) {
				    	dir.mkdir();
				    }
				  //定义上传路径    
				    String path =  basePath + "/" + fileName;   
				    File localFile = new File(path);    
				    try {
						file.transferTo(localFile);
						String serverAdress = WechatConfigLoader.getServerAddress();
						picurl = serverAdress +"imgtext/"+fileName;
						JSONObject json = new JSONObject();
						json.put("title", title);
						json.put("describes", describe);
						json.put("picurl", picurl);
						json.put("url", url);
						Reply reply = new Reply();
						reply.setId(rid);
						reply.setMsgtype(msgtype);
						reply.setKeyword(null);
						reply.setIsapply(MessageUtil.RESP_MESSAGE_STATUS_ISAPPLY);
						reply.setIsmisfortune(MessageUtil.RESP_MESSAGE_STATUS_ISNotMISFORTUNE);
						reply.setMsg(json.toString());
						replyService.updateImageFollowReply(reply);
						map.put("success", true);
						map.put("msg", "修改成功！");
						return map;
					} catch (Exception e) {
						map.put("success", false);
						map.put("msg", "上传失败！");
						return map;
					}   
				}
			} catch (Exception e) {
				map.put("success", false);
				map.put("msg", "上传失败！");
				return map;
			}
		}
		return map;
	}
	/**
	 * 删除关注时图文回复消息 （逻辑删除）
	 * @param id string id 消息id 必须
	 * @return
	 */
	@RequestMapping("/delImageFollowReplyList")
	@ResponseBody
	public Map<String,Object> delImageFollowReplyList(String id){
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		
		long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		replyService.delImageFollowReplyList(rid);
		map.put("success", true);
		map.put("msg", "删除成功！");
		return map;
	}
	/**
	 * 查询一条关注时图文回复消息 
	 * @param id string id 消息id 必须
	 * @return
	 */
	@RequestMapping("/oneImageFollowReplyList")
	@ResponseBody
	public Map<String,Object> oneImageFollowReplyList(String id){
		Map<String,Object> map = new HashMap<String,Object>();
		if(null == id || "".equals(id.trim())) {
			map.put("success", false);
			map.put("msg", "id不能为空！");
			return map;
		}
		
		long rid = 0l;
		try {
			rid = Long.valueOf(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请求参数错误！");
			return map;
		}
		Map<String ,Object> reMap =  replyService.oneImageFollowReplyList(rid);
		if(null == reMap || reMap.size()==0 ) {
			map.put("success", false);
			map.put("msg", "请求参数错误！");
		}else {
			reMap.put("msg", JSONObject.fromObject(reMap.get("msg")).get("title"));
			map.put("success", true);
			map.put("data", reMap);
		}
		return map;
	}
	
}
