package com.zhengyuan.liunao.controller.future;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.zhengyuan.liunao.entity.EntBreed;
import com.zhengyuan.liunao.mapper.FutMapper;
import com.zhengyuan.liunao.mapper.OtherMapper;
import com.zhengyuan.liunao.mapper.ShapeMapper;
import com.zhengyuan.liunao.target.TargetDealRecord;
import com.zhengyuan.liunao.target.TargetShape1;
import com.zhengyuan.liunao.utils.GaoDiRecord;
import com.zhengyuan.liunao.utils.K5Record;
import com.zhengyuan.liunao.utils.ReadGaoDiCsv;
import com.zhengyuan.liunao.utils.ReadK5Csv;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/Sys")
@Api("FutrueDateDeal相关api")
@Slf4j
public class OtherContller {

	@Autowired
	OtherMapper mapper;
	@Autowired
	FutMapper futMapper;
	@Autowired
	ShapeMapper shapeMapper;
	@Autowired
	OtherMapper otherMapper;

	 
	
	
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	@RequestMapping(value = "/addk5vix")
	@ResponseBody
	public void addk5vix() throws ParseException, IOException, InterruptedException {

		String pathzong = "D:\\zk5";
		File filezong = new File(pathzong);
		File[] arrayzong = filezong.listFiles();
		String breedname = null;
		List<String> tradetimes = otherMapper.gettradetimesbyk5vix();
		for (int ww = 106; ww < tradetimes.size(); ww=ww+5) {
			String tradetimestand1=tradetimes.get(ww);
			double totaltimes = 0;
			double average = 0; 
			
			String tradetimestand2=tradetimes.get(ww+1);
			double totaltimes2 = 0;
			double average2 = 0; 
			
			String tradetimestand3=tradetimes.get(ww+2);
			double totaltimes3 = 0;
			double average3 = 0; 
			
			String tradetimestand4=tradetimes.get(ww+3);
			double totaltimes4 = 0;
			double average4 = 0; 
			
			String tradetimestand5=tradetimes.get(ww+4);
			double totaltimes5 = 0;
			double average5 = 0; 
			for (int k = 0; k < arrayzong.length; k++) {
				breedname = arrayzong[k].getName();
				System.out.println(breedname);
				List fileList = getAllFiles(new File(pathzong + "\\" + breedname));
				for (int i = 0; i < fileList.size(); i++) {
					String fileName = fileList.get(i).toString().replace(pathzong + "\\" + breedname + "\\", "");
					List<K5Record> list = null;
					list = ReadK5Csv.readCsv(fileList.get(i).toString());
					for (int j = 0; j < list.size(); j++) {
						K5Record dealRecordk5 = list.get(j);
						String tradetime = dealRecordk5.getTradetime();
						double open = dealRecordk5.getOpen();
						double close = dealRecordk5.getClose();
						double high = dealRecordk5.getHigh();
						double low = dealRecordk5.getLow();
						double openinterest = dealRecordk5.getOpeninterest();
						double highlimit = dealRecordk5.getHighlimit();
						double lowlimit = dealRecordk5.getLowlimit();
						if (high == 0 || low == 0 || openinterest<200) {
							continue;
						}
						if (tradetime.equals(tradetimestand1)) {
							totaltimes++;
							average = average + (high - low) / low * 10000;
						}
						if (tradetime.equals(tradetimestand2)) {
							totaltimes2++;
							average2 = average2 + (high - low) / low * 10000;
						}
						if (tradetime.equals(tradetimestand3)) {
							totaltimes3++;
							average3 = average3 + (high - low) / low * 10000;
						}
						if (tradetime.equals(tradetimestand4)) {
							totaltimes4++;
							average4= average4 + (high - low) / low * 10000;
						}
						if (tradetime.equals(tradetimestand5)) {
							totaltimes5++;
							average5= average5 + (high - low) / low * 10000;
						}
					} 
				}
			}
			Map map = new HashMap();
			map.put("totaltimes", totaltimes);
			map.put("vix", average/totaltimes);
			map.put("tradetime", tradetimestand1);
			otherMapper.updatek5vix(map);
			
			Map map2 = new HashMap();
			map2.put("totaltimes", totaltimes2);
			map2.put("vix", average2/totaltimes2);
			map2.put("tradetime", tradetimestand2);
			otherMapper.updatek5vix(map2);
			
			Map map3 = new HashMap();
			map3.put("totaltimes", totaltimes3);
			map3.put("vix", average3/totaltimes3);
			map3.put("tradetime", tradetimestand3);
			otherMapper.updatek5vix(map3);
			
			Map map4 = new HashMap();
			map4.put("totaltimes", totaltimes4);
			map4.put("vix", average4/totaltimes4);
			map4.put("tradetime", tradetimestand4);
			otherMapper.updatek5vix(map4);
			
			Map map5 = new HashMap();
			map5.put("totaltimes", totaltimes5);
			map5.put("vix", average5/totaltimes5);
			map5.put("tradetime", tradetimestand5);
			otherMapper.updatek5vix(map5);
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/addgaodi1")
	@ResponseBody
	public void addgaodi1() throws ParseException, IOException, InterruptedException {
		int id = 1;

		List<GaoDiRecord> gaoDiRecords = ReadGaoDiCsv
				.readIndexsCsv("C:\\Users\\86180\\Desktop\\common\\all0_100000.csv");
		for (int j = 0; j < gaoDiRecords.size(); j++) {
			GaoDiRecord record = gaoDiRecords.get(j);
			Map map = new HashMap();
			map.put("id", id++);
			map.put("contract", record.getContract());
			map.put("topprice", record.getTopprice());
			map.put("topsite", record.getTopsite());
			map.put("hightouchsite", record.getHightouchsite());
			map.put("lowtouchsite", record.getLowtouchsite());
			map.put("profitlevel", record.getProfitlevel());
			otherMapper.addploygaodi0(map);
			map = null;
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/updateShpePrices")
	@ResponseBody
	public void updateShpePrices() throws ParseException {
		List<TargetShape1> list = shapeMapper.getTargetshape1orderbyid();
		for (int i = 0; i < list.size(); i++) {
			TargetShape1 shape1 = list.get(i);
			Map map = new HashMap();
			map.put("breed", "k5_" + shape1.getContract().replaceAll("\\d", ""));
			map.put("id", shape1.getIdsite());
			TargetDealRecord record = futMapper.getK5ById(map);
			Map mapUpdate = new HashMap();
			mapUpdate.put("id", shape1.getId());
			if (shape1.getHighlow().endsWith("high")) {
				mapUpdate.put("topprice", record.getHigh());
			} else {
				mapUpdate.put("topprice", record.getLow());
			}
			mapUpdate.put("openinterest", record.getOpeninterest());

			otherMapper.updateShpePrices(mapUpdate);

		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/addk5sum")
	@ResponseBody
	public void addk5sum() throws ParseException {
		List<String> list = futMapper.getTradedate();
		List<String> breeds = otherMapper.getBreeds();
		double k5sum = 0;
		for (int i = 0; i < list.size(); i++) {

			for (int j = 0; j < breeds.size(); j++) {
				Map map = new HashMap();
				map.put("breed", "k5_" + breeds.get(j).toLowerCase());
				map.put("tradedate", list.get(i));
				k5sum = k5sum + futMapper.getSizeByTradedat(map);
			}

			Map map = new HashMap();
			map.put("id", i + 1);
			map.put("tradedate", list.get(i));
			map.put("k5sum", k5sum);
			otherMapper.addk5sum(map);
			k5sum = 0;
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/addodds")
	@ResponseBody
	public void addodds() throws ParseException {
		List<TargetShape1> targetShape1s = shapeMapper.getTargetshape1();
		String lasttradedat = null;
		double hightimes = 0;
		double lowtimes = 0;
		double times = 0;
		double totaltimes = 0;
		int id = 1;
		for (int i = 0; i < targetShape1s.size(); i++) {
			TargetShape1 shape1 = targetShape1s.get(i);
			if (lasttradedat != null && !(lasttradedat.equals(shape1.getTradedate()))) {
				Map mapk5sum = new HashMap();
				mapk5sum.put("tradedate", lasttradedat);
				totaltimes = totaltimes + otherMapper.getSizeByTradedat(mapk5sum);
				Map map = new HashMap();
				map.put("id", id++);
				map.put("tradedate", lasttradedat);
				map.put("hightimes", hightimes);
				map.put("lowtimes", lowtimes);
				map.put("times", times);
				map.put("totaltimes", totaltimes);
				map.put("highodds", hightimes / totaltimes);
				map.put("lowodds", lowtimes / totaltimes);
				map.put("totalodds", times / totaltimes);
				map.put("shape", "shape1");
				otherMapper.addoddsshpe(map);
				hightimes = 0;
				lowtimes = 0;
				times = 0;
				totaltimes = 0;
			}
			times++;
			if (shape1.getHighlow().equals("high")) {
				hightimes++;
			} else if (shape1.getHighlow().equals("low")) {
				lowtimes++;
			}
			lasttradedat = shape1.getTradedate();
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/getk5sum")
	@ResponseBody
	public String getk5sum() throws ParseException {
		int sizes = 0;
		List<String> breeds = mapper.getBreeds();
		for (int i = 0; i < breeds.size(); i++) {
			Map map = new HashMap();
			map.put("breed", "k5_" + breeds.get(i).toLowerCase());
			int breedSizes = futMapper.getSize(map);
			sizes = sizes + breedSizes;
		}
		return String.valueOf(sizes);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/addBreed")
	@ResponseBody
	public void addBreed() throws ParseException {
		String pathzong = "D:\\zd1all";
		File filezong = new File(pathzong);
		File[] arrayzong = filezong.listFiles();
		String breedname = null;
		// 递归
		for (int k = 0; k < arrayzong.length; k++) {
			breedname = arrayzong[k].getName();
			Map map = new HashMap();
			map.put("id", k + 1);
			map.put("breed", breedname);
			map.put("kind", 1);
			mapper.addBreed(map);
		}

	}

	@SuppressWarnings("unchecked")
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
