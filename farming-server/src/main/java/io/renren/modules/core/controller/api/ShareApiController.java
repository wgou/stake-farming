package io.renren.modules.core.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.renren.common.exception.RRException;
import io.renren.common.utils.R;
import io.renren.modules.constants.Constants;
import io.renren.modules.constants.InviteEnum;
import io.renren.modules.core.context.WalletConext;
import io.renren.modules.core.entity.InviteEntity;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.RewardEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.entity.WithDrawEntity;
import io.renren.modules.core.param.IndexRewardParam;
import io.renren.modules.core.param.PageParam;
import io.renren.modules.core.service.InviteService;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.RewardService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.vo.IndexVO;
import io.renren.modules.utils.StakeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api
@RestController
@RequestMapping("/api/share")
public class ShareApiController {

	@Autowired
	PoolsService poolsService;
	@Autowired
	WalletsService walletsService;
	@Autowired
	RewardService rewardService;
	@Autowired
	InviteService inviteService;
	
	
	@ApiOperation("分享奖励列表")
	@PostMapping("list")
	public R list(@RequestBody PageParam param) throws Exception {
		Page<RewardEntity> pageObject = new Page<RewardEntity>(param.getCurrent(),param.getSize());
        pageObject.addOrder(OrderItem.desc("created"));
        LambdaQueryWrapper<RewardEntity> query =  new LambdaQueryWrapper<RewardEntity>();
       	query.eq(RewardEntity::getWallet, WalletConext.getWallet());
       	query.eq(RewardEntity::getInvited,InviteEnum.yes.getCode() );
        Page<RewardEntity> page = rewardService.page(pageObject, query);
		return R.ok(page);
		 
	}
	
	//https://denecan.com/?code=8888&referral=0x205443C6856727B83b0C975ce95B718a5a000c68
	@ApiOperation("分享")
	@PostMapping("link")
	public R link() throws Exception {
		String wallet = WalletConext.getWallet();
		WalletsEntity walletEntity  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, wallet));
		if(walletEntity == null) throw new RRException("Invalid wallet");
		PoolsEntity pools = poolsService.getById(walletEntity.getPoolsId());
		InviteEntity inviteVO = inviteService.getById(walletEntity.getInviteId());
		String shareUrl = inviteVO == null ? 
				String.format("%s?referral=%s", pools.getDomain(),walletEntity.getWallet()) : 
				String.format("%s?code=%s&referral=%s", pools.getDomain(),inviteVO.getCode(),walletEntity.getWallet());
		return R.ok().put("data", shareUrl);
	}
	
}
