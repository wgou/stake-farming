package io.renren.modules.core.controller.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import io.renren.common.utils.R;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.RewardEntity;
import io.renren.modules.core.param.IndexRewardParam;
import io.renren.modules.core.service.InviteService;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.RewardService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.vo.IndexVO;
import io.renren.modules.core.vo.RewardIndexVO;
import io.renren.modules.utils.StakeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.commons.collection.Lists;
@Slf4j
@Api
@RestController
@RequestMapping("/api/index")
public class IndexApiController {

	@Autowired
	PoolsService poolsService;
	@Autowired
	WalletsService walletsService;
	@Autowired
	RewardService rewardService;
	@Autowired
	InviteService inviteService;
	@Autowired
	ContractHandler handler;
	
	Integer defaultNode = 190;
	Integer defaultParticipants = 77000;
	BigDecimal defaultVerified = new BigDecimal("8700000");
	BigDecimal defaultReward = new BigDecimal("2100");
	
	
	@ApiOperation("首页数据统计")
	@PostMapping("index")
	public R index() throws Exception {
		Integer nodes = defaultNode + poolsService.count();
		Integer participants = defaultParticipants + walletsService.count();
		BigDecimal verified= defaultVerified.add(walletsService.summary());
		BigDecimal reward= defaultReward.add(rewardService.sumReward());
		String usdcVerified = StakeUtils.formatBigDecimal(verified);
		String ethReward =  StakeUtils.formatBigDecimal(reward);
		return R.ok(new IndexVO(nodes,participants,usdcVerified,ethReward));
	}
	
	@ApiOperation("首页奖励滚动列表")
	@PostMapping("rewards")
	public R rewards(@ApiParam(value = "本次取几条数据,第一次取10 ,之后每隔2s 取 1") @RequestBody(required = false) IndexRewardParam param) throws Exception {
		if(param == null || param.getCount() == null) return R.ok(Lists.newArrayList());
		List<RewardEntity> rewards = rewardService.getRandomRewardEntities(param.getCount());
	    List<Object> rewardVO = rewards.stream() .map(reward ->
	    	{
				try {
					return new RewardIndexVO(reward.getWallet(), reward.getRewardEth());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return new ArrayList<RewardIndexVO>();
			}).collect(Collectors.toList());
	    return R.ok(rewardVO);
		
	}
	
 
	
}
