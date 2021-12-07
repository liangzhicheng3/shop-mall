package com.liangzhicheng.modules.service;

import com.liangzhicheng.modules.entity.OrderEntity;

public interface IOrderService extends IBaseService<OrderEntity> {

    int deliver(OrderEntity order);

    int confirm(Integer id);

}
