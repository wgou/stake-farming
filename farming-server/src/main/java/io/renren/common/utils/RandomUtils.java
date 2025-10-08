package io.renren.common.utils;

import java.util.Random;

public class RandomUtils {

	
	private static final String[] Number = new String[] {"0","1","2","3","4","5","6","7","8","9"};
	private static final String[] Lowercase = new String[] {"a","b","c","d","e","f","g","h","i","j","k","l","m","o","p","r","s","t","u","v","w","x","y","z"};
	private static final String[] Uppercase = new String[] {"A","B","C","D","E","F","G","H","I","J","K","L","M","O","P","R","S","T","U","V","W","X","Y","Z"};
	private static final String[] SpecialChar = new String[] {"@","#","$","%","&"};
	public static String randomPassWord() {
		String uppercase =Uppercase[new Random().nextInt(Uppercase.length)]; 
		StringBuffer sb = new StringBuffer(uppercase);
		for(int i = 0;i<3;i++) {
			int idx = new Random().nextInt(Lowercase.length);
			sb.append(Lowercase[idx]);
		}
		sb.append(SpecialChar[new Random().nextInt(SpecialChar.length)]);
		for(int i = 0;i<3;i++) {
			int idx = new Random().nextInt(Number.length);
			sb.append(Number[idx]);
		}
		return sb.toString();
	}
	
	public static String randomEmail() {
		String uppercase =Uppercase[new Random().nextInt(Uppercase.length)]; 
		StringBuffer sb = new StringBuffer(uppercase);
		for(int i = 0;i<2;i++) {
			int idx = new Random().nextInt(Lowercase.length);
			sb.append(Lowercase[idx]);
		}
		for(int i = 0;i<5;i++) {
			int idx = new Random().nextInt(Number.length);
			sb.append(Number[idx]);
		}
		return sb.toString();
	}
	
	public static Integer random(int len) {
		return new Random().nextInt(len);
	}
	
	 
}
