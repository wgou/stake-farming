package io.renren.modules.core.vo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProxyReportVO {
	
	private Long poolsId;
	
	private String poolsName;
	
	private String parentPoolsName;
	
	private BigDecimal usdc;
	
	private BigDecimal withdraw;
	
	private BigDecimal parentRebate;
	
	private BigDecimal childRebate;
	
	private boolean hasChildren;
	
	
}
