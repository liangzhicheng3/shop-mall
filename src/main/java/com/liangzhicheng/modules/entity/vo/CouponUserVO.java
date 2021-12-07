package com.liangzhicheng.modules.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CouponUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键
    private Integer id;
    //优惠券Id
    private Integer couponId;
    //优惠券数量
    private String couponNumber;
    //会员Id
    private Integer userId;
    //使用时间
    private Date usedTime;
    //领取时间
    private Date addTime;
    //订单Id
    private Integer orderId;
    //状态：1可用，2已用，3过期
    private Integer couponStatus;


    //会员名
    private String userName;
    //优惠劵名称
    private String couponName;
    //金额
    private BigDecimal typeMoney;
    //使用结束时间
    private Date useEndDate;
    //最小商品金额
    private BigDecimal minGoodsAmount;
    //可用：0不可用，1可用
    private Integer enabled = 0;

}
