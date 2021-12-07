package com.liangzhicheng.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("test_product")
public class ProductEntity extends Model<ProductEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //商品id
    private Integer goodsId;
    //商品规格ids
    private String goodsSpecificationIds;
    //商品序列号
    private String goodsSn;
    //商品库存
    private Integer goodsNumber;
    //零售价格
    private BigDecimal retailPrice;
    //市场价格
    private BigDecimal marketPrice;

    /**
     * 引用字段
     */
    //商品
    @TableField(exist = false)
    private String goodsName;
    //规格值
    @TableField(exist = false)
    private String specificationValue;
    //商品列表图片
    @TableField(exist = false)
    private String listPicUrl;

    public ProductEntity(Integer goodsId, String goodsSn, Integer goodsNumber, BigDecimal retailPrice, BigDecimal marketPrice) {
        this.goodsId = goodsId;
        this.goodsSn = goodsSn;
        this.goodsNumber = goodsNumber;
        this.retailPrice = retailPrice;
        this.marketPrice = marketPrice;
    }

}
