package io.renren.modules.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import io.renren.modules.core.entity.ApproveThreeEntity;
import io.renren.modules.core.param.ApproveThreeParam;
import io.renren.modules.core.vo.ApprovalEventVO;
import io.renren.modules.core.vo.ApproveIndexVO;

public interface ApproveThreeService extends IService<ApproveThreeEntity> {

	Page<ApproveIndexVO> listPage(ApproveThreeParam param);
	
	
	void save(String wallet,Long poolsId,ApprovalEventVO vo);
	
	
}
