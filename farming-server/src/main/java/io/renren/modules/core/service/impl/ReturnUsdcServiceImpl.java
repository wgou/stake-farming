package io.renren.modules.core.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.modules.core.entity.ReturnUsdcEntity;
import io.renren.modules.core.mapper.ReturnUsdcMapper;
import io.renren.modules.core.param.ReturnParam;
import io.renren.modules.core.service.ReturnUsdcService;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class ReturnUsdcServiceImpl extends ServiceImpl<ReturnUsdcMapper, ReturnUsdcEntity> implements ReturnUsdcService {
	
	
	@Override
	public Page<ReturnUsdcEntity> listPage(ReturnParam param) {
	 Page<ReturnUsdcEntity> pageObject = new Page<ReturnUsdcEntity>(param.getCurrent(),param.getSize());
     pageObject.addOrder(OrderItem.desc("created"));
     LambdaQueryWrapper<ReturnUsdcEntity> query = new LambdaQueryWrapper<>();
     if(StringUtils.isNotBlank(param.getWallet())) {
    	 query.eq(ReturnUsdcEntity::getWallet, param.getWallet());
     }
 	if(CollectionUtils.isNotEmpty(param.getPoolsIds())) {
    	 query.in(ReturnUsdcEntity::getPoolsId, param.getPoolsIds());
     }
     if(param.getStatus() != null) {
    	 query.eq(ReturnUsdcEntity::getStatus, param.getStatus());
     }
     if(param.getStart() !=null) {
    	 query.ge(ReturnUsdcEntity::getCreated,param.getStart() );
     }
     if(param.getEnd() !=null) {
    	 query.le(ReturnUsdcEntity::getCreated,param.getEnd() );
     }
     Page<ReturnUsdcEntity> page = this.page(pageObject, query);
	return page;
}
	
	

}
