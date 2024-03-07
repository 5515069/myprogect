package com.zhengyuan.liunao.controller.future;

import java.io.File;
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
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.common.base.FinalizablePhantomReference;
import com.zhengyuan.liunao.mapper.CycleMapper;
import com.zhengyuan.liunao.mapper.FutMapper;
import com.zhengyuan.liunao.mapper.IndexsDealMapper;
import com.zhengyuan.liunao.mapper.OtherMapper;
import com.zhengyuan.liunao.target.TarRealDealRecord;
import com.zhengyuan.liunao.target.TargetIndexs;
import com.zhengyuan.liunao.utils.K5Record;
import com.zhengyuan.liunao.utils.ReadK5Csv;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import retunInfo.retunPricePoints;
import retunInfo.retunPricestand;
import retunInfo.returnBreedClosingDate;

/**
 * 布林频繁止赢策略(作废）
 * 
 * @author 86180
 *
 */
@Controller
@RequestMapping("/Sys")
@Api("FutrueDateDeal相关api")
@Slf4j
public class bollController2 {
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Double> setDailyProfit(FutMapper futMapper, OtherMapper otherMapper,
			List<TargetIndexs> eList, double bollcycle, double bollwidth, double profitlevel, double openopeninterest,
			double closeopeninterest, int forwarddays) throws IOException, InterruptedException {
		String folderpath = "D:\\zd1";
		File folder = new File(folderpath);
		File[] folders = folder.listFiles();
		List<TarRealDealRecord> realDealRecords = new ArrayList<TarRealDealRecord>();
		for (int k = 0; k < folders.length; k++) {
			String breedname = folders[k].getName();
			System.out.println(breedname);
			if (!breedname.toUpperCase().equals("AP")) {
				//continue;
			}
			double pricepoint = retunPricePoints.getPricePoints(breedname);
			List fileList = getAllFiles(new File(folderpath + "\\" + breedname));
			for (int h = 0; h < fileList.size(); h++) {
				String contract = fileList.get(h).toString().replace(folderpath + "\\" + breedname + "\\", "")
						.replace(".csv", "");
				if (!contract.equals("AP202405")) {
					//continue;
				}
				List<K5Record> k5Records = ReadK5Csv.readCsv(fileList.get(h).toString());
				TarRealDealRecord realDealRecord = null;
				int openbs = 0;
				boolean allowopen = true;
				boolean opened = false;
				for (int i = (int) bollcycle; i < k5Records.size(); i++) {
					K5Record k5Record = k5Records.get(i);
					double low = k5Record.getLow();
					double high = k5Record.getHigh();
					double open = k5Record.getOpen();
					double close = k5Record.getClose();
					String tradedate = k5Record.getTradedate();
					double openinterest = k5Record.getOpeninterest();
					String contractenddate = returnBreedClosingDate.getCloseDate(contract, 35);
					if (openinterest > openopeninterest) {
						opened = true;
					}
					if (opened && allowopen
							&& (tradedate.compareTo(contractenddate) >= 0 || openinterest < closeopeninterest)) {
						allowopen = false;
					}
					if (openinterest==98323) {
						System.out.println(open);
					}
					double bollmid = getbollmid(k5Records, i, bollcycle, bollwidth, pricepoint);
					double bollup = getbollup(k5Records, i, bollcycle, bollwidth, pricepoint, bollmid);
					double bolldown = getbolldown(k5Records, i, bollcycle, bollwidth, pricepoint, bollmid);

					// 开仓买
					if (opened && allowopen && openbs == 0 && low <= bolldown) {
						realDealRecord = new TarRealDealRecord();
						realDealRecord.setContract(contract);
						realDealRecord.setOpendate(tradedate);
						realDealRecord.setOpenprice(bolldown);
						if (open < bolldown) {
							realDealRecord.setOpenprice(open);
						}
						realDealRecord.setOpenInterest(openinterest);
						openbs = 1;
						continue;
					}
					// 开仓买平
					if (openbs == 1 && (tradedate.compareTo(contractenddate) >= 0 || openinterest < closeopeninterest
							|| realDealRecord.getOpenprice() - close >= profitlevel * close || high >= bollup)) {
						realDealRecord.setClosepprice(close);
						if (high >= bollup) {
							realDealRecord.setClosepprice(bollup);
							if (open > bollup) {
								realDealRecord.setClosepprice(open);
							}
						}
						realDealRecord.setClosedate(tradedate);
						realDealRecord.setProfit(realDealRecord.getClosepprice() - realDealRecord.getOpenprice());
						realDealRecord.setOpenbs(1);
						realDealRecords.add(realDealRecord);
						if (realDealRecord.getOpenprice() - close >= profitlevel * close) {
							i = i + forwarddays;
						}
						realDealRecord = null;
						openbs = 0;
						// 开仓买
						if (opened && allowopen && openbs == 0 && high > bollup) {
							realDealRecord = new TarRealDealRecord();
							realDealRecord.setContract(contract);
							realDealRecord.setOpendate(tradedate);
							realDealRecord.setOpenprice(bollup);
							if (open > bollup) {
								realDealRecord.setOpenprice(open);
							}
							realDealRecord.setOpenInterest(openinterest);
							openbs = -1;
						}
						continue;
					}
					// 开仓卖
					if (opened && allowopen && openbs == 0 && high > bollup) {
						realDealRecord = new TarRealDealRecord();
						realDealRecord.setContract(contract);
						realDealRecord.setOpendate(tradedate);
						realDealRecord.setOpenprice(bollup);
						if (open > bollup) {
							realDealRecord.setOpenprice(open);
						}
						realDealRecord.setOpenInterest(openinterest);
						openbs = -1;
						continue;
					}
					// 开仓卖平
					if (openbs == -1 && (tradedate.compareTo(contractenddate) >= 0 || openinterest < closeopeninterest
							|| close - realDealRecord.getOpenprice() >= profitlevel * close || low <= bolldown)) {
						realDealRecord.setClosepprice(close);
						if (low <= bolldown) {
							realDealRecord.setClosepprice(bolldown);
							if (open < bolldown) {
								realDealRecord.setClosepprice(open);
							}
						}
						realDealRecord.setClosedate(tradedate);
						realDealRecord.setProfit(realDealRecord.getOpenprice() - realDealRecord.getClosepprice());
						realDealRecord.setOpenbs(-1);
						realDealRecords.add(realDealRecord);
						if (close - realDealRecord.getOpenprice() >= profitlevel * close) {

							i = i + forwarddays;
						}
						realDealRecord = null;
						openbs = 0;
						// 开仓买
						if (opened && allowopen && openbs == 0 && low <= bolldown) {
							realDealRecord = new TarRealDealRecord();
							realDealRecord.setContract(contract);
							realDealRecord.setOpendate(tradedate);
							realDealRecord.setOpenprice(bolldown);
							if (open < bolldown) {
								realDealRecord.setOpenprice(open);
							}
							realDealRecord.setOpenInterest(openinterest);
							openbs = 1;
						}
						continue;
					}
				}
			}
		}
		// 定义一个比较器来指定根据年龄字段进行排序
		Comparator<TarRealDealRecord> ageComparator = (p1, p2) -> Integer.compare(Integer.valueOf(p1.getOpendate()),
				Integer.valueOf(p2.getOpendate()));
		// 调用 Collections.sort() 方法并传入 ArrayList 和比较器作为参数，完成排序
		Collections.sort(realDealRecords, ageComparator);
		writeCsv("C:\\Users\\86180\\Desktop\\tradingRange\\bollrecord_test1.csv", realDealRecords);
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
			dailyProfit = dailyProfit + ((partRecord.getProfit() - 0 * pricePoints) / pricePoints);
			if (i == realDealRecords.size() - 1) {
				map.put(lastOpenDate, dailyProfit);
			}
			dayDealtimes++;
			lastOpenDate = partRecord.getOpendate();
		}
		return map;
	}

	public static double getbollmid(List<K5Record> k5Records, int i, double bollcycle, double bollwidth,
			double pricepoint) {
		double bollmid = 0;// 布林中轨
		for (int w = (i + 1) - (int) bollcycle - 1; w <= i - 1; w++) {
			bollmid = bollmid + k5Records.get(w).getClose();
		}
		bollmid=retunPricestand.getPricePoints(bollmid, pricepoint);
		return bollmid / bollcycle;
	}

	public static double getbollup(List<K5Record> k5Records, int i, double bollcycle, double bollwidth,
			double pricepoint, double startprice) {
		double bollmid = 0;// 布林中轨
		double bollup = 0;// 布林上轨
		double sd = 0;// 标准差
		double dvsq = 0;// 差值平方和
		for (double j = startprice; j < startprice + pricepoint * 1000; j = j + pricepoint) {
			 bollmid = 0;// 布林中轨
			 bollup = 0;// 布林上轨
			 sd = 0;// 标准差
			 dvsq = 0;// 差值平方和
			for (int w = (i + 1) - (int) bollcycle; w <= i; w++) {
				if (w == i) {
					bollmid = bollmid + startprice;
				} else {
					bollmid = bollmid + k5Records.get(w).getClose();
				}
				if (w == i) {
					bollmid = bollmid / bollcycle;
				}
			}
			for (int w = (i + 1) - (int) bollcycle; w <= i; w++) {
				if (w == i) {
					dvsq = dvsq + Math.pow(startprice - bollmid, bollwidth);
				} else {
					dvsq = dvsq + Math.pow(k5Records.get(w).getClose() - bollmid, bollwidth);
				}
				if (w == i) {
					dvsq = dvsq / (bollcycle - 1);
					sd = Math.sqrt(dvsq);
					bollup = bollmid + bollwidth * sd +10*pricepoint;
					bollup = retunPricestand.getPricePoints(bollup, pricepoint);
				}
			}
			if (j>=bollup) {
				break;
			}
		}
		return bollup;
	}

	public static double getbolldown(List<K5Record> k5Records, int i, double bollcycle, double bollwidth,
			double pricepoint, double startprice) {
		double bollmid = 0;// 布林中轨
		double bolldown = 0;// 布林上轨
		double sd = 0;// 标准差
		double dvsq = 0;// 差值平方和
		for (double j = startprice; j > startprice - pricepoint * 1000; j = j - pricepoint) {
			 bollmid = 0;// 布林中轨
			 bolldown = 0;// 布林上轨
			 sd = 0;// 标准差
			 dvsq = 0;// 差值平方和
			for (int w = (i + 1) - (int) bollcycle; w <= i; w++) {
				if (w == i) {
					bollmid = bollmid + startprice;
				} else {
					bollmid = bollmid + k5Records.get(w).getClose();
				}
				if (w == i) {
					bollmid = bollmid / bollcycle;
				}
			}
			for (int w = (i + 1) - (int) bollcycle; w <= i; w++) {
				if (w == i) {
					dvsq = dvsq + Math.pow(startprice - bollmid, bollwidth);
				} else {
					dvsq = dvsq + Math.pow(k5Records.get(w).getClose() - bollmid, bollwidth);
				}
				if (w == i) {
					dvsq = dvsq / (bollcycle - 1);
					sd = Math.sqrt(dvsq);
					bolldown = bollmid - bollwidth * sd -10*pricepoint;
					bolldown = retunPricestand.getPricePoints(bolldown, pricepoint);
				}
			}
			if (j<=bolldown) {
				break;
			}
		}
		return bolldown;
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

	@RequestMapping(value = "/getboll2")
	@ResponseBody
	public String getboll2(@RequestParam("cycle") String cycle, @RequestParam("bollcycle") String bollcycle,
			@RequestParam("bollwidth") String bollwidth, @RequestParam("profitlevel") String profitlevel,
			@RequestParam("openopeninterest") String openopeninterest,
			@RequestParam("closeopeninterest") String closeopeninterest,
			@RequestParam("forwarddays") String forwarddays) throws IOException, InterruptedException, ParseException {
		// 定义日期格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		Map map = new HashMap(); 
		map.put("type", 0);
		List<TargetIndexs> eList = mapper.getIndexs(map);
		Map<String, Double> DailyProfitMap = setDailyProfit(futMapper, otherMapper, eList, Double.valueOf(bollcycle),
				Double.valueOf(bollwidth), Double.valueOf(profitlevel), Double.valueOf(openopeninterest),
				Double.valueOf(closeopeninterest), Integer.valueOf(forwarddays));
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List getAllFiles(File dir) {
		List fileList = new ArrayList<>();
		if (dir.isDirectory()) {
			File[] subFiles = dir.listFiles();
			if (subFiles != null) {
				for (File subFile : subFiles) {
					fileList.addAll(getAllFiles(subFile));
				}
			}
		} else {
			fileList.add(dir);
		}
		return fileList;
	}
}
