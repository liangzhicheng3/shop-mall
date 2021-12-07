package com.liangzhicheng.modules.service;

import com.liangzhicheng.modules.entity.CouponEntity;

import java.util.Map;

public interface ICouponService extends IBaseService<CouponEntity> {

    String publish(Map<String, Object> params);

}
