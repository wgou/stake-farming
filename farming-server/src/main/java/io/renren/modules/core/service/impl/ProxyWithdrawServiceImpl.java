package io.renren.modules.core.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.modules.constants.RealEnum;
import io.renren.modules.constants.TransferEnum;
import io.renren.modules.constants.WithdrawStatusEnum;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.ProxyWithdrawEntity;
import io.renren.modules.core.mapper.ProxyWithdrawMapper;
import io.renren.modules.core.param.ProxyReportParam;
import io.renren.modules.core.param.ProxyWithdrawPageParam;
import io.renren.modules.core.param.TransferRecordParam;
import io.renren.modules.core.param.WithDrawParam;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.ProxyWithdrawService;
import io.renren.modules.core.service.TransferRecordService;
import io.renren.modules.core.service.WithDrawService;
import io.renren.modules.core.vo.ProxyReportVO;
import net.dongliu.commons.collection.Lists;
@Service
public class ProxyWithdrawServiceImpl  extends ServiceImpl<ProxyWithdrawMapper, ProxyWithdrawEntity> implements ProxyWithdrawService {
	
	@Autowired
	PoolsService poolsService;
	@Autowired
	TransferRecordService transferRecordService;
	@Autowired
	WithDrawService withDrawService;

	@Override
	public Page<ProxyWithdrawEntity> listPage(ProxyWithdrawPageParam param) {
		 Page<ProxyWithdrawEntity> pageObject = new Page<ProxyWithdrawEntity>(param.getCurrent(),param.getSize());
         pageObject.addOrder(OrderItem.desc("created"));
         LambdaQueryWrapper<ProxyWithdrawEntity> inviteQuery = new LambdaQueryWrapper<>();
         if(param.getStatus() !=null) {
        	 inviteQuery.eq(ProxyWithdrawEntity::getStatus, param.getStatus() );
         }
         if(StringUtils.isNotBlank(param.getProxyAccount())) {
        	 inviteQuery.eq(ProxyWithdrawEntity::getProxyAccount, param.getProxyAccount() );
         }
         if(StringUtils.isNotBlank(param.getFromWallet())) {
        	 inviteQuery.eq(ProxyWithdrawEntity::getFromWallet, param.getFromWallet() );
         }
         if(StringUtils.isNotBlank(param.getToWallet())) {
        	 inviteQuery.eq(ProxyWithdrawEntity::getToWallet, param.getToWallet() );
         }
         if(param.getStart() !=null) {
        	 inviteQuery.ge(ProxyWithdrawEntity::getCreated, param.getStart() );
         }
         if(param.getEnd() !=null) {
        	 inviteQuery.le(ProxyWithdrawEntity::getCreated, param.getEnd() );
         }
         Page<ProxyWithdrawEntity> page = this.page(pageObject, inviteQuery);
		return page;
	}

	@Override
	public List<ProxyReportVO> reportList(ProxyReportParam param) {
		List<PoolsEntity> data = Lists.newArrayList();
		if(param.getParentPoolsId() == null) {
			data.add(poolsService.getOne(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getId, param.getPoolsId())));
		}else {
			data = poolsService.list(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getPId, param.getParentPoolsId() ));
		} 
		List<ProxyReportVO> reportData = Lists.newArrayList();
		for(PoolsEntity pools : data) {
			ProxyReportVO report = poolsProfit(pools,param.getStart(),param.getEnd());
			List<PoolsEntity> childPools = poolsService.list(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getPId, pools.getId() ));
			report.setHasChildren(CollectionUtils.isNotEmpty(childPools));
			BigDecimal childRebate = BigDecimal.ZERO;
			for(PoolsEntity child : childPools) {
				ProxyReportVO childReport = poolsProfit(child,param.getStart(),param.getEnd());//每一个下级的净收入
				childRebate = childRebate.add(childReport.getParentRebate());
			}
			report.setChildRebate(childRebate);
			reportData.add(report);
		}
		  // 按 usdc 从大到小排序
        reportData.sort(Comparator.comparing(ProxyReportVO::getUsdc, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
		return reportData;
	}
	
	
	
	private ProxyReportVO poolsProfit(PoolsEntity pools,Date start ,Date end) {

		ProxyReportVO report = new ProxyReportVO();
		report.setPoolsId(pools.getId());
		report.setParentPoolsName(pools.getPNickName());
		report.setPoolsName(pools.getNickName());
		
		TransferRecordParam recordParam = new TransferRecordParam();
		recordParam.setPoolsIds(Lists.newArrayList(pools.getId()));
		recordParam.setStatus(TransferEnum.yes.getCode());
		recordParam.setStart(start);
		recordParam.setEnd(end);
		BigDecimal totalUsdc = transferRecordService.sumRecord(recordParam);
		report.setUsdc(totalUsdc);
		
		
		WithDrawParam withParam = new WithDrawParam();
		withParam.setPoolsIds(Lists.newArrayList(pools.getId()));
		withParam.setStatus(WithdrawStatusEnum.Success.getCode());
		withParam.setReal(RealEnum.real.getCode());
		withParam.setStart(start);
		withParam.setEnd(end);
		BigDecimal totalWithDraw = withDrawService.sum(withParam);
		report.setWithdraw(totalWithDraw);
		
		BigDecimal totalProfit = totalUsdc.subtract(totalWithDraw); 
		BigDecimal rebate = pools.getPId() ==null || totalProfit.compareTo(BigDecimal.ZERO) <=0 ? BigDecimal.ZERO : totalProfit.multiply(pools.getRebate()).divide(new BigDecimal(100),2,RoundingMode.HALF_DOWN);
		report.setParentRebate(rebate); //上级返利
		
		return report;
		
		
	}
 

}
