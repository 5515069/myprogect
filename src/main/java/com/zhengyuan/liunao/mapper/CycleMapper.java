package com.zhengyuan.liunao.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CycleMapper {

	public int addcycle(Map map);
}
