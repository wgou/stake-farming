/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.common.filter.xss;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.core.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * XSS过滤处理
 *
 * @author Mark sunlightcs@gmail.com
 */
@Slf4j
public class XssAndSqlRequestWrapper extends HttpServletRequestWrapper {
    //没被包装过的HttpServletRequest（特殊场景，需要自己过滤）
    HttpServletRequest orgRequest;
    //html过滤
    private final static HTMLFilter htmlFilter = new HTMLFilter();

    public XssAndSqlRequestWrapper(HttpServletRequest request) {
        super(request);
        orgRequest = request;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (StringUtils.isNotBlank(value)) {
            // 如果是WebSocket请求，不进行XSS过滤
            if (isWebSocketRequest()) {
                return value;
            }
            
            String xssFiltered = xssEncode(value);
            if (!xssFiltered.equals(value)) {
                log.warn("XSS attempt detected in header: {}, original value: {}", name, value);
            }
            String sqlFiltered = SQLFilter.sqlInject(xssFiltered);
            if (sqlFiltered != null && !sqlFiltered.equals(xssFiltered)) {
                log.warn("SQL injection attempt detected in header: {}, original value: {}", name, value);
            }
            return sqlFiltered;
        }
        return value;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 如果是WebSocket请求，直接返回原始输入流
        if (isWebSocketRequest()) {
            log.debug("WebSocket request detected, skipping XSS filter for input stream");
            return super.getInputStream();
        }

        // 非json类型，直接返回
        if(!isJsonRequest()){
            return super.getInputStream();
        }
        
        // 为空，直接返回
        String json = IOUtils.toString(super.getInputStream(), "utf-8");
        if (StringUtils.isBlank(json)) {
            return super.getInputStream();
        }
        
        try {
            // 尝试解析JSON
            ObjectMapper mapper = new ObjectMapper();
            // 配置ObjectMapper以允许特殊字符
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            
            JsonNode jsonNode = mapper.readTree(json);
            
            // 如果是对象，处理所有字段
            if (jsonNode.isObject()) {
                ObjectNode objectNode = (ObjectNode) jsonNode;
                Iterator<String> fieldNames = objectNode.fieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    JsonNode fieldValue = objectNode.get(fieldName);
                    if (fieldValue.isTextual()) {
                        String value = fieldValue.asText();
                        log.debug("Processing field: {} = {}", fieldName, value);
                        // 检查是否是URL
                        if (value.contains("?") || value.contains("&") || value.contains("=")) {
                            log.debug("URL detected in field: {}", fieldName);
                            // 对URL进行特殊处理，但保持原始格式
                            value = value.replace("\\", "\\\\")
                                      .replace("\"", "\\\"")
                                      .replace("\n", "\\n")
                                      .replace("\r", "\\r")
                                      .replace("\t", "\\t");
                        }
                        // 进行SQL注入检查，但不修改原始值
                        String sqlFiltered = SQLFilter.sqlInject(value);
                        if (sqlFiltered != null && !sqlFiltered.equals(value)) {
                            log.warn("SQL injection attempt detected in field: {}, original value: {}", fieldName, value);
                            value = sqlFiltered;
                        }
                        // 进行XSS过滤，但保持基本格式
                        String xssFiltered = xssEncode(value);
                        if (!xssFiltered.equals(value)) {
                            log.warn("XSS attempt detected in field: {}, original value: {}", fieldName, value);
                            value = xssFiltered;
                        }
                        objectNode.put(fieldName, value);
                    }
                }
                json = objectNode.toString();
            }
            // 如果是数组，处理每个元素
            else if (jsonNode.isArray()) {
                ArrayNode arrayNode = (ArrayNode) jsonNode;
                for (int i = 0; i < arrayNode.size(); i++) {
                    JsonNode element = arrayNode.get(i);
                    if (element.isObject()) {
                        ObjectNode objectNode = (ObjectNode) element;
                        Iterator<String> fieldNames = objectNode.fieldNames();
                        while (fieldNames.hasNext()) {
                            String fieldName = fieldNames.next();
                            JsonNode fieldValue = objectNode.get(fieldName);
                            if (fieldValue.isTextual()) {
                                String value = fieldValue.asText();
                                log.debug("Processing array element field: {} = {}", fieldName, value);
                                // 检查是否是URL
                                if (value.contains("?") || value.contains("&") || value.contains("=")) {
                                    log.debug("URL detected in array element field: {}", fieldName);
                                    value = value.replace("\\", "\\\\")
                                              .replace("\"", "\\\"")
                                              .replace("\n", "\\n")
                                              .replace("\r", "\\r")
                                              .replace("\t", "\\t");
                                }
                                // 进行SQL注入检查
                                String sqlFiltered = SQLFilter.sqlInject(value);
                                if (sqlFiltered != null && !sqlFiltered.equals(value)) {
                                    log.warn("SQL injection attempt detected in array element field: {}, original value: {}", fieldName, value);
                                    value = sqlFiltered;
                                }
                                // 进行XSS过滤
                                String xssFiltered = xssEncode(value);
                                if (!xssFiltered.equals(value)) {
                                    log.warn("XSS attempt detected in array element field: {}, original value: {}", fieldName, value);
                                    value = xssFiltered;
                                }
                                objectNode.put(fieldName, value);
                            }
                        }
                    } else if (element.isTextual()) {
                        String value = element.asText();
                        log.debug("Processing array element: {}", value);
                        // 检查是否是URL
                        if (value.contains("?") || value.contains("&") || value.contains("=")) {
                            log.debug("URL detected in array element");
                            value = value.replace("\\", "\\\\")
                                      .replace("\"", "\\\"")
                                      .replace("\n", "\\n")
                                      .replace("\r", "\\r")
                                      .replace("\t", "\\t");
                        }
                        // 进行SQL注入检查
                        String sqlFiltered = SQLFilter.sqlInject(value);
                        if (sqlFiltered != null && !sqlFiltered.equals(value)) {
                            log.warn("SQL injection attempt detected in array element, original value: {}", value);
                            value = sqlFiltered;
                        }
                        // 进行XSS过滤
                        String xssFiltered = xssEncode(value);
                        if (!xssFiltered.equals(value)) {
                            log.warn("XSS attempt detected in array element, original value: {}", value);
                            value = xssFiltered;
                        }
                        arrayNode.set(i, TextNode.valueOf(value));
                    }
                }
                json = arrayNode.toString();
            }
            
            final ByteArrayInputStream bis = new ByteArrayInputStream(json.getBytes("utf-8"));
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return true;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    
                }

