package com.liangzhicheng.modules.mapper;

import com.liangzhicheng.modules.entity.CouponUserEntity;

import java.util.List;
import java.util.Map;

public interface IApiCouponUserMapper extends IBaseMapper<CouponUserEntity> {

    List<CouponUserEntity> getCouponUser(Map<String, Object> params);

}
