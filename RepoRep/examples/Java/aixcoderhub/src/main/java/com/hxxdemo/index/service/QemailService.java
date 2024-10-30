package com.hxxdemo.index.service;

import java.util.List;
import java.util.Map;

import com.hxxdemo.index.entity.Applyaix;
import com.hxxdemo.index.entity.CompanyUser;
import com.hxxdemo.index.entity.DownloadCount;
import com.hxxdemo.index.entity.Feedback;
import com.hxxdemo.index.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface QemailService {
	Boolean sendhub(String title,String code,String email);
	Boolean send(String title,String code,String email);
	Boolean sendWithAttach(String title,String code,String email, MultipartFile[] files);
	int countEmailByName(Map<String,Object> param);
	
	void insertEmail(Map<String,Object> params);
	
	String getEmailIdByMap(Map<String,Object> params);
	
	void updateIsapply(String id);
	
	void updateIsapplyByCode(Map<String,Object> params);
	
	List<User> queryUserByEmail(Map<String,Object> params);
	
	void  inserUser(User user) ;
	
	void insertApplyaix(Applyaix applyaix);
	
	void insertCompanyUser(CompanyUser companyUser);
	
	void insertFeedback(Feedback feedback);
	
	List<Map<String,Object>> getApplyIdByMap(Map<String,Object> params);
	
	void updateApplyIsapply(String id);
	
	List<Map<String,Object>> queryApplyaixById(String applyid);
	
	void insertDownLoadCount(DownloadCount downLoadCount);
	
	String queryDownLoadIdByMd5Code(String code);
	
	void updateDownloadNumByCode(String code);
}
