package io.renren.modules.core.param;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ActivitySaveParam {
    
    /**
     * 钱包地址
     */
    @NotBlank(message = "钱包地址不能为空")
    private String wallet;
    
    /**
     * 活动结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Date endDate;
    
    /**
     * 活动级别列表
     */
    @NotEmpty(message = "活动级别不能为空")
    @Valid
    private List<ActivityLevelParam> levels;
} 