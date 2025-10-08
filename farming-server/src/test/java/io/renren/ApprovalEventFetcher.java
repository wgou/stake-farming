package io.renren;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import com.alibaba.fastjson.JSON;

import io.renren.modules.constants.ApproveEnum;
import io.renren.modules.core.vo.ApprovalEventVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApprovalEventFetcher {

    private final Web3j web3j;
    private final String contractAddress;

    public static void main(String[] args) {
        ApprovalEventFetcher approvalEventFetcher = new ApprovalEventFetcher("https://eth-eur-1.chainplayer.io", "0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48");
        try {
        	System.out.println(approvalEventFetcher.getMaxBlock());
        	long max = approvalEventFetcher.getMaxBlock();
        	//while( i < max ) {
        	 List<ApprovalEventVO> list = approvalEventFetcher.fetchApprovalEvents("0x75879449913e368528a63588E637613327E393F9", 22251000, 	22252000);
        	 
    		System.out.println(JSON.toJSONString(list) );
        //	}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ApprovalEventFetcher(String rpcUrl, String contractAddress) {
        this.web3j = Web3j.build(new HttpService(rpcUrl));
        this.contractAddress = contractAddress;
    }
    
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

    public List<ApprovalEventVO> fetchApprovalEvents(String owner, long fromBlock, long toBlock) throws Exception {
        // 定义 Approval 事件
        Event approvalEvent = new Event(
                "Approval",
                Arrays.asList(
                        new TypeReference<org.web3j.abi.datatypes.Address>() {}, // owner
                        new TypeReference<org.web3j.abi.datatypes.Address>() {}, // spender
                        new TypeReference<Uint256>() {}  // value
                )
        );

        // 创建过滤器
        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(fromBlock)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(toBlock)),
                contractAddress
        ).addSingleTopic(EventEncoder.encode(approvalEvent));

        // 添加 owner 过滤条件
        if (owner != null) {
            // 将 owner 地址填充为 32 字节（64 个十六进制字符）
            String paddedOwner = Numeric.toHexStringWithPrefixZeroPadded(Numeric.toBigInt(owner), 64);
            filter.addSingleTopic(paddedOwner);
        }

        // 查询日志
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
}