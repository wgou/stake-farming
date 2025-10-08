/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.config;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.renren.common.utils.R;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//@Configuration
//@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
    		  .host("admin.bitxyz.xyz") // 设置新的主机和端口
    	      .pathMapping("/stake")    // 设置新的路径前缀
	            .apiInfo(apiInfo())
	            .select()
	            //加了ApiOperation注解的类，才生成接口文档
	            .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
	            .paths(PathSelectors.any())
	            .build()
	            .securitySchemes(security());
	           // .genericModelSubstitutes(R.class);
        
   
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("eth farming")
            .description("farming-api")
            .termsOfServiceUrl("https://admin.bitxyz.xyz")
            .version("3.0.0")
            .build();
    }

    private List<ApiKey> security() {
        return newArrayList(
            new ApiKey("token", "token", "header")
        );
    }

}