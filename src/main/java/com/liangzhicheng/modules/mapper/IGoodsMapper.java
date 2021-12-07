package com.liangzhicheng.modules.mapper;

import com.liangzhicheng.modules.entity.GoodsEntity;

public interface IGoodsMapper extends IBaseMapper<GoodsEntity> {

    Integer getMax();

}
