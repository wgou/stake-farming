package io.renren.modules.core.vo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class RewardIndexVO {

	private String wallet;
	
	private BigDecimal eth;
}
