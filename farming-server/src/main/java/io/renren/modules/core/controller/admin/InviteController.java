package io.renren.modules.core.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.common.exception.RRException;
import io.renren.common.utils.R;
import io.renren.modules.core.entity.InviteEntity;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.param.InviteAddParam;
import io.renren.modules.core.param.InviteParam;
import io.renren.modules.core.param.InviteUpdateParam;
import io.renren.modules.core.service.InviteService;
import io.renren.modules.core.service.PoolsService;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/admin/invite")
public class InviteController extends AbstractController{

	@Autowired
	InviteService inviteService;
	@Autowired
	PoolsService poolsService;
	
	
	@PostMapping("list")
	public R list(@RequestBody InviteParam param) {
		 return R.ok(inviteService.listPage(param));
	}
	@PostMapping("get")
	public R get(Long id) {
		if(id == null) throw new RRException("ID not exist");
		return R.ok(inviteService.get(id));
	} 
	

	@PostMapping("update")
	public R update(@RequestBody @Validated InviteUpdateParam param) {
		InviteEntity inviteDb = inviteService.getById(param.getId());
		if(inviteDb == null)  throw new RRException("Id null");
		InviteEntity invite = inviteService.getOne(new LambdaQueryWrapper<InviteEntity>().eq(InviteEntity::getCode, param.getCode().toLowerCase()));
		if(invite !=null )throw new RRException("Code already exists");
		
		PoolsEntity pools = poolsService.getById(inviteDb.getPoolsId());
		String url = pools.getDomain() + "?code=" + param.getCode().toLowerCase();
		InviteEntity inviteEntity = inviteService.getOne(new LambdaQueryWrapper<InviteEntity>().eq(InviteEntity::getInviteUrl, url));
		if(inviteEntity !=null) throw new RRException("InviteUrl already exists");
		
		inviteService.update(new LambdaUpdateWrapper<InviteEntity>()
				.set(InviteEntity::getName, param.getName())
				.set(InviteEntity::getCode, param.getCode())
				.set(InviteEntity::getInviteUrl, url)
				.eq(InviteEntity::getId, param.getId()));
		
		 return R.ok();
	}
	@PostMapping("del")
	public R del(@RequestBody InviteUpdateParam param) {
		if(param.getId() == null) throw new RRException("ID is null");
		inviteService.removeById(param.getId());
		 return R.ok();
	}
	
	

	@PostMapping("add")
	public R add(@RequestBody @Validated InviteAddParam param) {
		PoolsEntity pools = poolsService.getOne(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getOwnerName, getUser().getUsername()));
		if(pools == null) throw new RRException("The user does not have a fund pool");
		String url = pools.getDomain() + "?code=" + param.getCode().toLowerCase();
		InviteEntity inviteEntity = inviteService.getOne(new LambdaQueryWrapper<InviteEntity>().eq(InviteEntity::getInviteUrl, url));
		if(inviteEntity !=null) throw new RRException("InviteUrl already exists");
		inviteEntity = inviteService.getOne(new LambdaQueryWrapper<InviteEntity>().eq(InviteEntity::getCode, param.getCode().toLowerCase()));
		if(inviteEntity !=null) throw new RRException("Code already exists");
		InviteEntity manager = new InviteEntity();
		manager.setName(param.getName());
		manager.setCode(param.getCode().toLowerCase());
		manager.setPoolsId(pools.getId());
		manager.setInviteUrl(String.format("%s?code=%s",pools.getDomain(),param.getCode().toLowerCase()));
		inviteService.save(manager);
	    return R.ok();
	}
}
