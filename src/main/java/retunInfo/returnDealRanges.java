package retunInfo;

/**
 *  返回交易范围
 *
 */
public class returnDealRanges {

	//交易开始位置
	public static int getDealStartRanges(int times) {
		if (times==0) {
			return 0; 
		}else if (times==1) {
			return 1; 
		}else if (times==2) {
			return 100; 
		}else if (times==3) {
			return 500; 
		}
		return 0;  
	}
	//交易结束位置
	public static int getDealendRanges(int times) {
		if (times==0) {
			return 100000; 
		}else if (times==1) {
			return 100000; 
		}else if (times==2) {
			return 100000; 
		}else if (times==3) {
			return 1900; 
		}
		return 0;  
	}
}
