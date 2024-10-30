package com.hxxdemo.plug.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BuyerOrderDao {

	@Insert("insert into buyer_order (userid,orderno,create_time) values (#{userId},#{orderNo},now())")
	void saveBuyOrder(Map<String,Object> params);
	
	@Select("<script>select username ,email,`subject` ,front_url,out_trade_no ,payplan,paytype,price,DATE_FORMAT(pay_time,'%Y-%m-%d %T') pay_time,money,num,DATE_FORMAT(create_time,'%Y-%m-%d %T') create_time from professional_order where out_trade_no in ("
			+ "<foreach collection='list' item='map' separator=','>"
			+ "#{map.orderno}"
			+ "</foreach>"
			+ ") order by create_time desc </script>")
	List<Map<String, Object>> orderList(Map<String, Object> params);
	
	@Select("select count(1) from buyer_order where userid = #{userId}")
	int userOrderCount(Map<String, Object> params);
	
	@Select("select * from buyer_order where userid = #{userId} limit #{p} , #{r}")
	List<Map<String, Object>> userOrderList(Map<String, Object> params);
	
	@Select("select `value` from dictionary where `key` = 'payType'")
	String getKeyValue();
	
	@Select("select count(1) from buyer_order where userid = #{userId} and orderno = #{orderNo}")
	int checkOrder(Map<String, Object> params);
}
