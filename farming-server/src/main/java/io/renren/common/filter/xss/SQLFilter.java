/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.common.filter.xss;

import io.renren.common.exception.RRException;
import org.apache.commons.lang.StringUtils;

/**
 * SQL过滤
 *
 * @author Mark sunlightcs@gmail.com
 */
public class SQLFilter {

    /**
     * SQL注入过滤
     * @param str  待验证的字符串
     */
    public static String sqlInject(String str){
        if(StringUtils.isBlank(str)){
            return null;
        }

        // 检查是否是以太坊钱包地址
        if (isEthereumAddress(str)) {
            return str;
        }

        // 检查是否是认证token
        if (isAuthToken(str)) {
            return str;
        }

        //去掉'|"|;|\字符
        str = StringUtils.replace(str, "'", "");
        str = StringUtils.replace(str, "\"", "");
        str = StringUtils.replace(str, ";", "");
        str = StringUtils.replace(str, "\\", "");
        
        //转换成小写
        str = str.toLowerCase();
        //SQL注入拦截 处理wgou
        if (str.indexOf("select") != -1 //
        		|| str.indexOf("master") != -1 //
        		|| str.indexOf("truncate") != -1 
				|| str.indexOf("drop") != -1 //
				|| str.indexOf("alter") != -1 //
				|| str.indexOf("declare") != -1 //
                || str.indexOf("delete") != -1 //
                || str.indexOf("insert") != -1 //
                || str.indexOf("update") != -1 //
                || str.indexOf("into") != -1 //
                || str.indexOf("where") != -1 //
                || str.indexOf("or") != -1 //
                || str.indexOf("and") != -1 //
                || str.indexOf("union") != -1 //
                || str.indexOf('\'') != -1 //
              //  || str.indexOf('=') != -1 //
                || str.indexOf('>') != -1 //
                || str.indexOf('<') != -1 //
                || str.indexOf('&') != -1 //
                || str.indexOf('|') != -1 //
                || str.indexOf('^') != -1 //
            ) {
        	throw new RRException("包含非法字符");
        }
                
//      他这个不全面,在加一些。  
        //非法字符
//        String[] keywords = {"master", "truncate", "insert", "select", "delete", "update", "declare", "alter", "drop"};
//
//        //判断是否包含非法字符
//        for(String keyword : keywords){
//            if(str.indexOf(keyword) != -1){
//                throw new RRException("包含非法字符");
//            }
//        }

        return str;
    }

    /**
     * 检查是否是有效的以太坊钱包地址
     */
    private static boolean isEthereumAddress(String str) {
        // 检查是否以0x开头
        if (!str.startsWith("0x")) {
            return false;
        }
        
        // 检查长度是否为42（0x + 40个十六进制字符）
        if (str.length() != 42) {
            return false;
        }
        
        // 检查剩余字符是否都是有效的十六进制字符
        String hexPart = str.substring(2);
        return hexPart.matches("^[0-9a-fA-F]{40}$");
    }

    /**
     * 检查是否是认证token
     */
    private static boolean isAuthToken(String str) {
        // 检查长度是否在合理范围内（通常token长度在32-512之间）
        if (str.length() < 32 || str.length() > 512) {
            return false;
        }
        
        // 检查是否只包含有效的token字符（字母、数字、特殊字符）
        return str.matches("^[a-zA-Z0-9+/=_-]+$");
    }

    public static void main(String[] args) {
        System.out.println(sqlInject("https://dsxt.net?code=9099"));
    }
}
