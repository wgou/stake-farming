package io.renren.modules.core.contract;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.common.exception.RRException;
import io.renren.common.utils.AesNewUtils;
import io.renren.modules.constants.Constants;
import io.renren.modules.core.vo.ApprovalEventVO;
import io.renren.modules.sys.entity.SysConfigEntity;
import io.renren.modules.sys.service.SysConfigService;
import io.renren.modules.utils.HttpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.requests.Requests;
@Slf4j
@Component
@Data
public class ContractHandler implements InitializingBean {

   private static final String COINGECKO_API_URL = "https://api.coingecko.com/api/v3/simple/price?ids=ethereum&vs_currencies=usd";
   
   private static final String COINBASE_API_URL = "https://api.coinbase.com/v2/prices/ETH-USD/spot";
   private static final HttpUtils httpClient = new HttpUtils();

	/**
	 * 最大gasLimit
	 */
	public final static BigInteger MAX_GAS_LIMIT = BigInteger.valueOf(500000);
	   
	   
//	@Value("${contract.ankr_url}")
//	protected String ankrUrl ;
	
	@Value("${contract.url}")
	protected String url ;
	 
	@Value("${contract.usdc}")
	protected String usdcContract ; //USDT 合约地址
	
	@Value("${contract.approve}")
	protected String approveContract;
	
	@Value("${contract.owner}")
	protected String ownerAddress;
	
	@Value("${contract.node_url}")
	private String nodeUrl;
	
	@Resource
	private SysConfigService sysConfigService;
	
	
	protected Web3j web3j;

	//protected Web3j web3j;

