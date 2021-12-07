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
@TableName("test_cart")
public class CartEntity extends Model<CartEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //会员id
    private Long userId;
    //sessionId
    private String sessionId;
    //商品id
    private Integer goodsId;
    //商品序列号
    private String goodsSn;
    //商品id
    private Integer productId;
    //商品名称
    private String goodsName;
    //市场价
    private BigDecimal marketPrice;
    //零售价格
    private BigDecimal retailPrice;
    //数量
    private Integer number;
    //规格属性组成的字符串，用来显示用
    private String goodsSpecifitionNameValue;
    //product表对应的goods_specifition_ids
    private String goodsSpecifitionIds;
    //是否选中：0否，1是
    private Integer checked;
    //商品图片
    private String listPicUrl;

    /**
     * 引用字段
     */
    //用户名称
    @TableField(exist = false)
    private String userName;
    //product表中的零售价格
    @TableField(exist = false)
    private BigDecimal retailProductPrice;

}
