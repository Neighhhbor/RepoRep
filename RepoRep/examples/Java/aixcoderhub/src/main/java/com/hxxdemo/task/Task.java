package com.hxxdemo.task;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hxxdemo.task.dao.TaskDao;


@Component
public class  Task {
	
	private static Logger logger = LoggerFactory.getLogger(Task.class);
	@Autowired
	private TaskDao dao;
	@Scheduled(cron="0 15 2 ? * *")//每天两点十五触发
    private void processtest(){
		logger.info("========检查更新viplevel！start========");
		dao.updateSysUserLevel();
		logger.info("========检查更新viplevel！end========");
	}
	
    
}
