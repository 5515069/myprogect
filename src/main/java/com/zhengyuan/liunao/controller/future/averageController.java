package com.zhengyuan.liunao.controller.future;

import java.io.FileWriter;
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
import com.zhengyuan.liunao.target.TarRealDealRecord;
import com.zhengyuan.liunao.target.TargetIndexs;
import com.zhengyuan.liunao.utils.K5Record;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import retunInfo.retunPricePoints;
import retunInfo.returnBreedClosingDate;

@Controller
@RequestMapping("/Sys")
@Api("FutrueDateDeal相关api")
@Slf4j
public class averageController {
	static Random random = new Random();
	@Autowired
	IndexsDealMapper mapper;
	@Autowired
	CycleMapper cycleMapper;
	@Autowired
	OtherMapper otherMapper;
	@Autowired
	FutMapper futMapper;

	// 获取每天利润
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	public static Map<String, Double> setDailyProfit(FutMapper futMapper, OtherMapper otherMapper,
			List<TargetIndexs> eList, double average, double openopeninterest, double closeopeninterest)
					throws IOException, InterruptedException {
		List<TarRealDealRecord> realDealRecords = new ArrayList<TarRealDealRecord>();
		String lasttradedate = null;
		int time = 0;
		Map<String, String> holdmap=new HashMap<String, String>();
		for (int i = (int) average; i < eList.size(); i++) {
			TargetIndexs lastindexs = eList.get(i - 1);
			TargetIndexs indexs = eList.get(i);
			double averageIndexs = 0;
			for (int j = (int) (i - average + 1); j <= i; j++) {
				averageIndexs = averageIndexs + eList.get(j).getClose();
			}
			averageIndexs = averageIndexs / average;
			double lastaverageIndexs = 0;
			for (int j = (int) (i - average); j <= i - 1; j++) {
				lastaverageIndexs = lastaverageIndexs + eList.get(j).getClose();
			}
			lastaverageIndexs = lastaverageIndexs / average;
			
			// 收盘价突破average日均线买->
			if (lastindexs.getClose() <= lastaverageIndexs && indexs.getClose() > averageIndexs) {
				if (lasttradedate != null) {
					Map map = new HashMap();
					map.put("breed", "d1");
					map.put("tradedatestart", lasttradedate);
					map.put("tradedatestartend", indexs.getTradedate());
					List<K5Record> kRecords = futMapper.getTarK5RecordByTradedate(map);
					map = null;
					for (int w = 0; w < kRecords.size(); w++) {
						K5Record k5Record = kRecords.get(w);
						if (!k5Record.getTradedate().equals(lasttradedate)) {
							//continue;
						}
						String contractenddate = returnBreedClosingDate.getCloseDate(k5Record.getContract(), 25);
						if (k5Record.getOpeninterest() < openopeninterest
								|| k5Record.getTradedate().compareTo(contractenddate) >= 0) {
							continue;
						}
						Map map1 = new HashMap();
						if (holdmap.containsKey(k5Record.getContract())) {
							continue;
						}else { 
							map1.put("breed", "d1");
							map1.put("tradedatestart", k5Record.getTradedate());
							map1.put("tradedatestartend", indexs.getTradedate());
							map1.put("contract", k5Record.getContract());
							holdmap.put(k5Record.getContract(), "1");
						} 
						TarRealDealRecord realDealRecord = new TarRealDealRecord();
						realDealRecord.setContract(k5Record.getContract());
						realDealRecord.setOpendate(k5Record.getTradedate());
						realDealRecord.setOpenprice(k5Record.getClose());
						realDealRecord.setOpenInterest(k5Record.getOpeninterest());
						
						List<K5Record> kRecords2 = futMapper.getTarK5RecordByTradedateandContract(map1);
						map1 = null;
						for (int j = 0; j < kRecords2.size(); j++) {
							K5Record k5Record2 = kRecords2.get(j);
							if (k5Record2.getOpeninterest() < closeopeninterest
									|| k5Record2.getTradedate().compareTo(contractenddate) > 0
									|| j == kRecords2.size() - 1) {
								realDealRecord.setClosepprice(k5Record2.getClose());
								realDealRecord.setClosedate(k5Record2.getTradedate());
								realDealRecord
										.setProfit(realDealRecord.getOpenprice() - realDealRecord.getClosepprice());
								realDealRecord.setOpenbs(-1);
								int range1 = 1;//random.nextInt(10);
								if (range1 == 1 ) {
									realDealRecords.add(realDealRecord);
								} 
								break;
							}
						} 
					} 
				}
				time++;
				holdmap.clear();
				lasttradedate = indexs.getTradedate();
			}
			// 收盘价跌破average日均线卖
			if (lastindexs.getClose() >= lastaverageIndexs && indexs.getClose() < averageIndexs) {
				if (lasttradedate != null) {
					Map map = new HashMap();
					map.put("breed", "d1");
					map.put("tradedatestart", lasttradedate);
					map.put("tradedatestartend", indexs.getTradedate());
					List<K5Record> kRecords = futMapper.getTarK5RecordByTradedate(map);
					map = null;
					for (int w = 0; w < kRecords.size(); w++) {
						K5Record k5Record = kRecords.get(w);
						if (!k5Record.getTradedate().equals(lasttradedate)) {
							//continue;
						}
						String contractenddate = returnBreedClosingDate.getCloseDate(k5Record.getContract(), 25);
						if (k5Record.getOpeninterest() < openopeninterest
								|| k5Record.getTradedate().compareTo(contractenddate) >= 0) {
							continue;
						}
						Map map1 = new HashMap();
						if (holdmap.containsKey(k5Record.getContract())) {
							continue;
						}else { 
							map1.put("breed", "d1");
							map1.put("tradedatestart", k5Record.getTradedate());
							map1.put("tradedatestartend", indexs.getTradedate());
							map1.put("contract", k5Record.getContract());
							holdmap.put(k5Record.getContract(), "1"); 
						}
						
						TarRealDealRecord realDealRecord = new TarRealDealRecord();
						realDealRecord.setContract(k5Record.getContract());
						realDealRecord.setOpendate(k5Record.getTradedate());
						realDealRecord.setOpenprice(k5Record.getClose());
						realDealRecord.setOpenInterest(k5Record.getOpeninterest()); 
						List<K5Record> kRecords2 = futMapper.getTarK5RecordByTradedateandContract(map1);
						map1 = null;
						for (int j = 0; j < kRecords2.size(); j++) {
							K5Record k5Record2 = kRecords2.get(j);
							if (k5Record2.getOpeninterest() < closeopeninterest
									|| k5Record2.getTradedate().compareTo(contractenddate) > 0
									|| j == kRecords2.size() - 1) {
								realDealRecord.setClosepprice(k5Record2.getClose());
								realDealRecord.setClosedate(k5Record2.getTradedate());
								realDealRecord
										.setProfit(realDealRecord.getClosepprice()-realDealRecord.getOpenprice());
								realDealRecord.setOpenbs(1); 
								int range1 = 1;//random.nextInt(10);
								if (range1 == 1) {
									realDealRecords.add(realDealRecord);
								} 
								break;
							}
						} 
					} 
				}
				time++;
				holdmap.clear();
				lasttradedate = indexs.getTradedate();
			}
		}
		// 定义一个比较器来指定根据年龄字段进行排序
		Comparator<TarRealDealRecord> ageComparator = (p1, p2) -> Integer.compare(Integer.valueOf(p1.getOpendate()),
				Integer.valueOf(p2.getOpendate()));
		// 调用 Collections.sort() 方法并传入 ArrayList 和比较器作为参数，完成排序
		Collections.sort(realDealRecords, ageComparator);
		writeCsv("C:\\Users\\86180\\Desktop\\tradingRange\\averRealDealRecords_test2.csv", realDealRecords);
		String lastOpenDate = null;
		double dailyProfit = 0;
		Map<String, Double> map = new HashMap<String, Double>();
		double dayDealtimes = 0;// 每天交易量
		for (int i = 0; i < realDealRecords.size(); i++) {
			TarRealDealRecord partRecord = realDealRecords.get(i);
			if (lastOpenDate != null && !partRecord.getOpendate().equals(lastOpenDate)) {
				dayDealtimes = 1;// 100.0 / dayDealtimes;
				map.put(lastOpenDate, dailyProfit * dayDealtimes);
				dailyProfit = 0;
				dayDealtimes = 0;
			}
			String breedname = partRecord.getContract().replaceAll("\\d", "");
			double pricePoints = retunPricePoints.getPricePoints(breedname);
			dailyProfit = dailyProfit + (partRecord.getProfit() - 1.5 * pricePoints) / pricePoints;
			if (i == realDealRecords.size() - 1) {
				map.put(lastOpenDate, dailyProfit);
			}
			dayDealtimes++;
			lastOpenDate = partRecord.getOpendate();
		}
		return map;
	}