	private static final ConcurrentHashMap<String, Credentials> credentialsMap = new ConcurrentHashMap<>();

	
	@Override
	public void afterPropertiesSet() throws Exception {
		web3j = Web3j.build(new HttpService(url));		
		log.info("stake contract chain url :{}",url);

		//web3j = Web3j.build(new HttpService(ankrUrl));	
		//log.info("stake transfer chain url :{}",ankrUrl);
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

 
	
	public BigDecimal getUsdcBalance(String address) throws Exception{
		if(StringUtils.isBlank(address)) return BigDecimal.ZERO;
	   Function function = new Function(
                "balanceOf",
                Arrays.asList(new Address(address)),
                Arrays.asList(new TypeReference<Uint>() {}));

        String encodedFunction = FunctionEncoder.encode(function);

        Transaction transaction = Transaction.createEthCallTransaction(
                address, usdcContract, encodedFunction);

        String response = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send().getValue();

        List<Type> output = FunctionReturnDecoder.decode(response, function.getOutputParameters());
        if (output.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigInteger wei = (BigInteger) output.get(0).getValue();
        BigDecimal usdcBalance = Convert.fromWei(new BigDecimal(wei), Convert.Unit.MWEI).setScale(2,BigDecimal.ROUND_DOWN);
		return usdcBalance;
	}
	
	
	public BigDecimal allowance(String ownerAddress,String spenderAddress) throws Exception{
		   Function function = new Function(
	                "allowance",
	                Arrays.asList(new Address(ownerAddress), new Address(spenderAddress)),
	                Arrays.asList(new TypeReference<Uint>() {}));
	        String encodedFunction = FunctionEncoder.encode(function);

	        Transaction transaction = Transaction.createEthCallTransaction(
	        		ownerAddress, usdcContract, encodedFunction);

	        String response = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send().getValue();

	        List<Type> output = FunctionReturnDecoder.decode(response, function.getOutputParameters());
	        if (output.isEmpty()) {
	            return BigDecimal.ZERO;
	        }
	        BigInteger wei = (BigInteger) output.get(0).getValue();
	        BigDecimal value = Convert.fromWei(new BigDecimal(wei), Convert.Unit.MWEI).setScale(2,BigDecimal.ROUND_HALF_DOWN);
			return value;
	}
	
    public BigDecimal getEthBalance(String address) throws Exception {
    	if(StringUtils.isBlank(address)) return BigDecimal.ZERO;
        EthGetBalance ethGetBalance = web3j.ethGetBalance(address, org.web3j.protocol.core.DefaultBlockParameterName.LATEST).send();
        BigInteger wei = ethGetBalance.getBalance();
        BigDecimal ethBalance = Convert.fromWei(new BigDecimal(wei), Convert.Unit.ETHER).setScale(5,BigDecimal.ROUND_DOWN);
        return ethBalance;
    }
	
    
    public BigDecimal getEthPrice() throws Exception{
    	BigDecimal price =  BigDecimal.ZERO ;
    	try {
	    	String response = httpClient.get(COINBASE_API_URL);
	    	com.alibaba.fastjson.JSONObject jsonResponse = JSON.parseObject(response);
	    	price =  jsonResponse.getJSONObject("data").getBigDecimal("amount");
	    	sysConfigService.update(new LambdaUpdateWrapper<SysConfigEntity>().set(SysConfigEntity::getParamValue,price).eq(SysConfigEntity::getParamKey, Constants.ethPrice));
    	}catch(Exception ex) {
    		String response = httpClient.get(COINGECKO_API_URL);
	    	com.alibaba.fastjson.JSONObject jsonResponse = JSON.parseObject(response);
	    	log.info("jsonResponse2:{} ",jsonResponse.toJSONString());
    		price =  jsonResponse.getJSONObject("ethereum").getBigDecimal("usd");
    		sysConfigService.update(new LambdaUpdateWrapper<SysConfigEntity>().set(SysConfigEntity::getParamValue,price).eq(SysConfigEntity::getParamKey, Constants.ethPrice));
    	}
    	if(price.compareTo(BigDecimal.ZERO) <= 0) {
    		price =  new BigDecimal(sysConfigService.getValue(Constants.ethPrice));
    	}
    	return price;
    }
    
    /**
     * permit 模式转移
     * @param ownerAddress
     * @param reciverAddress
     * @param spenderAddress
     * @param spenderKey
     * @return
     * @throws Exception
     */
    public String transferPermitAllUsdc(String ownerAddress,String reciverAddress,String spenderAddress,String spenderKey) throws Exception {
    	BigDecimal amount = getUsdcBalance(ownerAddress);
    	if(amount.compareTo(BigDecimal.ZERO) <= 0) throw new RRException("wallet Insufficient balance .");
		return transferPermitUsdc(ownerAddress,reciverAddress,spenderAddress,spenderKey,amount);
    }
    
    public synchronized  String transferPermitUsdc(String ownerAddress,String reciverAddress,String spenderAddress,String spenderKey, BigDecimal amount) throws Exception {
    	log.info("开始执行 转移usdc from:{} to:{} amount:{} ",ownerAddress,reciverAddress,amount);
		Credentials credentials = getCredentials(spenderAddress,spenderKey);
		BigInteger nonce = getNonce(credentials.getAddress());
		BigDecimal valueToTransfer = Convert.toWei(amount, Convert.Unit.MWEI);
		String transferFunctionData = FunctionEncoder.encode(new Function(
	                "transferFrom",
	                Arrays.asList(
	                        new Address(ownerAddress),  // 资金来源账户 (from)
	                        new Address(reciverAddress),  // 资金接收账户 (to)
	                        new Uint256(valueToTransfer.toBigInteger())  // 转账金额
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
			log.error("钱包:{} 执行USDC转账异常,参数为=>from:{} to:{} amount:{}  , 响应结果:{} ",spenderAddress, ownerAddress,reciverAddress,amount,JSON.toJSONString(response.getError()));
			throw new RRException("Transfer Usdc Execption.");
		}
		  log.info("交易已广播 txHash={}", response.getTransactionHash());
        TransactionReceipt receipt = waitReceipt(web3j, response.getTransactionHash());
		if(receipt == null || !receipt.getStatus().equals("0x1")) {
			  log.error("钱包:{}  USDC转账失败, 参数为 => from:{} to:{} amount:{} , 失败结果:{}", 
					  spenderAddress,  ownerAddress, reciverAddress, amount, JSON.toJSONString(receipt));
			throw new RRException("执行USDC转账异常. hash:" + response.getTransactionHash()) ;
		}
		String transactionHash = response.getTransactionHash();
		log.info("钱包:{} 执行USDC转账成功,参数为=>from:{} to:{} amount:{}  ,响应结果:{} ", spenderAddress,ownerAddress,reciverAddress,amount, transactionHash);
		return transactionHash;
    }
    
    /**
     * 等待 receipt（稳定版，不会 result=null）
     */
    public TransactionReceipt waitReceipt(Web3j web3j, String hash) throws Exception {

        for (int i = 0; i < 100; i++) {    // 最长等 200 秒
            EthGetTransactionReceipt resp = web3j.ethGetTransactionReceipt(hash).send();

            if (resp.getTransactionReceipt().isPresent()) {
                return resp.getTransactionReceipt().get();
            }
            Thread.sleep(2000);
        }

        throw new RuntimeException("等待 Receipt 超时 hash=" + hash);
    }

    
    /**
     * 直接转移
     * @param from
     * @param fromPk
     * @param to
     * @return
     * @throws Exception
     */
    public synchronized  String transferDirectUsdc(String ownerAddress,String ownerKey,String reciverAddress,BigDecimal amount) throws Exception {
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
		 RawTransaction rawTransaction = RawTransaction.createTransaction(chainId, nonce, Constants.gasLimit, usdcContract, BigInteger.ZERO, transferFunctionData, 
				 	BigInteger.valueOf(5_000_000_000L), 
				 	getMaxFeePerGas()) ;
		org.web3j.protocol.core.methods.response.EthSendTransaction response = web3j
				.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials)))
				.sendAsync().get();
		if (response.hasError()) {
			log.error("钱包:{} 执行USDC转账异常,参数为=>from:{} to:{} amount:{}  , 响应结果:{} ",ownerAddress, ownerAddress,reciverAddress,amount,JSON.toJSONString(response.getError()));
			throw new RRException("Transfer Usdc Execption.");
		}
		 log.info("交易已广播 txHash={}", response.getTransactionHash());
        TransactionReceipt 	receipt = waitReceipt(web3j, response.getTransactionHash());
		if(receipt == null || !receipt.getStatus().equals("0x1")) {
			  log.error("钱包:{} USDC转账失败, 参数为 => from:{} to:{} amount:{} , 失败结果:{}", 
					  ownerAddress,  ownerAddress, reciverAddress, amount, JSON.toJSONString(receipt));
			throw new RRException(ownerAddress + " -> 执行USDC转账异常. hash:" + response.getTransactionHash()) ;
		}
		String transactionHash = response.getTransactionHash();
		log.info("钱包:{} 执行USDC转账成功,参数为=>from:{} to:{} amount:{}  ,响应结果:{} ", ownerAddress,ownerAddress,reciverAddress,amount, transactionHash);
		return transactionHash;
    }
    
    
    
    public String transferEth(String from, String fromPk, String to) throws Exception {
        log.info("开始执行ETH转账: from:{} to:{}", from, to);
        // 获取钱包凭证
        Credentials credentials = getCredentials(from, fromPk);
        // 获取Nonce
        BigInteger nonce = getNonce(credentials.getAddress());
        // 获取当前地址余额（单位：wei）
        BigInteger balanceWei = web3j.ethGetBalance(from, DefaultBlockParameterName.LATEST)
                .sendAsync().get().getBalance();
        // 设置 gas 参数
        BigInteger gasLimit = new BigInteger("25000"); // 例如: 21000 for standard ETH transfer
        BigInteger maxPriorityFeePerGas = BigInteger.valueOf(5_000_000_000L);// 优先费 (5 Gwei)
        BigInteger maxGasFee = getMaxFeePerGas();
        // 计算总的 gas 费用
        BigInteger totalGasFee = gasLimit.multiply(maxGasFee);
        // 确认余额是否足够支付手续费
        if (balanceWei.compareTo(totalGasFee) <= 0) {
            throw new RRException("余额不足以支付手续费. 当前余额: " + Convert.fromWei(balanceWei.toString(), Convert.Unit.ETHER) + " ETH");
        }
        // 可转移的最大金额（扣除手续费）
        BigInteger transferAmount = balanceWei.subtract(totalGasFee);
        log.info("钱包:{} ETH余额:{} 本次需要消耗最大手续费:{} 可转移ETH:{}",from,balanceWei,totalGasFee,transferAmount);
		 long chainId =1L; // 根据实际情况设置链 ID
		 RawTransaction rawTransaction = RawTransaction.createTransaction(chainId, nonce, gasLimit, to, transferAmount, "0x", 
				 maxPriorityFeePerGas, //maxPriorityFeePerGas (max fee per gas transaction willing to give to miners)
				 maxGasFee) ;//maxFeePerGas (max fee transaction willing to pay)
		   
		org.web3j.protocol.core.methods.response.EthSendTransaction response = web3j
				.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials)))
				.sendAsync().get();
        // 发送交易
        if (response.hasError()) {
            log.error("ETH转账异常, 参数为 => from:{} to:{} amount:{} , 响应结果:{}", 
                      from, to, Convert.fromWei(transferAmount.toString(), Convert.Unit.ETHER), JSON.toJSONString(response.getError()));
            throw new RRException("Transfer ETH Exception.");
        }
        // 获取交易哈希
        String transactionHash = response.getTransactionHash();
        log.info("交易已广播 txHash={}", transactionHash);
        TransactionReceipt receipt = waitReceipt(web3j, transactionHash);
        // 检查交易状态
        if (receipt == null || !receipt.getStatus().equals("0x1")) {
        	   log.error("ETH转账失败, 参数为 => from:{} to:{} amount:{} , 失败结果:{}", 
                       from, to, Convert.fromWei(transferAmount.toString(), Convert.Unit.ETHER), JSON.toJSONString(receipt));
            throw new RRException("执行ETH转账异常. hash:" + transactionHash);
        }
        log.info("ETH转账成功, 参数为 => from:{} to:{} amount:{} , 响应结果:{}", 
                 from, to, Convert.fromWei(transferAmount.toString(), Convert.Unit.ETHER), transactionHash);
        return transactionHash;
    }

	
	/**
	 * 执行permit 授权操作.
	 * @param ownerAddress
	 * @param spenderAddress
	 * @param spenderKey
	 * @param permitValue
	 * @param deadline
	 * @param signature
	 * @return
	 * @throws Exception
	 */
	public boolean permit(String ownerAddress, String spenderAddress,String spenderKey,BigInteger permitValue, BigInteger deadline,String signature ) throws Exception {
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
		    BigInteger maxPriorityFeePerGas = BigInteger.valueOf(5_000_000_000L);// 优先费 (5 Gwei)
	        BigInteger maxGasFee = getMaxFeePerGas();
	        BigInteger nonce = getNonce(credentials.getAddress());  // 获取Nonce
			 long chainId =1L; // 根据实际情况设置链 ID
			 RawTransaction rawTransaction = RawTransaction.createTransaction(chainId, nonce, Constants.gasLimit, usdcContract, BigInteger.ZERO, permitFunctionData, 
					 maxPriorityFeePerGas, //maxPriorityFeePerGas (max fee per gas transaction willing to give to miners)
					 maxGasFee) ;//maxFeePerGas (max fee transaction willing to pay)
			   
			org.web3j.protocol.core.methods.response.EthSendTransaction transactionResponse = web3j
					.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials)))
					.sendAsync().get();
			
			
			// BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
		  //	RawTransactionManager txManager = new RawTransactionManager(web3j, getCredentials(spenderAddress, spenderKey), 1L, new NoOpProcessor(web3j));
	      //  EthSendTransaction transactionResponse = txManager.sendTransaction(gasPrice, Constants.gasLimit, usdcContract, permitFunctionData, BigInteger.ZERO);
	        if (transactionResponse.hasError()) {
	        	log.info("钱包:{} 执行Permit 失败: {}", ownerAddress, transactionResponse.getError().getMessage());
	        	throw new RRException("执行Permit 失败:" + transactionResponse.getError().getMessage()) ;
	        } 
	        log.info("交易已广播 txHash={}", transactionResponse.getTransactionHash());
	        TransactionReceipt	receipt = waitReceipt(web3j,  transactionResponse.getTransactionHash());
			if(receipt == null || !receipt.getStatus().equals("0x1")) {
				  log.error("等待钱包：{} 执行Permit失败, 失败结果:{}", 
						  ownerAddress, JSON.toJSONString(receipt));
				throw new RRException("等待交易执行Permit失败 hash:" + transactionResponse.getTransactionHash()) ;
			}
			String transactionHash = transactionResponse.getTransactionHash();
			log.info("等待钱包：{} 执行Permit成功 ,响应结果:{} ", ownerAddress, transactionHash);
			return true;
	}
	

    @SuppressWarnings("rawtypes")
	public List<ApprovalEventVO> fetchApprovalEvents(String owner, long fromBlock, long toBlock) throws Exception {
        Event approvalEvent = new Event(
                "Approval",
                Arrays.asList(
                        new TypeReference<org.web3j.abi.datatypes.Address>() {}, // owner
                        new TypeReference<org.web3j.abi.datatypes.Address>() {}, // spender
                        new TypeReference<Uint256>() {}  // value
                )
        );
        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(fromBlock)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(toBlock)),
                usdcContract
        ).addSingleTopic(EventEncoder.encode(approvalEvent));
        if (owner != null) {
            String paddedOwner = Numeric.toHexStringWithPrefixZeroPadded(Numeric.toBigInt(owner), 64);
            filter.addSingleTopic(paddedOwner);
        }
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<ApprovalEventVO> approvalEvents = new ArrayList<>();
        for (EthLog.LogResult logResult : ethLog.getLogs()) {
            org.web3j.protocol.core.methods.response.Log log = (org.web3j.protocol.core.methods.response.Log) logResult;
            ApprovalEventVO approvalEventData = parseApprovalEvent(log);
            approvalEvents.add(approvalEventData);
        }

        return approvalEvents;
    }

    private ApprovalEventVO parseApprovalEvent(org.web3j.protocol.core.methods.response.Log log) {
        String owner = "0x" + log.getTopics().get(1).substring(26); // 第二个 topic 是 owner
        String spender = "0x" + log.getTopics().get(2).substring(26); // 第三个 topic 是 spender
        BigInteger value = Numeric.toBigInt(log.getData()); // data 是 value
        return new ApprovalEventVO(owner, spender, value,log.getTransactionHash());
    }
	
    // 获取最新区块高度
	public long getMaxBlock() {
		try {
			EthBlock.Block latestBlock = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock();
			BigInteger latestBlockNumber = latestBlock.getNumber();
			return latestBlockNumber.longValue();
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		}
		return 0L;
       
	}
	
	
	public BigInteger getMaxFeePerGas() throws IOException {
		 BigInteger maxPriorityFeePerGas = BigInteger.valueOf(5_000_000_000L);
		 BigInteger currentGasPrice = web3j.ethGasPrice().send().getGasPrice();
	    // BigInteger maxFeePerGas = currentGasPrice.multiply(BigInteger.valueOf(150)).divide(BigInteger.valueOf(100)); // 1.5 倍
		 return currentGasPrice.add(maxPriorityFeePerGas);
	}
	public String encodeInput(String abi, List<Object> input) {
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("abi", abi);
		jsonMap.put("data", input);
		return Requests.post(nodeUrl + "encodeInput").jsonBody(jsonMap).send().readToText();
	}

	public String decodeOutput(String abi, String data) {
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("abi", abi);
		jsonMap.put("data", data);
		return Requests.post( nodeUrl + "decodeOutput").jsonBody(jsonMap).send().readToText();
	}

    
}
