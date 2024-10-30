package com.hxxdemo.weixinsaomalogin.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hxxdemo.weixinsaomalogin.entity.Reply;

@Mapper
public interface ReplyDao {

	/**
	 * 插入文本回复消息
	 * @param reply
	 */
	@Insert("insert into weixin_reply (msg,msgtype,isapply,keyword) values (#{msg},#{msgtype},#{isapply},#{keyword})")
	void insertTextReply(Reply reply);
	
	/**
	 * 插入图文回复消息
	 * @param reply
	 */
	@Insert("insert into weixin_reply (msg,msgtype,isapply,keyword) values (#{msg},#{msgtype},#{isapply},#{keyword})")
	void insertImageReply(Reply reply);
	
	/**
	 * 插入未命中回复消息
	 * @param reply
	 */
	@Insert("insert into weixin_reply (msg,msgtype,isapply,ismisfortune) values (#{msg},#{msgtype},#{isapply},#{ismisfortune})")
	void insertMisfortuneReply(Reply reply);
	
	/**
	 * 查询未命中列表
	 * @param params
	 * @return
	 */
	@Select("<script>select * from weixin_reply where isapply=1 and ismisfortune = #{ismisfortune} <if test='title!=null and title!=null'> and msg like concat('%', #{title}, '%') </if> limit #{page},#{rows}</script>")
	List<Map<String ,Object>> queryMisfortuneList(Map<String,Object> params);
	
	/**
	 * 统计未命中个数
	 * @return
	 */
	
	@Select("<script>select count(1) from weixin_reply where isapply=1 and ismisfortune = #{ismisfortune} <if test='title!=null and title!=null'> and msg like concat('%', #{title}, '%') </if> </script>")
	int countMisfortuneList(Map<String,Object> params);
	
	/**
	 * 根据id修改未命中消息
	 * @param reply
	 */
	@Update("update weixin_reply set msg = #{msg}  where isapply =1 and  id = #{id} and ismisfortune = #{ismisfortune} and msgtype=#{msgtype}")
	void updateMisfortune(Reply reply);
	
	/**
	 * 根据id查询未命中消息
	 * @param id
	 * @return
	 */
	@Select("select * from weixin_reply where isapply =1 and ismisfortune=1  and id=#{id}")
	Map<String,Object> oneMisfortune(Long id);
	
	
	/**
	 * 根据id删除未命中消息
	 * @param id
	 */
	@Update("update weixin_reply set isapply=#{isapply} where isapply =1 and ismisfortune = #{ismisfortune} and id=#{id} and msgtype=#{msgtype} ")
	void delMisfortune(Reply reply);
	
	/**
	 * 查询开启未命中配置
	 * @return
	 */
	@Select("select * from weixin_replyConfig where isapply =1 and isopen=1")
	Map<String,Object> queryReplyConfig();
	
	/**
	 * 插入未命中配置
	 * @param param
	 */
	@Insert("insert into weixin_replyConfig (isopen,opentime) values (#{isopen},#{opentime})")
	void insertReplyConfig(Map<String,Object> param);
	/**
	 * 修改未命中配置
	 * @param param
	 */
	@Update("<script>update weixin_replyConfig set isopen=#{isopen} <if test='opentime!=null and opentime!=null'> , opentime=#{opentime} </if> where isapply=1</script>")
	void updateReplyConfg(Map<String,Object> param);
	
	/**
	 * 查询未命中时间
	 * @return
	 */
	@Select("select opentime from weixin_replyConfig where isapply =1")
	String queryReplyConfigTime();
	
	/**
	 * 统计配置个数 默认为一个
	 * @return
	 */
	@Select("select count(1) from weixin_replyConfig where isapply =1")
	int countReplyConfig();
	
	/**
	 * 重置未命中配置
	 */
	@Update("update weixin_replyConfig set isapply = 0 ")
	void resetReplyConfg();
	
	/**
	 * 根据关键词回复内容
	 * @param keyword
	 * @return list
	 */
	@Select("select * from weixin_reply where keyword =#{keyword} and isapply =1 and ismisfortune=0 limit 0,8")
	List<Map<String,Object>> getMsgReplayByKeyword(String keyword);
	
	/**
	 * 获取未命中列表
	 * @return
	 */
	@Select("select * from weixin_reply where isapply=1 and ismisfortune=1 ")
	List<Map<String,Object>> getMisfortune();
	
	/**
	 * 获取文本回复消息列表
	 * @param params
	 * @return
	 */
	@Select("<script>select * from weixin_reply where isapply=1 and msgtype = #{msgtype} <if test='title!=null and title!=null'> and msg like concat('%', #{title}, '%') </if> and ismisfortune =#{ismisfortune} limit #{page},#{rows}</script>")
	List<Map<String,Object>> getReplyTextList(Map<String,Object> params);
	
