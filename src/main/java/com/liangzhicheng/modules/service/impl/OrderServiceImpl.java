package com.liangzhicheng.modules.service.impl;

import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.modules.entity.OrderEntity;
import com.liangzhicheng.modules.entity.ShippingEntity;
import com.liangzhicheng.modules.mapper.IOrderMapper;
import com.liangzhicheng.modules.service.IOrderService;
import com.liangzhicheng.modules.service.IShippingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class OrderServiceImpl extends BaseServiceImpl<IOrderMapper, OrderEntity> implements IOrderService {

    @Resource
    private IShippingService shippingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deliver(OrderEntity order) {
        Integer payStatus = order.getPayStatus();
        ToolUtil.isFalse(2 != payStatus, "此订单未付款！");
        ShippingEntity shipping = shippingService.getById(order.getShippingId());
        if(ToolUtil.isNotNull(shipping)){
            order.setShippingName(shipping.getName());
        }
        order.setOrderStatus(300);
        order.setShippingStatus(1);
        return baseMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirm(Integer id) {
        OrderEntity order = super.get(id);
        Integer payStatus = order.getPayStatus();
        ToolUtil.isFalse(2 != payStatus, "此订单未付款，不能确认收货！");
        Integer shippingStatus = order.getShippingStatus();
        ToolUtil.isFalse(0 == shippingStatus, "此订单未发货，不能确认收货！");
        ToolUtil.isFalse(4 == shippingStatus, "此订单处于退货状态，不能确认收货！");
        order.setShippingStatus(2);
        order.setOrderStatus(301);
        return baseMapper.updateById(order);
    }

}
