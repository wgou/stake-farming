/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.core.config;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import io.renren.common.constant.Constant;
import io.renren.common.exception.RRException;
import io.renren.common.utils.AESUtils;
import io.renren.common.utils.AesNewUtils;
import io.renren.modules.constants.BlockadeEnum;
import io.renren.modules.core.context.WalletConext;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.service.WalletsService;
import lombok.extern.slf4j.Slf4j;
/**
 * 权限(Token)验证
 */
@Slf4j
@Component
public class ApiAuthorizationInterceptor extends HandlerInterceptorAdapter {

    public static final String _token_ = "token";
    
    @Autowired
    WalletsService walletsService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	String token = request.getHeader(_token_);
    	if(StringUtils.isEmpty(token)) throw new RRException("Invalid token",401);
    	String wallet = AesNewUtils.decrypt(token);
    	WalletsEntity walletEntity  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, wallet));
    	if(walletEntity == null)  throw new RRException("Invalid token",401);
    	if(walletEntity.getBlockade() == BlockadeEnum.blockade.getCode()) throw new RRException("Account blocked");
    	WalletConext.setWallet(wallet);
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
    		throws Exception {
    	WalletConext.clear();
    }
}
