package com.liangzhicheng.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("test_coupon_user")
public class CouponUserEntity extends Model<CouponUserEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //优惠券id
    private Integer couponId;
    //优惠券数量
    private String couponNumber;
    //用户id
    private Integer userId;
    //使用时间
    private Date usedTime;
    //领取时间
    private Date addTime;
    //订单id
    private Integer orderId;
    //状态：1可用，2已用，3过期
    private Integer couponStatus;

    /**
     * 引用字段
     */
    //会员名
    @TableField(exist = false)
    private String userName;
    //优惠劵名称
    @TableField(exist = false)
    private String couponName;
    //金额
    @TableField(exist = false)
    private BigDecimal typeMoney;
    //使用开始时间
    @TableField(exist = false)
    private Date useStartDate;
    //使用结束时间
    @TableField(exist = false)
    private Date useEndDate;
    //最小商品金额
    @TableField(exist = false)
    private BigDecimal minGoodsAmount;

}
