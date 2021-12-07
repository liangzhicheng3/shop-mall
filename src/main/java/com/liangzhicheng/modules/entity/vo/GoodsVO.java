package com.liangzhicheng.modules.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value="GoodsVO")
public class GoodsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Integer id;
    @ApiModelProperty("商品类型Id")
    private Integer categoryId;
    @ApiModelProperty("商品序列号")
    private String goodsSn;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("品牌Id")
    private Integer brandId;
    @ApiModelProperty("商品序列号")
    private Integer goodsNumber;
    @ApiModelProperty("关键字")
    private String keywords;
    @ApiModelProperty("简明介绍")
    private String goodsBrief;
    @ApiModelProperty("商品描述")
    private String goodsDesc;
    @ApiModelProperty("上架")
    private Integer isOnSale;
    @ApiModelProperty("添加时间")
    private Date addTime;
    @ApiModelProperty("排序")
    private Integer sortOrder;
    @ApiModelProperty("删除状态")
    private Integer isDelete;
    @ApiModelProperty("属性类别")
    private Integer attributeCategory;
    @ApiModelProperty("专柜价格")
    private BigDecimal counterPrice;
    @ApiModelProperty("附加价格")
    private BigDecimal extraPrice;
    @ApiModelProperty("是否新商品")
    private Integer isNew;
    @ApiModelProperty("商品单位")
    private String goodsUnit;
    @ApiModelProperty("商品主图")
    private String primaryPicUrl;
    @ApiModelProperty("商品列表图")
    private String listPicUrl;
    @ApiModelProperty("市场价")
    private BigDecimal marketPrice;
    @ApiModelProperty("零售价格(现价)")
    private BigDecimal retailPrice;
    @ApiModelProperty("销售量")
    private Integer sellVolume;
    @ApiModelProperty("主sku　product_id")
    private Integer primaryProductId;
    @ApiModelProperty("单位价格，单价")
    private BigDecimal unitPrice;
    @ApiModelProperty("推广描述")
    private String promotionDesc;
    @ApiModelProperty("推广标签")
    private String promotionTag;
    @ApiModelProperty("APP专享价")
    private BigDecimal appExclusivePrice;
    @ApiModelProperty("是否是APP专属")
    private Integer isAppExclusive;
    @ApiModelProperty("限购")
    private Integer isLimited;
    @ApiModelProperty("热销")
    private Integer isHot;
    @ApiModelProperty("购物车中商品数量")
    private Integer cartNum = 0;

    //冗余
    @ApiModelProperty("商品Id")
    private Integer productId;

}