	// 写入csv
	public static void writeCsv(String filePath, List<TarRealDealRecord> list)
			throws IOException, InterruptedException {
		String csvSplitBy = ","; // 分割符，csv文件特有
		try (FileWriter fw = new FileWriter(filePath)) {
			TarRealDealRecord index = null;
			for (int i = 0; i < list.size(); i++) {
				index = list.get(i);
				fw.append(index.getContract());
				fw.append(csvSplitBy);
				fw.append(index.getOpendate());
				fw.append(csvSplitBy);
				fw.append(index.getClosedate());
				fw.append(csvSplitBy);
				fw.append(String.valueOf(index.getOpenprice()));
				fw.append(csvSplitBy);
				fw.append(String.valueOf(index.getClosepprice()));
				fw.append(csvSplitBy);
				fw.append(String.valueOf(index.getOpenbs()));
				fw.append(csvSplitBy);
				fw.append(String.valueOf(index.getProfit()));
				fw.append('\n');

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/getaverage")
	@ResponseBody
	public String getaverage(@RequestParam("cycle") String cycle, @RequestParam("average") String average,
			@RequestParam("openopeninterest") String openopeninterest,
			@RequestParam("closeopeninterest") String closeopeninterest)
					throws IOException, InterruptedException, ParseException {
		// 定义日期格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		Map map = new HashMap(); 
		map.put("type", "0");
		map.put("breeds", "indexs_price");
		List<TargetIndexs> eList = mapper.getIndexs(map);
		Map<String, Double> DailyProfitMap = setDailyProfit(futMapper, otherMapper, eList, Double.valueOf(average),
			Double.valueOf(openopeninterest), Double.valueOf(closeopeninterest));
		List<TargetIndexs> cycleList = new ArrayList<TargetIndexs>();
		TargetIndexs tIndexs = null;
		TargetIndexs cycleIndex = null;
		int lastweek = 0;
		double high = 0;
		double low = 10000000;

	double profitSum = 0;
		for (int i = 0; i < eList.size(); i++) {
			TargetIndexs eIndexs = eList.get(i);
			if (DailyProfitMap.containsKey(eIndexs.getTradedate())) {
				profitSum = profitSum + DailyProfitMap.get(eIndexs.getTradedate());
				eIndexs.setOpeninterest(profitSum);
			} else {
				eIndexs.setOpeninterest(profitSum);
			}
		}
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
