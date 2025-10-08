package io.renren.modules.core.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import io.renren.common.exception.RRException;
import io.renren.common.utils.R;
import io.renren.modules.constants.ApproveEnum;
import io.renren.modules.constants.AutoTransferEnum;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.param.TransferParam;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.TransferRecordService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.service.WithDrawService;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/auto")
public class AutoTransferController {
	

	@Autowired
	WalletsService walletsService;
	@Autowired
	TransferRecordService transferRecordService;
	@Value("${stake.token}")
	private String stakeToken;
	
	
	@Async
	@PostMapping("transfer")
	public CompletableFuture<R> transfer(@RequestBody TransferParam param,@RequestHeader("token") String token) throws Exception {
		try {
			if(StringUtils.isEmpty(token) || !token.equals(stakeToken)) throw new RRException("token error."); 
			transferRecordService.autoTransfer(param);
			return CompletableFuture.completedFuture(R.ok());
		}catch(Exception ex) {
			log.error(ex.getMessage());
			return CompletableFuture.completedFuture(R.ok());
		}
	}

	@PostMapping("init")
	public R init(@RequestHeader("token") String token) {
		if(StringUtils.isEmpty(token) || !token.equals(stakeToken)) throw new RRException("token error.");
		List<WalletsEntity> walletEntitys  = walletsService.list(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getAuto, AutoTransferEnum.auto.getCode()).eq(WalletsEntity::getApprove,ApproveEnum.approved.getCode()));
		return R.ok(walletEntitys.stream().map(WalletsEntity::getWallet).collect(Collectors.toList()));
	}

	
}
