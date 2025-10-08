package io.renren.modules.core.param;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import net.dongliu.commons.collection.Lists;

@Data
public class PoolsParam {

	@ApiModelProperty("资金池ID - ADMIN 页面上除了下拉列表有此参数，默认不传 ; 客户端页面忽略此参数")
	private List<Long> poolsIds;
	
	
	private Long poolsId;
	
	
	
	public List<Long> getPoolsIds(){
		if(poolsIds !=null && poolsId !=null) {
			poolsIds.add(poolsId);
		}
		if(poolsId !=null) {
			poolsIds = Lists.newArrayList();
			poolsIds.add(poolsId);
		}
		return poolsIds;
	}
}
