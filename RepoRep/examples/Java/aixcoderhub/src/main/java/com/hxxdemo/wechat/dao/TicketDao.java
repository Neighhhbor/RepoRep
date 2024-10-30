package com.hxxdemo.wechat.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TicketDao {

	/**
	 * 获取ticket
	 * @return
	 */
	@Select ("select ticket from weixin_ticket where isapply = 1 and (create_time +interval expires_in second)>NOW()")
	String getTicket();
	
	/**
	 * 设置超时不可用
	 */
	@Update("update weixin_ticket set isapply = 0 where isapply = 1  ")
	void editExpires_in();
	/**
	 * 插入ticket
	 * @param ticket
	 * @param expires_in
	 */
	@Insert ("insert into weixin_ticket (ticket ,expires_in,create_time) values (#{ticket},#{expires_in} ,now())")
	void insertTicket(Map<String, Object> params);
	
}
