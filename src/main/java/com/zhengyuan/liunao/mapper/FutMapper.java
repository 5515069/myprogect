package com.zhengyuan.liunao.mapper;
 
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.zhengyuan.liunao.entity.EntDealRecord; 
import com.zhengyuan.liunao.target.TargetDealRecord;
import com.zhengyuan.liunao.utils.K5Record;

@Mapper
public interface FutMapper {

	@SuppressWarnings("rawtypes")
	public int addFut(Map map);
	
	public int addFutForeach(List<K5Record> list);
	
	public Integer getk1MaxId(Map map);
	
	@SuppressWarnings("rawtypes")
	public int getSize(Map map);
	
	@SuppressWarnings("rawtypes")
	public int getSizeByTradedat(Map map);
	
	@SuppressWarnings("rawtypes")
	List<TargetDealRecord>  getTarDealRecords(Map map);
	
	@SuppressWarnings("rawtypes")
	public List<TargetDealRecord>  getTartDealRecordsByContract(Map map);
	
	int deleteK5(Map map);
	
	List<String>  getTradedate();
	
	TargetDealRecord getK5ById(Map map); 
	
	@SuppressWarnings("rawtypes")
	List<K5Record> getTarK5RecordByTradedate(Map map);
	
	List<K5Record> getTarK5RecordByTradedateandContract(Map map);
	
	List<K5Record> getAllD1(Map map);
	
	List<K5Record> getAllD1ByTradedate(Map map);
	
	@SuppressWarnings("rawtypes")
	List<K5Record> getD1ByContract(Map map);
	
	List<K5Record> getK5RecordsInitialPrice(Map map);
	
	List<K5Record> getK5Records(Map map);
	
	int getK5Recordstotal(Map map);
	
	int deletek1allbreedsdate(Map map);
	
	int createtable(Map map);
	
	int deletetable(Map map);
	
	int tickdateAdd(Map map);
	
	int getkdatesize(Map map);
	
	int addfutforeach(List<EntDealRecord> listtest);
	
	
}
