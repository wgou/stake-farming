package io.renren.common.exception;

import lombok.Data;

/**
 * 转账异常
 *
 */
@Data
public class TransferException extends RuntimeException {

    private static final long serialVersionUID = 1L;
	private String msg;
    private int code = 500;
    public TransferException(String msg) {
		super(msg);
		this.msg = msg;
	}

	public TransferException(String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
	}
}
