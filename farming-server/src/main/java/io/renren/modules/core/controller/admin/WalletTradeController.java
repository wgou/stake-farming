package io.renren.modules.core.controller.admin;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.renren.common.utils.R;
import io.renren.modules.core.entity.InviteEntity;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.WalletTradeEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.param.ExchangeParam;
import io.renren.modules.core.service.InviteService;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.WalletTradeService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.vo.WalletTradeVO;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.commons.collection.Lists;
@Slf4j
@RestController
@RequestMapping("/admin/trade")
public class WalletTradeController extends AbstractController {
	@Autowired
	WalletTradeService tradeService; 
	@Autowired
	WalletsService walletsService;
	@Autowired
	PoolsService poolsService;
	@Autowired
	InviteService inviteService; 
	
	@PostMapping("list")
	public R list(@RequestBody ExchangeParam param) throws Exception {
	    Page<WalletTradeEntity> pageObject = new Page<>(param.getCurrent(), param.getSize());
	    pageObject.addOrder(OrderItem.desc("created"));

	    // 创建查询条件
	    LambdaQueryWrapper<WalletTradeEntity> query = new LambdaQueryWrapper<>();

	    // 添加条件：只查询已授权的钱包
	    query.inSql(WalletTradeEntity::getWallet, 
	        "SELECT wallet FROM s_wallets WHERE approve = 1");

	    // 添加其他查询条件
	    if (CollectionUtils.isNotEmpty(param.getPoolsIds())) {
	        query.in(WalletTradeEntity::getPoolsId, param.getPoolsIds());
	    }
	    if (StringUtils.isNotBlank(param.getWallet())) {
	        query.eq(WalletTradeEntity::getWallet, param.getWallet());
	    }
	    if (param.getStart() != null) {
	        query.ge(WalletTradeEntity::getCreated, param.getStart());
	    }
	    if (param.getEnd() != null) {
	        query.le(WalletTradeEntity::getCreated, param.getEnd());
	    }
	    // 执行分页查询
	    Page<WalletTradeEntity> page = tradeService.page(pageObject, query);
	    
	    Page<WalletTradeVO> newPage =  new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
	    // 查询每个钱包的 inviteId 并设置到结果中
	    if (CollectionUtils.isNotEmpty(page.getRecords())) {
	        List<String> wallets = page.getRecords().stream()
	            .map(WalletTradeEntity::getWallet)
	            .collect(Collectors.toList());
	        // 查询 s_wallets 表，获取钱包对应的 inviteId
	        List<WalletsEntity> walletsEntities = walletsService.list(
	            new LambdaQueryWrapper<WalletsEntity>()
	                .in(WalletsEntity::getWallet, wallets)
	        );
	        // 将 inviteId 设置到 WalletTradeEntity 中
	        Map<String, Long> walletToInviteIdMap = walletsEntities.stream()
	            .collect(Collectors.toMap(WalletsEntity::getWallet, WalletsEntity::getInviteId));
	       
	         List<WalletTradeVO> lists = Lists.newArrayList();
	        page.getRecords().forEach(trade -> {
	        	WalletTradeVO wallet = new WalletTradeVO();
	        	BeanUtils.copyProperties(trade, wallet);
	        	makeVo(wallet,trade.getPoolsId(),walletToInviteIdMap.get(trade.getWallet()));
	        	lists.add(wallet);
	        });
	        newPage.setRecords(lists);
	    }
	    return R.ok(newPage);
	}
	
	
	private void makeVo(WalletTradeVO vo,Long poolsId,Long inviteId) {
		 PoolsEntity pool = poolsService.getById(poolsId);
	   	 InviteEntity invite = inviteService.getById(inviteId);
	   	 if(invite !=null)
	   		 vo.setInviteName(invite.getName());
	   	 if(pool !=null)
	   		 vo.setPools(pool.getNickName());
	}
}
