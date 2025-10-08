package io.renren.modules.core.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import io.renren.modules.core.entity.TransferRecordEntity;
import io.renren.modules.core.param.TransferParam;
import io.renren.modules.core.param.TransferRecordParam;
import io.renren.modules.core.vo.TransferRecordVO;

public interface TransferRecordService  extends IService<TransferRecordEntity> {
	
	Page<TransferRecordVO> listPage(TransferRecordParam param)throws Exception;
	
	BigDecimal sumRecord(TransferRecordParam param);
	
	BigDecimal sum(Date start,Date end,List<Long> poolsId);
	
	
	void autoTransfer(TransferParam param)throws Exception;
	
}
