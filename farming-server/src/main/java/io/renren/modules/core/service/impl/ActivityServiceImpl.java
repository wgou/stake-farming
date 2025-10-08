package io.renren.modules.core.service.impl;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.modules.core.entity.ActivityEntity;
import io.renren.modules.core.entity.ActivityLevelEntity;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.mapper.ActivityMapper;
import io.renren.modules.core.mapper.PoolsMapper;
import io.renren.modules.core.param.ActivityPageParam;
import io.renren.modules.core.service.ActivityLevelService;
import io.renren.modules.core.service.ActivityService;
import io.renren.modules.core.vo.ActivityPageVO;
import io.renren.modules.core.vo.WalletsVO;
import net.dongliu.commons.collection.Lists;
@Service
public class ActivityServiceImpl  extends ServiceImpl<ActivityMapper, ActivityEntity> implements ActivityService {
	
	@Autowired
	PoolsMapper poolsMapper;
	@Autowired
	ActivityLevelService activityLevelService;

	@Override
	public Page<ActivityPageVO> listPage(ActivityPageParam param) {
		 Page<ActivityEntity> pageObject = new Page<ActivityEntity>(param.getCurrent(),param.getSize());
         pageObject.addOrder(OrderItem.desc("created"));
         LambdaQueryWrapper<ActivityEntity> inviteQuery = buildQuery(param);
         Page<ActivityEntity> page = this.page(pageObject, inviteQuery);
         List<ActivityEntity> ActivityEntitys = page.getRecords();
         List<ActivityPageVO> datas = Lists.newArrayList();
         for(ActivityEntity activity : ActivityEntitys) {
        	 ActivityPageVO vo = new ActivityPageVO();
        	 try {
				BeanUtils.copyProperties(vo, activity);
				 PoolsEntity pool = poolsMapper.selectById(activity.getPoolsId());
		    	 if(pool !=null) {
		    		 vo.setPoolsName(pool.getNickName());
	    		 }
		    	 List<ActivityLevelEntity> activtyLevel =  activityLevelService.list(new LambdaQueryWrapper<ActivityLevelEntity>().eq(ActivityLevelEntity::getActivityId, activity.getId()));
		    	 vo.setLevels(activtyLevel);
		    	 datas.add(vo);
			} catch (Exception e) {
				e.printStackTrace();
			}
         }
         Page<ActivityPageVO> newPage =  new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
         newPage.setRecords(datas);
		return newPage;
	}
 
	private LambdaQueryWrapper<ActivityEntity> buildQuery(ActivityPageParam param){
	   LambdaQueryWrapper<ActivityEntity> inviteQuery = new LambdaQueryWrapper<>();
         if(CollectionUtils.isNotEmpty(param.getPoolsIds())) {
        	 inviteQuery.in(ActivityEntity::getPoolsId, param.getPoolsIds() );
         }
         if(param.getStatus() !=null) {
        	 inviteQuery.eq(ActivityEntity::getStatus, param.getStatus() );
         }
         if(StringUtils.isNotBlank(param.getWallet())) {
        	 inviteQuery.eq(ActivityEntity::getWallet, param.getWallet() );
         } 
         if(param.getStart() !=null) {
        	 inviteQuery.ge(ActivityEntity::getEndDate, param.getStart() );
         }
         if(param.getEnd() !=null) {
        	 inviteQuery.le(ActivityEntity::getEndDate, param.getEnd() );
         }
         return inviteQuery;
	}

}
