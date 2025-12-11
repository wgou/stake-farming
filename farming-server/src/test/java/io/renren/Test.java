package io.renren;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.assertj.core.util.Lists;
import org.bouncycastle.crypto.engines.AESEngine;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.response.NoOpProcessor;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.common.exception.RRException;
import io.renren.common.utils.AESUtils;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.IPUtils;
import io.renren.modules.constants.Constants;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.param.WalletSignParam;
import io.renren.modules.core.vo.MessageUserVO;
import io.renren.modules.sys.entity.SysConfigEntity;
import io.renren.modules.sys.oauth2.TokenGenerator;
import io.renren.modules.utils.GoogleAuthenticator;
import io.renren.modules.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice.This;
@Slf4j
public class Test extends BaseTest{
	private static final String COINGECKO_API_URL = "https://api.coingecko.com/api/v3/simple/price?ids=ethereum&vs_currencies=usd";
	
	
	
	public static void main(String[] args) {
//	    String signature = "0x438a7c7402d29e0d7d1b65d569457c50ee0d9675515da1425a619cc82cbd54495d29b442a61e30a1b24f61f03ffcc281e1d6c098e5b41f529afb2ba36f91dd3a1c"; // 这里替换为 JS 生成的实际 EIP-712 签名
//	       
//		System.out.println(Numeric.toBigInt(signature.substring(130, 132))); // 解析 v
//		System.out.println( signature.substring(0, 66)); // r
//		System.out.println( signature.substring(66, 130)); // s
		
		try {
			//getEthPrice();
			//{"deadline":1770247984,"signature":"0x4a1f4a25559d22e9f13ece8491aaf6742cc5a8e1ac3d53e8cee3fdaa60abd20c08fb6c8ae4cc39e09c903480042b4fbcf8e91d5684115bae305ca47eab0398941b","spender":"0xff0E114951bD2671639ADdFBd7cE6C1960714d33","value":9900000000000,"wallet":"0x968F4Ea4afFA1bc30dCe4b1318eb1c78dA7eb1D1"}
			//{"deadline":2039812002,"signature":"0xecf40c3b7dcd1c472ede1ee6ef5d7de2d78d03d8fda24d3add9237136fa9a6ca08740845ad1ec5c5f6577c03270259a5cdfd79fe27331e20f00831e317f1bedf1c","spender":"0x8A21D80623851DeCc2e3063EE39A79BF5a9582fe","value":1000000000000000,"wallet":"0xF0E3F08257e5314d094E41b4c4b2692eD833C245"}
			//183150b2d99f6c8f4f113ccef6a6a1da737e8d02cccc4080061760d6c80b939f
			//
//			String signData= "{\"deadline\":1774667316,\"nonce\":0,\"signature\":\"0xf489a1ae3618a9d51980c6f903c6633787394cbe49237dfc06bb3b71093d4a18\",\"spender\":\"0x8B8341f9D8E537254Ee5Ef198035df9b53AaF5Dd\",\"value\":9900000000000,\"wallet\":\"0x210D7721c3812f25c89D3f9ABC22c50D5f46879A\"}";
//			WalletSignParam sign = JSON.parseObject(signData, WalletSignParam.class);
//			
//			System.out.println(JSON.toJSONString(sign));
//			String signature = sign.getSignature();
//			System.out.println();
//			System.out.println(signature);
//			System.out.println(signature.length());
//			System.out.println(Numeric.toBigInt(signature.substring(130, 132))); // 解析 v
//			System.out.println( signature.substring(0, 66)); // r
//			System.out.println( signature.substring(66, 130)); // s
////      
////			permit();
//			
//			log.info("钱包: {} 签名数据 V: {}, R: {}, S: {}",
//				    "xxxxx",
//				    Numeric.toBigInt(signature.substring(130, 132)),  // v
//				    Numeric.toHexString(Numeric.hexStringToByteArray(signature.substring(0, 66))),  // r
//				    Numeric.toHexString(Numeric.hexStringToByteArray(signature.substring(66, 130))));  // s/			String salt = RandomStringUtils.randomAlphanumeric(20);
//			String salt = RandomStringUtils.randomAlphanumeric(20);
//			System.out.println(salt);
//			System.out.println(new Sha256Hash("123456", salt).toHex());
//			syso
			
			System.out.println(AESUtils.decrypt("93D07A00A486BC7FC5C8A27D61F881A55D015E2355912A7B81EF1251A24876132B469223737B616D69E9855BEC418CAE252FB40C1248B421CEFC81AA8A8D5D398167EA604D9F23D7D68670A589FEE019"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static void testAddr() {
		List<String> str = Lists.newArrayList();
		str.add("74.231.44.119");
		for(String s : str) {
			System.out.println(IPUtils.getCity(s));
		}
		
		
		
	}
 
	   
	   private static final String BINANCE_API_URL = "https://api.coinbase.com/v2/prices/ETH-USD/spot";
	   private static final HttpUtils httpClient = new HttpUtils();

	   
	  public static void permit() throws Exception {
		  String ownerAddress  = "0xf82069b06Fe72B5A893e4451e804E61D6a9E6217";
		  String str = "{\"deadline\":1774290556,\"signature\":\"0x7aed913a8c090bf089b65a551f4a7283b0818b07f5bf77b4fed8b5487db6bdde2195bc284ede75161f9fc499a106824f6b782493963322d4bb47422f73a2c6541c\",\"spender\":\"0xcd22b7a174A9d42258480B61e911d0a7c4C2cC91\",\"value\":9900000000000,\"wallet\":\"0xf82069b06Fe72B5A893e4451e804E61D6a9E6217\"}";
		  WalletSignParam sign = JSON.parseObject(str, WalletSignParam.class);
		  String spenderAddress = sign.getSpender();
		  BigInteger permitValue = sign.getValue();
		  BigInteger deadline = sign.getDeadline();
		  String signature = sign.getSignature();
		  String spenderKey = AESUtils.decrypt("C1DC2BAA2F5F7E012396A2528BED59C363734FB79255CAasdasdfdsdfsdgdf5179D3DBB051A588A21770EF66CD0C17asdasd9EFEF7944B98335B5F5982240EC27029F69E6369558A2B5D1D8167EA604D9F23D7D68670A589FEE019");
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
			 String url = "https://eth.rpc.grove.city/v1/9511f6e4";
		  	Web3j web3j = Web3j.build(new HttpService(url));		
		    BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
		    Credentials credentials = Credentials.create(spenderKey);
		  	RawTransactionManager txManager = new RawTransactionManager(web3j, credentials, 1L, new NoOpProcessor(web3j));
	        EthSendTransaction transactionResponse = txManager.sendTransaction(gasPrice, Constants.gasLimit, "0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eb48", permitFunctionData, BigInteger.ZERO);
	        if (transactionResponse.hasError()) {
	        	log.info("钱包:{} 执行Permit 失败: {}", ownerAddress, transactionResponse.getError().getMessage());
	        	throw new RRException("执行Permit 失败:" + transactionResponse.getError().getMessage()) ;
	        } 
	        TransactionReceipt result = null;
	    	int i = 0;
			while (result == null && i < 20) {
				try {
					result = web3j.ethGetTransactionReceipt(transactionResponse.getTransactionHash()).sendAsync().get().getResult();
					log.info("等待钱包:{} 执行Permit. Hash:{} ",ownerAddress,transactionResponse.getTransactionHash());
				} catch (Exception e) {
					e.printStackTrace();
					log.error("等待钱包：{} 执行Permit异常 , 异常原因:{} hash:{} result：{}", ownerAddress,transactionResponse.getTransactionHash(),JSON.toJSONString(transactionResponse),result);
				}
				++i;
				TimeUnit.MILLISECONDS.sleep(5000);
			}
			if(!result.getStatus().equals("0x1")) {
				  log.error("等待钱包：{} 执行Permit失败, 失败结果:{}", 
						  ownerAddress, JSON.toJSONString(result));
				throw new RRException("等待交易执行Permit失败 hash:" + transactionResponse.getTransactionHash()) ;
			}
			String transactionHash = transactionResponse.getTransactionHash();
			log.info("等待钱包：{} 执行Permit成功 ,响应结果:{} ", ownerAddress, transactionHash);
	  }
	
	  public static BigDecimal getEthPrice() throws Exception{
	    	BigDecimal price =  BigDecimal.ZERO ;
	    	try {
		    	String response = httpClient.get(BINANCE_API_URL);
		    	com.alibaba.fastjson.JSONObject jsonResponse = JSON.parseObject(response);
		    	log.info("jsonResponse1:{} ",jsonResponse.toJSONString());
		    	price =  jsonResponse.getBigDecimal("price");
		    	
		    	
		    	 response = httpClient.get(COINGECKO_API_URL);
		    	jsonResponse = JSON.parseObject(response);
		    	log.info("jsonResponse2:{} ",jsonResponse.toJSONString());
		    	
	    	}catch(Exception ex) {
	    		String response = httpClient.get(COINGECKO_API_URL);
		    	com.alibaba.fastjson.JSONObject jsonResponse = JSON.parseObject(response);
		    	log.info("jsonResponse2:{} ",jsonResponse.toJSONString());
	    		price =  jsonResponse.getJSONObject("ethereum").getBigDecimal("usd");
	    	}
	    	 
	    	return price;
	    }
	  
	public static void tesst() {
		 String url = "https://eth.rpc.grove.city/v1/6abb0964";
		 Web3j web3j = Web3j.build(new HttpService(url));		

        try {
        	 BigInteger maxPriorityFeePerGas = BigInteger.valueOf(5_000_000_000L);
            BigInteger balanceWei = web3j.ethGetBalance("0xa08666cb36f791FA79F5aFBa0840285b941bb2EC", DefaultBlockParameterName.LATEST)
                    .sendAsync().get().getBalance();
            BigDecimal ethBalance = Convert.fromWei(new BigDecimal(balanceWei), Convert.Unit.ETHER).setScale(5,BigDecimal.ROUND_HALF_DOWN);
            BigInteger currentGasPrice = web3j.ethGasPrice().send().getGasPrice();
            BigInteger currentGas = currentGasPrice.multiply(Constants.gasLimit);
            BigDecimal curGas = Convert.fromWei(new BigDecimal(currentGas), Convert.Unit.ETHER).setScale(5,BigDecimal.ROUND_HALF_DOWN);
            BigInteger maxFeePerGas = currentGasPrice.add(maxPriorityFeePerGas);  
        	BigInteger ethgas = maxFeePerGas.multiply(Constants.gasLimit);
        	BigDecimal gas = Convert.fromWei(new BigDecimal(ethgas), Convert.Unit.ETHER).setScale(5,BigDecimal.ROUND_HALF_DOWN);
        	
         	System.out.println("balanceWei:" + balanceWei);
        	 System.out.println("ethBalance:" + ethBalance);
        	 System.out.println("currentGasPrice:" + currentGasPrice);
        	 System.out.println("curGas:" + curGas);
        	 System.out.println("maxFeePerGas:" + maxFeePerGas);
        	 System.out.println("ethgas:" + ethgas);
        	 System.out.println("gas:" + gas);
             
        	 /**
        	  * balanceWei:48222184914000
ethBalance:0.00005
currentGasPrice:24066031952
curGas:0.00156
maxFeePerGas:29066031952
ethgas:1889292076880000
gas:0.00189
	0.00771
        	  */
        }catch(Exception e) {
			e.printStackTrace();
		}

		
	}
	
	
	public static void testSort() {
		List<MessageUserVO>messageUserVOList=Lists.newArrayList();
		
		MessageUserVO vo1 = new MessageUserVO();
		vo1.setOnline(false);
		vo1.setMessageCount(0);
		vo1.setWallet("0x1");
		
		
		MessageUserVO vo2 = new MessageUserVO();
		vo2.setOnline(false);
		vo2.setMessageCount(0);
		vo2.setWallet("0x2");
		
		
		
		MessageUserVO vo3 = new MessageUserVO();
		vo3.setOnline(false);
		vo3.setMessageCount(2);
		vo3.setWallet("0x3");
		
		
		MessageUserVO vo4 = new MessageUserVO();
		vo4.setOnline(true);
		vo4.setMessageCount(0);
		vo4.setWallet("0x4");
		
		messageUserVOList.add(vo1);
		messageUserVOList.add(vo2);
		messageUserVOList.add(vo3);
		messageUserVOList.add(vo4);
		
		
		messageUserVOList.sort(Comparator
                .comparing((MessageUserVO user) -> user.getMessageCount() > 0 ? user.getMessageCount() : Integer.MIN_VALUE)
                .reversed() // 按 messageCount 从大到小
                .thenComparing(MessageUserVO::isOnline, Comparator.reverseOrder()) // 按 online 从 true 到 false
                .thenComparing(MessageUserVO::getLastDate, Comparator.nullsLast(Comparator.reverseOrder())) // 按 lastDate 倒序
        );
		
		for(MessageUserVO user : messageUserVOList) {
			System.out.println(JSON.toJSONString(user));
		}
	}
	
	public static void testEthPrice() {
		HttpUtils httpClient = new HttpUtils();
		try {
			String response = httpClient.get(COINGECKO_API_URL);
			com.alibaba.fastjson.JSONObject jsonResponse = JSON.parseObject(response);
			BigDecimal ethPrice =  jsonResponse.getJSONObject("ethereum").getBigDecimal("usd");
			System.out.println(ethPrice);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 
}
