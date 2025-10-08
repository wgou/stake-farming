package io.renren.modules.core.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.renren.common.utils.R;
import io.renren.modules.core.param.RewardParam;
import io.renren.modules.core.service.RewardService;

@RestController
@RequestMapping("/admin/reward")
public class RewadController extends AbstractController{

	@Autowired
	RewardService rewardService;
	
	@PostMapping("list")
	public R list(@RequestBody RewardParam param) throws Exception {
		 return R.ok(rewardService.listPage(param));
	}
	@PostMapping("get")
	public R get(String wallet) throws Exception {
		 return R.ok(rewardService.get(wallet));
	} 
}
