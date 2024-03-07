package com.zhengyuan.liunao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.zhengyuan.liunao.target.TargetBreed;
import com.zhengyuan.liunao.utils.GaoDiRecord;

@Mapper
public interface OtherMapper {

	
	@SuppressWarnings("rawtypes")
	public int addBreed(Map map);
	 
	public List<String> getBreeds();
	
	@SuppressWarnings("rawtypes")
	public int addoddsshpe(Map map);

	@SuppressWarnings("rawtypes")
	public int addk5sum(Map map);
	
	public int getSizeByTradedat(Map map);

	public int updateShpePrices(Map map);
	
	public int addploygaodi0(Map map);
	
	public List<GaoDiRecord> getGaoDis();
	
	public List<GaoDiRecord> getTargetPolyGaoDis();
	
	List<String> gettradetimesbyk5vix();
	
	public int updatek5vix(Map map);

	public List<TargetBreed> getBreedsByKind1(Map map);
	
	public List<TargetBreed> getBreedsByKind2(Map map);
	
	public TargetBreed getbreedByBreed(Map map);

	public List<String> getAllBreeds();
	
	public List<String> getacontrats(Map map);
	
	public int updateBreedsInitialPrice(Map map);
	
	public List<TargetBreed> getAllTargetBreeds();
	
	public int updateBreedsIndexs(Map map);

	public int updateBreedsindexskind1(Map map);
		 
}
