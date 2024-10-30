package com.hxxdemo.report.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.report.dao.ReportDao;
import com.hxxdemo.report.service.ReportService;

@Service("ReportService")
public class ReportServiceImpl  implements ReportService{
	
	@Autowired
	private ReportDao dao ;
	
	public void insertReportCity(Map<String, Object> params) {
		int count = dao.countToday(params.get("ip").toString());
		if(count == 0) {
			dao.insertReportCity(params);
		}else {
			dao.insertReportCityOther(params);
		}
	}
}
