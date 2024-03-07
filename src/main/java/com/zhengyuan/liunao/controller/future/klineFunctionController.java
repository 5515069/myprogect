package com.zhengyuan.liunao.controller.future;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; 
import com.zhengyuan.liunao.mapper.FutMapper; 
import com.zhengyuan.liunao.mapper.OtherMapper;
import com.zhengyuan.liunao.mapper.ShapeMapper; 
import com.zhengyuan.liunao.target.TargetTickDate;
import com.zhengyuan.liunao.utils.K5Record;
import com.zhengyuan.liunao.utils.ReadTickCsv; 
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 * 接口功能
 */
@Controller
@RequestMapping("/Sys")
@Api("FutrueDateDeal相关api")
@Slf4j
public class klineFunctionController extends Thread{
	static Random random = new Random();
	@Autowired
	OtherMapper mapper;
	@Autowired
	FutMapper futMapper;
	@Autowired
	ShapeMapper shapeMapper;
	@Autowired
	OtherMapper otherMapper;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/addk1date")
	@ResponseBody
	public String addk1date() throws ParseException, IOException, InterruptedException {
		String tickpath = "E:\\zzztick";
		File tickfile = new File(tickpath);
		File[] arraytick = tickfile.listFiles(); 
		for (int k = 0; k < arraytick.length; k++) {
			String breed = arraytick[k].getName(); 
			/*Map sizemap=new HashMap<>();
			sizemap.put("table", breed);
			int size=futMapper.getkdatesize(sizemap);
			if (size>0) {
				continue;
			}else {
				sleep(random.nextInt(1000*20)+1000*10);
				size=futMapper.getkdatesize(sizemap);
				if (size>0) {
					continue;
				}
			}*/
			String breedpath = tickpath + "\\" + breed;
			File pathfile = new File(breedpath);
			File[] patharray = pathfile.listFiles();
			System.out.println(breed);
			// 递归
			int id=2000000;
			for (int i = 0; i < patharray.length; i++) { 
				// 代码需要字段
				int UpdateTimeH = -1;
				int UpdateTimeM = -1;
				int UpdateTimeS = -1;
				// 集合 
				K5Record k5Record = null; 
				int closeTrayTimeH = 15; 
				double close=0;
				double high = 0;
				double low = 0;
				String newupdatetime = null;
				String lastnewupdatetime = null;
				double lastvolume = 0;
				String tradetime=null;
				TargetTickDate tickDate=null; 
				String contractpath = breedpath + "\\" + patharray[i].getName();
				List<TargetTickDate> tickList = ReadTickCsv.readziptickcsv(contractpath);
				System.out.println(contractpath);
				for (int j = 0; j < tickList.size(); j++) {   
					if (j == 0) {
						for (int w = 0; w < tickList.size(); w++) {
							tickDate = tickList.get(w);
							tradetime = tickDate.getTradetime();
							UpdateTimeH = Integer.valueOf(tradetime.substring(0, 2));
							if (UpdateTimeH == 21) {
								if (closeTrayTimeH != 1 && closeTrayTimeH != 2) {
									closeTrayTimeH = 23;
								}
							}
							if (UpdateTimeH == 0) {
								if (closeTrayTimeH != 2) {
									closeTrayTimeH = 1;
								}
							}
							if (UpdateTimeH == 2) {
								closeTrayTimeH = 2;
								break;
							}
						}
					}
					tickDate = tickList.get(j);
					String tradedate = tickDate.getTradedate();
					tradetime = tickDate.getTradetime(); 
					double volume = tickDate.getVolume();
					double currentprice = tickDate.getCurrentprice();
					double openinterest = tickDate.getOpeninterest(); 
					UpdateTimeH = Integer.valueOf(tradetime.substring(0, 2));
					UpdateTimeM = Integer.valueOf(tradetime.substring(3, 5));
					UpdateTimeS = Integer.valueOf(tradetime.substring(6, 8));
					if ((UpdateTimeH >= 3 && (UpdateTimeH < 8) || (UpdateTimeH > 15 && UpdateTimeH < 20))) {
						continue;
					}
					if (UpdateTimeH == 8) {
						continue;
					}
					if (UpdateTimeH == 20) {
						continue;
					}

					if (UpdateTimeH == 8 && UpdateTimeM == 59) {
						if (volume > 0 && volume != lastvolume) {
							newupdatetime = "9:01:00";
						}
					} else if (UpdateTimeH == 20 && UpdateTimeM == 59) {
						if (volume > 0 && volume != lastvolume) {
							newupdatetime = "21:01:00";
						}
					} else {
						if ((UpdateTimeH == 9 || UpdateTimeH == 21) && (UpdateTimeM == 0 && UpdateTimeS == 0)) {
							if (volume > 0 && volume != lastvolume) {
								newupdatetime = UpdateTimeH + ":01:00";
							}
						} else if ((UpdateTimeH == 10 || UpdateTimeH == 13)
								&& (UpdateTimeM == 30 && UpdateTimeS == 0)) {
							if (volume > 0 && volume != lastvolume) {
								newupdatetime = UpdateTimeH + ":31:00";
							}
						} else if ((UpdateTimeH == 10 && UpdateTimeM == 15)
								|| (UpdateTimeH == 11 && UpdateTimeM == 30)) {
							if (volume > 0 && volume != lastvolume) {
								newupdatetime = UpdateTimeH + ":" + UpdateTimeM + ":00";
							}
						} else if ((UpdateTimeH == 15) || (closeTrayTimeH == 23 && UpdateTimeH == 23)
								|| (closeTrayTimeH == 1 && UpdateTimeH == 1)) {
							if (volume > 0 && volume != lastvolume) {
								newupdatetime = UpdateTimeH + ":00:00";
							}
						} else if (closeTrayTimeH == 2 && UpdateTimeH == 2 && UpdateTimeM >= 30) {
							if (volume > 0 && volume != lastvolume) {
								newupdatetime = UpdateTimeH + ":30:00";
							}
						} else if (UpdateTimeM < 9) {
							if (volume > 0 && volume != lastvolume) {
								newupdatetime = UpdateTimeH + ":0" + (UpdateTimeM + 1) + ":00";
							}
						} else if (UpdateTimeM < 59) {
							if (volume > 0 && volume != lastvolume) {
								newupdatetime = UpdateTimeH + ":" + (UpdateTimeM + 1) + ":00";
							}
						} else if (UpdateTimeM == 59 && UpdateTimeH < 23) {
							if (volume > 0 && volume != lastvolume) {
								newupdatetime = (UpdateTimeH + 1) + ":00:00";
							}
						} else if (UpdateTimeM == 59 && UpdateTimeH == 23) {
							if (volume > 0 && volume != lastvolume) {
								newupdatetime = "00:00:00";
							}
						}
					}
					if (newupdatetime==null) {
						newupdatetime = lastnewupdatetime;
					}

					if (lastnewupdatetime == null) {
						k5Record = new K5Record(); 
						k5Record = minData1Copy(k5Record, tradedate, newupdatetime, currentprice, volume, openinterest);
						close = currentprice;
						high = currentprice;
						low = currentprice;
					} else if ((!lastnewupdatetime.equals(newupdatetime)) && newupdatetime != null) {
						Map map=new HashMap();
						String contract=patharray[i].getName().replace(".zip", "").toLowerCase();
						if (breed.equals("OI1")) {
							breed="OI";
							contract=contract.replace("RO", "OI");
						}
						if (breed.equals("WH2")) {
							breed="WH";
							contract=contract.replace("WS", "WH");
						}
						if (breed.equals("RI3")) {
							breed="RI";
							contract=contract.replace("ER", "RI");
						}
						if (breed.equals("ZC4")) {
							breed="ZC";
							contract=contract.replace("TC", "ZC");
						}
						if (breed.equals("MA5")) {
							breed="MA";
							contract=contract.replace("ME", "MA");
						}
						map.put("table", "k1_"+breed.toLowerCase()); 
						map.put("id", id);
						map.put("contract",  contract.toLowerCase());
						map.put("tradedate", k5Record.getTradedate());
						map.put("tradetime", k5Record.getTradetime());
						map.put("open",k5Record.getOpen() );
						map.put("close", k5Record.getClose());
						map.put("high", k5Record.getHigh());
						map.put("low", k5Record.getLow());
						map.put("volume", k5Record.getVolume());
						map.put("openinterest", k5Record.getOpeninterest()); 
						/*Integer maxid=futMapper.getk1MaxId(map);
						if (maxid==null) {
							futMapper.addFut(map);  
						}else if (maxid<id) {
							futMapper.addFut(map);  
						} */
						futMapper.addFut(map);  
						k5Record = null;
						k5Record = new K5Record(); 
						k5Record = minData1Copy(k5Record, tradedate, newupdatetime, currentprice, volume, openinterest);
						close = currentprice;
						high = currentprice;
						low = currentprice;
						id=id+1;
					}
					if (lastnewupdatetime != null && newupdatetime.equals(lastnewupdatetime)) {
						if (volume > 0 && volume != lastvolume) {
							close = currentprice;
							if (currentprice > high) {
								high = currentprice;
							}
							if (currentprice < low) {
								low = currentprice;
							}
							k5Record.setClose(currentprice);
							k5Record.setHigh(high);
							k5Record.setLow(low);
							k5Record.setVolume(volume);
							k5Record.setOpeninterest(openinterest);
						}
					}
					lastnewupdatetime = newupdatetime;
					lastvolume = volume;  
				} 
				
			}
		}

		return null;
	}
	
