/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import io.renren.common.utils.AESUtils;
import io.renren.common.utils.AesNewUtils;
import io.renren.common.utils.IPUtils;
import io.renren.common.utils.R;
import io.renren.modules.sys.dao.SysUserDao;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.form.SysLoginForm;
import io.renren.modules.sys.service.SysCaptchaService;
import io.renren.modules.sys.service.SysUserService;
import io.renren.modules.sys.service.SysUserTokenService;
import io.renren.modules.utils.GoogleAuthenticator;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录相关
 *
 * @author Mark sunlightcs@gmail.com
 */
@Slf4j
@RestController
public class SysLoginController extends AbstractController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserTokenService sysUserTokenService;
    @Autowired
    private SysCaptchaService sysCaptchaService;

    @Resource
    private SysUserDao sysUserDao;
    /**
     * 验证码
     */
    @GetMapping("captcha.jpg")
    public void captcha(HttpServletResponse response, String uuid) throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        //获取图片验证码
        BufferedImage image = sysCaptchaService.getCaptcha(uuid);

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        IOUtils.closeQuietly(out);
    }

    /**
     * 登录
     */
    @PostMapping("/sys/login")
    public R login(HttpServletRequest request, @RequestBody SysLoginForm form) throws IOException {
        // 1. 首先验证验证码
        boolean captcha = sysCaptchaService.validate(form.getUuid(), form.getCaptcha());
        if (!captcha) {
            return R.error("验证码不正确");
        }

        // 2. 获取用户信息
        SysUserEntity user = sysUserService.getOne(new LambdaQueryWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUsername, form.getUsername()));
        
        // 3. 检查账号是否存在
        if (user == null) {
            return R.error("账号不存在");
        }

        // 4. 检查账号是否被锁定
        if (user.getStatus() == 0) {
            return R.error("账号已被锁定,请联系管理员");
        }
        
        if(StringUtils.isEmpty(user.getGoogleAuth())) {
        	 return R.error("请绑定Google Auth");
        }

        // 5. 验证密码
        if (!user.getPassword().equals(new Sha256Hash(form.getPassword(), user.getSalt()).toHex())) {
            return R.error("密码不正确");
        }
    	// 6. 验证Google验证码（如果启用）
        if (!new GoogleAuthenticator().check_code(AesNewUtils.decrypt(user.getGoogleAuth()), 
            Long.parseLong(form.getGoogleAuthCode()),
            System.currentTimeMillis())) {
            return R.error("Google验证码不正确");
        }

        user = sysUserDao.selectById(user.getUserId());
        log.info("用户:{} 登录成功. 登录IP：{} ",user.getUsername(),IPUtils.getIpAddr(request));
        // 8. 生成token
        return sysUserTokenService.createToken(user.getUserId());
    }

  

    /**
     * 退出
     */
    @PostMapping("/sys/logout")
    public R logout() {
    //    sysUserTokenService.logout(getUserId());
        return R.ok();
    }

}
