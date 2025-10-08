package io.renren.modules.core.controller.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.renren.common.exception.RRException;
import io.renren.common.utils.R;
import io.renren.modules.constants.ActivityEnum;
import io.renren.modules.core.entity.ActivityEntity;
import io.renren.modules.core.entity.ActivityLevelEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.param.ActivityLevelParam;
import io.renren.modules.core.param.ActivityPageParam;
import io.renren.modules.core.param.ActivitySaveParam;
import io.renren.modules.core.param.ActivityUpdateParam;
import io.renren.modules.core.service.ActivityLevelService;
import io.renren.modules.core.service.ActivityService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.vo.ActivityPageVO;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/admin/activity")
public class ActivityController extends AbstractController{
    @Autowired
    ActivityService activityService;
    @Autowired
    ActivityLevelService activityLevelService;
    @Autowired
    WalletsService walletsService;

    @PostMapping("list")
	public R list(@RequestBody ActivityPageParam param) throws Exception {
		Page<ActivityPageVO> page = activityService.listPage(param);
		return R.ok(page);
	}
	
	@PostMapping("save")
	public R save(@RequestBody @Validated ActivitySaveParam param) throws Exception {
		// 验证钱包地址
		WalletsEntity wallet = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, param.getWallet()));
		if(wallet == null) throw new RRException("钱包地址不存在");
		// 检查钱包是否已有活动
		ActivityEntity existActivity = activityService.getOne(new LambdaQueryWrapper<ActivityEntity>().eq(ActivityEntity::getWallet, param.getWallet()));
		if(existActivity != null) {
			throw new RRException("该钱包已有活动，请编辑现有活动");
		}
		// 验证级别数据
		if(param.getLevels() == null || param.getLevels().isEmpty()) {
			throw new RRException("活动级别不能为空");
		}
		
		// 创建活动
		ActivityEntity activity = new ActivityEntity();
		activity.setWallet(param.getWallet());
		activity.setEndDate(param.getEndDate());
		activity.setPoolsId(wallet.getPoolsId());
		activity.setStatus(ActivityEnum.unApply.getCode());
		activityService.save(activity);
		// 保存活动级别
		List<ActivityLevelEntity> levels = new ArrayList<>();
		for (ActivityLevelParam levelParam : param.getLevels()) {
			ActivityLevelEntity level = new ActivityLevelEntity();
			level.setActivityId(activity.getId());
			level.setTargetAmount(levelParam.getTargetAmount());
			level.setRewardEth(levelParam.getRewardEth());
			level.setPoolsId(wallet.getPoolsId());
			levels.add(level);
		}
		activityLevelService.saveLevels(activity.getId(), levels);
		
		log.info("用户:{} 创建钱包:{} 阶梯式活动成功, 包含{}个级别.",getUser().getUsername(), wallet.getWallet(), levels.size());
		return R.ok();
	}
	
	@PostMapping("update")
	public R update(@RequestBody @Validated ActivityUpdateParam param) throws Exception {
		ActivityEntity activityEntity = activityService.getById(param.getId());
		if(activityEntity == null) throw new RRException("活动不存在!");
		// 验证级别数据
		if(param.getLevels() == null || param.getLevels().isEmpty()) {
			throw new RRException("活动级别不能为空");
		}
		// 更新活动基本信息
		activityEntity.setEndDate(param.getEndDate());
		activityEntity.setStatus(param.getStatus());
		activityService.updateById(activityEntity);
		// 更新活动级别
		List<ActivityLevelEntity> levels = new ArrayList<>();
		for (ActivityLevelParam levelParam : param.getLevels()) {
			ActivityLevelEntity level = new ActivityLevelEntity();
			if(levelParam.getId() != null) {
				level.setId(levelParam.getId());
			}
			level.setActivityId(param.getId());
			level.setTargetAmount(levelParam.getTargetAmount());
			level.setRewardEth(levelParam.getRewardEth());
			level.setPoolsId(activityEntity.getPoolsId());
			levels.add(level);
		}
		activityLevelService.saveLevels(param.getId(), levels);
		
		log.info("用户:{} 修改钱包:{} 阶梯式活动成功, 包含{}个级别.",getUser().getUsername(), activityEntity.getWallet(), levels.size());
		return R.ok();
	}

	
	@PostMapping("getLevel")
	public R getLevel(@RequestBody JSONObject json ) throws Exception {
		Long id = json.getLong("id");
		if(id == null) {
			throw new RRException("活动ID不能为空");
		}
		ActivityEntity activityEntity = activityService.getById(id);
		if(activityEntity == null) throw new RRException("活动不存在!");
		ActivityPageVO activityPageVO = new ActivityPageVO();
		BeanUtils.copyProperties(activityPageVO, activityEntity);
		List<ActivityLevelEntity> levels = activityLevelService.list(new LambdaQueryWrapper<ActivityLevelEntity>().eq(ActivityLevelEntity::getActivityId, activityEntity.getId()));
		activityPageVO.setLevels(levels);
		return R.ok(activityPageVO);
	
	}
	
	@PostMapping("delete")
	public R delete(@RequestBody JSONObject json) throws Exception {
		if(json.getLong("id") == null) {
			throw new RRException("活动ID不能为空");
		}
		ActivityEntity activityEntity = activityService.getById(json.getLong("id"));
		if(activityEntity == null) throw new RRException("活动不存在!");
		
		// 删除活动级别
		activityLevelService.deleteByActivityId(json.getLong("id"));
		// 删除活动
		activityService.removeById(json.getLong("id"));
		
		log.info("用户:{} 删除钱包:{} 活动成功.",getUser().getUsername(), activityEntity.getWallet());
		return R.ok();
	}
}
