package com.liangzhicheng.common.pay.wechat.object;

import lombok.Data;

import java.io.Serializable;

@Data
public class RefundResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private String return_code;
	private String return_msg;

	private String appid;
	private String mch_id;
	private String device_info;
	private String nonce_str;
	private String sign;
	private String result_code;
	private String err_code;
	private String err_code_des;

	private String transaction_id;
	private String out_trade_no;
	private String refund_id;
	private String out_refund_no;
	private String total_fee;
	private String settlement_total_fee;
	private String refund_fee;
	private String settlement_refund_fee;
	private String refund_status;

	private String refund_channel;
	private String fee_type;
	private String cash_fee;
	private String cash_refund_fee;

}
