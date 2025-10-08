package io.renren.modules.core.service;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.param.RegisterWalletParam;
import io.renren.modules.core.param.WalletsPageParam;
import io.renren.modules.core.vo.WalletsVO;

public interface WalletsService extends IService<WalletsEntity> {
	
	
	Page<WalletsVO> listPage(WalletsPageParam param)throws Exception;
	
	WalletsVO get(Long id) throws Exception;
	
	BigDecimal summary();
	
	void register(HttpServletRequest request,RegisterWalletParam register);
	
	public BigDecimal sumApprove(List<Long> poolsId);
	
	
	void refresh(WalletsEntity wallet);
	

}
