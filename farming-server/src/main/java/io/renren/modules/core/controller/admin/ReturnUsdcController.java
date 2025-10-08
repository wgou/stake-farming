package io.renren.modules.core.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.renren.common.utils.R;
import io.renren.modules.core.entity.ReturnUsdcEntity;
import io.renren.modules.core.param.ReturnParam;
import io.renren.modules.core.service.ReturnUsdcService;

@RestController
@RequestMapping("/admin/return")
public class ReturnUsdcController extends AbstractController{

	@Autowired
	ReturnUsdcService returnUsdcService;
	
	@PostMapping("list")
	public R list(@RequestBody ReturnParam param) throws Exception {
		Page<ReturnUsdcEntity> page = returnUsdcService.listPage(param);
		 return R.ok(page);
	}
	
  
	
}
