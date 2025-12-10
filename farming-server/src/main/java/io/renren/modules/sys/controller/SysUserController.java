/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import io.renren.common.annotation.SysLog;
import io.renren.common.exception.RRException;
import io.renren.common.utils.AesNewUtils;
import io.renren.common.utils.Constant;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.common.validator.Assert;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.form.PasswordForm;
import io.renren.modules.sys.form.SysUserFrom;
import io.renren.modules.sys.service.SysUserRoleService;
import io.renren.modules.sys.service.SysUserService;
import io.renren.modules.sys.vo.SysUserVO;
import io.renren.modules.utils.GoogleAuthenticator;

/**
 * 系统用户
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends AbstractController {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysUserRoleService sysUserRoleService;


	/**
	 * 所有用户列表
	 */
	@GetMapping("/list")
	@RequiresPermissions("sys:user:list")
	public R list(@RequestParam Map<String, Object> params){
		//只有超级管理员，才能查看所有管理员列表
		if(getUserId() != Constant.SUPER_ADMIN){
			params.put("createUserId", getUserId());
		}
		PageUtils page = sysUserService.queryPage(params);

		return R.ok().put("page", page);
	}
	
	/**
	 * 获取登录的用户信息
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@GetMapping("/info")
	public R info() throws IllegalAccessException, InvocationTargetException{
		SysUserEntity user = getUser();
		SysUserVO userVo = new SysUserVO();
		BeanUtils.copyProperties(userVo, user);
		return R.ok().put("user", userVo);
	}
	

	/**
	 * 保存用户
	 */
	@SysLog("保存用户")
	@PostMapping("/save")
	@RequiresPermissions("sys:user:save")
	public R save(@RequestBody SysUserFrom from){
		SysUserEntity user = new SysUserEntity();
		user.setUsername(from.getUsername());
		user.setPassword(from.getPassword());
		user.setEmail(from.getEmail());
		user.setStatus(from.getStatus());
		user.setRoleIdList(from.getRoleIdList());
		user.setCreateUserId(getUserId());
		user.setCreateUserName(getUser().getUsername());
		sysUserService.saveUser(user);
		
		return R.ok();
	}

	/**
	 * 修改登录用户密码
	 */
	@SysLog("修改密码")
	@PostMapping("/password")
	public R password(@RequestBody PasswordForm form){
		Assert.isBlank(form.getNewPassword(), "新密码不为能空");
		SysUserEntity user = getUser();
		String authKey = user.getGoogleAuth();   // 6. 验证Google验证码（如果启用）
        if (StringUtils.isNotEmpty(authKey)) {
            if (!new GoogleAuthenticator().check_code(AesNewUtils.decrypt(authKey), 
                Long.parseLong(form.getGoogleAuthCode()),
                System.currentTimeMillis())) {
                return R.error("Google验证码不正确");
            }
        }
		//sha256加密
		String password = new Sha256Hash(form.getPassword(), getUser().getSalt()).toHex();
		//sha256加密
		String newPassword = new Sha256Hash(form.getNewPassword(), getUser().getSalt()).toHex();
				
		//更新密码
		boolean flag = sysUserService.updatePassword(getUserId(), password, newPassword);
		if(!flag){
			return R.error("原密码不正确");
		}
		
		return R.ok();
	}
	
	/**
	 * 用户信息
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@GetMapping("/info/{userId}")
	@RequiresPermissions("sys:user:info")
	public R info(@PathVariable("userId") Long userId) throws IllegalAccessException, InvocationTargetException{
		SysUserEntity user = sysUserService.getById(userId);
		//获取用户所属的角色列表
		List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userId);
		user.setRoleIdList(roleIdList);
		
		SysUserVO userVO = new SysUserVO();
		BeanUtils.copyProperties(userVO, user);
		
		return R.ok().put("user", userVO);
	}
	
	
	 
	/**
	 * 修改用户
	 */
	@SysLog("修改用户")
	@PostMapping("/update")
	@RequiresPermissions("sys:user:update")
	public R update(@RequestBody SysUserFrom user){
		SysUserEntity dbuser = sysUserService.getById(user.getUserId());
		if(dbuser == null) throw new RRException("账号不存在.");
		SysUserEntity sysuser = sysUserService.getOne(new LambdaQueryWrapper<SysUserEntity>().eq(SysUserEntity::getUserId, Constant.SUPER_ADMIN));
        String authKey = sysuser.getGoogleAuth();   // 6. 验证Google验证码（如果启用）
        if (StringUtils.isNotEmpty(authKey)) {
            if (!new GoogleAuthenticator().check_code(AesNewUtils.decrypt(authKey), 
                Long.parseLong(user.getGoogleAuthCode()),
                System.currentTimeMillis())) {
                return R.error("Google验证码不正确");
            }
        }
		SysUserEntity update = new SysUserEntity();
		update.setUserId(user.getUserId());
		update.setUsername(user.getUsername());
		update.setPassword(user.getPassword());
		update.setEmail(user.getEmail());
		update.setStatus(user.getStatus());
		update.setRoleIdList(user.getRoleIdList());
		if(dbuser.getCreateUserId() ==null) {
			update.setCreateUserId(getUserId());
			update.setCreateUserName(getUser().getUsername());
		}
		sysUserService.update(update,dbuser.getSalt());
		return R.ok();
	}
	
	
	@RequestMapping("/getChildAccount")
	public R getChildAccount() {
		QueryWrapper<SysUserEntity> query = new QueryWrapper<>();
		query.lambda().eq(SysUserEntity::getCreateUserId, getUserId());
		List<SysUserEntity> sysUserEntities = sysUserDao.selectList(query);
		List<SysUserVO> voList = new ArrayList<>();
		for (SysUserEntity user : sysUserEntities) {
		    SysUserVO userVo = new SysUserVO();
		    try {
				BeanUtils.copyProperties( userVo,user);
			} catch (Exception e) {
				e.printStackTrace();
			}
		    voList.add(userVo);
		}
		return R.ok().put("list", voList);
	}
	
	
	/**
	 * 删除用户
	 */
	@PostMapping("/delete")
	@RequiresPermissions("sys:user:delete")
	public R delete(@RequestBody Long[] userIds){
		if(ArrayUtils.contains(userIds, 1L)){
			return R.error("系统管理员不能删除");
		}
		if(ArrayUtils.contains(userIds, getUserId())){
			return R.error("当前用户不能删除");
		}
		sysUserService.deleteBatch(userIds);
		return R.ok();
	}
	
	 
}
