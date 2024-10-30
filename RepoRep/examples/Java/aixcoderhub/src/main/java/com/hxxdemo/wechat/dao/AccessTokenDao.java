package com.hxxdemo.wechat.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hxxdemo.wechat.util.AccessToken;


@Mapper
public interface AccessTokenDao {
	/**
	 * accesstoken个数 只能有一条accessToken数据
	 * @return
	 */
	@Select("select count(1) from weixin_accesstoken where isapply=1")
	int countAccessToken();
	/**
	 * 插入accessToken
	 * @param accessToken
	 */
	@Insert("insert into weixin_accesstoken (accesstoken,expires_in,createtime,edittime,isapply) values (#{token},#{expiresIn},now(),now(),1)")
	void insertAccessToken(AccessToken accessToken);
	/**
	 * 修改accessToken
	 * @param accessToken
	 */
	@Update("update weixin_accesstoken set accesstoken=#{token},expires_in=#{expiresIn},edittime=now() where id= (select a.id from (select id from wx_accesstoken limit 0,1) a )")
	void updateAccessToken(AccessToken accessToken);
	/**
	 * 删除accessToken
	 * @param accessToken
	 */
	@Delete("update weixin_accesstoken set isapply = 0 where isapply =1")
	void deleteAccessToken();
	/**
	 *查询accessToken
	 * @return
	 */
	@Select("select * from weixin_accesstoken where (edittime+interval expires_in second)>NOW()  and isapply =1")
	List<Map<String,Object>> getAccessToken();
}
