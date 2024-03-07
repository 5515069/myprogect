package com.zhengyuan.liunao.controller.future;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.zhengyuan.liunao.entity.EntDealRecord;
import com.zhengyuan.liunao.entity.EntShape_1;
import com.zhengyuan.liunao.mapper.FutMapper;
import com.zhengyuan.liunao.mapper.OtherMapper;
import com.zhengyuan.liunao.mapper.ShapeMapper;
import com.zhengyuan.liunao.target.TargetDealRecord;
import com.zhengyuan.liunao.utils.K5Record;
import com.zhengyuan.liunao.utils.ReadCsv;
import com.zhengyuan.liunao.utils.ReadFiles;
import com.zhengyuan.liunao.utils.ReadK5Csv;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/Sys")
@Api("FutrueDateDeal相关api")
@Slf4j
public class FutrueDateDealContller extends Thread {
	@Autowired
	FutMapper futMapper;
	@Autowired
	OtherMapper mapper;
	@Autowired
	ShapeMapper shapeMapper;

	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	@RequestMapping(value = "/test")
	@ResponseBody
	public void test() throws IOException, InterruptedException {
		long timestamp = System.currentTimeMillis();
		for (int w = 0; w < 20; w++) {   
				Map map = new HashMap();
				map.put("breed", "k5_a");
				List<TargetDealRecord> list = futMapper.getTarDealRecords(map);
 
		}
		long timestamp2 = System.currentTimeMillis();
		System.out.println(timestamp2-timestamp);
		
	}

	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	@RequestMapping(value = "/getgaodidian")
	@ResponseBody
	public void getgaodidian() throws IOException, InterruptedException {
		List<String> breeds = mapper.getBreeds();
		int sumid = 0;
		for (int i = 0; i < breeds.size(); i++) {
			String breed = breeds.get(i);
			Map map = new HashMap();
			map.put("breed", "k5_" + breeds.get(i).toLowerCase());
			List<TargetDealRecord> list = futMapper.getTarDealRecords(map);
			String lastcontract = null;
			for (int j = 0; j < list.size(); j++) {
				TargetDealRecord record = list.get(j);
				if (!record.getContract().equals(lastcontract)) {
					Map temMap = new HashMap();
					temMap.put("breed", "k5_" + breeds.get(i).toLowerCase());
					temMap.put("contract", record.getContract());
					List<TargetDealRecord> temList = futMapper.getTartDealRecordsByContract(temMap);
					for (int k = 30; k < temList.size() - 30; k++) {
						TargetDealRecord temRecord = temList.get(k);
						boolean highLow = true;
						for (int w = k - 30; w <= k + 30; w++) {
							if (w == k) {
								continue;
							}
							TargetDealRecord temRecord60 = temList.get(w);
							if (temRecord.getHigh() < temRecord60.getHigh()) {
								highLow = false;
								break;
							}
						}
						if (highLow == true) {
							Map mapaddshape = new HashMap();
							mapaddshape.put("id", ++sumid);
							mapaddshape.put("contract", record.getContract());
							mapaddshape.put("tradedate", temRecord.getTradedate());
							mapaddshape.put("tradetime", temRecord.getTradetime());
							mapaddshape.put("idsite", record.getId() + k);
							mapaddshape.put("realsite", k + 1);
							mapaddshape.put("highlow", "high");
							shapeMapper.addshape1(mapaddshape);
						}
						highLow = true;
						for (int w = k - 30; w <= k + 30; w++) {
							if (w == k) {
								continue;
							}
							TargetDealRecord temRecord60 = temList.get(w);
							if (temRecord.getLow() > temRecord60.getLow()) {
								highLow = false;
								break;
							}
						}
						if (highLow == true) {
							Map mapaddshape = new HashMap();
							mapaddshape.put("id", ++sumid);
							mapaddshape.put("contract", record.getContract());
							mapaddshape.put("tradedate", temRecord.getTradedate());
							mapaddshape.put("tradetime", temRecord.getTradetime());
							mapaddshape.put("idsite", record.getId() + k);
							mapaddshape.put("realsite", k + 1);
							mapaddshape.put("highlow", "low");
							shapeMapper.addshape1(mapaddshape);
						}
					}

				}
				lastcontract = record.getContract();
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/zd1all")
	@ResponseBody
	public void addFutD1() throws IOException, InterruptedException {
		String folderfile = "D:\\zd1";
		File[] folders = ReadFiles.getAllFolders(folderfile);
		for (int i = 0; i < folders.length; i++) {
			String breedName = folders[i].getName();
			System.out.println(breedName);
			List fileList = ReadFiles.getAllFiles(folderfile + "\\" + breedName);
			for (int j = 0; j < fileList.size(); j++) {
				String fileName = fileList.get(j).toString().replace(folderfile + "\\" + breedName + "\\", "")
						.replace(".csv", "");
				List<EntDealRecord> list = ReadCsv.readCsv(fileList.get(j).toString());
				for (int k = 0; k < list.size(); k++) {
					EntDealRecord dealRecord = list.get(k);
					Map map = new HashMap();
					map.put("breedName", "d1");
					map.put("contract", fileName);
					map.put("tradedate", dealRecord.getTradingDay());
					map.put("tradetime", dealRecord.getUpdateTime());
					map.put("open", dealRecord.getOpen());
					map.put("close", dealRecord.getClose());
					map.put("high", dealRecord.getHigh());
					map.put("low", dealRecord.getLow());
					map.put("volume", dealRecord.getVolume());
					map.put("openinterest", dealRecord.getOpenInterest());
					map.put("highlimit", dealRecord.getUpperLimitPrice());
					map.put("lowlimit", dealRecord.getLowerLimitPrice());
					if (futMapper.addFut(map) <= 0) {
						System.out.println(fileList.get(i).toString() + ":" + k);
					}
					map = null;
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/addFutK1")
	@ResponseBody
	public void addFutK1() throws IOException, InterruptedException {
		String folderfile = "C:\\Users\\Administrator\\Desktop\\zk1";
		File[] folders = ReadFiles.getAllFolders(folderfile);
		for (int i = 0; i < folders.length; i++) {
			String breedName = folders[i].getName();
			System.out.println(breedName);
			List fileList = ReadFiles.getAllFiles(folderfile + "\\" + breedName);
			if (breedName.toLowerCase().compareTo("m") < 0 || breedName.toLowerCase().compareTo("ru") > 0) {
				continue;
			}
			int id=1;
			for (int j = 0; j < fileList.size(); j++) {
				String fileName = fileList.get(j).toString().replace(folderfile + "\\" + breedName + "\\", "")
						.replace(".csv", "");
				List<EntDealRecord> list = ReadCsv.readCsv(fileList.get(j).toString());
				for (int k = 0; k < list.size(); k++) {
					EntDealRecord dealRecord = list.get(k);
					Map map = new HashMap();
					map.put("id",id++);
					map.put("table", "k1_" + breedName.toLowerCase());
					map.put("contract", fileName.toLowerCase());
					map.put("tradedate", dealRecord.getTradingDay());
					map.put("tradetime", dealRecord.getUpdateTime());
					map.put("open", dealRecord.getOpen());
					map.put("close", dealRecord.getClose());
					map.put("high", dealRecord.getHigh());
					map.put("low", dealRecord.getLow());
					map.put("volume", dealRecord.getVolume());
					map.put("openinterest", dealRecord.getOpenInterest()); 
					if (futMapper.addFut(map) <= 0) {
						System.out.println(fileList.get(i).toString() + ":" + k);
					}
					map = null;
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/addFutK5_1")
	@ResponseBody
	public void addFutK5_1() throws IOException, InterruptedException {
		String folderfile = "D:\\zk5";
		File[] folders = ReadFiles.getAllFolders(folderfile);
		for (int i = 0; i < folders.length; i++) {
			String breedName = folders[i].getName();
			System.out.println(breedName);
			if (breedName.toLowerCase().compareTo("a") < 0 || breedName.toLowerCase().compareTo("bu") > 0) {
				continue;
			}
			Map mapdelete = new HashMap();
			mapdelete.put("breedName", "k5_" + breedName.toLowerCase());
			futMapper.deleteK5(mapdelete);
			sleep(1000 * 10);
			List fileList = ReadFiles.getAllFiles(folderfile + "\\" + breedName);
			int id = 0;
			for (int j = 0; j < fileList.size(); j++) {
				String fileName = fileList.get(j).toString().replace(folderfile + "\\" + breedName + "\\", "")
						.replace(".csv", "");
				List<EntDealRecord> list = ReadCsv.readCsv(fileList.get(j).toString());
				for (int k = 0; k < list.size(); k++) {
					EntDealRecord dealRecord = list.get(k);
					Map map = new HashMap();
					id++;
					map.put("id", id);
					map.put("breedName", "k5_" + breedName.toLowerCase());
					map.put("contract", fileName);
					map.put("tradedate", dealRecord.getTradingDay());
					map.put("tradetime", dealRecord.getUpdateTime());
					map.put("open", dealRecord.getOpen());
					map.put("close", dealRecord.getClose());
					map.put("high", dealRecord.getHigh());
					map.put("low", dealRecord.getLow());
					map.put("volume", dealRecord.getVolume());
					map.put("openinterest", dealRecord.getOpenInterest());
					map.put("highlimit", dealRecord.getUpperLimitPrice());
					map.put("lowlimit", dealRecord.getLowerLimitPrice());
					if (futMapper.addFut(map) <= 0) {
						System.out.println(fileList.get(i).toString() + ":" + k);
					}
					map = null;
				}
			}
		}
	}
 
 
 
}
