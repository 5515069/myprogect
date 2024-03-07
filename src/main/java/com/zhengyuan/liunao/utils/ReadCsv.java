package com.zhengyuan.liunao.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.zhengyuan.liunao.entity.EntDealRecord;
import com.zhengyuan.liunao.entity.EntIndexs;

 
 

public class ReadCsv {
	
	
	// 读取csvs
	public static List<EntIndexs> readIndexsCsv(String filePath) throws IOException, InterruptedException {
		List<EntIndexs> indexs = new ArrayList<EntIndexs>();
		EntIndexs index = null;
		File csv = new File(filePath);
		csv.setReadable(true);
		csv.setWritable(true);
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			isr = new InputStreamReader(new FileInputStream(csv), "UTF-8");
			br = new BufferedReader(isr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String line = "";
		String[] lineWords = null;
		try {
			while ((line = br.readLine()) != null) { 
				index = new EntIndexs();
				lineWords = line.split(",");
				index.setTradedate(lineWords[0]); 
				index.setIndexs(Double.valueOf(lineWords[3])); 
				index.setOpen(Double.valueOf(lineWords[4]));
				index.setClose(Double.valueOf(lineWords[5]));
				index.setHigh(Double.valueOf(lineWords[6]));
				index.setLow(Double.valueOf(lineWords[7]));
				index.setOpeninterest(Double.valueOf(lineWords[8]));
				indexs.add(index);
				index = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return indexs;
	}
	
	// 读取csvs
	public static List<EntDealRecord> readCsv(String filePath) throws IOException, InterruptedException {
		List<EntDealRecord> list = new ArrayList<EntDealRecord>();
		EntDealRecord mData1 = null;
		File csv = new File(filePath);
		csv.setReadable(true);
		csv.setWritable(true);
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			isr = new InputStreamReader(new FileInputStream(csv), "UTF-8");
			br = new BufferedReader(isr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String line = "";
		String[] lineWords = null;
		ArrayList<String> records = new ArrayList<>();
		try {
			while ((line = br.readLine()) != null) {
				mData1 = new EntDealRecord();
				lineWords = line.split(",");
				mData1.setTradingDay(lineWords[0]);
				mData1.setUpdateTime(lineWords[1]);
				mData1.setOpen(Double.valueOf(lineWords[2]));
				mData1.setClose(Double.valueOf(lineWords[3]));
				mData1.setHigh(Double.valueOf(lineWords[4]));
				mData1.setLow(Double.valueOf(lineWords[5]));
				mData1.setVolume(Double.valueOf(lineWords[6]).intValue());
				mData1.setOpenInterest(Double.valueOf(lineWords[7]).intValue());
				mData1.setUpperLimitPrice(Double.valueOf(lineWords[8]));
				mData1.setLowerLimitPrice(Double.valueOf(lineWords[9]));

				list.add(mData1);
				mData1 = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

}
