package com.hxxdemo.config;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;

public class Globals {
	/**
	 * 感谢关注aiXcoder,您已登录成功。
	 */
	public static String SCAN_SUBSCRIBE = "欢迎关注aiXcoder智能化软件开发！\n" +
			"\n" +
			"aiXcoder是一款基于代码大模型的智能化软件开发系统，旨在为企业和个人开发者提供实时智能开发辅助，提升软件开发效率和代码质量。\n" +
			"\n" +
			"\n" +
			"安装下载：\n" +
			"https://aixcoder.com/#/Download";
	/**
	 * 扫码成功。
	 */
	public static String SCAN_SCAN = "扫码成功。";
	
	/**
	 * loginId 15分钟过期时间
	 */
	public static Integer LOGINID_EXPIRETIME = 15;
	/**
	 * 白名单最小限制
	 */
	public static Integer WHITELISTMIN = 5;
	/**
	 * 白名单最大限制
	 */
	public static Integer WHITELISTMAX = 10;
	/**
	 * 未删除
	 */
	public static Integer ISDEL0= 0;
	/**
	 * 已删除
	 */
	public static Integer ISDEL1= 1;
	
	/**
	 *  0：等待训练 
	 */
	public static Integer MODELTRAINSTATUS0 = 0;
	/**
	 * 1：预处理 
	 */
	public static Integer MODELTRAINSTATUS1 = 1;
	/**
	 * 2：正在训练 
	 */
	public static Integer MODELTRAINSTATUS2 = 2;
	/**
	 * 3：训练完成
	 */
	public static Integer MODELTRAINSTATUS3 = 3;
	/**
	 *  4：训练失败
	 */
	public static Integer MODELTRAINSTATUS4 = 4;

	/**
	 * 邀请升级vip vip期限一年
	 */
	public static String CODEINVITATIONTIME = "interval 1 YEAR";
	/**
	 * 邀请升级vip vip期限一个月
	 */
	public static String CODEINVITATIONTIMEONEMONTH = "interval 1 MONTH";
	/**
	 * 邀请升级vip vip期限三个月
	 */
	public static String CODEINVITATIONTIMETHREEMONTH = "interval 3 MONTH";
	/**
	 * 百度效率云 vip三个月
	 */
	public static String BAIDUXIAOLVYUN = "interval 3 month";
	/**
	 * 邮箱验证码时间
	 */
	public static String CODEEMAILTIME = "interval 2 minute";
	/**
	 * 短信验证码时间 两分钟
	 */
	public static String CODESMSTIME = "interval 2 minute";
	/**
	 * 短信验证码时间 三十秒
	 */
	public static String CODESMSTIMESECOND = "interval 30 second";
	
	/**
	 * 插件token有效期 15天
	 */
	public static String PLUGTOKENTIME = "interval 15 day";
	/**
	 * webToken有效期 12小时
	 */
	public static String WEBTOKENTIME = "interval 12 hour";
	/**
	 * CSDNvip超时时间30天
	 */
	public static String CSDNEXPIRETIME = "interval 30 day";
	
