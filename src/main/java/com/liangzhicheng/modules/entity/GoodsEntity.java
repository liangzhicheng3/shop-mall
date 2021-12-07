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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("test_goods")
public class GoodsEntity extends Model<GoodsEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //分类id
    private Integer categoryId;
    //商品序列号
    private String goodsSn;
    //名称
    private String name;
    //品牌id
    private Integer brandId;
    //商品序列号
    private Integer goodsNumber;
    //关键字
    private String keywords;
    //简明介绍
    private String goodsBrief;
    //商品描述
    private String goodsDesc;
    //上架：0否，1是
    private Integer isOnSale;
    //排序
    private Integer sortOrder;
    //删除状态：0否，1是
    private Integer isDelete;
    //属性类别
    private Integer attributeCategory;
    //专柜价格
    private BigDecimal counterPrice;
    //附加价格
    private BigDecimal extraPrice;
    //是否新品：0否，1是
    private Integer isNew;
    //商品单位
    private String goodsUnit;
    //商品主图
    private String primaryPicUrl;
    //商品列表图
    private String listPicUrl;
    //零售价格
    private BigDecimal retailPrice;
    //销售量
    private Integer sellVolume;
    //主sku　product_id
    private Integer primaryProductId;
    //单位价格，单价
    private BigDecimal unitPrice;
    //推广描述
    private String promotionDesc;
    //推广标签
    private String promotionTag;
    //APP专享价
    private BigDecimal appExclusivePrice;
    //是否APP专属：0否，1是
    private Integer isAppExclusive;
    //限购
    private Integer isLimited;
    //热销：0否，1是
    private Integer isHot;
    //市场价格
    private BigDecimal marketPrice;
    //创建用户id
    private Long createUserId;
    //用户部门id
    private Long createUserDeptId;
    //更新用户id
    private Long updateUserId;
    //创建时间
    private Date addTime;
    //修改时间
    private Date updateTime;

    /**
     * 引用字段
     */
    //商品属性
    @TableField(exist = false)
    List<GoodsAttributeEntity> attributeEntityList = new ArrayList<>();
    //商品类型
    @TableField(exist = false)
    private String categoryName;
    //属性类别
    @TableField(exist = false)
    private String attributeCategoryName;
    //品牌
    @TableField(exist = false)
    private String brandName;
    //商品id
    @TableField(exist = false)
    private String productId;
    //购物车中商品数量
    @TableField(exist = false)
    private Integer cartNum = 0;
    //订单数量
    @TableField(exist = false)
    private Integer orderNum = 0;

}
