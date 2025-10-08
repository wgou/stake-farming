package io.renren.modules.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StakeUtils {

	
	public static BigDecimal usdcToEth(BigDecimal usdc,BigDecimal ethPrice) {
		return usdc!=null && usdc.compareTo(BigDecimal.ZERO) > 0 ? 
				usdc.divide(ethPrice, 5,BigDecimal.ROUND_DOWN) : BigDecimal.ZERO;
	}
	
	public static BigDecimal ethToUsdc(BigDecimal eth,BigDecimal ethPrice) {
		return eth!=null && eth.compareTo(BigDecimal.ZERO) > 0 ? 
				eth.multiply(ethPrice).setScale(2,BigDecimal.ROUND_DOWN) : BigDecimal.ZERO;
	}
	
	
	public static BigDecimal mul(BigDecimal a,BigDecimal b) {
		return a.multiply(b).setScale(5, BigDecimal.ROUND_DOWN);
	}
	
	public static BigDecimal div(BigDecimal a,BigDecimal b) {
		return a.divide(b, 5, BigDecimal.ROUND_DOWN);

	}
	
    public static String formatBigDecimal(BigDecimal number) {
        // 定义阈值
        BigDecimal thousand = new BigDecimal(1000);
        BigDecimal million = new BigDecimal(1000000);
        BigDecimal billion = new BigDecimal(1000000000);

        // 判断数字的大小，格式化为带单位的字符串
        if (number.compareTo(billion) >= 0) {
            return number.divide(billion, 2, RoundingMode.HALF_UP).toString() + " B";
        } else if (number.compareTo(million) >= 0) {
            return number.divide(million, 2, RoundingMode.HALF_UP).toString() + " M";
        } else if (number.compareTo(thousand) >= 0) {
            return number.divide(thousand, 2, RoundingMode.HALF_UP).toString() + " K";
        } else {
            return number.setScale(2, RoundingMode.HALF_UP).toString(); // 小于1000则不加单位
        }
    }
	 
}
