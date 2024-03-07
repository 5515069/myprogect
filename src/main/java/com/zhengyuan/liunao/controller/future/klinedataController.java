package com.zhengyuan.liunao.controller.future;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.zhengyuan.liunao.entity.Scores;
import com.zhengyuan.liunao.mapper.FutMapper;
import com.zhengyuan.liunao.service.GcCourseService;
import com.zhengyuan.liunao.tools.Layui;
import com.zhengyuan.liunao.utils.K5Record;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/Sys")
@Api("FutrueDateDeal相关api")
@Slf4j
public class klinedataController {

	@Autowired
	GcCourseService gcCourseService;
	@Autowired
	FutMapper futmapper;
	
	
	@RequestMapping("/test1")
	@ResponseBody
	public String test1() {
				
		return "aaaa"; 
	}
	
	
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	@RequestMapping("/getkdata")
	@ResponseBody
	public String getkdata(@RequestParam("key[cycle]")String cycle,
			@RequestParam("key[breed]")String breed,
			@RequestParam("key[contract]") String contract,
			@RequestParam("key[startdate]") String startdate,
			@RequestParam("key[enddate]") String enddate
			,@RequestParam("limit") String limit, @RequestParam("page") String page ) {
	    System.out.println(cycle);
	    int pagesize = Integer.parseInt(limit);
		int start = (Integer.parseInt(page) - 1) * pagesize; 
		Map map=new HashMap(); 
		if (cycle.equals("d1")) {
			map.put("cycle", cycle);
			map.put("contract", contract+"%"); 
			if (!breed.equals("0")) {
				map.put("contract", breed+"20%");
			} 
			if (!contract.isEmpty()) {
				map.put("contract", contract+"%"); 
			}
		}else {
			map.put("cycle", cycle+"_"+breed);
			map.put("contract", contract+"%"); 
		}   
		map.put("startdate", startdate); 
		map.put("enddate", enddate); 
		map.put("start", start);
		map.put("pagesize", pagesize);
		List<K5Record> k5Records=futmapper.getK5Records(map);
	    int total=futmapper.getK5Recordstotal(map);;
	    Layui l = Layui.data(total, k5Records);
		return JSON.toJSONString(l); 
	}
	
	@RequestMapping("/getGscdata")
	@ResponseBody
	public String getGscdata(@RequestParam("key[coursename]")String coursename,@RequestParam("key[type]") String type,
			@RequestParam("key[grade]")String gcg,@RequestParam("key[cla]")String gcc,@RequestParam("limit") String limit, @RequestParam("page") String page) {
		int lim = Integer.parseInt(limit);
		int start = (Integer.parseInt(page) - 1) * lim;
		List<Scores> data = new ArrayList<>();
		List<Scores> data2 = new ArrayList<>();
		data =  gcCourseService.findScores(coursename, type,gcg, gcc, start,lim);
		data2 = gcCourseService.findAllScores(coursename, type, gcg, gcc);
		int total = data2.size();
		Layui l = Layui.data(total, data);
		return JSON.toJSONString(l);
	}
}
