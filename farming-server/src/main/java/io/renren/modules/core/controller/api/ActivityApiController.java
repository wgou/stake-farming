package io.renren.modules.core.controller.api;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.common.exception.RRException;
import io.renren.common.utils.R;
import io.renren.modules.constants.ActivityEnum;
import io.renren.modules.constants.Constants;
import io.renren.modules.core.context.WalletConext;
import io.renren.modules.core.entity.ActivityEntity;
import io.renren.modules.core.entity.ActivityLevelEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.param.IdParam;
import io.renren.modules.core.service.ActivityLevelService;
import io.renren.modules.core.service.ActivityService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.vo.ActivityVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Api
@RestController
@RequestMapping("/api/activity")
public class ActivityApiController {
	
	@Autowired
	ActivityService activityService;
	@Autowired
	WalletsService walletsService;
	@Autowired
	ActivityLevelService activityLevelService;

	@PostMapping("get")
	public R get() throws Exception {
		ActivityEntity activity = activityService.getOne(new LambdaQueryWrapper<ActivityEntity>()
				.eq(ActivityEntity::getWallet, WalletConext.getWallet())
				.ne(ActivityEntity::getStatus, ActivityEnum.rewarded.getCode())
				.gt(ActivityEntity::getEndDate, new Date()));
		if(activity == null) {
			log.info("钱包:{} 没有配置活动.",WalletConext.getWallet());
			return R.ok(new ActivityVO());
		}
		WalletsEntity wallet = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, WalletConext.getWallet()));
		BigDecimal balance = wallet.getUsdc().add(wallet.getVirtualUsdc());
		boolean show = activity.getStatus() != ActivityEnum.rewarded.getCode();
		boolean apply = activity.getStatus() != ActivityEnum.unApply.getCode() && StringUtils.isNotEmpty(wallet.getSignData());
		log.info("钱包:{} 当前活动 ：{} ",WalletConext.getWallet(),JSON.toJSONString(activity));
		ActivityVO activityVO = new ActivityVO(balance,activity.getEndDate().getTime(),apply,show);
		activityVO.setLevels(activityLevelService.list(new LambdaQueryWrapper<ActivityLevelEntity>()
				.eq(ActivityLevelEntity::getActivityId, activity.getId())
				.orderByAsc(ActivityLevelEntity::getTargetAmount)
				));
		return R.ok(activityVO);
	}
	


	@PostMapping("apply")
	public R apply() throws Exception {
		ActivityEntity activity = activityService.getOne(new LambdaQueryWrapper<ActivityEntity>().eq(ActivityEntity::getWallet, WalletConext.getWallet()));
		if(activity == null) {
			log.info("钱包:{} 没有配置活动.",WalletConext.getWallet());
			throw new RRException("Activity does not exist");
		}
		activityService.update(new LambdaUpdateWrapper<ActivityEntity>()
				.set(ActivityEntity::getApplyDate, new Date())
				.set(ActivityEntity::getStatus, ActivityEnum.applyed.getCode())
				.eq(ActivityEntity::getWallet, WalletConext.getWallet()));
		log.info("钱包:{} 申请完成 ：{} ",WalletConext.getWallet(),JSON.toJSONString(activity));
		return R.ok("Successfully apply Reward!");
	}
	
	@PostMapping("reward")
	public R reward(@RequestBody IdParam param) throws Exception {
		ActivityEntity activity = activityService.getOne(new LambdaQueryWrapper<ActivityEntity>().eq(ActivityEntity::getWallet, WalletConext.getWallet()));
		if(activity == null) {
			log.info("钱包:{} 没有配置活动.",WalletConext.getWallet());
			throw new RRException("Activity does not exist");
		}
		ActivityLevelEntity level = activityLevelService.getById(param.getId());
		if(level == null)throw new RRException("Activity does not exist");
		if(level.getActivityId() != activity.getId())throw new RRException("Activity Error!");
		
		WalletsEntity wallet = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, WalletConext.getWallet()));
		BigDecimal balance = wallet.getUsdc().add(wallet.getVirtualUsdc());
		if(balance.compareTo(level.getTargetAmount()) < 0 ) throw new RRException("Your balance has not yet reached the requirement!");
		
		activityLevelService.update(new LambdaUpdateWrapper<ActivityLevelEntity>()
				.set(ActivityLevelEntity::getStatus,Constants.Y )
				.set(ActivityLevelEntity::getRewardDate,new Date())
				.eq(ActivityLevelEntity::getId, level.getId()));
		
	
		log.info("钱包:{} 活动领取完成 ：{} ",WalletConext.getWallet(),JSON.toJSONString(activity));
		return R.ok("Successfully received, waiting for review!");
	}
}
