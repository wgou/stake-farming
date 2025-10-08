package io.renren.modules.core.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.renren.common.utils.R;
import io.renren.modules.core.param.ApproveThreeParam;
import io.renren.modules.core.service.ApproveThreeService;
import io.renren.modules.core.vo.ApproveIndexVO;

@RestController
@RequestMapping("/admin/approve")
public class ApproveThreeController extends AbstractController{

	@Autowired
	ApproveThreeService approveThreeService;
	 
	
	@PostMapping("list")
	public R list(@RequestBody ApproveThreeParam param) throws Exception {
		Page<ApproveIndexVO> page = approveThreeService.listPage(param);
		 return R.ok(page);
	}
	
  
	
}
