package com.zhengyuan.liunao.utils;
 
import java.io.File; 
import java.util.ArrayList;
import java.util.List; 

public class ReadFiles {

	// 读取文件夹集合
	public static File[] getAllFolders(String filePath) {
		File filezong = new File(filePath);
		File[] arrayzong = filezong.listFiles();
		return arrayzong;
	}

	// 读取文件集合
	@SuppressWarnings("rawtypes")
	public static List getAllFiles(String filePath) {
		List fileList = getAllFiles(new File(filePath));
		return fileList;
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
