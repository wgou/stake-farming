package io.renren.modules.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import io.renren.modules.core.entity.InviteEntity;
import io.renren.modules.core.param.InviteParam;
import io.renren.modules.core.vo.InviteVO;

public interface InviteService extends IService<InviteEntity> {

	
	Page<InviteVO> listPage(InviteParam param);
	
	
	InviteVO get(Long id);
}
