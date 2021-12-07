package com.liangzhicheng.modules.service.impl;

import cn.hutool.core.map.MapUtil;
import com.liangzhicheng.common.exception.TransactionException;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.modules.entity.CouponEntity;
import com.liangzhicheng.modules.entity.CouponGoodsEntity;
import com.liangzhicheng.modules.entity.CouponUserEntity;
import com.liangzhicheng.modules.mapper.ICouponMapper;
import com.liangzhicheng.modules.service.ICouponGoodsService;
import com.liangzhicheng.modules.service.ICouponService;
import com.liangzhicheng.modules.service.ICouponUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

@Service
public class CouponServiceImpl extends BaseServiceImpl<ICouponMapper, CouponEntity> implements ICouponService {

    @Resource
    private ICouponUserService couponUserService;
    @Resource
    private ICouponGoodsService couponGoodsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String publish(Map<String, Object> params) {
        //发放方式：0按订单发放，1按用户发放，2商品转发送券，3按商品发放，4新用户注册，5线下发放，6评价好评红包（固定或随机红包）
        Integer sendType = MapUtil.getInt(params, "sendType");
        ToolUtil.isFalse(sendType == null, "发放方式不能为空");
        Integer couponId = MapUtil.getInt(params, "couponId");
        ToolUtil.isFalse(couponId == null, "优惠券不能为空");
        String couponNumber = MapUtil.getStr(params, "couponNumber");
        ToolUtil.isFalse(Integer.parseInt(couponNumber) < 1, "优惠券数量发送有误");
        if(sendType == 1){
            String userIds = MapUtil.getStr(params, "userIds");
            ToolUtil.isFalse(ToolUtil.isNull(userIds), "用户不能为空");
            if(userIds.contains(",")){
                StringTokenizer tokenizer = new StringTokenizer(userIds, ",");
                while(tokenizer.hasMoreElements()){
                    String userId = tokenizer.nextToken();
                    if(ToolUtil.isNull(userId)){
                        continue;
                    }
                    this.saveCouponUser(userId, couponId, couponNumber);
                }
            }else{
                this.saveCouponUser(userIds, couponId, couponNumber);
            }
        }else if(sendType == 3){
            String goodsIds = MapUtil.getStr(params, "goodsIds");
            ToolUtil.isFalse(ToolUtil.isNull(goodsIds), "商品不能为空");
            if(goodsIds.contains(",")){
                StringTokenizer tokenizer = new StringTokenizer(goodsIds, ",");
                while(tokenizer.hasMoreElements()){
                    String goodsId = tokenizer.nextToken();
                    if(ToolUtil.isNull(goodsId)){
                        continue;
                    }
                    this.saveCouponGoods(couponId, goodsId);
                }
            }else{
                this.saveCouponGoods(couponId, goodsIds);
            }
        }else{
            throw new TransactionException("此类优惠券不支持手动发放");
        }
        return "发放成功";
    }

    public void saveCouponUser(String userId, Integer couponId, String couponNumber){
        CouponUserEntity couponUser = new CouponUserEntity()
                .setUserId(Integer.parseInt(userId))
                .setCouponId(couponId)
                .setCouponNumber(couponNumber)
                .setAddTime(new Date());
        couponUserService.save(couponUser);
    }

    public void saveCouponGoods(Integer couponId, String goodsId){
        CouponGoodsEntity couponGoods = new CouponGoodsEntity()
                .setCouponId(couponId)
                .setGoodsId(Integer.parseInt(goodsId));
        couponGoodsService.save(couponGoods);
    }

}
