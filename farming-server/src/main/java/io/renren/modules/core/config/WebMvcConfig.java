/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC配置
 *
 * @author Mark sunlightcs@gmail.com
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private ApiAuthorizationInterceptor apiAuthorizationInterceptor;
    

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiAuthorizationInterceptor).addPathPatterns("/api/**")
        .excludePathPatterns(
                "/api/wallet/login",
                "/api/wallet/checkCode",
                "/api/index/rewards",
                "/api/index/index"
                
    		);
    }

}