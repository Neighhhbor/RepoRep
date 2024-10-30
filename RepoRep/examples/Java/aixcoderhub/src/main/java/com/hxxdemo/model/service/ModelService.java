package com.hxxdemo.model.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ModelService {

	/**
	 * 模型列表
	 * @param params
	 * @return
	 */
	List<Map<String,Object>> ModelList(Map<String,Object> params);
	
	/**
	 * 模型详情
	 * @param id
	 * @return
	 */
	Map<String,Object> modelDetail(Long id);
	
	/**
	 * 插入个人项目
	 * @param list
	 */
	void insertOwnerProject(List<Map<String,Object>> list);
	/**
	 * 插入公开项目
	 * @param list
	 */
	void insertopenProject(List<Map<String,Object>> list);
	/**
	 * 插入模型
	 * @param list
	 */
	void insertModel(Map<String,Object> params);
	
	/**
	 * 通过token 查询模型id
	 * @return
	 */
	Long getModelidByToken(String token);
	/**
	 * 通过token修改项目模型id
	 * @param token
	 */
	void updateProjectModelidByToken(Map<String,Object> params);
	/**
	 * 根据模型id查询模型是否被使用过
	 * @param modelid
	 * @return
	 */
	Long isUsedByModelId(Long modelid);
	
	/**
	 * 插入使用数据
	 * @param params
	 */
	void insertUse(Map<String,Object> params);
	
	/**
	 * 插入使用数据
	 * @param id
	 */
	void updateUse(Long id);
	/**
	 * 插入到使用者与模型被使用者关系库
	 * @param id
	 */
	void insertTrainUsers(Map<String,Object> params);
	
	/**
	 * 验证是否是用户的模型
	 * @return
	 */
	int isUserModelbyUserIdModelId(Map<String,Object> params) ;
	/**
	 * 修改模型信息
	 * @param params
	 */
	void updateModelDetail(Map<String,Object> params) ;
	/**
	 * 是否是正在训练中
	 * @param params
	 * @return
	 */
	int isTraining(Map<String,Object> params);
	
	/**
	 * 删除模型
	 * @param modelid
	 */
	void deleteModelById(long modelid);

}
