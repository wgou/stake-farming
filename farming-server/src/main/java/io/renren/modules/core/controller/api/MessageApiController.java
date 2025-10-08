package io.renren.modules.core.controller.api;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.common.exception.RRException;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.R;
import io.renren.modules.constants.MessageStatusEnum;
import io.renren.modules.core.context.WalletConext;
import io.renren.modules.core.entity.MessageEntity;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.service.MessageService;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.socket.WebSocketServer;
import io.renren.modules.core.vo.CustomerMessageVO;
import io.renren.modules.core.vo.MessageVO;
import io.renren.modules.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.dongliu.commons.collection.Lists;

@Api
@RestController
@RequestMapping("/api/message")
public class MessageApiController {

	    @Autowired
	    MessageService messageService;
	    @Autowired
	    WalletsService walletsService;
	    @Autowired
	    PoolsService poolsService;
	    @Autowired
	    SysUserService sysUserService;
	    @Autowired
	    WebSocketServer socketServer;

	  
	    @ApiOperation("获取用户未读消息")
		@PostMapping("count")
	    public R getMessages() {
	    	String wallet = WalletConext.getWallet();
	    	WalletsEntity walletEntity  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, wallet));
			if(walletEntity == null) throw new RRException("Invalid wallet");
	    	int count = messageService.count(new LambdaQueryWrapper<MessageEntity>().eq(MessageEntity::getReciverId, walletEntity.getId()).eq(MessageEntity::getStatus, MessageStatusEnum.UNREDY.getStatus()));
	    	PoolsEntity pools = poolsService.getById(walletEntity.getPoolsId());
	    	return R.ok(new CustomerMessageVO(socketServer.getOnline(pools.getOwnerId()),count));
	    }
	    
 		@ApiOperation("用户自己消息列表 -- 最近7天")
 		@PostMapping("getMessage")
 		public R getMessage() throws Exception {
 			String wallet = WalletConext.getWallet();
	    	WalletsEntity walletEntity  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, wallet));
			if(walletEntity == null) throw new RRException("Invalid wallet");
 			List<MessageEntity> lists = messageService.list(new LambdaQueryWrapper<MessageEntity>()
 					.eq(MessageEntity::getReciverId, walletEntity.getId()).or().eq(MessageEntity::getSenderId, walletEntity.getId())
 				//	.ge(MessageEntity::getCreated, DateUtils.addDateDays(new Date(), -7))
 				//	.le(MessageEntity::getCreated, new Date())
 					.orderByAsc(MessageEntity::getCreated));
 			List<MessageVO> datas = Lists.newArrayList();
			for(MessageEntity message : lists) {
				MessageVO vo = new MessageVO().build(message);
				datas.add(vo);
			}
			
			messageService.update(new LambdaUpdateWrapper<MessageEntity>().set(MessageEntity::getStatus, MessageStatusEnum.REDYED.getStatus()).eq(MessageEntity::getReciverId, walletEntity.getId()));
 			return R.ok(datas);
 		}
}
