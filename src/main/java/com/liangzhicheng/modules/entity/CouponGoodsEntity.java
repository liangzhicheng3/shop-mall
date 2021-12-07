package com.liangzhicheng.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("test_coupon_goods")
public class CouponGoodsEntity extends Model<CouponGoodsEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //优惠券id
    private Integer couponId;
    //商品id
    private Integer goodsId;

}
