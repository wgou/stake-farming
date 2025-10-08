/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.controller;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.renren.common.exception.RRException;
import io.renren.common.utils.Constant;
import io.renren.modules.sys.dao.SysUserDao;
import io.renren.modules.sys.entity.SysUserEntity;

/**
 * Controller公共组件
 *
 * @author Mark sunlightcs@gmail.com
 */
public abstract class AbstractController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    protected SysUserDao sysUserDao;

    protected SysUserEntity getUser() {
        return (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
    }

    protected SysUserEntity getFullUser() {
        return sysUserDao.selectById(getUserId());
    }

    protected Long getUserId() {
        return getUser().getUserId();
    }

    protected <T> Page<T> parsePage(JSONObject jsonObject) {
        Long page = jsonObject.getLong("page");
        Long size = jsonObject.getLong("size");
        return new Page<>(page, size);
    }
 

    protected <T> void _users(String userName, QueryWrapper<T> queryWrapper) throws RRException {
        if (StringUtils.isNotEmpty(userName) && getUserId().intValue() == Constant.SUPER_ADMIN) {
            QueryWrapper<SysUserEntity> query = new QueryWrapper<>();
            LambdaQueryWrapper<SysUserEntity> lambdaUser = query.lambda();
            lambdaUser.eq(SysUserEntity::getUsername, userName);
            SysUserEntity sysUserEntity = sysUserDao.selectOne(query);
            if (sysUserEntity == null) {
                throw new RRException("输入用户名错误!");
            }
            queryWrapper.eq("user_id", sysUserEntity.getUserId());
        }
    }

}
