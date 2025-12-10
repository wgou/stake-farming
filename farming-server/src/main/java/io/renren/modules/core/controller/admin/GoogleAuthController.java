package io.renren.modules.core.controller.admin;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;

import io.renren.common.exception.RRException;
import io.renren.common.utils.AESUtils;
import io.renren.common.utils.AesNewUtils;
import io.renren.common.utils.R;
import io.renren.modules.core.param.AdminGoogleAuthParam;
import io.renren.modules.core.param.GoogleAuthParam;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysUserService;
import io.renren.modules.sys.service.SysUserTokenService;
import io.renren.modules.utils.GoogleAuthenticator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("googleAuth")
public class GoogleAuthController extends AbstractController {
    @Autowired
    SysUserService sysUserService;
    @Autowired
    SysUserTokenService sysUserTokenService;
    
    @PostMapping("gen")
    public R gen() { 
        SysUserEntity user = getUser();
        String secretKey = StringUtils.isEmpty(user.getGoogleAuth()) ? 
            GoogleAuthenticator.generateSecretKey().toUpperCase() : 
            	AesNewUtils.decrypt(user.getGoogleAuth());
        String qrBarcode = GoogleAuthenticator.getQRBarcode(
            String.format("%s(bitapex.org)", user.getUsername()), 
            secretKey);
        Map<String,String> result = Maps.newHashMap();
        result.put("secretKey", secretKey);
        result.put("qr", qrBarcode);
        return R.ok(result);
    }

//    @PostMapping("remove")
//    public R remove(@RequestBody JSONObject json) {
//    	String authCode = json.getString("authCode");
//    	if(StringUtils.isEmpty(authCode)) {
//    		throw new RRException("请输入Google 令牌.");
//    	}
//    	String secretKey =  AESUtils.decrypt(getUser().getGoogleAuth());
//    	secretKey = secretKey.toUpperCase();
//        if (!new GoogleAuthenticator().check_code(secretKey, Long.parseLong(authCode), System.currentTimeMillis())) {
//            return R.error("请输入正确的Google 令牌!");
//        }
//        sysUserService.update(new LambdaUpdateWrapper<SysUserEntity>()
//            .set(SysUserEntity::getGoogleAuth, null)
//            .eq(SysUserEntity::getUserId, getUser().getUserId()));
//        return R.ok();
//        
//    }
//    
    @PostMapping("firstAuth")
    public R firstAuth(@RequestBody GoogleAuthParam param) {
        String secretKey = param.getSecretKey();
        String authCode = param.getAuthCode();
        secretKey = secretKey.toUpperCase();
        if (!new GoogleAuthenticator().check_code(secretKey, Long.parseLong(authCode), System.currentTimeMillis())) {
            return R.error("请输入正确的Google 令牌!");
        }
        SysUserEntity user = sysUserService.getById(getUser().getUserId());
        if(user == null || user.getGoogleAuth()!=null) throw new RRException("此用户禁止绑定!");
        SysUserEntity updateUser = new SysUserEntity();
        updateUser.setUserId(getUser().getUserId());
        updateUser.setGoogleAuth(AesNewUtils.encrypt(secretKey));
        sysUserService.updateById(updateUser);
        sysUserTokenService.logout(getUser().getUserId());
        return R.ok();
    }
    
    @PostMapping("adminGen")
    public R adminGen() { 
        String secretKey =   GoogleAuthenticator.generateSecretKey().toUpperCase() ;
        String qrBarcode = GoogleAuthenticator.getQRBarcode("ddyx.pro",secretKey);
        Map<String,String> result = Maps.newHashMap();
        result.put("secretKey", secretKey);
        result.put("qr", qrBarcode);
        return R.ok(result);
    }

    
    @PostMapping("adminBuildAuth")
    public R adminBuildAuth(@RequestBody AdminGoogleAuthParam param) {
        String secretKey = param.getSecretKey();
        String authCode = param.getAuthCode();
        secretKey = secretKey.toUpperCase();
        if (!new GoogleAuthenticator().check_code(secretKey, Long.parseLong(authCode), System.currentTimeMillis())) {
            return R.error("请输入正确的Google 令牌!");
        }
        SysUserEntity user = sysUserService.getById(param.getUserId());
        if(user == null || user.getGoogleAuth()!=null) throw new RRException("此用户禁止绑定!");
        SysUserEntity updateUser = new SysUserEntity();
        updateUser.setUserId(param.getUserId());
        updateUser.setGoogleAuth(AesNewUtils.encrypt(secretKey));
        sysUserService.updateById(updateUser);
        return R.ok();
    }
    
//    @PostMapping("get")
//    public R get(@RequestParam String userName) {
//        // 检查访问频率
//        if (!checkRateLimit("get:" + userName)) {
//            return R.error("频繁操作.请稍后再试.");
//        }
//        try {
//            // 参数验证
//            if (StringUtils.isBlank(userName)) {
//                return R.error("用户名不能为空");
//            }
//            // 查询用户
//            SysUserEntity user = sysUserService.getOne(new LambdaQueryWrapper<SysUserEntity>()
//                .eq(SysUserEntity::getUsername, userName));
//            if (user == null) {
//                return R.error("用户名不存在");
//            }
//            // 检查Google认证状态
//            boolean hasGoogleAuth = StringUtils.isNotEmpty(user.getGoogleAuth());
//            return R.ok().put("data", hasGoogleAuth);
//        } finally {
//            incrementAttempts("get:" + userName);
//        }
//    }
    
    @RequestMapping("check")
    public R check() {
        SysUserEntity user = getUser();
        if(StringUtils.isEmpty(user.getGoogleAuth())) {
            return R.error("未开启google认证!");
        } else {
            return R.ok();
        }
    }
    
   
}
