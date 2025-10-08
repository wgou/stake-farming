package io.renren.modules.core.controller.admin;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.common.exception.RRException;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.R;
import io.renren.modules.constants.MessageStatusEnum;
import io.renren.modules.constants.MessageTypeEnum;
import io.renren.modules.core.entity.InviteEntity;
import io.renren.modules.core.entity.MessageEntity;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.param.AdminMessageParam;
import io.renren.modules.core.param.MessageDelParam;
import io.renren.modules.core.service.InviteService;
import io.renren.modules.core.service.MessageService;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.socket.WebSocketServer;
import io.renren.modules.core.vo.MessageUserVO;
import io.renren.modules.core.vo.MessageVO;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.commons.collection.Lists;
@Slf4j
@RestController
@RequestMapping("/admin/message")
public class MessageController extends AbstractController{

	    @Autowired
	    MessageService messageService;
	    @Autowired
	    WalletsService walletsService;
	    @Autowired
	    PoolsService poolsService;
	    @Autowired
	    WebSocketServer socketServer;
	    @Autowired
	    InviteService inviteService;
	    
		@PostMapping("listUser")
		public R listUser() throws Exception {
			LambdaQueryWrapper<WalletsEntity> query = new LambdaQueryWrapper<WalletsEntity>();
			List<PoolsEntity> pools = null;
			if(getUser().getUserId() == io.renren.common.utils.Constant.SUPER_ADMIN) {
				pools = poolsService.list(new LambdaQueryWrapper<PoolsEntity>());
			}else {
				pools = poolsService.list(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getOwnerId, getUser().getUserId()).or().eq(PoolsEntity::getCreateUserId, getUser().getUserId()));
			}
			if(pools == null)throw new RRException("The current user has not created a fund pool");
			List<Long> poolsIds = pools.stream().map(PoolsEntity::getId).collect(Collectors.toList());
			//query.eq(WalletsEntity::getReals, RealEnum.real.getCode());
			query.in(WalletsEntity::getPoolsId, poolsIds);
			List<MessageUserVO> messageUserVOList =  Lists.newArrayList();
			List<WalletsEntity> lists = walletsService.list(query);
			for(WalletsEntity wallet : lists ) {
				LambdaQueryWrapper<MessageEntity> lambdQuery = new LambdaQueryWrapper<MessageEntity>();
				lambdQuery.in(MessageEntity::getReciverId, poolsIds);
				lambdQuery.eq(MessageEntity::getSenderId, wallet.getId());
				lambdQuery.eq(MessageEntity::getStatus, MessageStatusEnum.UNREDY.getStatus());
				int count = messageService.count(lambdQuery);
				MessageEntity lastMsg = messageService.getOne(new LambdaQueryWrapper<MessageEntity>()
						.eq(MessageEntity::getSenderId, wallet.getId()).or().eq(MessageEntity::getReciverId, wallet.getId())
						.orderByDesc(MessageEntity::getCreated).last(" limit 1"));
				//if(lastMsg == null) continue ;
				PoolsEntity pool = poolsService.getById(wallet.getPoolsId());
				InviteEntity invite = inviteService.getById(wallet.getInviteId());
				
				MessageUserVO message = new MessageUserVO(wallet.getId(), wallet.getWallet(),pool.getOwnerId(),pool.getOwnerName(), invite!=null ? invite.getName() : "",
						socketServer.getOnline(wallet.getId()),count,lastMsg!=null? DateUtils.format(lastMsg.getCreated(),DateUtils.DATE_TIME_PATTERN ): null);
				messageUserVOList.add(message);
			}
			messageUserVOList.sort(Comparator
		                .comparing(MessageUserVO::getLastDate, Comparator.nullsLast(Comparator.reverseOrder())) // 按 lastDate 倒序
		        );
			 
			return R.ok(messageUserVOList);
		}
		
	   
		@PostMapping("getMessage")
		public R getMessage(@RequestBody AdminMessageParam param) throws Exception {
			WalletsEntity wallet = walletsService.getById(param.getId());
			if(wallet == null) throw new RRException("User Not Exits");
			//用户的所有消息
			List<MessageEntity> lists = messageService.list(new LambdaQueryWrapper<MessageEntity>()
					.eq(MessageEntity::getSenderId, wallet.getId()).or().eq(MessageEntity::getReciverId, wallet.getId())
					//.ge(MessageEntity::getCreated, DateUtils.addDateDays(new Date(), -7))
					//.le(MessageEntity::getCreated, new Date())
					.orderByAsc(MessageEntity::getCreated));
			List<MessageVO> datas = Lists.newArrayList();
			for(MessageEntity message : lists) {
				MessageVO vo = new MessageVO().build(message);
				datas.add(vo);
			}
			PoolsEntity pools = poolsService.getOne(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getOwnerId, getUser().getUserId()));
			if(pools == null)throw new RRException("The current user has not created a fund pool");
			if(pools.getId() == wallet.getPoolsId()) {
				messageService.update(new LambdaUpdateWrapper<MessageEntity>()
					.set(MessageEntity::getStatus, MessageStatusEnum.REDYED.getStatus())
					.eq(MessageEntity::getSenderId, wallet.getId())
					.eq(MessageEntity::getReciverId, pools.getOwnerId()));
			}
			return R.ok(datas);
		}
		
		
		@PostMapping("unRead")
		public R unRead() throws Exception {
			PoolsEntity pools = poolsService.getOne(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getOwnerId, getUser().getUserId()));
			if(pools == null)throw new RRException("The current user has not created a fund pool");
			int count = messageService.count(new LambdaQueryWrapper<MessageEntity>()
					.eq(MessageEntity::getReciverId, pools.getOwnerId())
					.eq(MessageEntity::getStatus, MessageStatusEnum.UNREDY.getStatus()));
			return R.ok(count);
		}
		
		
		@PostMapping("del")
		public R del(@RequestBody @Validated MessageDelParam param) throws Exception {
			if(CollectionUtils.isNotEmpty(param.getMessageIds())) {
				List<MessageEntity> messages = messageService.list(new LambdaQueryWrapper<MessageEntity>().in(MessageEntity::getMessageId, param.getMessageIds()));
//				log.info("delete:{}",messages.size());
//				for(MessageEntity message : messages) {
//					log.info("删除消息:{}",JSON.toJSONString(message));
//					socketServer.sendMessageToUser(message.getMessageId(), message.getSenderId(), message.getReciverId(),"" , MessageTypeEnum.DElETE.getType());
//				}
				if(CollectionUtils.isNotEmpty(messages)) {
					List<Long> ids = messages.stream().map(MessageEntity::getId).collect(Collectors.toList());
					messageService.removeByIds(ids);
				}
			}
			return R.ok();
		}
		
		
		
		
}
