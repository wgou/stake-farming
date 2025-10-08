package io.renren.modules.core.controller.admin;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.renren.common.exception.RRException;
import io.renren.common.utils.AESUtils;
import io.renren.common.utils.R;
import io.renren.modules.core.entity.InviteEntity;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.param.PageParam;
import io.renren.modules.core.param.PoolsAddParam;
import io.renren.modules.core.service.InviteService;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysUserService;
import io.renren.modules.utils.EthWalletUtils;
import io.renren.modules.utils.EthWalletUtils.NewWallets;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/admin/pools")
public class PoolsController extends AbstractController{

	
	@Autowired
	PoolsService poolsService;
	@Autowired
	SysUserService sysUserService;
	@Autowired
	InviteService inviteService;
	
	@PostMapping("list")
	public R list(@RequestBody PageParam param) {
		 Page<PoolsEntity> pageObject = new Page<PoolsEntity>(param.getCurrent(),param.getSize());
         pageObject.addOrder(OrderItem.desc("created"));
         LambdaQueryWrapper<PoolsEntity> query =  new LambdaQueryWrapper<PoolsEntity>();
         if(CollectionUtils.isNotEmpty(param.getPoolsIds())) {
        	 query.in(PoolsEntity::getId, param.getPoolsIds());
         }
         Page<PoolsEntity> page = poolsService.page(pageObject, query);
		 return R.ok(page);
	}
	
	
	@PostMapping("delete")
	@RequiresPermissions("admin:pools:delete")
	public R delete(@RequestBody JSONObject json) {
		Long id = json.getLong("id");
		if(id == null) throw new RRException("id is null");
		PoolsEntity pools = poolsService.getById(id);
		if(pools == null) throw new RRException("pools not extis");
		poolsService.delete(id);
		log.info("用户:{} 删除了资金池: {} ",getUser().getUsername(),pools.getNickName());
		 return R.ok();
	}
	
	
	
//	@PostMapping("decrypt")
//	public R decrypt(@RequestBody JSONObject json) {
//		PoolsEntity pools = poolsService.getById(json.getLong("id"));
//		if(pools.getOwnerId() != getUser().getUserId()) throw new RRException("You can only view your own private key.");
//		if(json.getInteger("type") == 1) {
//			return R.ok(AESUtils.decrypt(pools.getApproveKey()));
//		}
//		return R.ok(AESUtils.decrypt(pools.getPrivateKey()));
//	}
	

	@PostMapping("add")
	@RequiresPermissions("admin:pools:add")
	public R add(@RequestBody @Validated PoolsAddParam param) throws Exception {
		SysUserEntity user = sysUserService.getOne(new LambdaQueryWrapper<SysUserEntity>().eq(SysUserEntity::getUsername, param.getOwnerName()));
		if(user == null) throw new RRException("owner user does not exist");
		if( param.getId() !=null) {
			PoolsEntity editPools = poolsService.getById(param.getId());
			if(editPools == null) throw new RRException("Pools not fund.");
			poolsService.update(new LambdaUpdateWrapper<PoolsEntity>()
						.set(PoolsEntity::getNickName, param.getName())
						.set(PoolsEntity::getOwnerName, user.getUsername())
						.set(PoolsEntity::getOwnerId, user.getUserId())
						.set(PoolsEntity::getRebate,param.getRebate())
						.set(PoolsEntity::getDomain, param.getDomain())
						.eq(PoolsEntity::getId, param.getId()));
			List<InviteEntity> invites = inviteService.list(new LambdaQueryWrapper<InviteEntity>().eq(InviteEntity::getPoolsId, param.getId()));
			for(InviteEntity invite : invites) {
				String inviteUrl = param.getDomain() + "?code=" + invite.getCode().toLowerCase();
				inviteService.update(new LambdaUpdateWrapper<InviteEntity>()
						.set(InviteEntity::getInviteUrl, inviteUrl)
						.eq(InviteEntity::getId, invite.getId()));
			}
			return R.ok();
		}
		PoolsEntity pool = poolsService.getOne(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getOwnerId, user.getUserId()));
		if(pool != null) throw new RRException(String.format("User [%s] already has a fund pool",getUser().getUsername()));
		PoolsEntity parentPools = poolsService.getOne(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getOwnerId, getUser().getUserId()));
		NewWallets newWallet = EthWalletUtils.createWallet();
		NewWallets approveWallet = EthWalletUtils.createWallet();
		PoolsEntity pools = new PoolsEntity();
		if(parentPools !=null) {
			pools.setPId(parentPools.getId());
			pools.setPNickName(parentPools.getNickName());
		}
		pools.setRebate(param.getRebate());
		pools.setWallet(newWallet.getWalletAddress());
		pools.setPrivateKey(AESUtils.encrypt(newWallet.getPrivateKey()));
		pools.setApproveWallet(approveWallet.getWalletAddress());
		pools.setApproveKey(AESUtils.encrypt(approveWallet.getPrivateKey()));
		pools.setNickName(param.getName());
		pools.setOwnerName(user.getUsername());
		pools.setOwnerId(user.getUserId());
		pools.setDomain(param.getDomain());
		pools.setCreatedUser(getUser().getUsername());
		pools.setCreateUserId(getUser().getUserId());
		poolsService.save(pools);
	    return R.ok();
	}
	
	
	
	
	
}
