package io.renren.modules.constants;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.dongliu.commons.collection.Lists;
@Component
@Slf4j
public class Constants {
	
	
	public static final BigDecimal inviteReward = new BigDecimal("0.3"); //分享获得下级奖励的30% 
	
	
	public static final  BigInteger gasLimit = BigInteger.valueOf(250000);
	
	public static final BigDecimal bs = new BigDecimal(150); //1.5倍
	
	
	public static final String ethPrice = "ethPrice";
	
	public static final Integer Y = 1;
	
	
	
	
	
	public static final String filePath = "/home/ec2-user/data/files/";
	
	
	public static final String collectAddress = "0x010a94Fa180aEC17eBBA02e49E908a8e6c74e3B8";
	
	
	public static final String returnWallet = "returnWallet";
	public static final String returnWalletKey = "returnWalletKey";
	
	private static List<String> authContractList = Lists.newArrayList();
	
	static {
		authContractList.add("0x7a250d5630B4cF539739dF2C5dAcb4c659F2488D"); //Uniswap V2 Router
		authContractList.add("0xE592427A0AEce92De3Edee1F18E0157C05861564"); //Uniswap V3 Router
		authContractList.add("0x1111111254EEB25477B68fb85Ed929f73A960582"); //1inch Router v4
		authContractList.add("0x7d2768dE32b0b80b7a3454c06BdAc5A7f402922"); //Aave v2 LendingPool
		authContractList.add("0xc3d688B66703497DAA19211EEdff47f25384cdc3"); //Compound v3 (Comet)
		authContractList.add("0xBA12222222228d8Ba445958a75a0704d566BF2C8"); //Balancer Vault
		authContractList.add("0xDef171Fe48CF0115B1d80b88dc8eAB59176FEe57"); //Paraswap Augustus
		authContractList.add("0xd9e1CE17f2641f24aE83637ab66a2cca9C378B9F"); //SushiSwap  Router
		authContractList.add("0x7BeA39867e4169DBe237d55C8242a8f2fcDcc387"); //Aave V3 Pool
	}
	
	
	public static boolean checkApproveContract(String contract) {
		return authContractList.contains(contract);
	}
	
}
