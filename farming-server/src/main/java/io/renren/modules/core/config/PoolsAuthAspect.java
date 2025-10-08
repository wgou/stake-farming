package io.renren.modules.core.config;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import io.renren.common.constant.Constant;
import io.renren.common.exception.RRException;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.param.PoolsParam;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.sys.entity.SysUserEntity;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Aspect
@Component
public class PoolsAuthAspect {
	

	@Autowired
	PoolsService poolsService;
	
	
	@Before("execution(* io.renren.modules.core.controller.admin..*(..)) ")
    public void modifyArgs(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof PoolsParam) { // 替换为你的参数对象类
            	PoolsParam param = (PoolsParam) arg;
            	SysUserEntity user = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
            	if(user.getUserId().intValue() != io.renren.common.utils.Constant.SUPER_ADMIN) {
            		List<Long> addPoolsId = getPoolsIds(user.getUserId());
            		if(param.getPoolsId() ==null || !addPoolsId.contains(param.getPoolsId())) {
            			param.setPoolsIds(addPoolsId); // 设置新的 poolsId
            		}
            	}
            }
        }
    }
	   
		
	private List<Long>  getPoolsIds(Long userId) {
		List<PoolsEntity> childPools = poolsService.list(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getOwnerId, userId).or().eq(PoolsEntity::getCreateUserId, userId));
		if(CollectionUtils.isEmpty(childPools)) throw new RRException("The current user has not created a fund pool");
		return childPools.stream().map(PoolsEntity::getId).collect(Collectors.toList());
	}

}
