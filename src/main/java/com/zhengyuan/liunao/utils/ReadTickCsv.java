package com.zhengyuan.liunao.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.zhengyuan.liunao.target.TargetTickDate; 

public class ReadTickCsv {

	// 读取zip文件夹下的文件
		public static List<TargetTickDate> readziptickcsv(String filePath) {
			List<TargetTickDate> listsum = new ArrayList<TargetTickDate>();
			try (ZipFile zipFile = new ZipFile(filePath);) {
				Enumeration<? extends ZipEntry> entries = zipFile.entries();

				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					if (1 == 1) {

					}
					InputStream stream = zipFile.getInputStream(entry); // 读取文件内容
					listsum = read(stream, listsum);
				}
			} catch (Exception e) {
			}
			return listsum;
		}

		private static List<TargetTickDate> read(InputStream in, List<TargetTickDate> list) {
			TargetTickDate f1 = null;
			String[] lineWords = null;
			try (InputStreamReader reader = new InputStreamReader(in, "UTF-8");
					BufferedReader br = new BufferedReader(reader);) {
				String con = null;
				while ((con = br.readLine()) != null) {
					f1 = new TargetTickDate();
					lineWords = con.split(",");
					f1.setTradedate(lineWords[0]);
					f1.setTradetime(lineWords[2]); 
					f1.setCurrentprice(Double.valueOf(lineWords[4])); 
					f1.setBidprice(Double.valueOf(lineWords[6])); 
					f1.setBidvolume(Double.valueOf(lineWords[7])); 
					f1.setAskprice(Double.valueOf(lineWords[8])); 
					f1.setAskvolume(Double.valueOf(lineWords[9])); 
					f1.setVolume(Double.valueOf(lineWords[5]));
					f1.setOpeninterest(Double.valueOf(lineWords[12]));
					list.add(f1);
					f1 = null;
				}
			} catch (Exception e) {
			}
			return list;
		}

}
