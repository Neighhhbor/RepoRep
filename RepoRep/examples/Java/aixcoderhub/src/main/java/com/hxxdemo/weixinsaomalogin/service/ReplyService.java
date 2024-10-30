package com.hxxdemo.weixinsaomalogin.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

import com.hxxdemo.weixinsaomalogin.entity.Reply;

public interface ReplyService {

	/**
	 * 插入文本回复消息
	 * @param reply
	 */
	void insertTextReply(Reply reply);
	
	/**
	 * 插入图文回复消息
	 * @param reply
	 */
	void insertImageReply(Reply reply);
	/**
	 * 插入未命中回复消息
	 * @param reply
	 */
	void insertMisfortuneReply(Reply reply);
	
	/**
	 * 查询未命中列表
	 * @param params
	 * @return
	 */
	List<Map<String ,Object>> queryMisfortuneList(Map<String,Object> params);
	/**
	 * 统计未命中个数
	 * @return
	 */
	int countMisfortuneList(Map<String,Object> params);
	
	/**
	 * 根据id修改未命中消息
	 * @param reply
	 */
	void updateMisfortune(Reply reply);
	
	/**
	 * 根据id查询未命中消息
	 * @param id
	 * @return
	 */
	Map<String,Object> oneMisfortune(Long id);
	
	/**
	 * 根据id删除未命中消息
	 * @param id
	 */
	void delMisfortune(Reply reply);
	
	/**
	 * 查询开启未命中配置
	 * @return
	 */
	Map<String,Object> queryReplyConfig();
	
	/**
	 * 插入未命中配置
	 * @param param
	 */
	void insertReplyConfig(Map<String,Object> param);
	/**
	 * 修改未命中配置
	 * @param param
	 */
	void updateReplyConfg(Map<String,Object> param);
	
	/**
	 * 查询未命中时间
	 * @return
	 */
	String queryReplyConfigTime();
	
	/**
	 * 统计配置个数 默认为一个
	 * @return
	 */
	int countReplyConfig();
	
	/**
	 * 重置未命中配置
	 */
	void resetReplyConfg();
	/**
	 * 根据关键词回复内容
	 * @param keyword
	 * @return list
	 */
	List<Map<String,Object>> getMsgReplayByKeyword(String keyword);
	
	/**
	 * 获取未命中列表
	 * @return
	 */
	List<Map<String,Object>> getMisfortune();
	
	/**
	 * 获取文本回复消息列表
	 * @param params
	 * @return
	 */
	List<Map<String,Object>> getReplyTextList(Map<String,Object> params);
	
	/**
	 * 获取文本回复消息个数
	 * @param params
	 * @return
	 */
	int countReplyTextList(Map<String,Object> params);
	
	/**
	 * 根据id查询文本消息
	 * @param id
	 * @return
	 */
	Map<String,Object> oneReplyText(Long id);
	
	/**
	 * 根据id修改文本消息
	 * @param reply
	 */
	void updateReplyText(Reply reply);
	/**
	 * 根据id删除文本消息
	 * @param id
	 */
	void delReplyText(Reply reply);
	/**
	 * 获图文回复消息列表
	 * @param params
	 * @return
	 */
	List<Map<String,Object>> queryReplyImgTextList(Map<String,Object> params);
	
	/**
	 * 获取图文回复消息个数
	 * @param params
	 * @return
	 */
	int countReplyImgTextList(Map<String,Object> params);
	/**
	 * 根据id查询文本消息
	 * @param id
	 * @return
	 */
	Map<String,Object> oneReplyImgText(Long id);
	
	/**
	 * 根据id删除图文消息
	 * @param id
	 */
	void delReplyImgText(Reply reply);
	
	/**
	 * 修改图文消息
	 * @param reply
	 */
	void updateReplyImgText(Reply reply);
	
	/**
	 * 通过keyword统计文本关键字个数（文本统计所有-图文与文本的关键字不能重复）
	 * @param keyword
	 * @return
	 */
	int countReplyTextByKeyword(String keyword);
	
	/**
	 * 通过keyword统计图文关键字个数（图文统计文本里面的-图文与文本的关键字不能重复）
	 * @param keyword
	 * @return
	 */
	int countReplyImgTextByKeyword(String keyword);
	
	/**
	 * 判断是否开启未命中
	 * @return
	 */
	int countReplyConfigIsopen();
	
	/**
	 * 统计关注时文本图文回复配置
	 * @param params
	 * @return
	 */
	int countFollowConfig(Map<String,Object> params);
	
	/**
	 * 插入关注时文本图文回复配置
	 * @param params
	 * @return
	 */
	void insertFollowConfig(Map<String,Object> params);
	
	/**
	 * 删除关注时文本图文回复配置
	 * @param params
	 * @return
	 */
	void delFollowConfig(Map<String,Object> params);
	
	/**
	 * 查询关注配置
	 * @param params
	 * @return
	 */
	Map<String,Object> oneFollowConfig(Map<String,Object> params);
	/**
	 * 修改关注时文本图文回复配置
	 * @param params
	 * @return
	 */
	void updateFollowConfig(Map<String,Object> params);
	/**
	 * 查询关注文本列表
	 * @param params
	 * @return
	 */
	List<Map<String ,Object>> queryTextFollowReplyList(Map<String,Object> params);
	
	/**
	 * 统计关注文本个数
	 * @return
	 */
	int countTextFollowReplyList(Map<String,Object> params);
	
	/**
	 * 根据id修改关注文本消息
	 * @param reply
	 */
	void updateTextFollowReply(Reply reply);
	
	/**
	 * 根据id删除关注文本消息
	 * @param reply
	 */
	void delTextFollowReply(Reply reply);
	/**
	 * 根据id查询关注文本消息
	 * @param id
	 * @return
	 */
	Map<String,Object> oneTextFollowReply(Long id);
	/**
	 * 查询关注图文列表
	 * @param params
	 * @return
	 */
	List<Map<String ,Object>> queryNewsFollowReplyList(Map<String,Object> params);
	
	/**
	 * 统计关注图文个数
	 * @return
	 */
	int countImageFollowReplyList(Map<String,Object> params);
	/**
	 * 根据id查询关注图文消息
	 * @param id
	 * @return
	 */
	Map<String,Object> oneImageFollowReplyList(Long id);
	/**
	 * 根据id删除关注图文消息
	 * @param id
	 * @return
	 */
	void delImageFollowReplyList(Long id);
	/**
	 * 修改关注图文消息
	 * @param reply
	 */
	void updateImageFollowReply(Reply reply);
	/**
	 * 查询关注图片列表
	 * @param params
	 * @return
	 */
	List<Map<String ,Object>> queryImageFollowReplyList(Map<String,Object> params);
	/**
	 * click事件回复消息
	 * @param params
	 * @return
	 */
	List<Map<String ,Object>> queryClickReplyList(Long id);
	
}
