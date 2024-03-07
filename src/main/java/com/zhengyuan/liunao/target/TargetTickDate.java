package com.zhengyuan.liunao.target;
 

public class TargetTickDate {

	private int id;
	private String contract;
	private String tradedate;
	private String tradetime;
	private double currentprice;
	private double bidprice;
	private double bidvolume;
	private double askprice;
	private double askvolume;
	private double volume;
	private double openinterest;
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
	public double getCurrentprice() {
		return currentprice;
	}
	public void setCurrentprice(double currentprice) {
		this.currentprice = currentprice;
	}
	
	public double getBidprice() {
		return bidprice;
	}
	public void setBidprice(double bidprice) {
		this.bidprice = bidprice;
	}
	public double getBidvolume() {
		return bidvolume;
	}
	public void setBidvolume(double bidvolume) {
		this.bidvolume = bidvolume;
	}
	public double getAskprice() {
		return askprice;
	}
	public void setAskprice(double askprice) {
		this.askprice = askprice;
	}
	public double getAskvolume() {
		return askvolume;
	}
	public void setAskvolume(double askvolume) {
		this.askvolume = askvolume;
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
	
	
}
