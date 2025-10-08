package io.renren.modules.core.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import io.renren.modules.core.entity.ProxyWithdrawEntity;
import io.renren.modules.core.param.ProxyReportParam;
import io.renren.modules.core.param.ProxyWithdrawPageParam;
import io.renren.modules.core.vo.ProxyReportVO;

public interface ProxyWithdrawService extends IService<ProxyWithdrawEntity> {

	
	Page<ProxyWithdrawEntity> listPage(ProxyWithdrawPageParam param);
	
	
	
	List<ProxyReportVO> reportList(ProxyReportParam param);
	
	
	
}
