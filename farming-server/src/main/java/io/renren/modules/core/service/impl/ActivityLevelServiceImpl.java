package io.renren.modules.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.modules.core.entity.ActivityLevelEntity;
import io.renren.modules.core.mapper.ActivityLevelMapper;
import io.renren.modules.core.service.ActivityLevelService;

@Service
public class ActivityLevelServiceImpl extends ServiceImpl<ActivityLevelMapper, ActivityLevelEntity> implements ActivityLevelService {

    @Override
    public List<ActivityLevelEntity> getByActivityId(Long activityId) {
        return this.list(new LambdaQueryWrapper<ActivityLevelEntity>()
                .eq(ActivityLevelEntity::getActivityId, activityId)
                .orderByAsc(ActivityLevelEntity::getTargetAmount));
    }

    @Override
    public void deleteByActivityId(Long activityId) {
        this.remove(new LambdaQueryWrapper<ActivityLevelEntity>()
                .eq(ActivityLevelEntity::getActivityId, activityId));
    }

    @Override
    public void saveLevels(Long activityId, List<ActivityLevelEntity> levels) {
        // 先删除原有级别
        deleteByActivityId(activityId);
        // 保存新级别
        for (ActivityLevelEntity level : levels) {
            level.setActivityId(activityId);
        }
        this.saveBatch(levels);
    }
} 