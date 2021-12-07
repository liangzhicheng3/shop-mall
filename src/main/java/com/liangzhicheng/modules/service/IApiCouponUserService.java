package com.liangzhicheng.modules.service;

import com.liangzhicheng.modules.entity.CouponUserEntity;
import com.liangzhicheng.modules.entity.vo.CouponUserVO;

import java.util.List;
import java.util.Map;

public interface IApiCouponUserService extends IBaseService<CouponUserEntity> {

    List<CouponUserVO> getCouponUser(Map<String, Object> params);

}
