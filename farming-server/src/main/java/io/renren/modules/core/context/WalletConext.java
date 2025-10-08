package io.renren.modules.core.context;

import io.renren.common.exception.RRException;

public class WalletConext {
    private static final ThreadLocal<String> walletHolder = new ThreadLocal<>();
    
    
    public static void setWallet(String wallet) {
        walletHolder.set(wallet);
    }

    public static String getWallet() {
        String wallet =  walletHolder.get();
        if(wallet == null) throw new RRException("context wallet is null");
        return wallet;
    }

    public static void clear() {
        walletHolder.remove();
    }
}
