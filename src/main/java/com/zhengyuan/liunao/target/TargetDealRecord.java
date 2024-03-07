package com.zhengyuan.liunao.target;

public class TargetDealRecord {
  
	private int id;
	private String contract;
	private String tradedate;
	private String tradetime;
	private double open;
	private double close;
	private double high;
	private double low;
	private double volume;
	private double openinterest;
	private double highlimit;
	private double lowlimit; 
	
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
	public double getVolume() {
		return volume;
	}
	public void setVolume(double volume) {
		this.volume = volume;
	}
	public double getOpeninterest() {
		return openinterest;
	}
	public void setOpeninterest(double openinterest) {
		this.openinterest = openinterest;
	}
	public double getHighlimit() {
		return highlimit;
	}
	public void setHighlimit(double highlimit) {
		this.highlimit = highlimit;
	}
	public double getLowlimit() {
		return lowlimit;
	}
	public void setLowlimit(double lowlimit) {
		this.lowlimit = lowlimit;
	}
	
}
