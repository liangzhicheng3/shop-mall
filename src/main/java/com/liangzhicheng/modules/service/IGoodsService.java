package com.liangzhicheng.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liangzhicheng.modules.entity.GoodsEntity;

import java.util.List;
import java.util.Map;

public interface IGoodsService extends IService<GoodsEntity> {

    int insert(GoodsEntity goods);

    int delete(String ids);

    int update(GoodsEntity goods);

    Integer getTotal(Map<String, Object> params);

    List<GoodsEntity> queryList(Map<String, Object> params);

    GoodsEntity get(Integer id);

}
