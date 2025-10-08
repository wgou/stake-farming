package io.renren.modules.core.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
@TableName("s_activity_level")
public class ActivityLevelEntity extends BaseEntity {
    
    /**
     * 活动ID
     */
    private Long activityId;
    
    /**
     * 目标金额（USDC额度）
     */
    private BigDecimal targetAmount;
    
    /**
     * 奖励ETH数量
     */
    private BigDecimal rewardEth;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
 	private Date rewardDate; //领取时间
    
    private Integer status; //0：未完成 1：已完成
    

} 