	/**
	 * 获取文本回复消息个数
	 * @param params
	 * @return
	 */
	@Select("<script>select count(1) from weixin_reply where isapply=1 and msgtype = #{msgtype} <if test='title!=null and title!=null'> and msg like concat('%', #{title}, '%') </if> and ismisfortune =#{ismisfortune} </script>")
	int countReplyTextList(Map<String,Object> params);
	
	/**
	 * 根据id查询文本消息
	 * @param id
	 * @return
	 */
	@Select("select * from weixin_reply where isapply=1 and msgtype='text' and ismisfortune=0 and id=#{id}")
	Map<String,Object> oneReplyText(Long id);
	
	/**
	 * 根据id修改文本消息
	 * @param reply
	 */
	@Update("update weixin_reply set msg = #{msg} ,keyword=#{keyword} where isapply =1 and  id = #{id} and ismisfortune = #{ismisfortune} and msgtype=#{msgtype}")
	void updateReplyText(Reply reply);
	
	/**
	 * 根据id删除文本消息
	 * @param id
	 */
	@Update("update weixin_reply set isapply=#{isapply} where isapply =1 and ismisfortune = #{ismisfortune} and id=#{id} and msgtype=#{msgtype}")
	void delReplyText(Reply reply);
	
	/**
	 * 获图文回复消息列表
	 * @param params
	 * @return
	 */
	@Select("<script>select * from weixin_reply where isapply=1 and msgtype = #{msgtype} <if test='title!=null and title!=null'> and msg like concat('%', #{title}, '%') </if> and ismisfortune =#{ismisfortune} limit #{page},#{rows}</script>")
	List<Map<String,Object>> queryReplyImgTextList(Map<String,Object> params);
	
	/**
	 * 获取图文回复消息个数
	 * @param params
	 * @return
	 */
	@Select("<script>select count(1) from weixin_reply where isapply=1 and msgtype = #{msgtype} <if test='title!=null and title!=null'> and msg like concat('%', #{title}, '%') </if> and ismisfortune =#{ismisfortune}  </script>")
	int countReplyImgTextList(Map<String,Object> params);
	/**
	 * 根据id查询图文消息
	 * @param id
	 * @return
	 */
	@Select("select * from weixin_reply where isapply=1 and msgtype='image' and ismisfortune=0 and id=#{id}")
	Map<String,Object> oneReplyImgText(Long id);
	/**
	 * 根据id删除图文消息
	 * @param id
	 */
	@Update("update weixin_reply set isapply=#{isapply} where isapply =1 and ismisfortune = #{ismisfortune} and id=#{id} and msgtype=#{msgtype}")
	void delReplyImgText(Reply reply);
	
	/**
	 * 修改图文消息
	 * @param reply
	 */
	@Update("update weixin_reply set msg=#{msg},keyword=#{keyword} where isapply =#{isapply} and ismisfortune = #{ismisfortune} and id=#{id} and msgtype=#{msgtype}")
	void updateReplyImgText(Reply reply);
	
	/**
	 * 通过keyword统计文本关键字个数（文本统计所有-图文与文本的关键字不能重复）
	 * @param keyword
	 * @return
	 */
	@Select("select count(1) from weixin_reply where keyword=#{keyword} and isapply=1 ")
	int countReplyTextByKeyword(String keyword);
	
	/**
	 * 通过keyword统计图文关键字个数（图文统计文本里面的-图文与文本的关键字不能重复）
	 * @param keyword
	 * @return
	 */
	@Select("select count(1) from weixin_reply where keyword=#{keyword} and msytype='text' and isapply=1 ")
	int countReplyImgTextByKeyword(String keyword);
	
	/**
	 * 判断是否开启未命中
	 * @return
	 */
	@Select("select count(1) from weixin_replyConfig where isapply=1 and isopen=1")
	int countReplyConfigIsopen();
	
	/**
	 * 统计关注时文本图文回复配置
	 * @param params
	 * @return
	 */
	@Select("<script>select count(1) from weixin_followConfig where msgtype=#{msgtype} <if test='isapply!=null and isapply!=null'> and isapply = #{isapply}</if></script>")
	int countFollowConfig(Map<String,Object> params);
	
	/**
	 * 插入关注时文本图文回复配置
	 * @param params
	 * @return
	 */
	@Insert("insert into weixin_followConfig (msgtype,isapply) values (#{msgtype},#{isapply})")
	void insertFollowConfig(Map<String,Object> params);
	
	/**
	 * 删除关注时文本图文回复配置
	 * @param params
	 * @return
	 */
	@Update("update weixin_followConfig set isapply=#{isapply} ,msgtype = null where msgtype=#{msgtype})")
	void delFollowConfig(Map<String,Object> params);
	
