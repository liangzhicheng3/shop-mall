package com.liangzhicheng.modules.mapper;

import com.liangzhicheng.modules.entity.GoodsEntity;

import java.util.List;
import java.util.Map;

public interface IApiGoodsMapper extends IBaseMapper<GoodsEntity> {

    List<GoodsEntity> listProduct(Map<String, Object> params);

}
