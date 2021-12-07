package com.liangzhicheng.modules.service;

import com.liangzhicheng.modules.entity.CartEntity;
import com.liangzhicheng.modules.entity.GoodsEntity;
import com.liangzhicheng.modules.entity.ProductEntity;
import com.liangzhicheng.modules.entity.UserEntity;

public interface IApiCartService extends IBaseService<CartEntity> {

    int save(UserEntity user, GoodsEntity goods, ProductEntity product, Integer goodsNum, String[] goodsSpecificationValue);

}
