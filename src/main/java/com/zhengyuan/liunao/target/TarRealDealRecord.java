package com.zhengyuan.liunao.target;

/**
 *真实交易记录
 */
public class TarRealDealRecord {
	// 合约
	private String contract;
	// 开仓日期
	private String opendate;
	// 开仓时间
	private String opentime;
	// 平仓日期
	private String closedate;
	// 平仓时间
	private String closetime;
	// 顶点价格
	private double topprice;
	// 开仓价格
	private double openprice;
	// 平仓价格
	private double closepprice;
	// 利润止损线
	private double profitlevel;
	// 关仓间隔
	private double holdtime;
	// 高点位置
	private int hightouchsite;
	// 地点位置
	private int lowtouchsite;
	//顶点位置
	private int topsite;
	// 当前持仓量
	private double OpenInterest;
	// 利润
	private double profit; 
	// 合约开始坐标
	private int contractstartsite;
	// 合约结束坐标
	private int contractendsite;
	//开仓方向 1：买 -1卖
	private int openbs;
	//均线 60
	private double average;
	

	 

	public String getContract() {
		return contract;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}

	public String getOpendate() {
		return opendate;
	}

	public void setOpendate(String opendate) {
		this.opendate = opendate;
	}

	public String getOpentime() {
		return opentime;
	}

	public void setOpentime(String opentime) {
		this.opentime = opentime;
	}

	public String getClosedate() {
		return closedate;
	}

	public void setClosedate(String closedate) {
		this.closedate = closedate;
	}

	public String getClosetime() {
		return closetime;
	}

	 
	public int getTopsite() {
		return topsite;
	}

	public void setTopsite(int topsite) {
		this.topsite = topsite;
	}

	public void setClosetime(String closetime) {
		this.closetime = closetime;
	}

	 
	public double getTopprice() {
		return topprice;
	}

	public void setTopprice(double topprice) {
		this.topprice = topprice;
	}

	 

	public double getOpenprice() {
		return openprice;
	}

	public void setOpenprice(double openprice) {
		this.openprice = openprice;
	}

	 

	public double getClosepprice() {
		return closepprice;
	}

	public void setClosepprice(double closepprice) {
		this.closepprice = closepprice;
	}

	public double getProfitlevel() {
		return profitlevel;
	}

	public void setProfitlevel(double profitlevel) {
		this.profitlevel = profitlevel;
	}

	 

	 
	public double getHoldtime() {
		return holdtime;
	}

	public void setHoldtime(double holdtime) {
		this.holdtime = holdtime;
	}

	public int getHightouchsite() {
		return hightouchsite;
	}

	public void setHightouchsite(int hightouchsite) {
		this.hightouchsite = hightouchsite;
	}

	 

	public int getLowtouchsite() {
		return lowtouchsite;
	}

	public void setLowtouchsite(int lowtouchsite) {
		this.lowtouchsite = lowtouchsite;
	}

	public double getOpenInterest() {
		return OpenInterest;
	}

	public void setOpenInterest(double openInterest) {
		OpenInterest = openInterest;
	}

	 

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	} 

	public int getContractstartsite() {
		return contractstartsite;
	}

	public void setContractstartsite(int contractstartsite) {
		this.contractstartsite = contractstartsite;
	}

	public int getContractendsite() {
		return contractendsite;
	}

	public void setContractendsite(int contractendsite) {
		this.contractendsite = contractendsite;
	}

	public int getOpenbs() {
		return openbs;
	}

	public void setOpenbs(int openbs) {
		this.openbs = openbs;
	}

	public double getAverage() {
		return average;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	 

	
}
