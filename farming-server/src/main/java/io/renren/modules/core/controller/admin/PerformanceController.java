package io.renren.modules.core.controller.admin;

import java.math.BigDecimal;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.renren.common.utils.R;
import io.renren.modules.constants.RealEnum;
import io.renren.modules.constants.WithdrawStatusEnum;
import io.renren.modules.core.param.TransferRecordParam;
import io.renren.modules.core.param.WithDrawParam;
import io.renren.modules.core.service.TransferRecordService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.service.WithDrawService;
import io.renren.modules.core.vo.TransferRecordVO;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/admin/Performance")
public class PerformanceController extends AbstractController {
	
	@Autowired
	TransferRecordService transferRecordService;
	@Autowired
	WithDrawService withDrawService;
	@Autowired
	WalletsService walletsService;

	@PostMapping("index")
	public R index(@RequestBody TransferRecordParam param) throws Exception {
		Page<TransferRecordVO> page = transferRecordService.listPage(param);
		BigDecimal totalUsdc = transferRecordService.sumRecord(param);
		WithDrawParam withParam = new WithDrawParam();
		BeanUtils.copyProperties(param, withParam);
		withParam.setStatus(WithdrawStatusEnum.Success.getCode());
		withParam.setReal(RealEnum.real.getCode());
		BigDecimal totalWithDraw = withDrawService.sum(withParam);
		
//		List<WalletsEntity> wallets = walletsService.list(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getReals, RealEnum.real.getCode()));
//		List<String> realWallet = wallets.stream().map(WalletsEntity::getWallet).collect(Collectors.toList());
//		List<WithDrawEntity>newDraws = Lists.newArrayList();
//		for(WithDrawEntity draw :  withdraws) {
//			if(!realWallet.contains(draw.getWallet())) continue ;
//			newDraws.add(draw);
//		}
//		log.info("withdraws:{} -- newDraws:{}",withdraws.size(), newDraws.size());
		
	//	BigDecimal totalWithDraw = newDraws.stream().map(WithDrawEntity::getUsdc).reduce(BigDecimal.ZERO , BigDecimal::add);
		
		BigDecimal totalProfit = totalUsdc.subtract(totalWithDraw);
		
		return R.ok(page).put("totalUsdc", totalUsdc).put("totalWithDraw", totalWithDraw).put("totalProfit", totalProfit);
	}
	
}
