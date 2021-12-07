package com.liangzhicheng.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("test_coupon")
public class CouponEntity extends Model<CouponEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //优惠券名称
    private String name;
    //金额
    private BigDecimal typeMoney;
    //发放方式
    private Integer sendType;
    //最小金额
    private BigDecimal minAmount;
    //最大金额
    private BigDecimal maxAmount;
    //发放开始时间
    private Date sendStartDate;
    //发放结束时间
    private Date sendEndDate;
    //使用开始时间
    private Date useStartDate;
    //使用结束时间
    private Date useEndDate;
    //最小商品金额
    private BigDecimal minGoodsAmount;

}
