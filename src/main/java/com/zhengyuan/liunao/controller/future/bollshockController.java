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
 * 布林频震荡策略策略1
 * 
 * @author 86180
 *
 */
@Controller
@RequestMapping("/Sys")
@Api("FutrueDateDeal相关api")
@Slf4j
public class bollshockController {
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
		int time = 0;
		double sum = 0;
		for (int k = 0; k < folders.length; k++) {
			String breedname = folders[k].getName();
			if (buhegepinzhong(breedname)) {
				continue;
			}
			// System.out.println(breedname);
			double pricepoint = retunPricePoints.getPricePoints(breedname);
			List fileList = getAllFiles(new File(folderpath + "\\" + breedname));
			for (int h = 0; h < fileList.size(); h++) {
				String contract = fileList.get(h).toString().replace(folderpath + "\\" + breedname + "\\", "")
						.replace(".csv", "");
				List<K5Record> k5Records = ReadK5Csv.readCsv(fileList.get(h).toString());
				TarRealDealRecord realDealRecord = null;
				boolean allowopen = true;
				boolean opened = false;
				for (int i = (int) bollcycle + 5; i < k5Records.size() - 5; i++) {
					K5Record k5Record = k5Records.get(i);
					double low = k5Record.getLow();
					double high = k5Record.getHigh();
					double open = k5Record.getOpen();
					double close = k5Record.getClose();
					double nextclose1 = k5Records.get(i + 0).getClose();
					double nextclose2 = k5Records.get(i + 1).getClose();
					String tradedate = k5Record.getTradedate();
					double openinterest = k5Record.getOpeninterest();
					String contractenddate = returnBreedClosingDate.getCloseDate(contract, 15);
					double highlimit = k5Record.getHighlimit();
					double lowlimit = k5Record.getLowlimit();
					double nexthighlimit = k5Records.get(i + 1).getHighlimit();
					double nextlowlimit = k5Records.get(i + 1).getLowlimit();
					double nextlow = k5Records.get(i + 1).getLow();
					if (openinterest > openopeninterest) {
						opened = true;
					}
					if (opened && allowopen
							&& (tradedate.compareTo(contractenddate) >= 0 || openinterest < closeopeninterest)) {
						allowopen = false;
					}
					double bollmid = 0;// 布林中轨
					double bollup = 0;// 布林上轨
					double bolldown = 0;// 布林下轨
					double sd = 0;// 标准差
					double dvsq = 0;// 差值平方和
					int n = 0;
					for (int w = (i + 1) - (int) bollcycle - n; w <= i - n; w++) {
						if (w == i) {
							bollmid = bollmid + k5Records.get(w).getOpen();
						} else {
							bollmid = bollmid + k5Records.get(w).getClose();
						}
						if (w == i) {
							bollmid = bollmid / bollcycle;
						}
					}
					for (int w = (i + 1) - (int) bollcycle - n; w <= i - n; w++) {
						if (w == i) {
							dvsq = dvsq + Math.pow(k5Records.get(w).getOpen() - bollmid, bollwidth);
						} else {
							dvsq = dvsq + Math.pow(k5Records.get(w).getClose() - bollmid, bollwidth);
						}
						if (w == i) {
							dvsq = dvsq / (bollcycle - 1);
							sd = Math.sqrt(dvsq);
							bollup = bollmid + bollwidth * sd + pricepoint * 0;
							bollup = retunPricestand.getPricePoints(bollup, pricepoint);
							bolldown = bollmid - bollwidth * sd - pricepoint * 0;
							bolldown = retunPricestand.getPricePoints(bolldown, pricepoint);
						}
					} 
					if (k5Record.getOpeninterest() < 50000 || k5Record.getOpeninterest() > 200000000) {
						continue;
					}
					if (tradedate.compareTo(contractenddate) >= 0) {
						continue;
					}

					double bili = 100;
					if (folderpath.equals("D:\\zh1")) {
						if (!((k5Record.getTradetime().equals("9:25:00"))
								|| k5Record.getTradetime().equals("21:25:00"))) {
							// continue;
						}
						if (open < bollup && high > bollup + pricepoint * bili) {
							double openprice = bollup + pricepoint * bili;
							double closeprice = nextclose2;
							if (open > bollup + pricepoint * bili * 0.5) {
								// continue;
							}
							double profit = (openprice - closeprice - 0.5 * pricepoint) / pricepoint * 10;
							time++;

							sum = sum + profit;
							// i=i+10;
							// 交易记录
							realRecords(realDealRecord, contract, tradedate, k5Record, openprice, closeprice, profit,
									realDealRecords, -1);
						}

						if (open > bolldown && low < bolldown - pricepoint * bili) {
							double openprice = bolldown - pricepoint * bili;
							double closeprice = nextclose2;
							if (open < bolldown - pricepoint * bili * 0.5) {
								// continue;
							}
							double profit = (closeprice - openprice - 0.5 * pricepoint) / pricepoint * 10;
							time++;

							sum = sum + profit;
							// i=i+10;
							// 交易记录
							realRecords(realDealRecord, contract, tradedate, k5Record, openprice, closeprice, profit,
									realDealRecords, 1);
						}

					}

					// profit利润相关：updown增加相关量 2：持仓量 3:openprice
					// 由open和updown作为值profit不同
					if (folderpath.equals("D:\\zd1")) {

						if (open > bollup && high > open + pricepoint * bili) {
							double openprice = open + pricepoint * bili;
							double closeprice = nextclose2;
							if (open > bollup + pricepoint * bili * 0.5) {
								// continue;
							}
							double profit = (openprice - closeprice - 0.5 * pricepoint) / pricepoint * 10;
							time++;

							sum = sum + profit;
							// i=i+10;
							// 交易记录
							realRecords(realDealRecord, contract, tradedate, k5Record, openprice, closeprice, profit,
									realDealRecords, -1);
						}

						if (open < bolldown && low < open - pricepoint * bili) {
							double openprice = open - pricepoint * bili;
							double closeprice = nextclose2;
							if (open < bolldown - pricepoint * bili * 0.5) {
								// continue;
							}
							double profit = (closeprice - openprice - 0.5 * pricepoint) / pricepoint * 10;
							time++;

							// sum = sum + profit;
							// i=i+10;
							// 交易记录
							realRecords(realDealRecord, contract, tradedate, k5Record, openprice, closeprice, profit,
									realDealRecords, 1);
						}
					}

				}
			}
			System.out.println(breedname + ":" + sum + ":" + time);
			// sum=0;
		}
		System.out.println(time);
		System.out.println(sum);
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
			dailyProfit = dailyProfit + (partRecord.getProfit() - 0 * pricePoints);
			if (i == realDealRecords.size() - 1) {
				map.put(lastOpenDate, dailyProfit);
			}
			dayDealtimes++;
			lastOpenDate = partRecord.getOpendate();
		}
		return map;
	}

	public static void realRecords(TarRealDealRecord realDealRecord, String contract, String tradedate,
			K5Record k5Record, double openprice, double closeprice, double profit,
			List<TarRealDealRecord> realDealRecords, int openbs) {
		realDealRecord = new TarRealDealRecord();
		realDealRecord.setContract(contract);
		realDealRecord.setOpendate(tradedate);
		realDealRecord.setOpentime(k5Record.getTradetime());
		realDealRecord.setOpenprice(openprice);
		realDealRecord.setClosepprice(closeprice);
		realDealRecord.setOpenInterest(k5Record.getOpeninterest());
		realDealRecord.setProfit(profit);
		realDealRecord.setOpenbs(openbs);
		realDealRecords.add(realDealRecord);
		realDealRecord = null;
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
				fw.append(index.getOpentime());
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

	@RequestMapping(value = "/getbollshock")
	@ResponseBody
	public String getbollshock(@RequestParam("cycle") String cycle, @RequestParam("bollcycle") String bollcycle,
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

	@SuppressWarnings("unused")
	public static boolean buhegepinzhong(String breedname) {
		if (1 == 2) {
			return false;
		}
		if (breedname.toUpperCase().contains("BC") || breedname.toUpperCase().contains("AU")
				|| breedname.toUpperCase().contains("CU") || breedname.toUpperCase().contains("SN")
				|| breedname.toUpperCase().contains("SC") || breedname.toUpperCase().contains("NI")
				|| breedname.toUpperCase().contains("EC") || breedname.toUpperCase().contains("LH")
				|| breedname.toUpperCase().contains("LC")) {
			return true;
		} else {
			return false;
		}
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