	/**
	 * 激活验证码
	 */
	public static Integer MSGCODETYPE0 = 0;
	/**
	 * 试用验证码
	 */
	public static Integer MSGCODETYPE1 = 1;
	/**
	 * 注册验证码
	 */
	public static Integer MSGCODETYPE2 = 2;
	/**
	 * 找回密码验证码
	 */
	public static Integer MSGCODETYPE3 = 3;
	/**
	 * 短信登录验证码
	 */
	public static Integer MSGCODETYPE4 = 4;
	/**
	 * 邮箱登录验证码
	 */
	public static Integer MSGCODETYPE5 = 5;
	/**
	 * 短信绑定微信验证码
	 */
	public static Integer MSGCODETYPE6 = 6;
	/**
	 * 邮箱绑定微信验证码
	 */
	public static Integer MSGCODETYPE7 = 7;
	/**
	 * 身份验证码
	 */
	public static Integer MSGCODETYPE8 = 8;
	/**
	 * 身份验证码
	 */
	public static Integer MSGCODETYPE9 = 9;
//	/**
//	 * ok
//	 */
//	public static String ERRORCODE0= "0";
//	/**
//	 * 注册密码不能为空
//	 */
//	public static String ERRORCODE1001= "1001";
//	/**
//	 * 密码长度要在8~16以内
//	 */
//	public static String ERRORCODE1002= "1002";
//	/**
//	 * 请再次输入密码
//	 */
//	public static String ERRORCODE1003= "1003";
//	/**
//	 * 两次密码 输入不一致
//	 */
//	public static String ERRORCODE1004= "1004";
//	/**
//	 * 请去掉两头空格
//	 */
//	public static String ERRORCODE1005= "1005";
//	/**
//	 * 邮箱或密码错误
//	 */
//	public static String ERRORCODE1006= "1006";
//	/**
//	 * 密码不能为空
//	 */
//	public static String ERRORCODE1007= "1007";
//	/**
//	 * 用户未登录
//	 */
//	public static String ERRORCODE1008= "1008";
//	/**
//	 * github项目名称不能为空
//	 */
//	public static String ERRORCODE10010= "10010";/**
//	 * github项目名称不能为空
//	 */
//	public static String ERRORMESSAGE10010="github项目名称不能为空";
//	/**
//	 * 您未绑定github账户
//	 */
//	public static String ERRORCODE10011= "10011";
//	/**
//	 * 您未绑定github账户
//	 */
//	public static String ERRORMESSAGE10011="您未绑定github账户";
//	/**
//	 * 模型名称不能为空
//	 */
//	public static String ERRORCODE10020= "10020";
//	/**
//	 * 模型名称不能为空
//	 */
//	public static String ERRORMESSAGE10020="模型名称不能为空";
//	/**
//	 * 模型类型不能为空
//	 */
//	public static String ERRORCODE10021= "10021";
//	/**
//	 * 模型类型不能为空
//	 */
//	public static String ERRORMESSAGE10021="模型类型不能为空";
//	/**
//	 * 项目模型不能为空
//	 */
//	public static String ERRORCODE10022= "10022";
//	/**
//	 * 项目模型不能为空
//	 */
//	public static String ERRORMESSAGE10022="项目模型不能为空";
//	/**
//	 * 请确定您使用的模型
//	 */
//	public static String ERRORCODE10023= "10023";
//	/**
//	 * 请确定您使用的模型
//	 */
//	public static String ERRORMESSAGE10023="请确定您使用的模型";
//	/**
//	 * 模型简介不能为空
//	 */
//	public static String ERRORCODE10024= "10024";
//	/**
//	 * 模型简介不能为空
//	 */
//	public static String ERRORMESSAGE10024="模型简介不能为空";
//	/**
//	 * 模型详情不能为空
//	 */
//	public static String ERRORCODE10025= "10025";
//	/**
//	 * 模型详情不能为空
//	 */
//	public static String ERRORMESSAGE10025="模型详情不能为空";
//	/**
//	 *  
//	 */
//	public static String ERRORCODE10026= "10026";
//	/**
//	 *  
//	 */
//	public static String ERRORMESSAGE10026=" ";
//	/**
//	 * 上传文件不能为空
//	 */
//	public static String ERRORCODE2001 = "2001";
//	/**
//	 * 上传异常
//	 */
//	public static String ERRORCODE2002 = "2002";
//	/**
//	 * 上传类型为"bmp", "jpg", "jpeg", "png", "gif"
//	 */
//	public static String ERRORCODE2003 = "2003";
//	/**
//	 * 图片个数不能超过八个
//	 */
//	public static String ERRORCODE2004 = "2004";
//	/**
//	 * 邮箱不能为空
//	 */
//	public static String ERRORCODE3001 = "3001";
//	/**
//	 * 非法邮箱
//	 */
//	public static String ERRORCODE3002 = "3002";
//	/**
//	 * 邮箱已经被注册过了
//	 */
//	public static String ERRORCODE3003 = "3003";
//	/**
//	 * 邮件已发送,请耐心等待…
//	 */
//	public static String ERRORCODE3004 = "3004";
//	/**
//	 * 发送邮件失败,请联系管理员
//	 */
//	public static String ERRORCODE3005 = "3005";
//	/**
//	 * 用户未登录不能激活
//	 */
//	public static String ERRORCODE4001 = "4001";
//	/**
//	 * 此账号已经激活，不需要激活了
//	 */
//	public static String ERRORCODE4002 = "4002";
//	/**
//	 * 激活邮件已发送至您的邮箱
//	 */
//	public static String ERRORCODE4003 = "4003";
//	/**
//	 * 发送失败
//	 */
//	public static String ERRORCODE4004 = "4004";
//	/**
//	 * 验证码不能为空
//	 */
//	public static String ERRORCODE4005 = "4005";
//	/**
//	 * 验证码失效,请重试
//	 */
//	public static String ERRORCODE4006 = "4006";
//	/**
//	 * 邀请码不能为空
//	 */
//	public static String ERRORCODE4007 = "4007";
//	/**
//	 * 邀请码失效,请重试
//	 */
//	public static String ERRORCODE4008 = "4008";
//	/**
//	 * 姓名不能为空
//	 */
//	public static String ERRORCODE5001="5001";
//	/**
//	 * 用途不能为空
//	 */
//	public static String ERRORCODE5002="5002";
//	/**
//	 * 下载码不能为空
//	 */
//	public static String ERRORCODE6001="6001";
//	/**
//	 * 版本不能为空
//	 */
//	public static String ERRORCODE7001="7001";
//	/**
//	 * 内容不能为空
//	 */
//	public static String ERRORCODE7002="7002";
//	/**
//	 * 公司名称不能为空
//	 */
//	public static String ERRORCODE8001="8001";
//	/**
//	 * 联系方式不能为空
//	 */
//	public static String ERRORCODE8002="8002";
//	/**
//	 * 联系人不能为空
//	 */
//	public static String ERRORCODE8003="8003";
//	/**
//	 * 软件用途不能为空
//	 */
//	public static String ERRORCODE8004="8004";
//	/**
//	 * 手机号不能为空
//	 */
//	public static String ERRORCODE9001 = "9001";
//	/**
//	 * 手机号长度为11位
//	 */
//	public static String ERRORCODE9002 = "9002";
//	/**
//	 * 手机号错误
//	 */
//	public static String ERRORCODE9003 = "9003";
//	/**
//	 * 短信已发送，请耐心等待...
//	 */
//	public static String ERRORCODE9004 = "9004";
//	/**
//	 * 别试了,请好好填写您的信息
//	 */
//	public static String ERRORCODE9994="9994";
//	/**
//	 * 文件飞上月球了
//	 */
//	public static String ERRORCODE9995="9995";
//	/**
//	 * 非法操作
//	 */
//	public static String ERRORCODE9996="9996";
//	/**
//	 * 参数传递错误
//	 */
//	public static String ERRORCODE9997="9997";
//	/**
//	 * 功能维护中
//	 */
//	public static String ERRORCODE9998="9998";
//	/**
//	 * 未找到返回结果
//	 */
//	public static String ERRORCODE9999 = "9999";
//	
//	/**
//	 * ok
//	 */
//	public static String ERRORMESSAGE0="ok";
//	/**
//	 * 注册密码不能为空
//	 */
//	public static String ERRORMESSAGE1001="注册密码不能为空";
//	/**
//	 * 密码长度要在8~16以内
//	 */
//	public static String ERRORMESSAGE1002="密码长度要在8~16以内";
//	/**
//	 * 请再次输入密码
//	 */
//	public static String ERRORMESSAGE1003="请再次输入密码";
//	/**
//	 * 两次密码 输入不一致
//	 */
//	public static String ERRORMESSAGE1004="两次密码 输入不一致";
//	/**
//	 * 请去掉两头空格
//	 */
//	public static String ERRORMESSAGE1005="请去掉两头空格";
//	/**
//	 * 邮箱或密码错误
//	 */
//	public static String ERRORMESSAGE1006="邮箱或密码错误";
//	/**
//	 * 密码不能为空
//	 */
//	public static String ERRORMESSAGE1007="密码不能为空";
//	/**
//	 * 用户未登录
//	 */
//	public static String ERRORMESSAGE1008="用户未登录";
//	/**
//	 * 上传文件不能为空
//	 */
//	public static String ERRORMESSAGE2001 = "文件不能为空";
//	/**
//	 * 上传异常
//	 */
//	public static String ERRORMESSAGE2002 = "上传异常";
//	/**
//	 * 上传类型为"bmp", "jpg", "jpeg", "png", "gif"
//	 */
//	public static String ERRORMESSAGE2003 = "上传类型为bmp, jpg, jpeg, png, gif";
//	/**
//	 * 图片个数不能超过八个
//	 */
//	public static String ERRORMESSAGE2004 = "图片个数不能超过八个";
//	/**
//	 * 邮箱不能为空
//	 */
//	public static String ERRORMESSAGE3001 = "邮箱不能为空";
//	/**
//	 * 非法邮箱
//	 */
//	public static String ERRORMESSAGE3002 = "非法邮箱";
//	/**
//	 * 邮箱已经被注册过了
//	 */
//	public static String ERRORMESSAGE3003 = "邮箱已经被注册过了";
//	/**
//	 * 邮件已发送,请耐心等待…
//	 */
//	public static String ERRORMESSAGE3004 = "邮件已发送,请耐心等待…";
//	/**
//	 * 发送邮件失败,请联系管理员
//	 */
//	public static String ERRORMESSAGE3005 = "发送邮件失败,请联系管理员";
//	/**
//	 * 用户未登录不能激活
//	 */
//	public static String ERRORMESSAGE4001 = "用户未登录不能激活";
//	/**
//	 * 此账号已经激活，不需要激活了
//	 */
//	public static String ERRORMESSAGE4002 = "此账号已经激活，不需要激活了";
//	/**
//	 * 激活邮件已发送至您的邮箱
//	 */
//	public static String ERRORMESSAGE4003 = "激活邮件已发送至您的邮箱";
//	/**
//	 * 发送失败
//	 */
//	public static String ERRORMESSAGE4004 = "发送失败";
//	/**
//	 * 验证码不能为空
//	 */
//	public static String ERRORMESSAGE4005 = "验证码不能为空";
//	/**
//	 * 验证码失效,请重试
//	 */
//	public static String ERRORMESSAGE4006 = "验证码失效,请重试";
//	/**
//	 * 邀请码不能为空
//	 */
//	public static String ERRORMESSAGE4007 = "邀请码不能为空";
//	/**
//	 * 邀请码失效,请重试
//	 */
//	public static String ERRORMESSAGE4008 = "邀请码失效,请重试";
//	/**
//	 * 姓名不能为空
//	 */
//	public static String ERRORMESSAGE5001="姓名不能为空";
//	/**
//	 * 用途不能为空
//	 */
//	public static String ERRORMESSAGE5002="用途不能为空";
//	/**
//	 * 下载码不能为空
//	 */
//	public static String ERRORMESSAGE6001="下载码不能为空";
//	/**
//	 * 版本不能为空
//	 */
//	public static String ERRORMESSAGE7001="版本不能为空";
//	/**
//	 * 内容不能为空
//	 */
//	public static String ERRORMESSAGE7002="内容不能为空";
//	/**
//	 * 公司名称不能为空
//	 */
//	public static String ERRORMESSAGE8001="公司名称不能为空";
//	/**
//	 * 联系方式不能为空
//	 */
//	public static String ERRORMESSAGE8002="联系方式不能为空";
//	/**
//	 * 联系人不能为空
//	 */
//	public static String ERRORMESSAGE8003="联系人不能为空";
//	/**
//	 * 软件用途不能为空
//	 */
//	public static String ERRORMESSAGE8004="软件用途不能为空";
//	/**
//	 * 手机号不能为空
//	 */
//	public static String ERRORMESSAGE9001 = "手机号不能为空";
//	/**
//	 * 手机号长度为11位
//	 */
//	public static String ERRORMESSAGE9002 = "手机号长度为11位";
//	/**
//	 * 手机号错误
//	 */
//	public static String ERRORMESSAGE9003 = "手机号错误";
//	/**
//	 * 短信已发送，请耐心等待...
//	 */
//	public static String ERRORMESSAGE9004 = "短信已发送，请耐心等待...";
//	/**
//	 * 别试了,请好好填写您的信息
//	 */
//	public static String ERRORMESSAGE9994="别试了,请好好填写您的信息";
//	/**
//	 * 文件飞上月球了
//	 */
//	public static String ERRORMESSAGE9995 = "文件飞上月球了";
//	/**
//	 * 非法操作
//	 */
//	public static String ERRORMESSAGE9996 = "非法操作";
//	/**
//	 * 参数传递错误
//	 */
//	public static String ERRORMESSAGE9997 = "参数传递错误";
//	/**
//	 * 功能维护中
//	 */
//	public static String ERRORMESSAGE9998 = "功能维护中";
//	/**
//	 * 未找到返回结果
//	 */
//	public static String ERRORMESSAGE9999 = "未找到返回结果";
	
