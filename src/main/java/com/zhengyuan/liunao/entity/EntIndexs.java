package com.zhengyuan.liunao.entity;

public class EntIndexs {

	private int id;
	private String tradedate;
	private String tradetime; 
	private double indexs;
	private double open;
	private double close;
	private double high;
	private double low;
	private double openinterest;
	private double risefall;
	private String type;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTradedate() {
		return tradedate;
	}
	public void setTradedate(String tradedate) {
		this.tradedate = tradedate;
	}
	public String getTradetime() {
		return tradetime;
	}
	public void setTradetime(String tradetime) {
		this.tradetime = tradetime;
	} 
	
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	
	public double getIndexs() {
		return indexs;
	}
	public void setIndexs(double indexs) {
		this.indexs = indexs;
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
	
	public double getOpeninterest() {
		return openinterest;
	}
	public void setOpeninterest(double openinterest) {
		this.openinterest = openinterest;
	}
	public double getRisefall() {
		return risefall;
	}
	public void setRisefall(double risefall) {
		this.risefall = risefall;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
