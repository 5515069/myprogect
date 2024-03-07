package com.zhengyuan.liunao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.zhengyuan.liunao.target.TargetShape1;

@Mapper
public interface ShapeMapper {

	@SuppressWarnings("rawtypes")
	public int addshape1(Map map);
	
	public List<TargetShape1> getTargetshape1(); 
	
	public List<TargetShape1>  getTargetshape1orderbyid();
}
