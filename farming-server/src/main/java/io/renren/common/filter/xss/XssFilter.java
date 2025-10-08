/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.common.filter.xss;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import io.renren.common.constant.Constant;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * XSS过滤
 *
 * @author Mark sunlightcs@gmail.com
 */
public class XssFilter implements Filter {

	private static final List<String> EXCLUDE_URLS = Arrays.asList(
		"/sys/login",
		"/sys/user/*",
		"/ws/*",
		"/api/index/rewards",
		"/sys/captcha.jpg",
		"/admin/withdraw/reject",
		"/admin/wallets/sendMessage"
	);

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		// 检查是否是忽略的URL
		String requestURI = httpRequest.getRequestURI();
		boolean isExcluded = false;
		for (String excludeUrl : EXCLUDE_URLS) {
			if (excludeUrl.endsWith("/*")) {
				// 处理通配符匹配
				String prefix = excludeUrl.substring(0, excludeUrl.length() - 2);
				if (requestURI.startsWith(prefix)) {
					isExcluded = true;
					break;
				}
			} else if (excludeUrl.equals(requestURI)) {
				// 精确匹配
				isExcluded = true;
				break;
			}
		}
		
		if(isExcluded) {
			chain.doFilter(request, response);
			return;
		}
		
		String contentType = httpRequest.getContentType();
		String method = httpRequest.getMethod();
		
		// 如果是GET请求，直接使用XssAndSqlRequestWrapper处理参数
		if ("GET".equalsIgnoreCase(method)) {
			XssAndSqlRequestWrapper xssRequest = new XssAndSqlRequestWrapper(httpRequest);
			chain.doFilter(xssRequest, response);
			return;
		}
		
		// 如果是POST请求，检查Content-Type
		if ("POST".equalsIgnoreCase(method)) {
			// 如果是JSON请求，需要特殊处理
			if (contentType != null && (
				contentType.toLowerCase().contains(MediaType.APPLICATION_JSON_VALUE.toLowerCase()) ||
				contentType.toLowerCase().contains("application/json") ||
				contentType.toLowerCase().contains("text/json")
			)) {
				try {
					XssAndSqlRequestWrapper xssRequest = new XssAndSqlRequestWrapper(httpRequest);
					chain.doFilter(xssRequest, response);
					return;
				} catch (Exception e) {
					// 如果处理过程中出现异常，返回原始请求
					chain.doFilter(request, response);
					return;
				}
			}
			// 如果是表单提交，使用XssAndSqlRequestWrapper处理参数
			if (contentType != null && (
				contentType.toLowerCase().contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE.toLowerCase()) ||
				contentType.toLowerCase().contains("application/x-www-form-urlencoded")
			)) {
				XssAndSqlRequestWrapper xssRequest = new XssAndSqlRequestWrapper(httpRequest);
				chain.doFilter(xssRequest, response);
				return;
			}
		}
		
		// 其他情况直接放行
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}