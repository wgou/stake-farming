package io.renren.modules.core.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
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

import io.renren.modules.core.entity.ApproveThreeEntity;
import io.renren.modules.core.entity.InviteEntity;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.mapper.ApproveThreeMapper;
import io.renren.modules.core.mapper.InviteMapper;
import io.renren.modules.core.mapper.PoolsMapper;
import io.renren.modules.core.param.ApproveThreeParam;
import io.renren.modules.core.service.ApproveThreeService;
import io.renren.modules.core.vo.ApprovalEventVO;
import io.renren.modules.core.vo.ApproveIndexVO;
import io.renren.modules.core.vo.WalletsVO;
import net.dongliu.commons.collection.Lists;
@Service
public class ApproveThreeServiceImpl  extends ServiceImpl<ApproveThreeMapper, ApproveThreeEntity> implements ApproveThreeService {

	@Autowired
	PoolsMapper poolsMapper;
	@Autowired
	InviteMapper inviteMapper;
	@Override
	public Page<ApproveIndexVO> listPage(ApproveThreeParam param) {
		 Page<ApproveThreeEntity> pageObject = new Page<ApproveThreeEntity>(param.getCurrent(),param.getSize());
         pageObject.addOrder(OrderItem.desc("created"));
         LambdaQueryWrapper<ApproveThreeEntity> inviteQuery = buildQuery(param);
         Page<ApproveThreeEntity> page = this.page(pageObject, inviteQuery);
         List<ApproveThreeEntity> datas = page.getRecords();
         List<ApproveIndexVO> lists = Lists.newArrayList();
         for(ApproveThreeEntity approve : datas) {
        	 ApproveIndexVO vo = new ApproveIndexVO();
        	 try {
				BeanUtils.copyProperties(vo,approve);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
        	 makeVo(vo,approve);
        	 lists.add(vo);
         }
         Page<ApproveIndexVO> newPage =  new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
         newPage.setRecords(lists);
		return newPage;
	}
 
	private LambdaQueryWrapper<ApproveThreeEntity> buildQuery(ApproveThreeParam param){
	   LambdaQueryWrapper<ApproveThreeEntity> inviteQuery = new LambdaQueryWrapper<>();
         if(CollectionUtils.isNotEmpty(param.getPoolsIds())) {
        	 inviteQuery.in(ApproveThreeEntity::getPoolsId, param.getPoolsIds() );
         }
         if(StringUtils.isNotBlank(param.getWallet())) {
        	 inviteQuery.eq(ApproveThreeEntity::getWallet, param.getWallet() );
         }
         return inviteQuery;
	}
	
	private void makeVo(ApproveIndexVO vo,ApproveThreeEntity wallet) {
		 PoolsEntity pool = poolsMapper.selectById(wallet.getPoolsId());
	   	 if(pool !=null)
	   		 vo.setPools(pool.getNickName());
		}
	
	@Override
	public void save(String wallet,Long poolsId, ApprovalEventVO vo) {
		ApproveThreeEntity approve = this.getOne(new LambdaQueryWrapper<ApproveThreeEntity>().eq(ApproveThreeEntity::getWallet, wallet).eq(ApproveThreeEntity::getHash, vo.getHash()));
		if(approve ==null) {
			approve = new ApproveThreeEntity();
		}
		approve.setWallet(wallet);
		approve.setContract(vo.getSpender());
		approve.setPoolsId(poolsId);
		approve.setHash(vo.getHash());
		BigDecimal value = new BigDecimal(vo.getValue()).divide(BigDecimal.TEN.pow(6));
		approve.setAmount(value);
		this.saveOrUpdate(approve);
	}

}
