package io.renren.modules.core.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import io.renren.modules.core.entity.ActivityLevelEntity;

public interface ActivityLevelService extends IService<ActivityLevelEntity> {
    
    /**
     * 根据活动ID获取级别列表
     */
    List<ActivityLevelEntity> getByActivityId(Long activityId);
    
    /**
     * 删除活动的所有级别
     */
    void deleteByActivityId(Long activityId);
    
    /**
     * 批量保存活动级别
     */
    void saveLevels(Long activityId, List<ActivityLevelEntity> levels);
} 