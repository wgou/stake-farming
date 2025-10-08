package io.renren.modules.utils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.utils.Numeric;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EthWalletUtils {
	/**
     * 以太坊自定义的签名消息都以以下字符开头
     * 参考 eth_sign in https://github.com/ethereum/wiki/wiki/JSON-RPC
     */
    public static final String PERSONAL_MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";
	
	/**
     * 签名
     *
     * @param content    原文信息
     * @param privateKey 私钥
     */
    public static String signPrefixedMessage(String content, String privateKey) {

        // todo 如果验签不成功，就不需要Hash.sha3 直接content.getBytes()就可以了
        // 原文信息字节数组
//        byte[] contentHashBytes = Hash.sha3(content.getBytes());
        byte[] contentHashBytes = content.getBytes();
        // 根据私钥获取凭证对象
        Credentials credentials = Credentials.create(privateKey);
        //
        Sign.SignatureData signMessage = Sign.signPrefixedMessage(contentHashBytes, credentials.getEcKeyPair());

        byte[] r = signMessage.getR();
        byte[] s = signMessage.getS();
        byte[] v = signMessage.getV();

        byte[] signByte = Arrays.copyOf(r, v.length + r.length + s.length);
        System.arraycopy(s, 0, signByte, r.length, s.length);
        System.arraycopy(v, 0, signByte, r.length + s.length, v.length);
        return Numeric.toHexString(signByte);
    }
    
    /**
     * 对签名消息，原始消息，账号地址三项信息进行认证，判断签名是否有效
	* @param signature
	* @param message
	* @param address
	* @return
	*/
	public static boolean validate(String signature, String message, String address) {
	   //参考 eth_sign in https://github.com/ethereum/wiki/wiki/JSON-RPC
	   // eth_sign
	   // The sign method calculates an Ethereum specific signature with:
	   //    sign(keccak256("\x19Ethereum Signed Message:\n" + len(message) + message))).
	   //
	   // By adding a prefix to the message makes the calculated signature recognisable as an Ethereum specific signature.
	   // This prevents misuse where a malicious DApp can sign arbitrary data (e.g. transaction) and use the signature to
	   // impersonate the victim.
	   String prefix = PERSONAL_MESSAGE_PREFIX + message.length();
	   byte[] msgHash = Hash.sha3((prefix + message).getBytes());
	
	   byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
	   byte v = signatureBytes[64];
	   if (v < 27) {
	       v += 27;
	   }
	
	   SignatureData sd = new SignatureData(
	           v,
	           Arrays.copyOfRange(signatureBytes, 0, 32),
	           Arrays.copyOfRange(signatureBytes, 32, 64));
	
	   String addressRecovered = null;
	   boolean match = false;
	
	   // Iterate for each possible key to recover
	   for (int i = 0; i < 4; i++) {
	       BigInteger publicKey = Sign.recoverFromSignature(
	               (byte) i,
	               new ECDSASignature(new BigInteger(1, sd.getR()), new BigInteger(1, sd.getS())),
	               msgHash);
	
	       if (publicKey != null) {
	           addressRecovered = "0x" + Keys.getAddress(publicKey);
	           if (addressRecovered.toLowerCase().equals(address.toLowerCase())) {
	               match = true;
	               break;
	           }
	       }
	   }
	   return match;
	}
	    

    private final static ImmutableList<ChildNumber> BIP44_ETH_ACCOUNT_ZERO_PATH =
            ImmutableList.of(new ChildNumber(44, true), new ChildNumber(60, true),
                    ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);
    private final  static String ZONER = "00000000000000000000000000000000000000000000000000000";
    /**
     * 创建钱包
     */
    public static NewWallets createWallet()  throws MnemonicException.MnemonicLengthException {
        java.security.SecureRandom secureRandom = new java.security.SecureRandom();
        //SecureRandom secureRandom = new SecureRandom();
        byte[] entropy = new byte[DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS / 8];
       // secureRandom.engineNextBytes(entropy);
        secureRandom.nextBytes(entropy);

        //生成12位助记词
        List<String>  str = MnemonicCode.INSTANCE.toMnemonic(entropy);

        //使用助记词生成钱包种子
        byte[] seed = MnemonicCode.toSeed(str, "");
        DeterministicKey masterPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
        DeterministicHierarchy deterministicHierarchy = new DeterministicHierarchy(masterPrivateKey);
        DeterministicKey deterministicKey = deterministicHierarchy
                .deriveChild(BIP44_ETH_ACCOUNT_ZERO_PATH, false, true, new ChildNumber(0));
        byte[] bytes = deterministicKey.getPrivKeyBytes();
        ECKeyPair keyPair = ECKeyPair.create(bytes);
        //通过公钥生成钱包地址
        String address = Keys.getAddress(keyPair.getPublicKey());
        address = Keys.toChecksumAddress(address);
        NewWallets wallets = new NewWallets();
        wallets.setMnemonic(Joiner.on(" ").join(str));
        wallets.setWalletAddress(address);
        String privateKey = keyPair.getPrivateKey().toString(16);
        if(privateKey.length() > 64){
            throw new RuntimeException("privateKey error.");
        }
        if(privateKey.length() < 64){ //位数不足,补0
           String full = ZONER + privateKey;
           privateKey = full.substring(full.length() -64);
        }
        wallets.setPrivateKey(privateKey);
       return wallets;
    }
    
    public static boolean isValidBasicAddress(String address) {
        if (address == null) return false;
        if (!address.startsWith("0x")) return false;
        if (address.length() != 42) return false;
        return address.substring(2).matches("^[0-9a-fA-F]{40}$");
    }
    
    public static boolean isValidChecksumAddress(String address) {
        if (!isValidBasicAddress(address)) {
            return false;
        }
        // 如果是全小写或全大写，不强制校验 checksum
        String noPrefix = address.substring(2);
        if (noPrefix.equals(noPrefix.toLowerCase()) || noPrefix.equals(noPrefix.toUpperCase())) {
            return true;
        }
        // 验证 EIP-55 校验
        try {
            String checksumAddr = Keys.toChecksumAddress(address);
            return checksumAddr.equals(address);
        } catch (Exception e) {
            return false;
        }
    }

    @Data
    public static class NewWallets {
        private String walletAddress;

        private String privateKey;

        private String mnemonic;

        private String json;

    }


}
