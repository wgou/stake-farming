package io.renren.modules.core.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.renren.modules.core.entity.ActivityLevelEntity;
import lombok.Data;

@Data
public class ActivityPageVO {
    
    private Long id;
    
    private String wallet;
    
    
    private String poolsName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Date applyDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Date endDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Date rewardDate;
    
    private Integer status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Date created;
    
    /**
     * 活动级别列表
     */
    private List<ActivityLevelEntity> levels;
    

}
