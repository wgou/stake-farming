package io.renren.common.utils;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import io.renren.common.constant.Constant;

/**
 * 加密工具类 描述:<br/>
 * TODO; <br/>
 * ClassName: AESUtils <br/>
 * date: 2018年1月30日 下午9:34:08 <br/>
 *
 * @version
 */
public class AESUtils {

	/**
	 * 加密类型
	 */
	private static final String AES = "AES";
	


	/**
	 * 按给定的密钥加密数据
	 *
	 * @return
	 */
	public static byte[] encrypt(byte[] src, String key) throws Exception {
		Cipher cipher = Cipher.getInstance(AES);
		SecretKeySpec securekey = new SecretKeySpec(key.getBytes(), AES);
		cipher.init(Cipher.ENCRYPT_MODE, securekey);// 设置密钥和加密形式
		return cipher.doFinal(src);
	}

	/**
	 * 按给定的密钥进行解密
	 *
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] src, String key) throws Exception {
		Cipher cipher = Cipher.getInstance(AES);
		SecretKeySpec securekey = new SecretKeySpec(key.getBytes(), AES);// 设置加密Key
		cipher.init(Cipher.DECRYPT_MODE, securekey);// 设置密钥和解密形式
		return cipher.doFinal(src);
	}

	/**
	 * 二行制转十六进制字符串
	 *
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}

	/**
	 *
	 * hex2byte:(描述这个方法的作用/参数). <br/>
	 *
	 * @author DUDU
	 * @param b
	 * @return
	 */
	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("长度不是偶数");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}


	public final static String decrypt(String data) {
		try {
			return new String(decrypt(hex2byte(data.getBytes()),  Constant.CRYPTKEY));
		} catch (Exception e) {
		}
		return null;
	}


	/**
	 * 按系统给定的密钥进行加密
	 * @param data
	 * @param keuy
	 * @return
	 */
	public final static String encrypt(String data) {
		try {
			return byte2hex(encrypt(data.getBytes(),  Constant.CRYPTKEY));
		} catch (Exception e) {
		}
		return null;
	}
	

}
