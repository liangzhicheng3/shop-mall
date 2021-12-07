package com.liangzhicheng.modules.service.impl;

import com.liangzhicheng.common.utils.BeansUtil;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.modules.entity.CartEntity;
import com.liangzhicheng.modules.entity.GoodsEntity;
import com.liangzhicheng.modules.entity.ProductEntity;
import com.liangzhicheng.modules.entity.UserEntity;
import com.liangzhicheng.modules.entity.vo.CartVO;
import com.liangzhicheng.modules.mapper.IApiCartMapper;
import com.liangzhicheng.modules.service.IApiCartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ApiCartServiceImpl extends BaseServiceImpl<IApiCartMapper, CartEntity> implements IApiCartService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(UserEntity user,
                     GoodsEntity goods,
                     ProductEntity product,
                     Integer goodsNum,
                     String[] goodsSpecificationValue) {
        Integer userId = user.getId();
        CartVO cartVO = new CartVO();
        cartVO.setUserId(userId.longValue());
        cartVO.setSessionId("1");
        cartVO.setGoodsId(goods.getId());
        cartVO.setGoodsSn(product.getGoodsSn());
        cartVO.setProductId(product.getId());
        cartVO.setGoodsName(goods.getName());
        cartVO.setMarketPrice(product.getMarketPrice());
        cartVO.setRetailPrice(product.getRetailPrice());
        cartVO.setNumber(goodsNum);
        cartVO.setChecked(1);
        cartVO.setListPicUrl(goods.getListPicUrl());
        cartVO.setGoodsSpecifitionIds(product.getGoodsSpecificationIds());
        if(goodsSpecificationValue != null){
            cartVO.setGoodsSpecifitionNameValue(StringUtils.join(goodsSpecificationValue, ";"));
        }
        CartEntity cart = BeansUtil.copyEntity(cartVO, CartEntity.class);
        //更新购物车搭配减价
        Map<String, Object> cartParams = new HashMap<>();
        cartParams.put("userId", userId);
        List<CartEntity> cartList = baseMapper.queryListSelf(cartParams);
        if(ToolUtil.listSizeGT(cartList)){
            List<CartEntity> updateRetailPriceList = new ArrayList<>(cartList.size());
            for(Iterator<CartEntity> it = cartList.iterator(); it.hasNext();){
                CartEntity entity = it.next();
                if (!entity.getRetailPrice().equals(entity.getRetailProductPrice())) {
                    entity.setRetailPrice(entity.getRetailProductPrice());
                    updateRetailPriceList.add(entity);
                }
            }
            for(Iterator<CartEntity> it = cartList.iterator(); it.hasNext();){
                CartEntity entity = it.next();
                //存在原始的
                if (entity.getChecked() != null && entity.getChecked() == 1) {
                    for(Iterator<CartEntity> its = cartList.iterator(); its.hasNext();){
                        CartEntity entitys = its.next();
                        if (!entitys.getId().equals(entity.getId())) {
                            updateRetailPriceList.add(entity);
                        }
                    }
                }
            }
            if(ToolUtil.listSizeGT(updateRetailPriceList)){
                this.updateBatchById(updateRetailPriceList);
            }
        }
        return baseMapper.insert(cart);
    }

}
