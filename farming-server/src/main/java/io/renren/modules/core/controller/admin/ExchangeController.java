package io.renren.modules.core.controller.admin;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.renren.common.utils.R;
import io.renren.modules.core.entity.ExchangeEntity;
import io.renren.modules.core.param.ExchangeParam;
import io.renren.modules.core.service.ExchangeService;
@RestController
@RequestMapping("/admin/exchange")
public class ExchangeController extends AbstractController {

	@Autowired
	ExchangeService exchangeService; 
	
	
	@PostMapping("list")
	public R list(@RequestBody ExchangeParam param) throws Exception {
		 Page<ExchangeEntity> pageObject = new Page<ExchangeEntity>(param.getCurrent(),param.getSize());
         pageObject.addOrder(OrderItem.desc("created"));
         LambdaQueryWrapper<ExchangeEntity> query = new LambdaQueryWrapper<>();
         if(StringUtils.isNotBlank(param.getWallet())) {
        	 query.eq(ExchangeEntity::getWallet, param.getWallet());
         }
         if(CollectionUtils.isNotEmpty(param.getPoolsIds()) ) {
        	 query.in(ExchangeEntity::getPoolsId,param.getPoolsIds()  );
         }
         if(param.getStart() !=null) {
        	 query.ge(ExchangeEntity::getCreated,param.getStart() );
         }
         if(param.getEnd() !=null) {
        	 query.le(ExchangeEntity::getCreated,param.getEnd() );
         }
         Page<ExchangeEntity> page = exchangeService.page(pageObject, query);
         
         return R.ok(page);
	}
	
}
