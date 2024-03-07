package retunInfo;

import java.time.LocalDate;

public class returnBreedClosingDate {
	 
	
	public static String getCloseDate(String contract,int n) {   
		contract=  contract.replaceAll("[a-zA-Z]", "");
		contract=contract.substring(0,4)+"-"+contract.substring(4,6)+"-"; 
		LocalDate localDate = LocalDate.parse(contract+"15");  // 解析日期字符串为LocalDate对象 
		contract = localDate.minusDays(n).toString();
		return contract.replace("-", "");
	}
	
	
	public static String getPricePoints(String fileName) {  
		String breedName=fileName;
		String breedenddate=null; 
		int y = 0;//年
		int m = 0;//月
		String breedrealenddate = null;//品种结束日期
		if (breedName.length() == 7) {
			breedName = breedName.substring(0, 1);
		} else {
			breedName = breedName.substring(0, 2);
		}
		breedenddate=fileName.replace(breedName, "");
		y = Integer.valueOf(breedenddate.substring(0, 4));
		m = Integer.valueOf(breedenddate.substring(4, 6));
		if (m == 1) {
			y = y - 1;
			m = 12;
		} else {
			m = m - 1;
		}
		if (m<10) {
			breedrealenddate = y + "0" + m + "23";
		}else {
			breedrealenddate = y + "" + m + "23";
		}  
		return breedrealenddate;
	}
	public static String getClosingDate(String fileName) {  
		String breedName=fileName;
		String breedenddate=null; 
		int y = 0;//年
		int m = 0;//月
		String breedrealenddate = null;//品种结束日期
		if (breedName.length() == 7) {
			breedName = breedName.substring(0, 1);
		} else {
			breedName = breedName.substring(0, 2);
		}
		breedenddate=fileName.replace(breedName, "");
		y = Integer.valueOf(breedenddate.substring(0, 4));
		m = Integer.valueOf(breedenddate.substring(4, 6));
		if (m == 2) {
			y = y - 1;
			m = 12;
		} else if (m==1) {
			y=y-1;
			m = 11;
		}else {
			m=m-2;
		} 
		if (m<10) {
			breedrealenddate = y + "0" + m + "01";
		}else {
			breedrealenddate = y + "" + m + "01";
		}  
		return breedrealenddate;
	}
}