	/**
	 * 查询关注配置
	 * @param params
	 * @return
	 */
	@Select("<script>select * from weixin_followConfig where msgtype=#{msgtype} <if test='isapply!=null and isapply!=null'> and isapply = #{isapply}</if></script>")
	Map<String,Object> oneFollowConfig(Map<String,Object> params);
	
	/**
	 * 修改关注时文本图文回复配置
	 * @param params
	 * @return
	 */
	@Update("update weixin_followConfig set isapply=#{isapply} where msgtype=#{msgtype})")
	void updateFollowConfig(Map<String,Object> params);
	
	/**
	 * 查询关注文本列表
	 * @param params
	 * @return
	 */
	@Select("<script>select * from weixin_reply where isapply=1 and msgtype=#{msgtype} and keyword is null and ismisfortune = #{ismisfortune} <if test='title!=null and title!=null'> and msg like concat('%', #{title}, '%') </if> limit #{page},#{rows}</script>")
	List<Map<String ,Object>> queryTextFollowReplyList(Map<String,Object> params);
	
	/**
	 * 统计关注文本个数
	 * @return
	 */
	@Select("<script>select count(1) from weixin_reply where isapply=1 and msgtype=#{msgtype} and keyword is null and ismisfortune = #{ismisfortune} <if test='title!=null and title!=null'> and msg like concat('%', #{title}, '%') </if> </script>")
	int countTextFollowReplyList(Map<String,Object> params);
	/**
	 * 根据id修改关注文本消息
	 * @param reply
	 */
	@Update("update weixin_reply set msg = #{msg}  where isapply =#{isapply} and keyword is #{keyword} and id = #{id} and ismisfortune = #{ismisfortune} and msgtype=#{msgtype}")
	void updateTextFollowReply(Reply reply);
	
	/**
	 * 根据id删除关注文本消息
	 * @param reply
	 */
	@Update("update weixin_reply set isapply =#{isapply}  where isapply =1  and id = #{id} and ismisfortune = #{ismisfortune} and msgtype=#{msgtype}")
	void delTextFollowReply(Reply reply);
	
	/**
	 * 根据id查询关注文本消息
	 * @param id
	 * @return
	 */
	@Select("select * from weixin_reply where isapply =1 and keyword is null and ismisfortune = 0 and id =#{id} and msgtype='text'")
	Map<String,Object> oneTextFollowReply(Long id);
	/**
	 * 查询关注图文列表
	 * @param params
	 * @return
	 */
	@Select("<script>select * from weixin_reply where isapply=1 and msgtype=#{msgtype} and keyword is null and ismisfortune = #{ismisfortune} <if test='title!=null and title!=null'> and msg like concat('%', #{title}, '%') </if> limit #{page},#{rows}</script>")
	List<Map<String ,Object>> queryNewsFollowReplyList(Map<String,Object> params);
	
	/**
	 * 统计关注图文个数
	 * @return
	 */
	@Select("<script>select count(1) from weixin_reply where isapply=1 and msgtype=#{msgtype} and keyword is null and ismisfortune = #{ismisfortune} <if test='title!=null and title!=null'> and msg like concat('%', #{title}, '%') </if> </script>")
	int countImageFollowReplyList(Map<String,Object> params);
	
	/**
	 * 根据id查询关注图文消息
	 * @param id
	 * @return
	 */
	@Select("select * from weixin_reply where isapply =1 and keyword is null and ismisfortune = 0 and id =#{id} and msgtype='image'")
	Map<String,Object> oneImageFollowReplyList(Long id);
	/**
	 * 根据id删除关注图文消息
	 * @param id
	 * @return
	 */
	@Update("update weixin_reply set isapply =0  where isapply =1  and id = #{id} and ismisfortune = 0 and msgtype='image'")
	void delImageFollowReplyList(Long id);
	/**
	 * 修改关注图文消息
	 * @param reply
	 */
	@Update("update weixin_reply set msg=#{msg} where isapply =#{isapply} and ismisfortune = #{ismisfortune} and id=#{id} and msgtype=#{msgtype} and keyword is null")
	void updateImageFollowReply(Reply reply);
	
	/**
	 * 查询关注图片列表
	 * @param params
	 * @return
	 */
	@Select("<script>select * from weixin_reply where isapply=1 and msgtype=#{msgtype} and keyword is null and ismisfortune = #{ismisfortune} <if test='title!=null and title!=null'> and msg like concat('%', #{title}, '%') </if> limit #{page},#{rows}</script>")
	List<Map<String ,Object>> queryImageFollowReplyList(Map<String,Object> params);
	
	/**
	 * click事件回复消息
	 * @param params
	 * @return
	 */
	@Select("SELECT * FROM weixin_reply WHERE keyword=(SELECT uuid keyword FROM weixin_menu where id =#{id}) AND isapply=1 AND ismisfortune = 0")
	List<Map<String ,Object>> queryClickReplyList(Long id);
}
