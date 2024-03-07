package com.zhengyuan.liunao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.zhengyuan.liunao.entity.EntIndexs;
import com.zhengyuan.liunao.target.TargetIndexs;

@Mapper
public interface IndexsDealMapper {

	@SuppressWarnings("rawtypes")
	public int addIndexs(Map map);
	
	public List<EntIndexs> getIndex();
	public List<TargetIndexs> getIndexs(Map map);
	 
}
