package com.zhengyuan.liunao.entity;

/**
 * 
 * 交易记录
 */
public class EntDealRecord {
	public EntDealRecord() {
		super();
	}

	public EntDealRecord(String tradingDay, String updateTime, double open, double close, double high, double low,
			int volume, int openInterest, double upperLimitPrice, double lowerLimitPrice) {
		super();
		TradingDay = tradingDay;
		UpdateTime = updateTime;
		this.open = open;
		this.close = close;
		this.high = high;
		this.low = low;
		Volume = volume;
		OpenInterest = openInterest;
		UpperLimitPrice = upperLimitPrice;
		LowerLimitPrice = lowerLimitPrice;
	}

	private int id;
	private String contract;
	// 交易日期年-月-日
	private String TradingDay;
	// 交易时间时-分-秒
	private String UpdateTime;
	// 开盘价
	private double open;
	// 收盘价
	private double close;
	// 最高价
	private double high;
	// 最低价
	private double low;
	// 当日成交量
	private int Volume;
	// 总持仓量
	private int OpenInterest;
	// 涨停价
	private double UpperLimitPrice;
	// 跌停价
	private double LowerLimitPrice;
	// 临时用字段
	private String fileName;

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContract() {
		return contract;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}

	public String getTradingDay() {
		return TradingDay;
	}

	public void setTradingDay(String tradingDay) {
		TradingDay = tradingDay;
	}

	public String getUpdateTime() {
		return UpdateTime;
	}

	public void setUpdateTime(String updateTime) {
		UpdateTime = updateTime;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public int getVolume() {
		return Volume;
	}

	public void setVolume(int volume) {
		Volume = volume;
	}

	public int getOpenInterest() {
		return OpenInterest;
	}

	public void setOpenInterest(int openInterest) {
		OpenInterest = openInterest;
	}

	public double getUpperLimitPrice() {
		return UpperLimitPrice;
	}

	public void setUpperLimitPrice(double upperLimitPrice) {
		UpperLimitPrice = upperLimitPrice;
	}

	public double getLowerLimitPrice() {
		return LowerLimitPrice;
	}

	public void setLowerLimitPrice(double lowerLimitPrice) {
		LowerLimitPrice = lowerLimitPrice;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
