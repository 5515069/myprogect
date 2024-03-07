package retunInfo;

public class retunPricePoints {

	/**
	 * 返回品种点差
	 * @param breed
	 * @returnSSS
	 */
	public static double getPricePoints(String breed) {
		breed=breed.toUpperCase();
		if (breed.equals("AU")) {
			return 0.02;
		} else if (breed.equals("EC") || breed.equals("SC")) {
			return 0.1;
		} else if (breed.equals("TC") || breed.equals("ZC")) {
			return 0.2;
		} else if (breed.equals("FB") || breed.equals("J") || breed.equals("JM") || breed.equals("I")) {
			return 0.5;
		} else if (breed.equals("P") || breed.equals("PF") || breed.equals("PK") || breed.equals("PX")
				|| breed.equals("SM") || breed.equals("SP") || breed.equals("TA")) {
			return 2;
		} else if (breed.equals("AL") || breed.equals("BR") || breed.equals("CF") || breed.equals("CJ")
				|| breed.equals("CY") || breed.equals("LH") || breed.equals("NR") || breed.equals("PB")
				|| breed.equals("RU") || breed.equals("SF") || breed.equals("SI") || breed.equals("SS")
				|| breed.equals("ZN") || breed.equals("L")) {
			return 5;
		} else if (breed.equals("BC") || breed.equals("CU") || breed.equals("NI") || breed.equals("SN")) {
			return 10;
		} else if (breed.equals("LC")) {
			return 50;
		}
		return 1;
	}
}
