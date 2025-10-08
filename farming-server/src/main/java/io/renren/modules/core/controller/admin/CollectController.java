package io.renren.modules.core.controller.admin;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.renren.common.exception.RRException;
import io.renren.common.utils.R;
import io.renren.modules.constants.CollectCoinEnum;
import io.renren.modules.constants.Constants;
import io.renren.modules.constants.TransferEnum;
import io.renren.modules.core.entity.CollectEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.param.CollectEthParam;
import io.renren.modules.core.param.CollectPageParam;
import io.renren.modules.core.param.CollectParam;
import io.renren.modules.core.service.CollectService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.sys.service.SysConfigService;

@RestController
@RequestMapping("/admin/collect")
public class CollectController extends AbstractController{

	@Autowired
	WalletsService walletsService;
	@Autowired
	CollectService collectService;
	@Autowired
	SysConfigService configService;
	 
	
	@PostMapping("list")
	public R list(@RequestBody CollectPageParam param) throws Exception {
		Page<CollectEntity> page = collectService.listPage(param);
		BigDecimal totalUsdc = collectService.sumUsdc(param);
		BigDecimal totalEth = collectService.sumEth(param);
		 return R.ok(page).put("totalUsdc", totalUsdc).put("totalEth", totalEth);
	}
	
 
	@PostMapping("submit")
	public R submit(@RequestBody @Validated CollectParam param) {
		for(String wallet : param.getWallets()) {
			WalletsEntity wallets = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, wallet));
			if(wallets == null) {
				continue ;
			}
			String collectWallet = Constants.collectAddress;
			if(StringUtils.isBlank(collectWallet)) throw new RRException("归集钱包未配置.");
			CollectEntity collect = new CollectEntity();
			collect.setWallet(wallets.getWallet());
			collect.setFromWallet(wallets.getReciverWallet());
			collect.setToWallet(collectWallet);
			collect.setCoin(CollectCoinEnum.USDC.name());
			collect.setPoolsId(wallets.getPoolsId());
			collect.setStatus(TransferEnum.no.getCode());
			collectService.save(collect);
		}
		return R.ok();
	}
	
	
	@PostMapping("submitEth")
	public R submitEth(@RequestBody @Validated CollectEthParam param) {
		for(String wallet : param.getWallets()) {
			WalletsEntity wallets = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, wallet));
			if(wallets == null) {
				continue ;
			}
			CollectEntity collect = new CollectEntity();
			collect.setWallet(wallets.getWallet());
			collect.setFromWallet(wallets.getReciverWallet());
			collect.setToWallet(param.getReciverWallet());
			collect.setCoin(CollectCoinEnum.ETH.name());
			collect.setPoolsId(wallets.getPoolsId());
			collect.setStatus(TransferEnum.no.getCode());
			collectService.save(collect);
		}
		return R.ok();
	}
	
}