	/**
	 * ok
	 */
	public static String ERRORCODE0="0";
	/**
	 * ok
	 */
	public static String ERRORMESSAGE0="ok";
	/**
	 * 邮箱不能为空
	 */
	public static String ERRORCODE1001="1001";
	/**
	 * 邮箱不能为空
	 */
	public static String ERRORMESSAGE1001="邮箱不能为空";
	/**
	 * 密码不能为空
	 */
	public static String ERRORCODE1002="1002";
	/**
	 * 密码不能为空
	 */
	public static String ERRORMESSAGE1002="密码不能为空";
	/**
	 * 密码长度要在8~16以内
	 */
	public static String ERRORCODE1003="1003";
	/**
	 * 密码长度要在8~16以内
	 */
	public static String ERRORMESSAGE1003="密码长度要在8~16以内";
	/**
	 * 再次输入密码
	 */
	public static String ERRORCODE1004="1004";
	/**
	 * 再次输入密码
	 */
	public static String ERRORMESSAGE1004="再次输入密码";
	/**
	 * 两次密码 输入不一致
	 */
	public static String ERRORCODE1005="1005";
	/**
	 * 两次密码 输入不一致
	 */
	public static String ERRORMESSAGE1005="两次密码 输入不一致";
	/**
	 * 请去掉密码两头空格
	 */
	public static String ERRORCODE1006="1006";
	/**
	 * 请去掉密码两头空格
	 */
	public static String ERRORMESSAGE1006="请去掉密码两头空格";
	/**
	 * 非法邮箱
	 */
	public static String ERRORCODE1007="1007";
	/**
	 * 非法邮箱
	 */
	public static String ERRORMESSAGE1007="非法邮箱";
	/**
	 * 邮箱已经被注册过了
	 */
	public static String ERRORCODE1008="1008";
	/**
	 * 邮箱已经被注册过了
	 */
	public static String ERRORMESSAGE1008="邮箱已经被注册过了";
	/**
	 * 验证码不能为空
	 */
	public static String ERRORCODE1009="1009";
	/**
	 * 验证码不能为空
	 */
	public static String ERRORMESSAGE1009="验证码不能为空";
	/**
	 * 已发送,请耐心等待…
	 */
	public static String ERRORCODE1010="1010";
	/**
	 * 已发送,请耐心等待…
	 */
	public static String ERRORMESSAGE1010="已发送,请耐心等待…";
	//发送失败,请联系管理员
	/**
	 * 请勿频繁请求，稍后重试... 
	 */
	public static String ERRORCODE1011="1011";
	/**
	 * 请勿频繁请求，稍后重试...
	 */
	public static String ERRORMESSAGE1011="请勿频繁请求，稍后重试...";
	/**
	 * 验证码失效,请重试
	 */
	public static String ERRORCODE1012="1012";
	/**
	 * 验证码失效,请重试
	 */
	public static String ERRORMESSAGE1012="验证码失效,请重试";
	/**
	 * 手机号或邮箱不能为空
	 */
	public static String ERRORCODE1013="1013";
	/**
	 * 手机号或邮箱不能为空
	 */
	public static String ERRORMESSAGE1013="手机号或邮箱不能为空";
	/**
	 * 手机号不能为空
	 */
	public static String ERRORCODE10131="10131";
	/**
	 * 手机号不能为空
	 */
	public static String ERRORMESSAGE10131="手机号不能为空";
	/**
	 * 手机号格式错误或手机号登录暂不支持您所在的国家或地区，请尝试用邮箱登录
	 */
	public static String ERRORCODE10132="10132";
	/**
	 * 手机号格式错误或手机号登录暂不支持您所在的国家或地区，请尝试用邮箱登录
	 */
	public static String ERRORMESSAGE10132="手机号格式错误或手机号登录暂不支持您所在的国家或地区，请尝试用邮箱登录";
	/**
	 * 手机已绑定其它账号
	 */
	public static String ERRORCODE10133="10133";
	/**
	 * 手机已绑定其它账号
	 */
	public static String ERRORMESSAGE10133="手机已绑定其它账号";
	/**
	 * 邮箱已绑定其它账号
	 */
	public static String ERRORCODE10134="10134";
	/**
	 * 邮箱已绑定其它账号
	 */
	public static String ERRORMESSAGE10134="邮箱已绑定其它账号";
	/**
	 * 手机号长度为11位
	 */
	public static String ERRORCODE1014="1014";
	/**
	 * 手机号长度为11位
	 */
	public static String ERRORMESSAGE1014="手机号长度为11位";
	/**
	 * 手机号错误
	 */
	public static String ERRORCODE1015="1015";
	/**
	 * 手机号错误
	 */
	public static String ERRORMESSAGE1015="手机号错误";
	/**
	 * 您的账号已被注册
	 */
	public static String ERRORCODE1016="1016";
	/**
	 * 您的账号已被注册
	 */
	public static String ERRORMESSAGE1016="您的账号已被注册";
	/**
	 * 请输入图文验证码
	 */
	public static String ERRORCODE1017="1017";
	/**
	 * 请输入图文验证码
	 */
	public static String ERRORMESSAGE1017="请输入图文验证码";
	/**
	 * 无效验证码
	 */
	public static String ERRORCODE1018="1018";
	/**
	 * 无效验证码
	 */
	public static String ERRORMESSAGE1018="无效验证码";
	/**
	 * 卸载原因不能为空
	 */
	public static String ERRORCODE1019="1019";
	/**
	 * 卸载原因不能为空
	 */
	public static String ERRORMESSAGE1019="卸载原因不能为空";
	/**
	 * 手机号或邮箱不可用
	 */
	public static String ERRORCODE1020="1020";
	/**
	 * 手机号或邮箱不可用
	 */
	public static String ERRORMESSAGE1020="手机号或邮箱不可用";
	/**
	 * 已绑定了其它微信
	 */
	public static String ERRORCODE1021="1021";
	/**
	 * 已绑定了其它微信
	 */
	public static String ERRORMESSAGE1021="已绑定了其它微信";
	/**
	 * 微信已绑定其它账户
	 */
	public static String ERRORCODE1022="1022";
	/**
	 * 微信已绑定其它账户
	 */
	public static String ERRORMESSAGE1022="微信已绑定其它账户";
	/**
	 * 请登录
	 */
	public static String ERRORCODE2001="2001";
	/**
	 * 请登录
	 */
	public static String ERRORMESSAGE2001="请登录";
	/**
	 * 重复操作
	 */
	public static String ERRORCODE20011="20011";
	/**
	 * 重复操作
	 */
	public static String ERRORMESSAGE20011="重复操作";
	/**
	 * 邮箱或密码错误
	 */
	public static String ERRORCODE2002="2002";
	/**
	 * 邮箱或密码错误
	 */
	public static String ERRORMESSAGE2002="邮箱或密码错误";
	/**
	 * 邀请码错误
	 */
	public static String ERRORCODE2003="2003";
	/**
	 * 邀请码错误
	 */
	public static String ERRORMESSAGE2003="邀请码错误";
	/**
	 * 账户或密码错误
	 */
	public static String ERRORCODE2004="2004";
	/**
	 * 账户或密码错误
	 */
	public static String ERRORMESSAGE2004="账户或密码错误";
	/**
	 * 申请已经提交了
	 */
	public static String ERRORCODE2005="2005";
	/**
	 * 申请已经提交了
	 */
	public static String ERRORMESSAGE2005="申请已经提交了";
	/**
	 * 试用已经过期
	 */
	public static String ERRORCODE2006="2006";
	/**
	 * 试用已经过期
	 */
	public static String ERRORMESSAGE2006="试用已过期。您可以重新申请试用";
	/**
	 * 上传文件不能为空
	 */
	public static String ERRORCODE3001="3001";
	/**
	 * 上传文件不能为空
	 */
	public static String ERRORMESSAGE3001="上传文件不能为空";
	/**
	 * 上传异常
	 */
	public static String ERRORCODE3002="3002";
	/**
	 * 上传异常
	 */
	public static String ERRORMESSAGE3002="上传异常";
	/**
	 * 上传类型为"bmp", "jpg", "jpeg", "png", "gif"
	 */
	public static String ERRORCODE3003="3003";
	/**
	 * 上传类型为"bmp", "jpg", "jpeg", "png", "gif"
	 */
	public static String ERRORMESSAGE3003="上传类型为\"bmp\", \"jpg\", \"jpeg\", \"png\", \"gif\"";
	/**
	 * 图片个数不能超过八个
	 */
	public static String ERRORCODE3004="3004";
	/**
	 * 图片个数不能超过八个
	 */
	public static String ERRORMESSAGE3004="图片个数不能超过八个";
	/**
	 * 非法操作
	 */
	public static String ERRORCODE4001="4001";
	/**
	 * 非法操作
	 */
	public static String ERRORMESSAGE4001="非法操作";
	/**
	 * 功能维护中
	 */
	public static String ERRORCODE4002="4002";
	/**
	 * 功能维护中
	 */
	public static String ERRORMESSAGE4002="功能维护中";
	/**
	 * 未找到返回结果
	 */
	public static String ERRORCODE4003="4003";
	/**
	 * 未找到返回结果
	 */
	public static String ERRORMESSAGE4003="未找到返回结果";
	/**
	 * 您的账号有安全隐患，请点击我忘记了密码并修改密码或使用验证码登录！
	 */
	public static String ERRORCODE4004="4004";
	/**
	 * 您的账号有安全隐患，请点击我忘记了密码并修改密码或使用验证码登录！
	 */
	public static String ERRORMESSAGE4004="您的账号有安全隐患，请点击我忘记了密码并修改密码或使用验证码登录！";
	/**
	 * github项目名称不能为空
	 */
	public static String ERRORCODE5001="5001";
	/**
	 * github项目名称不能为空
	 */
	public static String ERRORMESSAGE5001="github项目名称不能为空";
	/**
	 * 您未绑定github账户
	 */
	public static String ERRORCODE5002="5002";
	/**
	 * 您未绑定github账户
	 */
	public static String ERRORMESSAGE5002="您未绑定github账户";
	/**
	 * 模型名称不能为空
	 */
	public static String ERRORCODE5003="5003";
	/**
	 * 模型名称不能为空
	 */
	public static String ERRORMESSAGE5003="模型名称不能为空";
	/**
	 * 模型类型不能为空
	 */
	public static String ERRORCODE5004="5004";
	/**
	 * 模型类型不能为空
	 */
	public static String ERRORMESSAGE5004="模型类型不能为空";
	/**
	 * 项目模型不能为空
	 */
	public static String ERRORCODE5005="5005";
	/**
	 * 项目模型不能为空
	 */
	public static String ERRORMESSAGE5005="项目模型不能为空";
	/**
	 * 请确定您使用的模型
	 */
	public static String ERRORCODE5006="5006";
	/**
	 * 请确定您使用的模型
	 */
	public static String ERRORMESSAGE5006="请确定您使用的模型";
	/**
	 * 模型简介不能为空
	 */
	public static String ERRORCODE5007="5007";
	/**
	 * 模型简介不能为空
	 */
	public static String ERRORMESSAGE5007="模型简介不能为空";
	/**
	 * 模型详情不能为空
	 */
	public static String ERRORCODE5008="5008";
	/**
	 * 模型详情不能为空
	 */
	public static String ERRORMESSAGE5008="模型详情不能为空";
	/**
	 * 已经投过票了
	 */
	public static String ERRORCODE5009="5009";
	/**
	 * 已经投过票了
	 */
	public static String ERRORMESSAGE5009="已经投过票了";
	/**
	 * 模型正在训练中,不能被删除
	 */
	public static String ERRORCODE5010="5010";
	/**
	 * 模型正在训练中,不能被删除
	 */
	public static String ERRORMESSAGE5010="模型正在训练中,不能被删除";
	
