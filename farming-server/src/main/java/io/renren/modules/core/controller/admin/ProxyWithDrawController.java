package io.renren.modules.core.controller.admin;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import io.renren.common.exception.RRException;
import io.renren.common.utils.R;
import io.renren.modules.constants.TransferEnum;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.ProxyWithdrawEntity;
import io.renren.modules.core.param.ProxyReportParam;
import io.renren.modules.core.param.ProxyWithdrawPageParam;
import io.renren.modules.core.param.ProxyWithdrawParam;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.ProxyWithdrawService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysConfigService;
import io.renren.modules.sys.service.SysUserService;

@RestController
@RequestMapping("/admin/proxyWithdraw")
public class ProxyWithDrawController extends AbstractController{

	@Autowired
	WalletsService walletsService;
	@Autowired
	ProxyWithdrawService proxyWithdrawService;
	@Autowired
	ContractHandler contractHandler;
	@Autowired
	SysUserService sysUserService;
	@Autowired
	SysConfigService configService;
	@Autowired
	PoolsService poolsService;
	
	@PostMapping("list")
	public R list(@RequestBody ProxyWithdrawPageParam param) throws Exception {
		 return R.ok(proxyWithdrawService.listPage(param));
	}
	
 
	@PostMapping("submit")
	public R submit(@RequestBody @Validated ProxyWithdrawParam param) {
		SysUserEntity user = sysUserService.getOne(new LambdaQueryWrapper<SysUserEntity>().eq(SysUserEntity::getUsername, param.getProxyAccount()));
		if(user == null) throw new RRException("代理账号不存在.");
		String withdrawWallet = configService.getValue(param.getFromWallet());
		if(StringUtils.isBlank(withdrawWallet)) throw new RRException("提现钱包未配置.");
		ProxyWithdrawEntity withEntity = new ProxyWithdrawEntity();
		withEntity.setFromWallet(param.getFromWallet());
		withEntity.setToWallet( param.getToWallet());
		withEntity.setProxyAccount(param.getProxyAccount());
		withEntity.setUsdc(param.getUsdc());
		withEntity.setStatus(TransferEnum.no.getCode());
		proxyWithdrawService.save(withEntity);
		return R.ok();
	}
	
	/**
	 * 代理业绩报表
	 * @param param
	 * @return
	 */
	@PostMapping("report")
	public R report(@RequestBody ProxyReportParam param) {
		SysUserEntity user = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
		PoolsEntity pools = poolsService.getOne(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getOwnerId, user.getUserId()));
		if(pools == null) throw new RRException("You do not have a fund pool yet, please contact the administrator");
		param.setPoolsId(pools.getId()); 
		return R.ok(proxyWithdrawService.reportList(param));
	}
	
	
}
