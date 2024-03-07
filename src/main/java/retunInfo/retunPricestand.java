package retunInfo;

public class retunPricestand {

	public static double getPricePoints(double price,double pricepoints) {
		if (pricepoints==1) {
			return Math.round(price);
		}else if (pricepoints==2) {
			return (Math.round(price)/2)*2.0;
		}else if (pricepoints==5) {
			return (Math.round(price)/5)*5.0;
		}else if (pricepoints==10) {
			return (Math.round(price)/10)*10.0;
		}else if (pricepoints==50) {
			return (Math.round(price)/50)*500;
		}else if (pricepoints==0.02) {
			return (Math.round(price*100)/2)*2/100.0;
		}else if (pricepoints==0.1) {
			return (Math.round(price*10))/10.0;
		}else if (pricepoints==0.2) {
			return (Math.round(price*10)/2)*2/10.0;
		}else if (pricepoints==0.5) {
			return (Math.round(price*10)/5)*5/10.0;
		}
		
		return 0;
	}
}
