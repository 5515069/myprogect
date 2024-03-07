package com.zhengyuan.liunao.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ReadK5Csv {

	public static List<K5Record> readCsv(String filePath) throws IOException, InterruptedException {
		List<K5Record> list = new ArrayList<K5Record>();
		K5Record record = null;
		record = new K5Record();
		record.setTradedate("0");
		record.setTradetime("0");
		record.setOpen(0);
		record.setClose(0);
		record.setHigh(0);
		record.setLow(0);
		record.setVolume(0);
		record.setOpeninterest(0);
		record.setHighlimit(0);
		record.setLowlimit(0);
		list.add(record);
		record = null;
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
				record = new K5Record();
				lineWords = line.split(",");
				record.setTradedate(lineWords[0]);
				record.setTradetime(lineWords[1]);
				record.setOpen(Double.valueOf(lineWords[2]));
				record.setClose(Double.valueOf(lineWords[3]));
				record.setHigh(Double.valueOf(lineWords[4]));
				record.setLow(Double.valueOf(lineWords[5]));
				record.setVolume(Double.valueOf(lineWords[6]).intValue());
				record.setOpeninterest(Double.valueOf(lineWords[7]).intValue());
				record.setHighlimit(Double.valueOf(lineWords[8]));
				record.setLowlimit(Double.valueOf(lineWords[9]));

				list.add(record);
				record = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

}
