package com.liangzhicheng.modules.service.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.pay.wechat.object.RefundResult;
import com.liangzhicheng.common.bean.RedisBean;
import com.liangzhicheng.common.utils.BeansUtil;
import com.liangzhicheng.common.utils.JSONUtil;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.modules.entity.*;
import com.liangzhicheng.modules.entity.vo.GoodsPurchaseVO;
import com.liangzhicheng.modules.entity.vo.OrderVO;
import com.liangzhicheng.modules.mapper.IApiOrderMapper;
import com.liangzhicheng.modules.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class ApiOrderServiceImpl extends BaseServiceImpl<IApiOrderMapper, OrderEntity> implements IApiOrderService {

    @Resource
    private IApiAddressService apiAddressService;
    @Resource
    private IApiCartService apiCartService;
    @Resource
    private RedisBean redisBean;
    @Resource
    private IApiProductService apiProductService;
    @Resource
    private IApiCouponUserService apiCouponUserService;
    @Resource
    private IApiOrderGoodsService apiOrderGoodsService;
    @Resource
    private IApiWechatService apiWechatService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO commit(Map<String, Object> params) {
        Integer userId = MapUtil.getInt(params, "userId");
        Integer addressId = MapUtil.getInt(params, "addressId");
        Integer couponUserId = MapUtil.getInt(params, "couponUserId");
        String type = MapUtil.getStr(params, "type");
        ToolUtil.isFalse(ToolUtil.isNull(addressId)
                || ToolUtil.isNull(couponUserId)
                || ToolUtil.isBlank(type), ApiConstant.PARAM_IS_NULL);
        AddressEntity address = apiAddressService.get(addressId);
        ToolUtil.isFalse(ToolUtil.isNull(address), "????????????????????????");
        //??????????????????????????????
        Integer freightPrice = 0;
        //????????????????????????????????????
        List<CartEntity> checkedGoodsList = new ArrayList<>();
        BigDecimal goodsTotalPrice = new BigDecimal(0.00);
        if("cart".equals(type)){
            Map<String, Object> param = new HashMap<>();
            param.put("userId", userId);
            param.put("sessionId", 1);
            param.put("checked", 1);
            LinkedHashMap<String, Object> cartMap = apiCartService.queryList(param);
            checkedGoodsList = (List<CartEntity>) cartMap.get("records");
            ToolUtil.isFalse(checkedGoodsList == null, "???????????????");
            //??????????????????
            for(CartEntity cart : checkedGoodsList){
                goodsTotalPrice = goodsTotalPrice.add(cart.getRetailPrice().multiply(new BigDecimal(cart.getNumber())));
            }
        }else{
            String value = redisBean.hGet(Constants.MALL_GOODS_PURCHASE, userId.toString());
            log.info("[????????????] redis hash get???{}", value);
            GoodsPurchaseVO goodsPurchase = JSONUtil.parseObject(value, GoodsPurchaseVO.class);
            ProductEntity product = apiProductService.getSelf(goodsPurchase.getProductId());
            //?????????????????????
            //????????????
            goodsTotalPrice = product.getRetailPrice().multiply(new BigDecimal(goodsPurchase.getNumber()));
            CartEntity cart = BeansUtil.copyEntity(product, CartEntity.class);
            cart.setNumber(goodsPurchase.getNumber());
            cart.setProductId(goodsPurchase.getProductId());
            checkedGoodsList.add(cart);
        }
        //???????????????????????????
        BigDecimal couponPrice = new BigDecimal(0.00);
        CouponUserEntity couponUser = null;
        if(couponUserId != null && couponUserId > 0){
            couponUser = apiCouponUserService.getSelf(couponUserId);
            if(ToolUtil.isNotNull(couponUser) && couponUser.getCouponStatus() == 1){
                couponPrice = couponUser.getTypeMoney();
            }
        }
        //????????????
        BigDecimal orderTotalPrice = goodsTotalPrice.add(new BigDecimal(freightPrice));
        //??????????????????????????????????????????????????????
        BigDecimal actualPrice = orderTotalPrice.subtract(couponPrice);
        //????????????
        OrderEntity order = new OrderEntity();
        order.setOrderSn(ToolUtil.generateOrderSn());
        order.setUserId(userId);
        //????????????
        order.setShippingId(0);
        order.setShippingFee(new BigDecimal(0));
        //????????????
        order.setConsignee(address.getUserName());
        order.setMobile(address.getTelNumber());
        order.setProvince(address.getProvinceName());
        order.setCity(address.getCityName());
        order.setDistrict(address.getCountyName());
        order.setAddress(address.getDetailInfo());
        //??????
        order.setFreightPrice(freightPrice);
        //????????????
        order.setPostscript(MapUtil.getStr(params, "postscript"));
        //?????????
        order.setCouponId(couponUserId);
        order.setCouponPrice(couponPrice);
        //??????
        order.setGoodsPrice(goodsTotalPrice);
        order.setOrderPrice(orderTotalPrice);
        order.setActualPrice(actualPrice);
        //????????????
        order.setOrderStatus(0);
        order.setShippingStatus(0);
        order.setPayStatus(0);
        //??????
        order.setIntegral(0);
        order.setIntegralMoney(new BigDecimal(0));
        if(type.equals("cart")) {
            order.setOrderType("1");
        }else{
            order.setOrderType("4");
        }
        order.setAddTime(new Date());
        baseMapper.insert(order);
        //??????????????????
        List<OrderGoodsEntity> orderGoodsList = new ArrayList<>(checkedGoodsList.size());
        for(Iterator<CartEntity> it = checkedGoodsList.iterator(); it.hasNext();){
            CartEntity item = it.next();
            OrderGoodsEntity orderGoods = new OrderGoodsEntity();
            orderGoods.setOrderId(order.getId());
            orderGoods.setGoodsId(item.getGoodsId());
            orderGoods.setProductId(item.getProductId());
            orderGoods.setGoodsSn(item.getGoodsSn());
            orderGoods.setGoodsName(item.getGoodsName());
            orderGoods.setListPicUrl(item.getListPicUrl());
            orderGoods.setNumber(item.getNumber());
            orderGoods.setMarketPrice(item.getMarketPrice());
            orderGoods.setRetailPrice(item.getRetailPrice());
            orderGoods.setGoodsSpecifitionIds(item.getGoodsSpecifitionIds());
            orderGoods.setGoodsSpecifitionNameValue(item.getGoodsSpecifitionNameValue());
            orderGoodsList.add(orderGoods);
        }
        if(ToolUtil.listSizeGT(orderGoodsList)){
            apiOrderGoodsService.saveBatch(orderGoodsList);
        }
        //????????????????????????????????????
        if(ToolUtil.isNotNull(couponUser) && couponUser.getCouponStatus() == 1){
            couponUser.setCouponStatus(2);
            apiCouponUserService.update(couponUser);
        }
        //?????????????????????????????????????????????
        if("cart".equals(type)){
            Map<String, Object> cartParams = new HashMap<>();
            cartParams.put("userId", userId);
            cartParams.put("sessionId", 1);
            cartParams.put("checked", 1);
            apiCartService.remove(
                    Wrappers.<CartEntity>lambdaQuery()
                            .eq(CartEntity::getUserId, userId)
                            .eq(CartEntity::getSessionId, 1)
                            .eq(CartEntity::getChecked, 1));
        }
        return BeansUtil.copyEntity(order, OrderVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String cancel(Map<String, Object> params) {
        OrderEntity order = baseMapper.getSelf(MapUtil.getInt(params, "orderId"));
        if(ToolUtil.isNotNull(order)){
            Integer userId = MapUtil.getInt(params, "userId");
            ToolUtil.isFalse(!order.getUserId().equals(userId), ApiConstant.PARAM_ERROR);
            Integer orderStatus = order.getOrderStatus();
            ToolUtil.isFalse(orderStatus == 300, "???????????????????????????");
            ToolUtil.isFalse(orderStatus == 301, "???????????????????????????");
            //??????
            String content = "";
            if(orderStatus == 2){
                Map<String, Object> refundParams = new HashMap<>();
                refundParams.put("outTradeNo", order.getOrderSn());
                refundParams.put("orderMoney", 0.01);
                refundParams.put("refundMoney", 0.01);
                RefundResult refundResult = apiWechatService.refund(refundParams);
                if(refundResult.getResult_code().equals("SUCCESS")){
                    if(order.getOrderStatus() == 201){
                        order.setOrderStatus(401);
                    }else if(order.getOrderStatus() == 300){
                        order.setOrderStatus(402);
                    }
                    order.setPayStatus(4);
                    baseMapper.updateById(order);
                    content = "????????????";
                }else{
                    content = "????????????";
                }
            }else{
                order.setOrderStatus(101);
                baseMapper.updateById(order);
                content = "????????????";
            }
            return content;
        }
        return "";
    }

}
