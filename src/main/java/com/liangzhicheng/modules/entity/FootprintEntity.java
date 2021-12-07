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

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("test_footprint")
public class FootprintEntity extends Model<FootprintEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //会员id
    private Long userId;
    //商品id
    private Integer goodsId;
    //创建时间
    private Long addTime;

    /**
     * 引用字段
     */
    //用户名称
    @TableField(exist = false)
    private String userName;
    //商品名称
    @TableField(exist = false)
    private String goodsName;
    //商品列表图
    @TableField(exist = false)
    private String listPicUrl;
    //简明介绍
    @TableField(exist = false)
    private String goodsBrief;
    //零售价格
    @TableField(exist = false)
    private BigDecimal retailPrice;

}
