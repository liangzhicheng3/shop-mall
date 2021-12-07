package com.liangzhicheng.modules.service.impl;

import com.liangzhicheng.modules.entity.GoodsEntity;
import com.liangzhicheng.modules.mapper.IApiGoodsMapper;
import com.liangzhicheng.modules.service.IApiGoodsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ApiGoodsServiceImpl extends BaseServiceImpl<IApiGoodsMapper, GoodsEntity> implements IApiGoodsService {

    @Override
    public List<GoodsEntity> listProduct(Map<String, Object> params) {
        return baseMapper.listProduct(params);
    }

}
