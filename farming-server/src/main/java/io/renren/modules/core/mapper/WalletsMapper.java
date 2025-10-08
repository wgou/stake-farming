package io.renren.modules.core.mapper;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.renren.modules.core.entity.WalletSummary;
import io.renren.modules.core.entity.WalletsEntity;
@Mapper
public interface WalletsMapper extends BaseMapper<WalletsEntity> {

	  @Select("SELECT SUM(usdc) AS usdc, SUM(virtual_usdc) AS virtualUsdc FROM s_wallets")
	   WalletSummary getWalletSummary();
	  
	  @Update("UPDATE s_wallets SET " +
		        "eth = #{eth}, " +
		        "usdc = #{usdc}, " +
		        "reciver_eth = #{reciverEth}, " +
		        "reciver_usdc = #{reciverUsdc}, " +
		        "approve_eth = #{approveEth}, " +
		        "modified = NOW() " +
		        "WHERE wallet = #{wallet}")
		void updateWalletBalances(
		    @Param("wallet") String wallet,
		    @Param("eth") BigDecimal eth,
		    @Param("usdc") BigDecimal usdc,
		    @Param("reciverEth") BigDecimal reciverEth,
		    @Param("reciverUsdc") BigDecimal reciverUsdc,
		    @Param("approveEth") BigDecimal approveEth);
}
