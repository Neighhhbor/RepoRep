package com.hxxdemo.modeltrail.controller;

import com.google.common.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hxxdemo.config.Globals;
import com.hxxdemo.exception.utils.R;
import com.hxxdemo.modeltrail.service.ModelTrailService;
import com.hxxdemo.plug.service.PlugService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@RequestMapping("trial")
@Controller
public class ModelTrailController {

	@Autowired
	ModelTrailService service;
	@Autowired
	private PlugService plugService;

	private static final Logger log = LoggerFactory.getLogger(ModelTrailController.class);

	private static final ReentrantLock lock = new ReentrantLock();
	@Autowired
	private Cache<String, Object> guavaCache;
	/**
	 * 申请试用
	 * @return
	 */
	@RequestMapping("applyTrial")
	@ResponseBody
	public R applyTrail(@RequestParam String token,@RequestParam(defaultValue = "zh") String retLanguage) {
		//检查用户
		Long userId = service.checkUserLogin(token);
		if (null == userId) {
			return R.error(Globals.ERRORCODE2001,Globals.ERRORMESSAGE2001);
		}
		try {
			lock.lock();
			long currentTimeMillis = System.currentTimeMillis();
			Object cacheTime = guavaCache.getIfPresent(token);
			if (cacheTime == null) {
				guavaCache.put(token, currentTimeMillis);
			} else {
				if (currentTimeMillis - (Long) cacheTime < 2000) {
					log.info("防止表单重复提交，token:{}", token);
					guavaCache.put(token, currentTimeMillis);
					return R.ok();
				}
			}
		} finally {
			lock.unlock();
		}
		//检查申请状态
		Integer userStatus = service.getWebUserStatus(token);
		//状态 1正在申请 2 申请通过  0 未申请
		if (null == userStatus) {
			if (!retLanguage.equals("zh")) {
				retLanguage = "en";
			}
			//添加到数据库 通过申请
			service.saveTrail(userId,retLanguage);
			return R.ok();
		}else {
			if (userStatus == 2) {
				//验证过期时间
				Integer isExpire = service.getWebUserExpireStatus(userId);
				if (isExpire>0) {
					return R.error(Globals.ERRORCODE2005,Globals.ERRORMESSAGE2005);
				}else {
					//编辑通过申请
					service.editTrial(userId,retLanguage);
					return R.ok();
				}
			}else {
//				return R.error(Globals.ERRORCODE2005,Globals.ERRORMESSAGE2005);
				//编辑通过申请
				service.editTrial(userId,retLanguage);
				return R.ok();
			}
		}
	}
	/**
	 * 检查申请
	 * @return
	 */
	@RequestMapping("checkTrial")
	@ResponseBody
	public R checkTrail(@RequestParam String token){
		Long userId = service.checkPlugUserLogin(token);
		if (null == userId) {
			return R.error(Globals.ERRORCODE2001,Globals.ERRORMESSAGE2001);
		}
		//查询状态
		Integer userStatus = service.getUserStatus(token);
		if (null == userStatus) {
			return R.ok().put("status", 0);
		}else {
			if (userStatus == 2) {
				//验证过期时间
				Integer isExpire = service.getWebUserExpireStatus(userId);
				if (isExpire == 0 ) {
					userStatus = 3;
					return R.error(Globals.ERRORCODE2006,Globals.ERRORMESSAGE2006).put("status", userStatus);
				}
			}
			return R.ok().put("status", userStatus);
		}
	}
	/**
	 * 检查申请
	 * @return
	 */
	@RequestMapping("webCheckTrial")
	@ResponseBody
	public R webCheckTrail(@RequestParam String token,String loginId){
		boolean bool = false;
		Long userId = service.checkUserLogin(token);
		if (null == userId) {
			return R.ok().put("status", -1);
		}
		if (null!=loginId && !loginId.equals("")) {
			//检查是否登录过
			int isLogin = plugService.getPlugLoginId(loginId);
			if (isLogin == 0 ) {
				String username = service.getUserName(userId);
				//添加loginId保存15分钟
				plugService.savePlugLoginId(loginId,username,userId,Globals.LOGINID_EXPIRETIME);
			}
		}
		//查询状态
		Integer userStatus = service.getWebUserStatus(token);
		String email = service.getUserEmail(token);
		if (null != email &&  !email.equals("")) {
			bool = true;
		}
		if (null == userStatus) {
			return R.ok().put("status", 0).put("email", bool);
		}else {
			if (userStatus == 2) {
				//验证过期时间
				Integer isExpire = service.getWebUserExpireStatus(userId);
				if (isExpire == 0 ) {
					userStatus = 3;
				}
			}
			return R.ok().put("status", userStatus).put("email", bool);
		}
	}
	
}