	// 赋值
	public static K5Record minData1Copy(K5Record record, String tradedate, String tradetime, double currentprice,
			double volume, double openinterest) {
		record.setTradedate(tradedate);
		record.setTradetime(tradetime);
		record.setOpen(currentprice);
		record.setClose(currentprice);
		record.setHigh(currentprice);
		record.setLow(currentprice);
		record.setVolume(volume);
		record.setOpeninterest(openinterest); 
		return record;
	}
	
	
	
	
	
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/createdeletetable")
	@ResponseBody
	public String createdeletetable(@RequestParam("type") String type) throws ParseException, IOException, InterruptedException {
		List<String> breeds = otherMapper.getAllBreeds();
		for (int i = 0; i < breeds.size(); i++) {
			String breed = breeds.get(i).toLowerCase();
			Map map = new HashMap();
			map.put("table", "k1_" + breed);
			if (type.equals("createtable")) {
				futMapper.createtable(map);
			}else if(type.equals("deletetable")){
				futMapper.deletetable(map);
			}
			
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/deletek1allbreedsdate")
	@ResponseBody
	public String deletek1allbreedsdate() throws ParseException, IOException, InterruptedException {
		List<String> breeds = otherMapper.getAllBreeds();
		for (int i = 0; i < breeds.size(); i++) {
			Map map = new HashMap();
			map.put("table", "k1_" + breeds.get(i));
			futMapper.deletek1allbreedsdate(map);
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/addBreedsInitialPrice")
	@ResponseBody
	public String addBreedsInitialPrice() throws ParseException, IOException, InterruptedException {
		List<String> breeds = otherMapper.getAllBreeds();
		for (int i = 0; i < breeds.size(); i++) {
			String breed = breeds.get(i);
			Map map = new HashMap<>();
			map.put("sqltable", "d1");
			map.put("breed", breed + "2%");
			List<K5Record> k5Records = futMapper.getK5RecordsInitialPrice(map);
			String lasttradedate = null;
			double opensum = 0;
			double opentime = 0;
			for (int j = 0; j < 100; j++) {
				K5Record record = k5Records.get(j);
				String tradedate = record.getTradedate();
				double open = record.getOpen();
				if (lasttradedate != null && !lasttradedate.equals(tradedate)) {
					double averageopen = opensum / opentime;
					Map initalPricemap = new HashMap<>();
					initalPricemap.put("breed", breed);
					initalPricemap.put("initialprice", averageopen);
					otherMapper.updateBreedsInitialPrice(initalPricemap);
					break;
				}
				opensum = opensum + open;
				opentime = opentime + 1;
				lasttradedate = tradedate;
			}
		}
		return null;
	}

	@RequestMapping(value = "/getfunctionindexs")
	@ResponseBody
	public String getfutureindexs(@RequestParam("cycle") String cycle)
			throws IOException, InterruptedException, ParseException {
		return null;
	}

}
