package com.zhengyuan.liunao.controller.future;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSON;
import com.zhengyuan.liunao.mapper.CycleMapper;
import com.zhengyuan.liunao.mapper.FutMapper;
import com.zhengyuan.liunao.mapper.IndexsDealMapper;
import com.zhengyuan.liunao.mapper.OtherMapper;
import com.zhengyuan.liunao.target.TargetBreed;
import com.zhengyuan.liunao.target.TargetIndexs;
import com.zhengyuan.liunao.utils.K5Record;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 * 布林频繁止赢策略
 * 
 * @author 86180
 *
 */
@Controller
@RequestMapping("/Sys")
@Api("FutrueDateDeal相关api")
@Slf4j
public class klineoddsanddataController {
	static Random random = new Random();
	@Autowired
	IndexsDealMapper mapper;
	@Autowired
	CycleMapper cycleMapper;
	@Autowired
	IndexsDealMapper indexMapper;
	@Autowired
	OtherMapper otherMapper;
	@Autowired
	FutMapper futMapper;

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public static void createindexs(FutMapper futMapper, IndexsDealMapper indexMapper, OtherMapper otherMapper,
			String type) {

		List<K5Record> k5Records = new ArrayList<K5Record>();
		List<K5Record> k5Recordspart = null;
		if (type.equals("-1")) {
			type = "-1";
		} else if (type.equals("0")) {
			Map map = new HashMap();
			map.put("breed", "d1");
			k5Records = futMapper.getAllD1ByTradedate(map);
		} else if (type.matches("\\d+")) {
			if (Integer.valueOf(type) > 0 && Integer.valueOf(type) < 10) {
				Map map = new HashMap();
				map.put("kind1", type);
				List<TargetBreed> breeds = otherMapper.getBreedsByKind1(map);
				for (int i = 0; i < breeds.size(); i++) {
					TargetBreed targetbreed = breeds.get(i);
					String breed=targetbreed.getBreed();
					Map contractMap = new HashMap<>();
					contractMap.put("breed", "d1");
					contractMap.put("contractstart", breed + "20010101");
					contractMap.put("contractend", breed + "20300101");
					k5Recordspart = futMapper.getD1ByContract(contractMap);
					k5Records.addAll(k5Recordspart);
				}
			} else if (Integer.valueOf(type) >= 10 && Integer.valueOf(type) <= 1000000) {
				Map map = new HashMap();
				map.put("kind2", type);
				List<TargetBreed> breeds = otherMapper.getBreedsByKind2(map);
				for (int i = 0; i < breeds.size(); i++) {
					TargetBreed targetbreed = breeds.get(i);
					String breed=targetbreed.getBreed();
					Map contractMap = new HashMap<>();
					contractMap.put("breed", "d1");
					contractMap.put("contractstart", breed + "20010101");
					contractMap.put("contractend", breed + "20300101");
					k5Recordspart = futMapper.getD1ByContract(contractMap);
					k5Records.addAll(k5Recordspart);
				}
			}
		} else {
			Map contractMap = new HashMap<>();
			contractMap.put("breed", "d1");
			contractMap.put("contractstart", type + "20010101");
			contractMap.put("contractend", type + "20300101");
			k5Records = futMapper.getD1ByContract(contractMap);
		}
		// 定义一个比较器来指定根据年龄字段进行排序
		Comparator<K5Record> ageComparator = (p1, p2) -> Integer.compare(Integer.valueOf(p1.getTradedate()),
				Integer.valueOf(p2.getTradedate()));
		// 调用 Collections.sort() 方法并传入 ArrayList 和比较器作为参数，完成排序
		Collections.sort(k5Records, ageComparator);
		// 每天指数
		HashMap<String, Double> dayindexs = new HashMap<String, Double>();
		// 合约每天收盘价
		HashMap<String, Double> contractdayclose = new HashMap<String, Double>();
		String lasttradedate = null;
		double risefallsum = 0;
		double totaltimes = 0;
		String frontlasttradedate = null;
		double opensum = 0;
		double closesum = 0;
		double highsum = 0;
		double lowsum = 0;
		double openinterestsum = 0;
		for (int i = 0; i < k5Records.size(); i++) {
			K5Record record = k5Records.get(i);
			String contract = record.getContract();
			String tradedate = record.getTradedate();
			double close = record.getClose();
			if (!tradedate.equals(lasttradedate) || i == k5Records.size() - 1) {
				double risefall = 0;
				if (totaltimes != 0) {
					risefall = risefallsum / totaltimes;
				}

				if (i == 0) {
					dayindexs.put(tradedate, 10000.0);
					frontlasttradedate = tradedate;
				} else if (i == k5Records.size() - 1) {
					dayindexs.put(tradedate,
							dayindexs.get(frontlasttradedate) + dayindexs.get(frontlasttradedate) * risefall);
				} else {
					dayindexs.put(lasttradedate,
							dayindexs.get(frontlasttradedate) + dayindexs.get(frontlasttradedate) * risefall);
					frontlasttradedate = lasttradedate;
				}

				if (i != 0) {
					Map indexsmap = new HashMap();
					indexsmap.put("table", "indexs");
					indexsmap.put("tradedate", lasttradedate);
					double indexs = 0;
					if (i == k5Records.size() - 1) {
						indexsmap.put("close", dayindexs.get(tradedate));
						indexs = dayindexs.get(tradedate);
					} else {
						indexsmap.put("close", dayindexs.get(frontlasttradedate));
						indexs = dayindexs.get(frontlasttradedate);
					}
					int n = 1;
					if (totaltimes == 0) {
						n = 0;
						closesum = 1;
						totaltimes = 1;
					}
					indexsmap.put("open", indexs + (opensum - closesum) / closesum * indexs * n);
					indexsmap.put("high", indexs + (highsum - closesum) / closesum * indexs * n);
					indexsmap.put("low", indexs + (lowsum - closesum) / closesum * indexs * n);
					indexsmap.put("risefall", risefall * 10000);
					indexsmap.put("type", type);
					indexsmap.put("openinterest", openinterestsum / totaltimes);
					indexMapper.addIndexs(indexsmap);
				}
				opensum = 0;
				closesum = 0;
				highsum = 0;
				lowsum = 0;
				openinterestsum = 0;
				totaltimes = 0;
				risefallsum = 0;
			}
			if (!contractdayclose.containsKey(contract)) {
				contractdayclose.put(contract, close);// 文件第一天收盘价
			} else if (record.getOpeninterest() > 2000) {
				double contractyestdayclose = contractdayclose.get(contract);
				if ((close - contractyestdayclose) / contractyestdayclose > -0.2
						&& (close - contractyestdayclose) / contractyestdayclose < 0.2) {
					risefallsum = risefallsum + (close - contractyestdayclose) / contractyestdayclose;
					opensum = opensum + record.getOpen();
					closesum = closesum + close;
					highsum = highsum + record.getHigh();
					lowsum = lowsum + record.getLow();
					openinterestsum = openinterestsum + record.getOpeninterest();
					totaltimes++;
				}
			}

			contractdayclose.put(contract, close);
			lasttradedate = tradedate;
		}

	}

	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	public static void createpriceindexs(FutMapper futMapper, IndexsDealMapper indexMapper, OtherMapper otherMapper,
			String type) {

		List<K5Record> k5Records = new ArrayList<K5Record>();
		List<K5Record> k5Recordspart = null;
		HashMap<String, Double> breedsInitialPriceMap=new HashMap<String, Double>();
		if (type.equals("-1")) {
			type = "-1";
		} else if (type.equals("0")) {
			List<TargetBreed> breeds=otherMapper.getAllTargetBreeds();
			for (int i = 0; i < breeds.size(); i++) {
				breedsInitialPriceMap.put(breeds.get(i).getBreed(), breeds.get(i).getInitialprice());
			}
			Map map = new HashMap();
			map.put("breed", "d1");
			k5Records = futMapper.getAllD1ByTradedate(map);
		} else if (type.matches("\\d+")) {
			if (Integer.valueOf(type) > 0 && Integer.valueOf(type) < 10) {
				Map map = new HashMap();
				map.put("kind1", type);
				List<TargetBreed> breeds = otherMapper.getBreedsByKind1(map); 
				for (int i = 0; i < breeds.size(); i++) {
					breedsInitialPriceMap.put(breeds.get(i).getBreed(), breeds.get(i).getInitialprice());
				}
				for (int i = 0; i < breeds.size(); i++) {
					TargetBreed targetbreed = breeds.get(i);
					String breed=targetbreed.getBreed();
					Map contractMap = new HashMap<>();
					contractMap.put("breed", "d1");
					contractMap.put("contractstart", breed + "20010101");
					contractMap.put("contractend", breed + "20300101");
					k5Recordspart = futMapper.getD1ByContract(contractMap);
					k5Records.addAll(k5Recordspart);
				}
			} else if (Integer.valueOf(type) >= 10 && Integer.valueOf(type) <= 1000000) {
				Map map = new HashMap();
				map.put("kind2", type);
				List<TargetBreed> breeds = otherMapper.getBreedsByKind2(map);
				for (int i = 0; i < breeds.size(); i++) {
					breedsInitialPriceMap.put(breeds.get(i).getBreed(), breeds.get(i).getInitialprice());
				}
				for (int i = 0; i < breeds.size(); i++) {
					TargetBreed targetbreed = breeds.get(i);
					String breed=targetbreed.getBreed();
					Map contractMap = new HashMap<>();
					contractMap.put("breed", "d1");
					contractMap.put("contractstart", breed + "20010101");
					contractMap.put("contractend", breed + "20300101");
					k5Recordspart = futMapper.getD1ByContract(contractMap);
					k5Records.addAll(k5Recordspart);
				}
			}
		} else {
			Map map = new HashMap();
			map.put("breed", type);
			TargetBreed breed = otherMapper.getbreedByBreed(map);
			breedsInitialPriceMap.put(type, breed.getInitialprice());
			Map contractMap = new HashMap<>();
			contractMap.put("breed", "d1");
			contractMap.put("contractstart", type + "20010101");
			contractMap.put("contractend", type + "20300101");
			k5Records = futMapper.getD1ByContract(contractMap);
		}
		// 定义一个比较器来指定根据年龄字段进行排序
		Comparator<K5Record> ageComparator = (p1, p2) -> Integer.compare(Integer.valueOf(p1.getTradedate()),
				Integer.valueOf(p2.getTradedate()));
		// 调用 Collections.sort() 方法并传入 ArrayList 和比较器作为参数，完成排序
		Collections.sort(k5Records, ageComparator);
		// 每天指数
		String lasttradedate = null;
		String frontlasttradedate = null;
		double currentpricesum = 0;
		double totaltimes = 0;
		// 初始指数
		double lastindexs = 10000.0;
		HashMap<String, Double> initalbreedlevelMap = new HashMap<String, Double>();
		// 品种d1每天收盘价和
		HashMap<String, Double> breedDayCloseSumMap = new HashMap<String, Double>();
		// 品种d1每天数量
		HashMap<String, Double> breedDayTimesMap = new HashMap<String, Double>();
		// 每天d1数量
		double daytimes = 0;
		// 目前已上市的品种
		HashMap<String, String> breedsExistsMap = new HashMap<String, String>();
		double opensum = 0;
		double closesum = 0;
		double highsum = 0;
		double lowsum = 0;
		double openinterestsum = 0;
		for (int i = 0; i < k5Records.size(); i++) {
			K5Record record = k5Records.get(i);
			String contract = record.getContract();
			double close = record.getClose();
			String tradedate = record.getTradedate();
			String breed = contract.replaceAll("\\d", "");
			breedsExistsMap.put(breed, breed);
			int daytimeslowlimit = (breedsExistsMap.size() / 5 + 1) * 4;
			if (breedsExistsMap.size() == 1) {
				daytimeslowlimit = 0;
			}
			double breedlevel=0.0;
			if (!initalbreedlevelMap.containsKey(breed)) {
				double openPrice=breedsInitialPriceMap.get(breed);;
				initalbreedlevelMap.put(breed, lastindexs/openPrice);
				breedlevel=lastindexs/openPrice;
				Map map = new HashMap();
				map.put("breed", breed);
				if (type.matches("\\d+")) {
					if (Integer.valueOf(type)==0) {
						map.put("indexs", lastindexs);
						otherMapper.updateBreedsIndexs(map);
					} else if (Integer.valueOf(type) > 0 && Integer.valueOf(type) < 10) {
						map.put("indexskind1", lastindexs);
						otherMapper.updateBreedsindexskind1(map);
					}
				} 
				
			}else {
				breedlevel=initalbreedlevelMap.get(breed);
			} 
			if (contract.contains("fb")) {
				continue;
			}
			if (record.getOpeninterest() < 2000) {
				continue;
			}
			
			if (!tradedate.equals(lasttradedate) || i == k5Records.size() - 1 && i != 0) {
				Map map = new HashMap();
				map.put("tradedate", lasttradedate);
				List<String> contrats = otherMapper.getacontrats(map);
				for (int j = 0; j < contrats.size(); j++) {
					String contractbreed = contrats.get(j).replaceAll("\\d", "");
					if (breedDayCloseSumMap.containsKey(contractbreed + lasttradedate)) {
						double breedpricesum = breedDayCloseSumMap.get(contractbreed + lasttradedate);
						double breedtimes = breedDayTimesMap.get(contractbreed + lasttradedate);
						if (breedpricesum > 0) {
							currentpricesum = currentpricesum + breedpricesum / breedtimes;
							totaltimes++;
						}
						breedDayCloseSumMap.put(contractbreed + lasttradedate, 0.0);
						breedDayTimesMap.put(contractbreed + lasttradedate, 0.0);
					}
				}
				if (daytimes > daytimeslowlimit) {
					if (totaltimes != 0) {
						lastindexs = currentpricesum / totaltimes;
						Map indexsmap = new HashMap();
						indexsmap.put("table", "indexs_price");
						indexsmap.put("tradedate", lasttradedate);
						if (lasttradedate == null) {
							indexsmap.put("tradedate", tradedate);
						}
						indexsmap.put("close", lastindexs);
						indexsmap.put("open", lastindexs + (opensum - closesum) / closesum * lastindexs);
						indexsmap.put("high", lastindexs + (highsum - closesum) / closesum * lastindexs);
						indexsmap.put("low", lastindexs + (lowsum - closesum) / closesum * lastindexs);
						indexsmap.put("openinterest", openinterestsum / totaltimes);
						indexsmap.put("type", type);
						indexMapper.addIndexs(indexsmap);
						opensum = 0;
						closesum = 0;
						highsum = 0;
						lowsum = 0;
						openinterestsum = 0;
					}

				}
				currentpricesum = 0;
				totaltimes = 0;
				frontlasttradedate = lasttradedate;
				daytimes = 0;

			}

			if (breedDayCloseSumMap.containsKey(breed + tradedate)) {
				double breedDayCloseSum = breedDayCloseSumMap.get(breed + tradedate);
				double breedDayTimes = breedDayTimesMap.get(breed + tradedate);
				breedDayCloseSumMap.put(breed + tradedate, close * breedlevel + breedDayCloseSum);
				breedDayTimesMap.put(breed + tradedate, breedDayTimes + 1.0);
			} else {
				breedDayCloseSumMap.put(breed + tradedate, close * breedlevel);
				breedDayTimesMap.put(breed + tradedate, 1.0);
			}
			opensum = opensum + record.getOpen() * breedlevel;
			closesum = closesum + close * breedlevel;
			highsum = highsum + record.getHigh() * breedlevel;
			lowsum = lowsum + record.getLow() * breedlevel;
			openinterestsum = openinterestsum + record.getOpeninterest();
			daytimes++;

			lasttradedate = tradedate;

		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/oddsAndData")
	@ResponseBody
	public String oddsAndData(@RequestParam("cycle") String cycle, @RequestParam("kind1value") String kind1value,
			@RequestParam("breedsvalue") String breedsvalue, @RequestParam("indexstypevalue") String indexstypevalue)
					throws IOException, InterruptedException, ParseException {
		// 定义日期格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		if ((kind1value.equals("-1") && breedsvalue.equals("-1")) || (kind1value.isEmpty() && breedsvalue.isEmpty())) {
			return null;
		}
		List<TargetIndexs> eList = null;
		Map map = new HashMap();
		String type = "-1";
		if (!kind1value.equals("-1")) {
			type = kind1value;
		}
		if (!breedsvalue.equals("-1")) {
			type = breedsvalue;
		}
		map.put("type", type);
		if (indexstypevalue.equals("0")) {
			map.put("breeds", "indexs");
			eList = mapper.getIndexs(map);
			if (eList.size() <= 0) {
				createindexs(futMapper, indexMapper, otherMapper, type);
				eList = mapper.getIndexs(map);
				if (eList.size() < 0) {
					return null;
				}
			}
		} else if (indexstypevalue.equals("1")) {
			map.put("breeds", "indexs_price");
			eList = mapper.getIndexs(map);
			if (eList.size() <= 0) {
				createpriceindexs(futMapper, indexMapper, otherMapper, type);
				eList = mapper.getIndexs(map);
				if (eList.size() < 0) {
					return null;
				}
			}

		}

		List<TargetIndexs> cycleList = new ArrayList<TargetIndexs>();
		TargetIndexs tIndexs = null;
		TargetIndexs cycleIndex = null;
		int lastweek = 0;
		double high = 0;
		double low = 10000000;
		if (cycle.equals("1week")) {
			for (int i = 0; i < eList.size(); i++) {
				cycleIndex = eList.get(i);
				if (cycleIndex.getWeek() != lastweek) {
					if (tIndexs != null) {
						cycleList.add(tIndexs);
						high = 0;
						low = 10000000;
					}
					tIndexs = null;
					tIndexs = new TargetIndexs();
					tIndexs.setOpen(cycleIndex.getOpen());

				}
				if (high < cycleIndex.getHigh()) {
					high = cycleIndex.getHigh();
				}
				if (low > cycleIndex.getLow()) {
					low = cycleIndex.getLow();
				}
				tIndexs.setOpeninterest(cycleIndex.getOpeninterest());
				tIndexs.setTradedate(cycleIndex.getTradedate());
				tIndexs.setClose(cycleIndex.getClose());
				tIndexs.setHigh(high);
				tIndexs.setLow(low);
				lastweek = cycleIndex.getWeek();
			}
			cycleList.add(tIndexs);
			high = 0;
			low = 10000000;
			tIndexs = null;
			for (int i = 0; i < cycleList.size(); i++) {
				TargetIndexs eIndexs = cycleList.get(i);
				Date currentDate = dateFormat.parse(eIndexs.getTradedate() + " 15:00:00");
				long timestamp = currentDate.getTime();
				eIndexs.setTradedate(String.valueOf(timestamp));
			}
			return JSON.toJSONString(cycleList);
		}

		int lastmonth = 0;
		if (cycle.equals("1month")) {
			for (int i = 0; i < eList.size(); i++) {
				cycleIndex = eList.get(i);
				if (cycleIndex.getMonth() != lastmonth) {
					if (tIndexs != null) {
						cycleList.add(tIndexs);
						high = 0;
						low = 10000000;
					}
					tIndexs = null;
					tIndexs = new TargetIndexs();
					tIndexs.setOpen(cycleIndex.getOpen());

				}
				if (high < cycleIndex.getHigh()) {
					high = cycleIndex.getHigh();
				}
				if (low > cycleIndex.getLow()) {
					low = cycleIndex.getLow();
				}
				tIndexs.setOpeninterest(cycleIndex.getOpeninterest());
				tIndexs.setTradedate(cycleIndex.getTradedate());
				tIndexs.setClose(cycleIndex.getClose());
				tIndexs.setHigh(high);
				tIndexs.setLow(low);
				lastmonth = cycleIndex.getMonth();
			}
			cycleList.add(tIndexs);
			high = 0;
			low = 10000000;
			tIndexs = null;
			for (int i = 0; i < cycleList.size(); i++) {
				TargetIndexs eIndexs = cycleList.get(i);
				Date currentDate = dateFormat.parse(eIndexs.getTradedate() + " 15:00:00");
				long timestamp = currentDate.getTime();
				eIndexs.setTradedate(String.valueOf(timestamp));
			}
			return JSON.toJSONString(cycleList);
		}
		int lastseason = 0;
		if (cycle.equals("1season")) {
			for (int i = 0; i < eList.size(); i++) {
				cycleIndex = eList.get(i);
				if (cycleIndex.getSeason() != lastseason) {
					if (tIndexs != null) {
						cycleList.add(tIndexs);
						high = 0;
						low = 10000000;
					}
					tIndexs = null;
					tIndexs = new TargetIndexs();
					tIndexs.setOpen(cycleIndex.getOpen());

				}
				if (high < cycleIndex.getHigh()) {
					high = cycleIndex.getHigh();
				}
				if (low > cycleIndex.getLow()) {
					low = cycleIndex.getLow();
				}
				tIndexs.setOpeninterest(cycleIndex.getOpeninterest());
				tIndexs.setTradedate(cycleIndex.getTradedate());
				tIndexs.setClose(cycleIndex.getClose());
				tIndexs.setHigh(high);
				tIndexs.setLow(low);
				lastseason = cycleIndex.getSeason();
			}
			cycleList.add(tIndexs);
			high = 0;
			low = 10000000;
			tIndexs = null;
			for (int i = 0; i < cycleList.size(); i++) {
				TargetIndexs eIndexs = cycleList.get(i);
				Date currentDate = dateFormat.parse(eIndexs.getTradedate() + " 15:00:00");
				long timestamp = currentDate.getTime();
				eIndexs.setTradedate(String.valueOf(timestamp));
			}
			return JSON.toJSONString(cycleList);
		}

		int lastyear = 0;
		if (cycle.equals("1year")) {
			for (int i = 0; i < eList.size(); i++) {
				cycleIndex = eList.get(i);
				if (cycleIndex.getYear() != lastyear) {
					if (tIndexs != null) {
						cycleList.add(tIndexs);
						high = 0;
						low = 10000000;
					}
					tIndexs = null;
					tIndexs = new TargetIndexs();
					tIndexs.setOpen(cycleIndex.getOpen());

				}
				if (high < cycleIndex.getHigh()) {
					high = cycleIndex.getHigh();
				}
				if (low > cycleIndex.getLow()) {
					low = cycleIndex.getLow();
				}
				tIndexs.setOpeninterest(cycleIndex.getOpeninterest());
				tIndexs.setTradedate(cycleIndex.getTradedate());
				tIndexs.setClose(cycleIndex.getClose());
				tIndexs.setHigh(high);
				tIndexs.setLow(low);
				lastyear = cycleIndex.getYear();
			}
			cycleList.add(tIndexs);
			high = 0;
			low = 10000000;
			tIndexs = null;
			for (int i = 0; i < cycleList.size(); i++) {
				TargetIndexs eIndexs = cycleList.get(i);
				Date currentDate = dateFormat.parse(eIndexs.getTradedate() + " 15:00:00");
				long timestamp = currentDate.getTime();
				eIndexs.setTradedate(String.valueOf(timestamp));
			}
			return JSON.toJSONString(cycleList);
		}

		if (cycle.equals("1day")) {
			for (int i = 0; i < eList.size(); i++) {
				TargetIndexs eIndexs = eList.get(i);
				Date currentDate = dateFormat.parse(eIndexs.getTradedate() + " 15:00:00");
				long timestamp = currentDate.getTime();
				eIndexs.setTradedate(String.valueOf(timestamp));
			}
			return JSON.toJSONString(eList);
		}
		return null;
	}

}