	/**
	 * 用户未登录不能激活
	 */
	public static String ERRORCODE6001="6001";
	/**
	 * 用户未登录不能激活
	 */
	public static String ERRORMESSAGE6001="用户未登录不能激活";
	/**
	 * 此账号已经激活，不需要激活了
	 */
	public static String ERRORCODE6002="6002";
	/**
	 * 此账号已经激活，不需要激活了
	 */
	public static String ERRORMESSAGE6002="此账号已经激活，不需要激活了";
	/**
	 * 激活邮件已发送至您的邮箱
	 */
	public static String ERRORCODE6003="6003";
	/**
	 * 激活邮件已发送至您的邮箱
	 */
	public static String ERRORMESSAGE6003="激活邮件已发送至您的邮箱";
	/**
	 * token令牌不能为空
	 */
	public static String ERRORCODE7001 = "7001";
	/**
	 * token令牌不能为空
	 */
	public static String ERRORMESSAGE7001 = "token令牌不能为空";
	/**
	 * uuid不能为空
	 */
	public static String ERRORCODE7002 = "7002";
	/**
	 * uuid不能为空
	 */
	public static String ERRORMESSAGE7002 = "uuid不能为空";
	/**
	 * 本机唯一id不能为空
	 */
	public static String ERRORCODE7003 = "7003";
	/**
	 * 本机唯一id不能为空
	 */
	public static String ERRORMESSAGE7003 = "本机唯一id不能为空";
	/**
	 * 无效的token令牌
	 */
	public static String ERRORCODE7004 = "7004";
	/**
	 * 无效的token令牌
	 */
	public static String ERRORMESSAGE7004 = "无效的token令牌";
	/**
	 * 无效的uuid
	 */
	public static String ERRORCODE7005 = "7005";
	/**
	 * 无效的uuid
	 */
	public static String ERRORMESSAGE7005 = "无效的uuid";
	/**
	 * 系统名称不能为空
	 */
	public static String ERRORCODE7006 = "7006";
	/**
	 * 系统名称不能为空
	 */
	public static String ERRORMESSAGE7006 = "系统名称不能为空";
	/**
	 * 编译器版本不能为空
	 */
	public static String ERRORCODE7007 = "7007";
	/**
	 * 编译器版本不能为空
	 */
	public static String ERRORMESSAGE7007 = "编译器版本不能为空";
	/**
	 * 渠道编码不能为空
	 */
	public static String ERRORCODE7008 = "7008";
	/**
	 * 渠道编码不能为空
	 */
	public static String ERRORMESSAGE7008 = "渠道编码不能为空";
	/**
	 * code码不能为空
	 */
	public static String ERRORCODE7009 = "7009";
	/**
	 * code码不能为空
	 */
	public static String ERRORMESSAGE7009 = "token令牌不能为空";
	/**
	 * 订单不存在
	 */
	public static String ERRORCODE7010 = "7010";
	/**
	 * 订单不存在
	 */
	public static String ERRORMESSAGE7010 = "订单不存在";
	/**
	 * 用户名或密码不能为空
	 */
	public static String ERRORCODE8001="8001";
	/**
	 * 用户名或密码不能为空
	 */
	public static String ERRORMESSAGE8001="用户名或密码不能为空";
	/**
	 * 用户名或密码错误
	 */
	public static String ERRORCODE8002="8002";
	/**
	 * 用户名或密码错误
	 */
	public static String ERRORMESSAGE8002="用户名或密码错误";
	/**
	 * token令牌不能为空
	 */
	public static String ERRORCODE8003="8003";
	/**
	 * token令牌不能为空
	 */
	public static String ERRORMESSAGE8003="token令牌不能为空";
	/**
	 * 用户名不能为空
	 */
	public static String ERRORCODE8004="8004";
	/**
	 * 用户名不能为空
	 */
	public static String ERRORMESSAGE8004="用户名不能为空";
	/**
	 * 姓名不能为空
	 */
	public static String ERRORCODE9001="9001";
	/**
	 * 姓名不能为空
	 */
	public static String ERRORMESSAGE9001="姓名不能为空";
	/**
	 * 用途不能为空
	 */
	public static String ERRORCODE9002="9002";
	/**
	 * 用途不能为空
	 */
	public static String ERRORMESSAGE9002="用途不能为空";
	/**
	 * 下载码不能为空
	 */
	public static String ERRORCODE9003="9003";
	/**
	 * 下载码不能为空
	 */
	public static String ERRORMESSAGE9003="下载码不能为空";
	/**
	 * 版本不能为空
	 */
	public static String ERRORCODE9004="9004";
	/**
	 * 版本不能为空
	 */
	public static String ERRORMESSAGE9004="版本不能为空";
	/**
	 * 内容不能为空
	 */
	public static String ERRORCODE9005="9005";
	/**
	 * 内容不能为空
	 */
	public static String ERRORMESSAGE9005="内容不能为空";
	/**
	 * 公司名称不能为空
	 */
	public static String ERRORCODE9006="9006";
	/**
	 * 公司名称不能为空
	 */
	public static String ERRORMESSAGE9006="公司名称不能为空";
	/**
	 * 联系方式不能为空
	 */
	public static String ERRORCODE9007="9007";
	/**
	 * 联系方式不能为空
	 */
	public static String ERRORMESSAGE9007="联系方式不能为空";
	/**
	 * 问题描述不能为空
	 */
	public static String ERRORCODE90071="90071";
	/**
	 * 问题描述不能为空
	 */
	public static String ERRORMESSAGE90071="问题描述不能为空";
	/**
	 * 联系人不能为空
	 */
	public static String ERRORCODE9008="9008";
	/**
	 * 联系人不能为空
	 */
	public static String ERRORMESSAGE9008="联系人不能为空";
	/**
	 * 软件用途不能为空
	 */
	public static String ERRORCODE9009="9009";
	/**
	 * 软件用途不能为空
	 */
	public static String ERRORMESSAGE9009="软件用途不能为空";
	/**
	 * 邀请码不能为空
	 */
	public static String ERRORCODE9010="9010";
	/**
	 * 邀请码不能为空
	 */
	public static String ERRORMESSAGE9010="邀请码不能为空";
	/**
	 * 邀请码失效,请重试
	 */
	public static String ERRORCODE9011="9011";
	/**
	 * 邀请码失效,请重试
	 */
	public static String ERRORMESSAGE9011="邀请码失效,请重试";
	/**
	 * 别试了,请好好填写您的信息
	 */
	public static String ERRORCODE9012="9012";
	/**
	 * 别试了,请好好填写您的信息
	 */
	public static String ERRORMESSAGE9012="别试了,请好好填写您的信息";
	/**
	 * 文件飞上月球了
	 */
	public static String ERRORCODE9013="9013";
	/**
	 * 文件飞上月球了
	 */
	public static String ERRORMESSAGE9013="文件飞上月球了";
	/**
	 * 请将信息填写完整
	 */
	public static String ERRORCODE9014 = "9014";
	/**
	 * 请将信息填写完整
	 */
	public static String ERRORMESSAGE9014 = "请将信息填写完整";
	/**
	 * 此活动已经结束
	 */
	public static String ERRORCODE9015 = "9015";
	/**
	 * 此活动已经结束
	 */
	public static String ERRORMESSAGE9015 = "此活动已经结束";
	/**
	 *  请选择您的城市
	 */
	public static String ERRORCODE9016 = "9016";
	/**
	 * 请选择您的城市
	 */
	public static String ERRORMESSAGE9016 = "请选择您的城市";
	/**
	 * 请选择手机号进行注册
	 */
	public static String ERRORCODE9017 = "9017";
	/**
	 * 请选择手机号进行注册
	 */
	public static String ERRORMESSAGE9017 = "请选择手机号进行注册";
	/**
	 * 不支持邮箱注册，请选择手机号获取验证码
	 */
	public static String ERRORCODE9018 = "9018";
	/**
	 * 不支持邮箱注册，请选择手机号获取验证码
	 */
	public static String ERRORMESSAGE9018 = "不支持邮箱注册，请选择手机号获取验证码";
	
	public static void retValueHandler(JSONObject json) {
		Map<String, Object> map = new HashMap<String, Object>(); 
		try {
			Class clazz = Class.forName("com.hxxdemo.config.EnGlobals");
			Field[] fields = clazz.getFields();
			for( Field field : fields ){
				if (("ERRORCODE"+json.getString("errorcode")).equals(field.getName())) {
					json.put("errorcode", field.get(clazz));
				}
				if (("ERRORMESSAGE"+json.getString("errorcode")).equals(field.getName())) {
					json.put("errormessage", field.get(clazz));
				}
				if (map.size()==2) {
					break;
				}
	        }
			if (json.getString("errorcode").equals("500")) {
				json.put("errormessage", "Unknown exception, please contact the administrator");
			}
		} catch (Exception e) {
		}
	}
}
