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
import com.zhengyuan.liunao.utils.GaoDiRecord;
import com.zhengyuan.liunao.utils.K5Record;
import com.zhengyuan.liunao.utils.ReadK5Csv;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import retunInfo.retunPricePoints;
import retunInfo.returnBreedClosingDate;

@Controller
@RequestMapping("/Sys")
@Api("FutrueDateDeal相关api")
@Slf4j
public class TopReverseController {

	@Autowired
	IndexsDealMapper mapper;
	@Autowired
	CycleMapper cycleMapper;
	@Autowired
	OtherMapper otherMapper;
	@Autowired
	FutMapper futMapper;
	
	@RequestMapping(value = "/getTopReverse")
	@ResponseBody
	public String getTopReverse(@RequestParam("cycle") String cycle, @RequestParam("profitlevel") String profitlevel,
			@RequestParam("openPosition") String openPosition, @RequestParam("closePosition") String closePosition,
			@RequestParam("openopeninterest") String openopeninterest,
			@RequestParam("closeopeninterest") String closeopeninterest, @RequestParam("closeDate") String closeDate)
					throws IOException, InterruptedException, ParseException {

		Map<String, Double> DailyProfitMap = getDailyProfit(otherMapper, Double.valueOf(profitlevel),
				Integer.valueOf(openPosition), Integer.valueOf(closePosition), Double.valueOf(openopeninterest),
				Double.valueOf(closeopeninterest), Integer.valueOf(closeDate));
		// 定义日期格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		Map map = new HashMap(); 
		map.put("type", "0");
		List<TargetIndexs> eList = mapper.getIndexs(map);
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

	public static Map<String, Double> getDailyProfit(OtherMapper otherMapper, Double profitlevel, int openPosition,
			int closePosition, Double openopeninterest, Double closeopeninterest, int closeDate)
					throws IOException, InterruptedException {
		String filePath5k = "D:\\zk5";
		// 真实交易总记录
		List<GaoDiRecord> allGaoDiRecords = otherMapper.getGaoDis();
		GaoDiRecord allGaoDiRecord = null;
		// 范围交易记录
		List<TarRealDealRecord> partGaoDiRecords = new ArrayList<TarRealDealRecord>();
		TarRealDealRecord partGaoDiRecord = null;
		// 5分钟k线数据
		List<K5Record> k5Records = null;
		K5Record k5Record = null;
		String lastContract = null;
		int contractendsitetem = 0;
		int contractendsite = 0;
		for (int i = 0; i < allGaoDiRecords.size(); i++) {
			if (i % 2000 == 0) {
				System.out.println(i);
			}
			allGaoDiRecord = allGaoDiRecords.get(i);
			String contract = allGaoDiRecord.getContract();
			double topprice = allGaoDiRecord.getTopprice();
			int hightouchsite = allGaoDiRecord.getHightouchsite();
			int lowtouchsite = allGaoDiRecord.getLowtouchsite();
			String closeDates = returnBreedClosingDate.getCloseDate(contract, 20);
			String breedname = contract.replaceAll("\\d", "");
			double pricePoints = retunPricePoints.getPricePoints(breedname);
			if (!contract.equals(lastContract)) {
				k5Records = ReadK5Csv.readCsv(filePath5k + "\\" + breedname + "\\" + contract + ".csv");
				int site = hightouchsite;
				if (lowtouchsite != 0) {
					site = lowtouchsite;
				}
				for (int j = site; j < k5Records.size(); j++) {
					if (k5Records.get(j).getTradedate().compareTo(closeDates) >= 0
							|| k5Records.get(j).getOpeninterest() < closeopeninterest || j == k5Records.size() - 1) {
						contractendsite = j;

						break;
					}
				}
			}
			contractendsitetem = contractendsite;
			lastContract = contract;
			partGaoDiRecord = null;
			partGaoDiRecord = new TarRealDealRecord();
			partGaoDiRecord.setContract(allGaoDiRecord.getContract());
			partGaoDiRecord.setTopprice(topprice);
			partGaoDiRecord.setProfitlevel(allGaoDiRecord.getProfitlevel());
			partGaoDiRecord.setTopsite(allGaoDiRecord.getHightouchsite());

			if (hightouchsite != 0) {
				// +0代表最高最低价买+1代表从前前收盘价买加50代表从前500收盘价买
				if (contractendsitetem < hightouchsite + 1 + openPosition) {
					continue;
				}
				for (int j = hightouchsite; j <= hightouchsite + 1 + openPosition; j++) {
					k5Record = k5Records.get(j);
					if (k5Record.getLow() <= topprice - profitlevel * pricePoints) {
						hightouchsite = 0;
						break;
					}
				}
				if (hightouchsite == 0) {
					continue;
				}

				partGaoDiRecord.setOpendate(k5Records.get(hightouchsite + openPosition).getTradedate());
				partGaoDiRecord.setOpentime(k5Records.get(hightouchsite + openPosition).getTradetime());
				partGaoDiRecord.setOpenprice(k5Records.get(hightouchsite + openPosition).getClose());
				partGaoDiRecord.setOpenInterest(k5Records.get(hightouchsite + openPosition).getOpeninterest());
				if (contractendsitetem > hightouchsite + closePosition) {
					contractendsitetem = hightouchsite + closePosition;
				}
				for (int j = hightouchsite + openPosition; j <= contractendsitetem; j++) {
					partGaoDiRecord.setHightouchsite(hightouchsite + openPosition);
					k5Record = k5Records.get(j);
					if (k5Record.getHigh() <= topprice - profitlevel * pricePoints) {
						partGaoDiRecord.setClosedate(k5Record.getTradedate());
						partGaoDiRecord.setClosetime(k5Record.getTradetime());
						if (k5Record.getOpen() < topprice + profitlevel * pricePoints) {
							partGaoDiRecord.setClosepprice(k5Record.getClose());
						} else {
							partGaoDiRecord.setClosepprice(topprice - profitlevel * pricePoints);
						}
						partGaoDiRecord.setHoldtime(j - hightouchsite - openPosition);
						partGaoDiRecord.setProfit(partGaoDiRecord.getClosepprice()-partGaoDiRecord.getOpenprice());
						partGaoDiRecords.add(partGaoDiRecord);
						break;
					}
					if (j == contractendsitetem) {
						k5Record = k5Records.get(j);
						partGaoDiRecord.setClosedate(k5Record.getTradedate());
						partGaoDiRecord.setClosetime(k5Record.getTradetime());
						partGaoDiRecord.setClosepprice(k5Record.getClose());
						partGaoDiRecord.setHoldtime(j - hightouchsite - openPosition);
						partGaoDiRecord.setProfit(partGaoDiRecord.getClosepprice()-partGaoDiRecord.getOpenprice());
						partGaoDiRecords.add(partGaoDiRecord);
					}
				}

			}
			if (lowtouchsite != 0) {
				// +0代表最高最低价买+1代表从前前收盘价买加50代表从前500收盘价买
				if (contractendsitetem < lowtouchsite + 1 + openPosition) {
					continue;
				}
				for (int j = lowtouchsite; j <= lowtouchsite + 1 + openPosition; j++) {
					k5Record = k5Records.get(j);
					if (k5Record.getLow() >= topprice + profitlevel * pricePoints) {
						lowtouchsite = 0;
						break;
					}
				}
				if (lowtouchsite == 0) {
					continue;
				}

				partGaoDiRecord.setOpendate(k5Records.get(lowtouchsite + openPosition).getTradedate());
				partGaoDiRecord.setOpentime(k5Records.get(lowtouchsite + openPosition).getTradetime());
				partGaoDiRecord.setOpenprice(k5Records.get(lowtouchsite + openPosition).getClose());
				partGaoDiRecord.setOpenInterest(k5Records.get(lowtouchsite  + openPosition).getOpeninterest());

				if (contractendsitetem > lowtouchsite + closePosition) {
					contractendsitetem = lowtouchsite + closePosition;
				}
				for (int j = lowtouchsite + openPosition; j <= contractendsitetem; j++) {
					partGaoDiRecord.setLowtouchsite(lowtouchsite + openPosition);
					k5Record = k5Records.get(j);
					if (k5Record.getLow() >= topprice + profitlevel * pricePoints) {
						partGaoDiRecord.setClosedate(k5Record.getTradedate());
						partGaoDiRecord.setClosetime(k5Record.getTradetime());
						if (k5Record.getOpen() > topprice + profitlevel * pricePoints) {
							partGaoDiRecord.setClosepprice(k5Record.getClose());
						} else {
							partGaoDiRecord.setClosepprice(topprice + profitlevel * pricePoints);
						}
						partGaoDiRecord.setHoldtime(j - lowtouchsite - openPosition);
						partGaoDiRecord.setProfit(partGaoDiRecord.getOpenprice()-partGaoDiRecord.getClosepprice());
						partGaoDiRecords.add(partGaoDiRecord);
						break;
					}
					if (j == contractendsitetem) {
						k5Record = k5Records.get(j);
						partGaoDiRecord.setClosedate(k5Record.getTradedate());
						partGaoDiRecord.setClosetime(k5Record.getTradetime());
						partGaoDiRecord.setClosepprice(k5Record.getClose());
						partGaoDiRecord.setHoldtime(j - lowtouchsite - openPosition);
						partGaoDiRecord.setProfit(partGaoDiRecord.getOpenprice()-partGaoDiRecord.getClosepprice());
						partGaoDiRecords.add(partGaoDiRecord);
					}
				}
			}
		}
		// 定义一个比较器来指定根据年龄字段进行排序
		Comparator<TarRealDealRecord> ageComparator = (p1, p2) -> Integer.compare(Integer.valueOf(p1.getOpendate()),
				Integer.valueOf(p2.getOpendate()));
		// 调用 Collections.sort() 方法并传入 ArrayList 和比较器作为参数，完成排序
		Collections.sort(partGaoDiRecords, ageComparator);

		String lastOpenDate = null;
		double dailyProfit = 0;
		Map<String, Double> map = new HashMap<String, Double>();
		Map<String, TarRealDealRecord> positionMap = new HashMap<String, TarRealDealRecord>(); 
		double dayDealtimes=0;//每天交易量
		for (int i = 0; i < partGaoDiRecords.size(); i++) {
			TarRealDealRecord partRecord = partGaoDiRecords.get(i);
			//同一顶点去重复订单
			if (positionMap.containsKey(partRecord.getContract() + partRecord.getTopprice())) {
				TarRealDealRecord positionPartRecord = positionMap
						.get(partRecord.getContract() + partRecord.getTopprice());
				if ((partRecord.getHightouchsite() == 0 && positionPartRecord.getHightouchsite() == 0)
						|| (partRecord.getLowtouchsite() == 0 && positionPartRecord.getLowtouchsite() == 0)) {
					if (partRecord.getOpendate().compareTo(positionPartRecord.getOpendate()) >= 0
							&& partRecord.getOpendate().compareTo(positionPartRecord.getClosedate()) <= 0) {
						continue;
					}
					if (positionPartRecord.getOpendate().compareTo(partRecord.getOpendate()) >= 0
							&& positionPartRecord.getOpendate().compareTo(partRecord.getClosedate()) <= 0) {
						continue;
					}
				}
			} else {
				positionMap.put(partRecord.getContract() + partRecord.getTopprice(), partRecord);
			}
			
			if (lastOpenDate != null && !partRecord.getOpendate().equals(lastOpenDate)) {
				dayDealtimes=10.0/dayDealtimes;
				map.put(lastOpenDate, dailyProfit*dayDealtimes);
				dailyProfit = 0;
				dayDealtimes=0;
			}
			String breedname = partRecord.getContract().replaceAll("\\d", "");
			double pricePoints = retunPricePoints.getPricePoints(breedname);
			dailyProfit = dailyProfit + (partRecord.getProfit()-pricePoints) / pricePoints;
			if (i == partGaoDiRecords.size() - 1) {
				map.put(lastOpenDate, dailyProfit);
			}
			dayDealtimes++;
			lastOpenDate = partRecord.getOpendate(); 
		} 
		return map;
	}

}
