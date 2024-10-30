package com.hxxdemo.wechat.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OrderDao {

	@Select("select count(1) from weixin_prepayment where isapply = 0 and userid = #{userid} and priceid=#{priceid}")
	int countProductId(Map<String, Object> params);
	
	@Insert("insert into weixin_prepayment (userid,productid,create_time,update_time,priceid) values (#{userid},#{productid},now(),now(),#{priceid})")
	void createProductid(Map<String, Object> params);
	
	@Select("select productid from weixin_prepayment where isapply = 0 and userid=#{userid} and priceid=#{priceid} ")
	String getProductid(Map<String, Object> params);
	
	@Insert("insert into weixin_order (openid,productid,total_fee,transaction_id,create_time,result,expire_time,userid) values (#{openid},#{out_trade_no},"
			+ "#{total_fee},#{transaction_id},now(),#{result},NOW() + INTERVAL + (SELECT deadline  FROM weixin_price where id = "
			+ "(SELECT priceid FROM weixin_prepayment where productid = #{out_trade_no} and priceid = #{total_fee} and isapply = 1)) MONTH ,(select userid from weixin_prepayment where productid=#{out_trade_no}) )")
	void saveOrder(Map<String, String> params);

	@Update("update weixin_prepayment set isapply = 1 ,update_time = now()  where productid = #{productid}")
	void updateProductidStatus(String productid);
	
	@Select("select count(1) from weixin_price where id = #{priceid} and isapply = 1  ")
	int isPrice(Long priceid);
	
	@Select("select price from weixin_price where id = #{priceid}")
	int getPriceByPriceid(Long priceid);
	
	@Update("UPDATE sys_user SET viplevel = 2, expire_time = NOW() + INTERVAL + (SELECT deadline  FROM weixin_price where id = "
			+ "(SELECT priceid FROM weixin_prepayment where productid = #{out_trade_no} and priceid = #{total_fee} and isapply = 1)) MONTH "
			+ "WHERE id = (SELECT userid FROM weixin_prepayment where productid = #{out_trade_no} and priceid = #{total_fee} and isapply = 1)")
	void updateUserExpire_time(Map<String, String> params);
	
	@Select("select * from weixin_price where isapply = 1 order by `order`")
	List<Map<String, Object>> getPrice();
	
	@Select("select title from weixin_price where id = #{priceid}")
	String getOrderSubject(Long priceid);
	
	@Select("select count(1) from weixin_order where productid=#{productid}")
	int countOrder(String productid);
	
	@Select("SELECT  a.productid ,DATE_FORMAT(a.create_time,'%Y-%m-%d %T') create_time,c.title, '微信支付'  payment  "
			+ "from weixin_order a LEFT JOIN weixin_prepayment b on a.productid = b.productid  "
			+ "LEFT JOIN weixin_price c on b.priceid = c.id where a.userid = #{userid}  "
			+ "ORDER BY a.create_time desc LIMIT #{page} ,#{rows}")
	List<Map<String, Object>> getOrderList(Map<String, Object> params);
	
	@Select("SELECT count(1)  from weixin_order where userid = #{userid} ")
	int countOrderList(Map<String, Object> params);
}
