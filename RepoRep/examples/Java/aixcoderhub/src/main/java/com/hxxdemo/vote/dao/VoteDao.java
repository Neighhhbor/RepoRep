package com.hxxdemo.vote.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface VoteDao {

	/**
	 * 查询模型是否有投票
	 * @param modelId
	 * @return
	 */
	@Select("select count(1) from train_vote where modelid=#{modelId}")
	int countIsVote(Long modelId);
	/**
	 * 添加投票
	 * @param modelMap
	 */
	@Insert("insert into train_vote (userid,votesnum,modelid,type,createtime,edittime) values(#{userid},#{votesnum},#{id},#{type},now(),now())")
	void addVote(Map<String,Object> modelMap);
	/**
	 * 修改投票
	 * @param modelId
	 */
	@Update("update train_vote set votesnum = votesnum+1 where modelid=#{modelId}")
	void editVote(Long modelId);
	/**
	 * 添加投票人
	 * @param modelMap
	 */
	@Insert("insert into train_voters (userid,voterid,modelid,createtime) values (#{userid},#{voterid},#{id},now())")
	void addVoter(Map<String,Object> modelMap);
	
	/**
	 * 是否投票了
	 * @param modelMap
	 * @return
	 */
	@Select("select count(1) from train_voters where modelid=#{id} and voterid=#{voterid}")
	int isVoter(Map<String,Object> modelMap);
}
