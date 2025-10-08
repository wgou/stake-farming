package io.renren.modules.core.service;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import io.renren.modules.core.entity.WithDrawEntity;
import io.renren.modules.core.param.WithDrawIndexParam;
import io.renren.modules.core.param.WithDrawParam;
import io.renren.modules.core.vo.WithDrawVO;

public interface WithDrawService extends IService<WithDrawEntity> {
	
	Page<WithDrawVO> pageList(WithDrawParam param);
	
	void withDraw(WithDrawIndexParam param) throws Exception ;
	
	List<WithDrawEntity> query(WithDrawParam param);
	
	BigDecimal sum(String wallet);
	
	BigDecimal sum(WithDrawParam param);
	

}
