package io.renren.modules.core.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
@Data
@TableName("s_activity")
public class ActivityEntity extends BaseEntity{
	
	private String wallet;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
	private Date applyDate; //申请时间
	
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
	private Date endDate; //活动结束时间
    
	private Integer status; //活动状态 0：未申请 1：已申请 2：已完成

}
