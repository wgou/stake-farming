import java.math.BigInteger;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

public class Test1 {

	public static void main(String[] args) {
		 // WIF 私钥（示例）
        String wif = "L1BhBMxxCVoyshpKGwDzkk52zHuLEUvQcFxoNgbPCCzVS6LHcyHg";
        NetworkParameters params = MainNetParams.get(); 
       
        // 使用 DumpedPrivateKey 来从 WIF 解码私钥
        DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(params, wif);
        ECKey key = dumpedPrivateKey.getKey();
        
        // 获取十六进制格式的私钥
        String privateKeyHex = key.getPrivateKeyAsHex();
        
        System.out.println("Private Key (Hex): " + privateKeyHex);
        
        
        // Base58 解码
        byte[] decoded = Base58.decode(wif);

        // WIF 私钥的前两个字节是版本字节，最后四个字节是校验码
        byte[] privateKeyBytes = new byte[decoded.length - 5]; // 去掉版本字节和校验码

        System.arraycopy(decoded, 1, privateKeyBytes, 0, privateKeyBytes.length);

        // 转换为十六进制字符串
        String privateKeyHex1 = toHexString(privateKeyBytes);

        System.out.println("Private Key (Hex): " + privateKeyHex1);
	}
	
	
    // 自定义方法：将字节数组转换为十六进制字符串
    public static String toHexString(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        String hex = bi.toString(16);
        // 确保十六进制字符串长度是偶数
        while (hex.length() < bytes.length * 2) {
            hex = "0" + hex;
        }
        return hex;
    }
}
