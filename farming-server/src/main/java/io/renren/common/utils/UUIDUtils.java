
package io.renren.common.utils;

import java.util.UUID;

/**  
 * 随机字符串; <br/>  
 * ClassName: UUIDUtils <br/>  
 * date: 2016年10月20日 下午4:31:58 <br/>  
 * @author  许超(xuchao@retail-tek.com)   
 * @version  
 */
public class UUIDUtils {
	
	/**
	 * 获取32位随机字符串 
	 * @return
	 */
	public static String getUUID32() {
		String uuid = UUID.randomUUID().toString();
		return uuid.replace("-", "");
	}
}

