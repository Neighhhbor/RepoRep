package com.hxxdemo.weixinsaomalogin.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.hxxdemo.weixinsaomalogin.entity.Msglog;
@Mapper
public interface MsglogDao {

	/**
	 * 插入微信消息
	 * @param snsUserInfo
	 */
	@Insert("insert into weixin_msglog (FromUserName,content,MsgType,event,ToUserName,CreateTime,PicUrl,status,entertime) values (#{FromUserName},#{content},#{MsgType},#{event},#{ToUserName},#{CreateTime},#{PicUrl},#{status},now())")
	void insertMsgLog(Msglog msglog);
	
	/**
	 * 时间内是否发送过未命中消息 
	 * @param msglog
	 * @return 0 否 1 是
	 */
	@Select("select count(1) from weixin_msglog where ToUserName = #{ToUserName} and FromUserName=#{FromUserName} and (entertime + INTERVAL #{opentime} MINUTE)>now() AND MsgType <>'event'  ")
	int countMisfortuneTimeNum(Msglog msglog);
	
	/**
	 * 公众号用户所有回复消息列表
	 * @param params
	 * @return
	 */
	@Select("<script>SELECT a.* ,b.nickname,b.headimgurl,b.remark FROM (SELECT * FROM weixin_msglog where <![CDATA[ FromUserName <> #{originalId} ]]> AND <![CDATA[ MsgType <> 'event' ]]> <if test='title!=null '> and content like concat('%', #{title}, '%')</if> <if test='nickname!=null '> and FromUserName IN ( SELECT c.gopenid FromUserName from weixin_user c where c.nickname LIKE  concat('%', #{nickname}, '%') OR c.remark LIKE concat('%', #{nickname}, '%') GROUP BY c.gopenid)</if> ORDER BY CreateTime DESC limit #{page},#{rows} ) a LEFT JOIN weixin_user b on a.fromusername = b.gopenid</script>")
	List<Map<String,Object>> queryMsgLogList(Map<String,Object> params);
	/**
	 * 统计公众号用户所有回复消息
	 * @param params
	 * @return
	 */
	@Select("<script>SELECT count(1) FROM weixin_msglog where <![CDATA[ FromUserName <> #{originalId} ]]> AND <![CDATA[ MsgType <> 'event' ]]> <if test='title!=null '> and content like concat('%', #{title}, '%')</if>  <if test='nickname!=null '> and FromUserName IN ( SELECT c.gopenid FromUserName from weixin_user c where c.nickname LIKE  concat('%', #{nickname}, '%') OR c.remark LIKE concat('%', #{nickname}, '%') GROUP BY c.gopenid)</if> </script>")
	int countMsgLogList(Map<String,Object> params);
	
}
