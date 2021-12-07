package com.liangzhicheng.modules.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value="ProductVO")
public class ProductVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Integer id;
    @ApiModelProperty("商品id")
    private Integer goodsId;
    @ApiModelProperty("商品规格ids")
    private String goodsSpecificationIds;
    @ApiModelProperty("商品序列号")
    private String goodsSn;
    @ApiModelProperty("商品库存")
    private Integer goodsNumber;
    @ApiModelProperty("零售价格")
    private BigDecimal retailPrice;
    @ApiModelProperty("市场价格")
    private BigDecimal marketPrice;

    //商品id
    private String productId;
    //商品
    private String goodsName;
    //商品图片
    private String listPicUrl;

}
