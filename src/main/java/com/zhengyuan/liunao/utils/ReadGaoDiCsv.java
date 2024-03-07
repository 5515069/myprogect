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

 
 

public class ReadGaoDiCsv {
	
	 
	public static List<GaoDiRecord> readIndexsCsv(String filePath) throws IOException, InterruptedException {
		List<GaoDiRecord> indexs = new ArrayList<GaoDiRecord>();
		GaoDiRecord record = null;
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
				if (line.contains("12345")) {
					continue;
				}
				record = new GaoDiRecord();
				lineWords = line.split(",");
				record.setContract(lineWords[0]); 
				record.setTopprice(Double.valueOf(lineWords[5])); 
				record.setTopsite(Integer.valueOf(lineWords[12]));
				record.setHightouchsite(Integer.valueOf(lineWords[10]));
				record.setLowtouchsite(Integer.valueOf(lineWords[11]));
				record.setProfitlevel(Double.valueOf(lineWords[8])); 
				indexs.add(record);
				record = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return indexs;
	}
	
	 

}
