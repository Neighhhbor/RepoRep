package com.hxxdemo.task.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TaskDao {

	@Update("update sys_user set viplevel =1  where expire_time < now() and viplevel=2 ")
	void updateSysUserLevel();
}
