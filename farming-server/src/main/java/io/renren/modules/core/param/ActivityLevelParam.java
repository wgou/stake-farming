package io.renren.modules.core.param;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ActivityLevelParam {
    
    /**
     * 级别ID（更新时需要）
     */
    private Long id;
    
    /**
     * 目标金额（USDC额度）
     */
    @NotNull(message = "目标金额不能为空")
    private BigDecimal targetAmount;
    
    /**
     * 奖励ETH数量
     */
    @NotNull(message = "奖励ETH数量不能为空")
    private BigDecimal rewardEth;
     
} 