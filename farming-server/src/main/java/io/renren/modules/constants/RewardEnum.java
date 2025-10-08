package io.renren.modules.constants;

import java.math.BigDecimal;

import io.renren.common.exception.RRException;
import io.renren.modules.utils.StakeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * 存款每日收益率
 * @author Administrator
 *
 */
@AllArgsConstructor
@Getter
public enum RewardEnum {

	one(new BigDecimal("1.3"),  	new BigDecimal("100"),  	new BigDecimal("5000")),
	two(new BigDecimal("1.6"),  	new BigDecimal("5000"), 	new BigDecimal("20000")),
	three(new BigDecimal("1.9"),	new BigDecimal("20000"),	new BigDecimal("50000")),
	four(new BigDecimal("2.2"), 	new BigDecimal("50000"),	new BigDecimal("100000")),
	five(new BigDecimal("2.5"), 	new BigDecimal("100000"),	new BigDecimal("200000")),
	six(new BigDecimal("2.8"),  	new BigDecimal("200000"),	new BigDecimal("500000")),
	seven(new BigDecimal("3.1"),  	new BigDecimal("500000"),	new BigDecimal("1000000")),
	figh(new BigDecimal("3.5"),  	new BigDecimal("1000000"),	new BigDecimal("2000000")),
	ngiht(new BigDecimal("4.1"),  	new BigDecimal("2000000"),	new BigDecimal(Integer.MAX_VALUE));
	
	
	private BigDecimal ratio;
	private BigDecimal min;
	private BigDecimal max;
	
	
	
	public static RewardEnum match(BigDecimal value) {
		RewardEnum[] rewards = RewardEnum.values();
		for(RewardEnum reward : rewards) {
			if(value.compareTo(reward.getMin()) >=0 && value.compareTo(reward.getMax()) <0) {
				return reward;
			}
		}
		throw new RRException(String.format("USDT:[%s] 没有匹配的收益率",value));
	}
	
	public static BigDecimal day(BigDecimal ratio) {
		return StakeUtils.div(StakeUtils.div(ratio,new BigDecimal("100")), new BigDecimal("4"));
	}
 
	
	
	public static void main(String[] args) {
		System.out.println(RewardEnum.match(new BigDecimal("104.00000")));
	}
}