                @Override
                public int read() throws IOException {
                    return bis.read();
                }
            };
        } catch (Exception e) {
            log.error("Error processing JSON request: {}", e.getMessage(), e);
            // 如果JSON解析失败，返回原始输入流
            return super.getInputStream();
        }
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (StringUtils.isNotBlank(value)) {
            // 如果是WebSocket请求，不进行XSS过滤
            if (isWebSocketRequest()) {
                log.debug("WebSocket request detected, skipping XSS filter for parameter: {}", name);
                return value;
            }

            log.debug("Processing parameter: {} = {}", name, value);
            String xssFiltered = xssEncode(value);
            if (!xssFiltered.equals(value)) {
                log.warn("XSS attempt detected in parameter: {}, original value: {}", name, value);
            }
            String sqlFiltered = SQLFilter.sqlInject(xssFiltered);
            if (sqlFiltered != null && !sqlFiltered.equals(xssFiltered)) {
                log.warn("SQL injection attempt detected in parameter: {}, original value: {}", name, value);
            }
            return sqlFiltered;
        }
        return value;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] parameters = super.getParameterValues(name);
        if (parameters == null || parameters.length == 0) {
            return null;
        }

        // 如果是WebSocket请求，不进行XSS过滤
        if (isWebSocketRequest()) {
            log.debug("WebSocket request detected, skipping XSS filter for parameter values: {}", name);
            return parameters;
        }

        for (int i = 0; i < parameters.length; i++) {
            log.debug("Processing parameter value: {} = {}", name, parameters[i]);
            String xssFiltered = xssEncode(parameters[i]);
            if (!xssFiltered.equals(parameters[i])) {
                log.warn("XSS attempt detected in parameter value: {}, original value: {}", name, parameters[i]);
            }
            String sqlFiltered = SQLFilter.sqlInject(xssFiltered);
            if (sqlFiltered != null && !sqlFiltered.equals(xssFiltered)) {
                log.warn("SQL injection attempt detected in parameter value: {}, original value: {}", name, parameters[i]);
            }
            parameters[i] = sqlFiltered;
        }
        return parameters;
    }

    @Override
    public Map<String,String[]> getParameterMap() {
        Map<String,String[]> map = new LinkedHashMap<>();
        Map<String,String[]> parameters = super.getParameterMap();
        
        // 如果是WebSocket请求，不进行XSS过滤
        if (isWebSocketRequest()) {
            log.debug("WebSocket request detected, skipping XSS filter for parameter map");
            return parameters;
        }

        for (String key : parameters.keySet()) {
            String[] values = parameters.get(key);
            for (int i = 0; i < values.length; i++) {
                log.debug("Processing parameter map: {} = {}", key, values[i]);
                String xssFiltered = xssEncode(values[i]);
                if (!xssFiltered.equals(values[i])) {
                    log.warn("XSS attempt detected in parameter map: {}, original value: {}", key, values[i]);
                }
                String sqlFiltered = SQLFilter.sqlInject(xssFiltered);
                if (sqlFiltered != null && !sqlFiltered.equals(xssFiltered)) {
                    log.warn("SQL injection attempt detected in parameter map: {}, original value: {}", key, values[i]);
                }
                values[i] = sqlFiltered;
            }
            map.put(key, values);
        }
        return map;
    }

    private String xssEncode(String input) {
        return htmlFilter.filter(input);
    }

    /**
     * 获取最原始的request
     */
    public HttpServletRequest getOrgRequest() {
        return orgRequest;
    }

    /**
     * 获取最原始的request
     */
    public static HttpServletRequest getOrgRequest(HttpServletRequest request) {
        if (request instanceof XssAndSqlRequestWrapper) {
            return ((XssAndSqlRequestWrapper) request).getOrgRequest();
        }

        return request;
    }

    private boolean isJsonRequest() {
        String contentType = super.getHeader(HttpHeaders.CONTENT_TYPE);
        return contentType != null && contentType.toLowerCase().contains(MediaType.APPLICATION_JSON_VALUE.toLowerCase());
    }

    /**
     * 判断是否是WebSocket请求
     */
    private boolean isWebSocketRequest() {
        String upgrade = super.getHeader("Upgrade");
        String connection = super.getHeader("Connection");
        return "websocket".equalsIgnoreCase(upgrade) && 
               connection != null && 
               connection.toLowerCase().contains("upgrade");
    }
}
