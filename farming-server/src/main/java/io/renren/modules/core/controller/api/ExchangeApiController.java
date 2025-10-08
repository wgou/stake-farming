package io.renren.modules.core.controller.api;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.renren.common.exception.RRException;
import io.renren.common.utils.R;
import io.renren.modules.core.context.WalletConext;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.ExchangeEntity;
import io.renren.modules.core.entity.WalletFundsEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.param.PageParam;
import io.renren.modules.core.service.ExchangeService;
import io.renren.modules.core.service.WalletFundsService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.utils.StakeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@Api
@RestController
@RequestMapping("/api/exchange")
public class ExchangeApiController {

	@Autowired
	WalletsService walletsService;
	@Autowired
	ExchangeService exchangeService;
	@Autowired
	ContractHandler handler;
	@Autowired
	WalletFundsService walletFundsService;
	
	
	@ApiOperation("Exchangeable quantity")
	@PostMapping("index")
	public R index() throws Exception {
		String wallet = WalletConext.getWallet();
		WalletsEntity walletEntity  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, wallet));
		if(walletEntity == null) throw new RRException("Invalid wallet");
		WalletFundsEntity fundsEntity = walletFundsService.getOne(new LambdaQueryWrapper<WalletFundsEntity>().eq(WalletFundsEntity::getWallet, wallet));
		BigDecimal balance = fundsEntity == null ? BigDecimal.ZERO : fundsEntity.getBalanceReward() ;
		return R.ok(balance);
	}
	
	@ApiOperation("点击all")
	@PostMapping("toUsdc")
	public R toUsdc() throws Exception {
		String wallet = WalletConext.getWallet();
		WalletsEntity walletEntity  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, wallet));
		if(walletEntity == null) throw new RRException("Invalid wallet");
		WalletFundsEntity fundsEntity = walletFundsService.getOne(new LambdaQueryWrapper<WalletFundsEntity>().eq(WalletFundsEntity::getWallet, wallet));
		BigDecimal balance = fundsEntity == null ? BigDecimal.ZERO : fundsEntity.getBalanceReward() ;
		return R.ok(StakeUtils.ethToUsdc(balance, handler.getEthPrice()));
	}
	
	@ApiOperation("点击exchange")
	@PostMapping("submit")
	public R submit() throws Exception {
		exchangeService.exchange(WalletConext.getWallet());
		return R.ok();
	}
	

	@ApiOperation("Swap History - 分页,前端下滑 分页")
	@PostMapping("list")
	public R list(@RequestBody PageParam param) throws Exception {
		Page<ExchangeEntity> pageObject = new Page<ExchangeEntity>(param.getCurrent(),param.getSize());
        pageObject.addOrder(OrderItem.desc("created"));
        LambdaQueryWrapper<ExchangeEntity> query =  new LambdaQueryWrapper<ExchangeEntity>();
       	query.eq(ExchangeEntity::getWallet, WalletConext.getWallet());
        Page<ExchangeEntity> page = exchangeService.page(pageObject, query);
		return R.ok(page);
	}
	
	
}
