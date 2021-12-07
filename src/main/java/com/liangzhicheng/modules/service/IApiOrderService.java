package com.liangzhicheng.modules.service;

import com.liangzhicheng.modules.entity.OrderEntity;
import com.liangzhicheng.modules.entity.vo.OrderVO;

import java.util.Map;

public interface IApiOrderService extends IBaseService<OrderEntity> {

    OrderVO commit(Map<String, Object> requestMap);

    String cancel(Map<String, Object> params);

}
