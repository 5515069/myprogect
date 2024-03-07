package retunInfo;

public class returnBreedName {

	
	public static String getPricePoints(String fileName) {   
		String breedName = null;//品种结束日期
		if (fileName.length() == 7) {
			breedName = fileName.substring(0, 1);
		} else {
			breedName = fileName.substring(0, 2);
		} 
		return breedName;
	}
	
}
