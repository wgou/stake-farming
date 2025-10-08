package io.renren.modules.core.vo;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalEventVO {

    private  String owner;
    private  String spender;
    private  BigInteger value;
    private  String hash;
  

    @Override
    public String toString() {
        return "ApprovalEvent{" +
                "owner='" + owner + '\'' +
                ", spender='" + spender + '\'' +
                ", value=" + value + '\'' + 
                ", hash=" + hash + '\'' +
                '}';
    }


}
