package com.liangzhicheng.modules.service;

import com.liangzhicheng.common.pay.wechat.object.RefundResult;

import java.util.Map;

public interface IApiWechatService {

    Map<Object, Object> prepay(Map<String, Object> params);

    String orderQuery(Map<String, Object> params);

    RefundResult refund(Map<String, Object> params);

    Map<String, Object> refundQuery(Map<String, Object> params);

}
