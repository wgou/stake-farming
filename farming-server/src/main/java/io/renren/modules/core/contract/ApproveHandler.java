package io.renren.modules.core.contract;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import io.renren.common.utils.AesNewUtils;
import io.renren.modules.core.vo.ApprovalEventVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
@Data
public class ApproveHandler implements InitializingBean {
 
	   
	@Value("${contract.url}")
	protected String url ;
	 
	@Value("${contract.usdc}")
	protected String usdcContract ; //USDT 合约地址
	 
	
	protected Web3j web3j;
	

	private static final ConcurrentHashMap<String, Credentials> credentialsMap = new ConcurrentHashMap<>();

	
	@Override
	public void afterPropertiesSet() throws Exception {
		web3j = Web3j.build(new HttpService(url));		
		log.info("stake contract chain url :{}",url);
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
	
	 
    
}
