package io.renren.modules.core.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.modules.core.entity.InviteEntity;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.mapper.InviteMapper;
import io.renren.modules.core.mapper.PoolsMapper;
import io.renren.modules.core.param.InviteParam;
import io.renren.modules.core.service.InviteService;
import io.renren.modules.core.vo.InviteVO;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.commons.collection.Lists;
@Slf4j
@Service
public class InviteServiceImpl extends ServiceImpl<InviteMapper, InviteEntity> implements InviteService {
	
	@Autowired
	PoolsMapper poolsMapper;
	
	@Override
	public Page<InviteVO> listPage(InviteParam param) {
		 Page<InviteEntity> pageObject = new Page<InviteEntity>(param.getCurrent(),param.getSize());
         pageObject.addOrder(OrderItem.desc("created"));
         LambdaQueryWrapper<InviteEntity> inviteQuery = new LambdaQueryWrapper<>();
         if(CollectionUtils.isNotEmpty(param.getPoolsIds())) {
        	 inviteQuery.in(InviteEntity::getPoolsId, param.getPoolsIds() );
         }
         Page<InviteEntity> page = this.page(pageObject, inviteQuery);
         List<InviteEntity> datas = page.getRecords();
         List<InviteVO> vos = Lists.newArrayList();
         for(InviteEntity manage : datas) {
        	 PoolsEntity pools = poolsMapper.selectById(manage.getPoolsId());
        	 InviteVO managerVO =  InviteVO.builder().id(manage.getId()).code(manage.getCode()).name(manage.getName()).inviteUrl(manage.getInviteUrl()).poolId(manage.getPoolsId()).poolName(pools.getNickName()).build();
        	 vos.add(managerVO);
         }
         Page<InviteVO> pageVo = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
         pageVo.setRecords(vos);
		return pageVo;
	}

	@Override
	public InviteVO get(Long id) {
		 InviteEntity invite =  this.getById(id);
		 if(invite ==null) return  InviteVO.builder().build();
		 PoolsEntity pools = poolsMapper.selectById(invite.getPoolsId());
		 if(pools !=null) {
			 InviteVO managerVO =  InviteVO.builder()
					 .code(invite.getCode())
					 .name(invite.getName())
					 .inviteUrl(invite.getInviteUrl())
					 .poolId(invite.getPoolsId())
					 .name(pools.getNickName())
					 .build();
			  return managerVO;
		 }
		 InviteVO managerVO =  InviteVO.builder()
				 .code(invite.getCode())
				 .name(invite.getName())
				 .inviteUrl(invite.getInviteUrl())
				 .build();
		 return managerVO;
			
	}
 

}
