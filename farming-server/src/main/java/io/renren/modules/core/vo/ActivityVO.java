package io.renren.modules.core.vo;

import java.math.BigDecimal;
import java.util.List;

import io.renren.modules.core.entity.ActivityLevelEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class ActivityVO {
	
	private BigDecimal usdc;
	
	private Long endTime; //结束时间
	
	private Boolean isApply = false; //是否申请
	
	private Boolean isShow = false; //是否显示
	
   private List<ActivityLevelEntity> levels;
   
   public ActivityVO(BigDecimal usdc,Long endTime,Boolean isApply,Boolean isShow) {
	   this.usdc = usdc;
	   this.endTime = endTime;
	   this.isApply = isApply;
	   this.isShow = isShow;
	   
   }
}
