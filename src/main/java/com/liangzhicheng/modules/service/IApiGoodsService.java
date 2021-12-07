package com.liangzhicheng.modules.service;

import com.liangzhicheng.modules.entity.GoodsEntity;

import java.util.List;
import java.util.Map;

public interface IApiGoodsService extends IBaseService<GoodsEntity> {

    List<GoodsEntity> listProduct(Map<String, Object> params);

}
