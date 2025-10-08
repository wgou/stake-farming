package io.renren.modules.core.controller.api;

import java.math.BigDecimal;

import org.apache.http.annotation.Contract;
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
import io.renren.modules.core.entity.WalletFundsEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.entity.WithDrawEntity;
import io.renren.modules.core.param.PageParam;
import io.renren.modules.core.param.WithDrawIndexParam;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.WalletFundsService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.service.WithDrawService;
import io.renren.modules.utils.StakeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping("/api/withdraw")
public class WithDrawApiController {

	@Autowired
	PoolsService poolsService;
	@Autowired
	WalletsService walletsService;
	@Autowired
	WithDrawService withDrawService;
	@Autowired
	WalletFundsService walletFundsService;
	@Autowired
	ContractHandler handler;
	

	@ApiOperation("Avaiable amount")
	@PostMapping("avaiable")
	public R avaiable() throws Exception {
		String wallet = WalletConext.getWallet();
		WalletsEntity walletEntity  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, wallet));
		if(walletEntity == null) throw new RRException("Invalid wallet");
		WalletFundsEntity fundsEntity = walletFundsService.getOne(new LambdaQueryWrapper<WalletFundsEntity>().eq(WalletFundsEntity::getWallet, wallet));
		BigDecimal balance = fundsEntity == null ? BigDecimal.ZERO : fundsEntity.getExtractable() ;
		return R.ok(balance);
	}
	
	
	
	@ApiOperation("WithDraw 提交")
	@PostMapping("submit")
	public R submit(@RequestBody WithDrawIndexParam param) throws Exception {
		withDrawService.withDraw(param);
		return R.ok();
	}
	
	@ApiOperation("提现记录")
	@PostMapping("list")
	public R list(@RequestBody PageParam param) throws Exception {
		Page<WithDrawEntity> pageObject = new Page<WithDrawEntity>(param.getCurrent(),param.getSize());
        pageObject.addOrder(OrderItem.desc("created"));
        LambdaQueryWrapper<WithDrawEntity> query =  new LambdaQueryWrapper<WithDrawEntity>();
       	query.eq(WithDrawEntity::getWallet, WalletConext.getWallet());
        Page<WithDrawEntity> page = withDrawService.page(pageObject, query);
		return R.ok(page);
	}
	
}
