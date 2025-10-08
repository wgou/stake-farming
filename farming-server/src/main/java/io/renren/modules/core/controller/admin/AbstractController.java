package io.renren.modules.core.controller.admin;

import org.apache.shiro.SecurityUtils;

import io.renren.modules.sys.entity.SysUserEntity;

public abstract class AbstractController {

    protected SysUserEntity getUser() {
        return (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
    }

    protected boolean isAdmin() {
    	return getUser().getUserId() == io.renren.common.utils.Constant.SUPER_ADMIN;
    }
}
