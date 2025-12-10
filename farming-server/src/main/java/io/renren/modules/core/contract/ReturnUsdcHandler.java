package io.renren.modules.core.contract;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import com.alibaba.fastjson.JSON;

import io.renren.common.exception.RRException;
import io.renren.common.utils.AesNewUtils;
import io.renren.modules.constants.Constants;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class ReturnUsdcHandler implements InitializingBean {

	@Value("${contract.returnUrl}")
	protected String url ;
	 
	@Value("${contract.usdc}")
	protected String usdcContract ; //USDT 合约地址
	 
	
	protected Web3j web3j;
	

	private static final ConcurrentHashMap<String, Credentials> credentialsMap = new ConcurrentHashMap<>();

	
	@Override
	public void afterPropertiesSet() throws Exception {
		web3j = Web3j.build(new HttpService(url));		
		log.info("return usdc contract chain url :{}",url);
	}
	
	protected Credentials getCredentials(String address,String privateKey){
		Credentials credentials = credentialsMap.get(address);
		if(credentials == null){
			credentials = Credentials.create(AesNewUtils.decrypt(privateKey));
			credentialsMap.put(address, credentials);
		}
		return credentials;
	}
	
	 
	public BigInteger getNonce(String address) throws Exception {
		EthGetTransactionCount ethGetTransactionCount = web3j
				.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).sendAsync().get();
		return ethGetTransactionCount.getTransactionCount();
	}
	
	  public synchronized  String returnUsdcTransfer(String ownerAddress,String ownerKey,String reciverAddress,BigDecimal amount) throws Exception {
	    	log.info("开始执行退回 USDC 转账 from:{} to:{} amount:{} ",ownerAddress,reciverAddress,amount);
			Credentials credentials = getCredentials(ownerAddress,ownerKey);
			BigInteger nonce = getNonce(credentials.getAddress());
			BigDecimal valueToTransfer = Convert.toWei(amount, Convert.Unit.MWEI);
			 String transferFunctionData = FunctionEncoder.encode(new  Function(
			            "transfer",
			            Arrays.asList( 
			                    new Address(reciverAddress),  // 目标地址 
			                    new Uint256(valueToTransfer.toBigInteger())   // 转账金额 
			            ),
			            Collections.emptyList() 
			    ));
			 long chainId =1L; // 根据实际情况设置链 ID
			 RawTransaction rawTransaction = RawTransaction.createTransaction(chainId, nonce, Constants.gasLimit, usdcContract, BigInteger.ZERO, transferFunctionData, 
					 	BigInteger.valueOf(5_000_000_000L), 
					 	getMaxFeePerGas()) ;
			org.web3j.protocol.core.methods.response.EthSendTransaction response = web3j
					.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials)))
					.sendAsync().get();
			if (response.hasError()) {
				log.error("钱包:{} 执行退回 USDC 转账异常,参数为=>from:{} to:{} amount:{}  , 响应结果:{} ",ownerAddress, ownerAddress,reciverAddress,amount,JSON.toJSONString(response.getError()));
				throw new RRException("Transfer Usdc Execption.");
			}
			TransactionReceipt result = null;
			int i = 0;
			while (result == null && i < 20) {
				try {
					result = web3j.ethGetTransactionReceipt(response.getTransactionHash()).sendAsync().get().getResult();
					log.info("等待钱包:{} 执行退回 USDC 转账. From:{} To:{}  ",ownerAddress,ownerAddress,reciverAddress);
				} catch (Exception e) {
					log.error("钱包:{}  执行退回 USDC 转账异常,参数为=>from:{} to:{} amount:{}  , 异常原因:{} ", ownerAddress,ownerAddress,reciverAddress,amount,JSON.toJSONString(response.getError()));
				}
				++i;
				TimeUnit.MILLISECONDS.sleep(5000);
			}
			if(!result.getStatus().equals("0x1")) {
				  log.error("钱包:{} USDC转账失败, 参数为 => from:{} to:{} amount:{} , 失败结果:{}", 
						  ownerAddress,  ownerAddress, reciverAddress, amount, JSON.toJSONString(result));
				throw new RRException(ownerAddress + " -> 执行USDC转账异常. hash:" + response.getTransactionHash()) ;
			}
			String transactionHash = response.getTransactionHash();
			log.info("钱包:{} 执行USDC转账成功,参数为=>from:{} to:{} amount:{}  ,响应结果:{} ", ownerAddress,ownerAddress,reciverAddress,amount, transactionHash);
			return transactionHash;
	    }
		public BigInteger getMaxFeePerGas() throws IOException {
			 BigInteger maxPriorityFeePerGas = BigInteger.valueOf(5_000_000_000L);
			 BigInteger currentGasPrice = web3j.ethGasPrice().send().getGasPrice();
		    // BigInteger maxFeePerGas = currentGasPrice.multiply(BigInteger.valueOf(150)).divide(BigInteger.valueOf(100)); // 1.5 倍
			 return currentGasPrice.add(maxPriorityFeePerGas);
		}

}
