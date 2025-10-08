package io.renren.modules.core.controller.admin;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import io.renren.common.utils.R;
import io.renren.modules.constants.ApproveEnum;
import io.renren.modules.constants.FreezeEnum;
import io.renren.modules.constants.InviteEnum;
import io.renren.modules.constants.KillEnum;
import io.renren.modules.constants.RealEnum;
import io.renren.modules.constants.TransferEnum;
import io.renren.modules.constants.WithdrawStatusEnum;
import io.renren.modules.core.entity.InviteEntity;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.service.InviteService;
import io.renren.modules.core.service.PoolsService;
import net.dongliu.commons.collection.Maps;

@RestController
@RequestMapping("/admin/common")
public class CommonConroller extends AbstractController {
	
	@Autowired
	PoolsService poolsService;
	@Autowired
	InviteService inviteService;
	
	@PostMapping("select")
	public R select() {
		Map<String,Object> commonMaps = Maps.newHashMap();
		
		Map<Long, String> poolsMap = poolsMap();
		Map<Long,String> inviterListMap = inviterMap();
	    Map<Integer,String> killMap = KillEnum.toMap();
	    Map<Integer,String> approveMap = ApproveEnum.toMap();
	    Map<Integer,String> realMap = RealEnum.toMap();
	    Map<Integer,String> inviteMap = InviteEnum.toMap();
	    Map<Integer,String> withdrawMap = WithdrawStatusEnum.toMap();
	    Map<Integer,String> FreezeMap = FreezeEnum.toMap();
	    Map<Integer,String> collectMap = TransferEnum.toMap();
	    
	    
	    
	    commonMaps.put("pools",poolsMap); //资金池
	    commonMaps.put("kills",killMap); //是否杀掉
	    commonMaps.put("approves",approveMap); //是否授权
	    commonMaps.put("reals",realMap); //虚拟or 真实
	    commonMaps.put("invitesList",inviterListMap); //招聘人
	    commonMaps.put("invite",inviteMap); //是否推荐
	    commonMaps.put("withdraw",withdrawMap); //是否推荐
	    commonMaps.put("Freeze",FreezeMap); //是否冻结
	    commonMaps.put("collect",collectMap); 
	    
	    
		return R.ok(commonMaps);
	}
	
	
	private   Map<Long, String> poolsMap(){
		List<PoolsEntity> poolsList =  isAdmin() ? 
					poolsService.list(new LambdaQueryWrapper<>()) :
					poolsService.list(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getOwnerId, getUser().getUserId()).or().eq(PoolsEntity::getCreateUserId, getUser().getUserId()));
	    Map<Long, String> poolsMap = poolsList.stream()
                .collect(Collectors.toMap(PoolsEntity::getId, PoolsEntity::getNickName));
	    return poolsMap;
	}
	
	private Map<Long,String> inviterMap(){
		List<PoolsEntity> poolsList =  isAdmin() ? 
				poolsService.list(new LambdaQueryWrapper<>()) :
				poolsService.list(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getOwnerId, getUser().getUserId()).or().eq(PoolsEntity::getCreateUserId, getUser().getUserId()));
		if(CollectionUtils.isEmpty(poolsList)) return Maps.newHashMap();
		List<Long> poolsIds = poolsList.stream().map(PoolsEntity::getId).collect(Collectors.toList());
		List<InviteEntity> inviteList = inviteService.list(new LambdaQueryWrapper<InviteEntity>().in(InviteEntity::getPoolsId, poolsIds));
		Map<Long, String> inviteMap = inviteList.stream()
	                .collect(Collectors.toMap(InviteEntity::getId, InviteEntity::getName));
		    return inviteMap;
	}

}
