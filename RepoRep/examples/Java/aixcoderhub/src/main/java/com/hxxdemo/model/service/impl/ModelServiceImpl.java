package com.hxxdemo.model.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hxxdemo.model.dao.ModelDao;
import com.hxxdemo.model.service.ModelService;
@Service("ModelService")
public class ModelServiceImpl  implements ModelService{

	@Autowired ModelDao modelDao;
	
	/**
	 * 模型列表
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> ModelList(Map<String,Object> params){
		return modelDao.ModelList(params);
	}
	/**
	 * 模型详情
	 * @param id
	 * @return
	 */
	public Map<String,Object> modelDetail(Long id){
		return modelDao.modelDetail(id);
	}
	
	/**
	 * 插入个人项目
	 * @param list
	 */
	public void insertOwnerProject(List<Map<String,Object>> list) {
		modelDao.insertOwnerProject(list);
	}
	/**
	 * 插入公开项目
	 * @param list
	 */
	public void insertopenProject(List<Map<String,Object>> list) {
		modelDao.insertopenProject(list);
	}
	/**
	 * 插入模型
	 * @param list
	 */
	public void insertModel(Map<String,Object> params) {
		modelDao.insertModel(params);
	}
	/**
	 * 通过token 查询模型id
	 * @return
	 */
	public Long getModelidByToken(String token) {
		return modelDao.getModelidByToken(token);
	}
	/**
	 * 通过token修改项目模型id
	 * @param token
	 */
	public void updateProjectModelidByToken(Map<String,Object> params) {
		modelDao.updateProjectModelidByToken(params);
	}
	/**
	 * 根据模型id查询模型是否被使用过
	 * @param modelid
	 * @return
	 */
	public Long isUsedByModelId(Long modelid) {
		return modelDao.isUsedByModelId(modelid);
	}
	/**
	 * 插入使用数据
	 * @param params
	 */
	public void insertUse(Map<String,Object> params) {
		modelDao.insertUse(params);
	}
	/**
	 * 插入使用数据
	 * @param id
	 */
	public void updateUse(Long id) {
		modelDao.updateUse(id);
	}
	/**
	 * 插入到使用者与模型被使用者关系库
	 * @param id
	 */
	public void insertTrainUsers(Map<String,Object> params) {
		modelDao.insertTrainUsers(params);
	}
	/**
	 * 验证是否是用户的模型
	 * @return
	 */
	public int isUserModelbyUserIdModelId(Map<String,Object> params) {
		return modelDao.isUserModelbyUserIdModelId(params);
	}
	/**
	 * 修改模型信息
	 * @param params
	 */
	public void updateModelDetail(Map<String,Object> params) {
		modelDao.updateModelDetail(params);
	}
	/**
	 * 是否是正在训练中
	 * @param params
	 * @return
	 */
	public int isTraining(Map<String,Object> params) {
		return modelDao.isTraining(params);
	}
	/**
	 * 删除模型
	 * @param modelid
	 */
	public void deleteModelById(long modelid) {
		modelDao.deleteModelById(modelid);
	}
}
