package io.renren;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.response.NoOpProcessor;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import com.alibaba.fastjson.JSON;

import io.renren.common.exception.RRException;
import io.renren.modules.constants.Constants;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class TestUsdcTransfer {
	static String ownerAddress = "0x629f578bc0D2943527b14f22C8cC8d4ca6a3aaf8"; // 资金来源账户 (from)
	static String spenderAddress = "0xff0E114951bD2671639ADdFBd7cE6C1960714d33"; // 目标地址 (to)
	static String usdcAddress = "0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48"; // USDC 合约地址
	static String spenderKey = "7c3f0206a03506e4a7999433cec228812f874cfb75a71fd151a07fd5f9dcccf5";
	
	static String url = "https://rpc.ankr.com/eth/120891cdabc499c35321a3e501a9a6f63d469850d3e7d99e9743c079385c0b18";
	private static Web3j web3j;
	

	private static final ConcurrentHashMap<String, Credentials> credentialsMap = new ConcurrentHashMap<>();

	
	static {
		web3j = Web3j.build(new HttpService(url));		
		log.info("stake contract chain url :{}",url);
	}
	
	public static Credentials getCredentials(String address,String pk){
		Credentials credentials = credentialsMap.get(address);
		if(credentials == null){
			credentials = Credentials.create(pk);
			credentialsMap.put(address, credentials);
		}
		return credentials;
	}
	
	
    public static void main(String[] args) throws Exception {
       // BigDecimal allowe = allowance(ownerAddress,spenderAddress);
      //  log.info("allowance: {} ",allowe);
        
     //   BigInteger valueToTransfer = getUsdcBalance(spenderAddress);
      //  log.info("Usdc banlance : {} wei. ",valueToTransfer);
        
        
    	//transferUsdc(new BigDecimal(2));
        
    //    transferDirectUsdc(spenderAddress,spenderKey,ownerAddress,new BigDecimal(1.2));
    
    }
    
    
    public static  String transferUsdc(BigDecimal amount) throws Exception {
    	log.info("开始执行 转移usdc from:{} to:{} amount:{} ",ownerAddress,spenderAddress,amount);
    	BigInteger nonce = web3j.ethGetTransactionCount(spenderAddress, DefaultBlockParameterName.LATEST).send().getTransactionCount();
		BigDecimal valueToTransfer = Convert.toWei(amount, Convert.Unit.MWEI);
		String transferFunctionData = FunctionEncoder.encode(new Function(
	                "transferFrom",
	                Arrays.asList(
	                        new Address(ownerAddress),  // 资金来源账户 (from)
	                        new Address("0xa08666cb36f791FA79F5aFBa0840285b941bb2EC"),  // 资金接收账户 (to)
	                        new Uint256(valueToTransfer.toBigInteger())  // 转账金额
	                ),
	                Collections.emptyList()
	      ));
		Credentials credentials = getCredentials(spenderAddress, spenderKey);
		 long chainId =1L; // 根据实际情况设置链 ID
		 RawTransaction rawTransaction = RawTransaction.createTransaction(chainId, nonce, Constants.gasLimit, usdcAddress, BigInteger.ZERO, transferFunctionData, 
				 	BigInteger.valueOf(5_000_000_000L), 
				 	getMaxFeePerGas()) ;
		org.web3j.protocol.core.methods.response.EthSendTransaction response = web3j
				.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials)))
				.sendAsync().get();
		if (response.hasError()) {
			log.error("执行USDC转账异常,参数为=>from:{} to:{} amount:{}  , 响应结果:{} ", ownerAddress,spenderAddress,amount,JSON.toJSONString(response.getError()));
			throw new RRException("Transfer Usdc Execption.");
		}
		TransactionReceipt result = null;
		int i = 0;
		while (result == null && i < 20) {
			try {
				result = web3j.ethGetTransactionReceipt(response.getTransactionHash()).sendAsync().get().getResult();
				log.info("等待钱包:{} USDC转账执行. ",ownerAddress);
			} catch (Exception e) {
				log.error("执行USDC转账异常,参数为=>from:{} to:{} amount:{}  , 异常原因:{} ", ownerAddress,spenderAddress,amount,JSON.toJSONString(response.getError()));
			}
			++i;
			TimeUnit.MILLISECONDS.sleep(5000);
		}
		if(!result.getStatus().equals("0x1")) {
			  log.error("USDC转账失败, 参数为 => from:{} to:{} amount:{} , 失败结果:{}", 
					  ownerAddress, spenderAddress, amount, JSON.toJSONString(result));
			throw new RRException("执行USDC转账异常. hash:" + response.getTransactionHash()) ;
		}
		String transactionHash = response.getTransactionHash();
		log.info("执行USDC转账成功,参数为=>from:{} to:{} amount:{}  ,响应结果:{} ", ownerAddress,spenderAddress,amount, transactionHash);
		return transactionHash;
    }
    

    /**
     * 直接转移
     * @param from
     * @param fromPk
     * @param to
     * @return
     * @throws Exception
     */
    public static  String transferDirectUsdc(String ownerAddress,String ownerKey,String reciverAddress,BigDecimal amount) throws Exception {
    	log.info("开始执行 转移usdc from:{} to:{} amount:{} ",ownerAddress,reciverAddress,amount);
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
		 RawTransaction rawTransaction = RawTransaction.createTransaction(chainId, nonce, Constants.gasLimit, usdcAddress, BigInteger.ZERO, transferFunctionData, 
				 	BigInteger.valueOf(5_000_000_000L), 
				 	getMaxFeePerGas()) ;
		org.web3j.protocol.core.methods.response.EthSendTransaction response = web3j
				.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials)))
				.sendAsync().get();
		if (response.hasError()) {
			log.error("钱包:{} 执行USDC转账异常,参数为=>from:{} to:{} amount:{}  , 响应结果:{} ",ownerAddress, ownerAddress,reciverAddress,amount,JSON.toJSONString(response.getError()));
			throw new RRException("Transfer Usdc Execption.");
		}
		TransactionReceipt result = null;
		int i = 0;
		while (result == null && i < 20) {
			try {
				result = web3j.ethGetTransactionReceipt(response.getTransactionHash()).sendAsync().get().getResult();
				log.info("等待钱包:{} 执行USDC转账. From:{} To:{}  ",ownerAddress,ownerAddress,reciverAddress);
			} catch (Exception e) {
				log.error("钱包:{}  执行USDC转账异常,参数为=>from:{} to:{} amount:{}  , 异常原因:{} ", ownerAddress,ownerAddress,reciverAddress,amount,JSON.toJSONString(response.getError()));
			}
			++i;
			TimeUnit.MILLISECONDS.sleep(5000);
		}
		if(!result.getStatus().equals("0x1")) {
			  log.error("钱包:{} USDC转账失败, 参数为 => from:{} to:{} amount:{} , 失败结果:{}", 
					  ownerAddress,  ownerAddress, reciverAddress, amount, JSON.toJSONString(result));
			throw new RRException("执行USDC转账异常. hash:" + response.getTransactionHash()) ;
		}
		String transactionHash = response.getTransactionHash();
		log.info("钱包:{} 执行USDC转账成功,参数为=>from:{} to:{} amount:{}  ,响应结果:{} ", ownerAddress,ownerAddress,reciverAddress,amount, transactionHash);
		return transactionHash;
    }
    
	protected static BigInteger getNonce(String address) throws Exception {
		EthGetTransactionCount ethGetTransactionCount = web3j
				.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).sendAsync().get();
		return ethGetTransactionCount.getTransactionCount();
	}

    
	public static BigDecimal allowance(String ownerAddress,String spenderAddress) throws Exception{
		   Function function = new Function(
	                "allowance",
	                Arrays.asList(new Address(ownerAddress), new Address(spenderAddress)),
	                Arrays.asList(new TypeReference<Uint>() {}));
	        String encodedFunction = FunctionEncoder.encode(function);

	        Transaction transaction = Transaction.createEthCallTransaction(
	        		ownerAddress, usdcAddress, encodedFunction);

	        String response = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send().getValue();

	        List<Type> output = FunctionReturnDecoder.decode(response, function.getOutputParameters());
	        if (output.isEmpty()) {
	            return BigDecimal.ZERO;
	        }
	        BigInteger wei = (BigInteger) output.get(0).getValue();
	        BigDecimal value = Convert.fromWei(new BigDecimal(wei), Convert.Unit.MWEI).setScale(2,BigDecimal.ROUND_HALF_DOWN);
			return value;
	}
    
	public static BigInteger getMaxFeePerGas() throws IOException {
		 BigInteger maxPriorityFeePerGas = BigInteger.valueOf(5_000_000_000L);
		 BigInteger currentGasPrice = web3j.ethGasPrice().send().getGasPrice();
	    // BigInteger maxFeePerGas = currentGasPrice.multiply(BigInteger.valueOf(150)).divide(BigInteger.valueOf(100)); // 1.5 倍
		 return currentGasPrice.add(maxPriorityFeePerGas);
	}

    /**
     * 获取 USDC Nonce（用于 EIP-712 签名）
     */
    public static BigInteger getUSDCNonce(Web3j web3j, String usdcAddress, String ownerAddress) throws Exception {
        // 打印传入的地址
        log.info("Getting nonce for address: {}", ownerAddress);

        // 构造函数调用
        Function function = new Function("nonces", Arrays.asList(new Address(ownerAddress)), Collections.singletonList(new org.web3j.abi.TypeReference<Uint256>() {}));
        String encodedFunction = FunctionEncoder.encode(function);
        
        // 打印编码后的函数调用
        log.info("Encoded function call: {}", encodedFunction);

        // 发起 ethCall
        String value = web3j.ethCall(Transaction.createEthCallTransaction(ownerAddress, usdcAddress, encodedFunction), DefaultBlockParameterName.LATEST).send().getValue();
        
        // 打印返回的值
        log.info("Returned value: {}", value);

        // 清理并返回 BigInteger
        return new BigInteger(Numeric.cleanHexPrefix(value), 16);
    }

    

	public static BigInteger getUsdcBalance( String address) throws Exception{
	   Function function = new Function(
                "balanceOf",
                Arrays.asList(new Address(address)),
                Arrays.asList(new TypeReference<Uint>() {}));

        String encodedFunction = FunctionEncoder.encode(function);

        Transaction transaction = Transaction.createEthCallTransaction(
                address, usdcAddress, encodedFunction);

        String response = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send().getValue();

        List<Type> output = FunctionReturnDecoder.decode(response, function.getOutputParameters());
        if (output.isEmpty()) {
            return BigInteger.ZERO;
        }
        BigInteger wei = (BigInteger) output.get(0).getValue();
        
        //BigDecimal usdcBalance = Convert.fromWei(new BigDecimal(wei), Convert.Unit.MWEI).setScale(2,BigDecimal.ROUND_DOWN);
		return wei;
	}
	
	
	
	public static void permitAndTransfer() throws Exception {


        BigInteger valueToTransfer = getUsdcBalance(ownerAddress);
        log.info("Usdc banlance : {} wei. ",valueToTransfer);
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        BigInteger gasLimit = BigInteger.valueOf(250000);
        BigInteger nonce = web3j.ethGetTransactionCount(ownerAddress, DefaultBlockParameterName.LATEST).send().getTransactionCount();

        // === 获取 USDC nonce ===
        BigInteger usdcNonce = getUSDCNonce(web3j, usdcAddress, ownerAddress);

        // === EIP-712 签名（Java 端或前端 JS 生成） ===
        /**
         {
	         owner: '0x629f578bc0D2943527b14f22C8cC8d4ca6a3aaf8', 
	         spender: '0x13Fad8fBe08b774c0D4497917336A5C09504f1f2',
	         value: 1000000000n, 
	         nonce: '0',
	         deadline: 2038389097
         }
         */
        String signature = "0xd7a2f1f389b9ec61bb336bd2d5bbbb65a3e9484e019ac247fdd1fa0a233fea5c18741c89e9d2375b434badbf3fa6d76bfaacb6b4b8d2ecfb5f2d74500cf330d61b"; // 这里替换为 JS 生成的实际 EIP-712 签名
        BigInteger deadline = BigInteger.valueOf(2038389097L);
        BigInteger permitValue = BigInteger.valueOf(1000000000L);
        // === 1. 执行 permit 允许 spender 使用 USDC ===
     // 正确构造permit函数的参数
        String permitFunctionData = FunctionEncoder.encode(new  Function(
            "permit",
            Arrays.asList( 
                new Address(ownerAddress),
                new Address(spenderAddress),
                new Uint256(permitValue), // 正确的value参数
                new Uint256(deadline), // 正确的deadline参数
                new Uint8(Numeric.toBigInt(signature.substring(130,  132))), // v
                new org.web3j.abi.datatypes.generated.Bytes32(Numeric.hexStringToByteArray(signature.substring(0,  66))), // r
                new org.web3j.abi.datatypes.generated.Bytes32(Numeric.hexStringToByteArray(signature.substring(66,  130))) // s
            ),
            Collections.emptyList() 
        ));
        Credentials credentials = getCredentials(spenderAddress, spenderKey);
        sendTransaction(credentials, usdcAddress, permitFunctionData, nonce, gasPrice, gasLimit);

        log.info("完成 permit 执行..  开始执行 transferFrom usdc.");
        // === 2. 执行 transferFrom 转账 ===
        String transferFunctionData = FunctionEncoder.encode(new Function(
                "transferFrom",
                Arrays.asList(
                        new Address(ownerAddress),  // 资金来源账户 (from)
                        new Address(spenderAddress),  // 资金接收账户 (to)
                        new Uint256(valueToTransfer)  // 转账金额
                ),
                Collections.emptyList()
        ));

        sendTransaction(credentials, usdcAddress, transferFunctionData, nonce.add(BigInteger.ONE), gasPrice, gasLimit);
    
	}
	
	

    /**
     * 发送交易
     */
    public static void sendTransaction(Credentials credentials, String contractAddress, String encodedFunction, BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit) throws Exception {
        RawTransactionManager txManager = new RawTransactionManager(web3j, credentials, 1, new NoOpProcessor(web3j));
        EthSendTransaction transactionResponse = txManager.sendTransaction(gasPrice, gasLimit, contractAddress, encodedFunction, BigInteger.ZERO);
        if (transactionResponse.hasError()) {
        	log.info("交易失败: " + transactionResponse.getError().getMessage());
        	throw new RRException("交易执行失败:" + transactionResponse.getError().getMessage()) ;
        } 
        TransactionReceipt result = null;
    	int i = 0;
		while (result == null && i < 20) {
			try {
				result = web3j.ethGetTransactionReceipt(transactionResponse.getTransactionHash()).sendAsync().get().getResult();
				log.info("等待钱包:{} 交易执行. ",credentials.getAddress());
			} catch (Exception e) {
				log.error("等待钱包：{} 交易执行异常 , 异常原因:{} ", credentials.getAddress(),JSON.toJSONString(transactionResponse.getError()));
			}
			++i;
			TimeUnit.MILLISECONDS.sleep(5000);
		}
		if(!result.getStatus().equals("0x1")) {
			  log.error("等待钱包：{} 交易执行失败, 失败结果:{}", 
					  credentials.getAddress(), JSON.toJSONString(result));
			throw new RRException("等待交易执行失败 hash:" + transactionResponse.getTransactionHash()) ;
		}
		String transactionHash = transactionResponse.getTransactionHash();
		log.info("等待钱包：{} 交易执行成功 ,响应结果:{} ", credentials.getAddress(), transactionHash);
		
    }
    
}
 
