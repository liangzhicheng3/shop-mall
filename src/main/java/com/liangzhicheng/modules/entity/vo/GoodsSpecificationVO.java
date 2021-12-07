package com.liangzhicheng.modules.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsSpecificationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键
    private Integer id;
    //商品
    private Integer goodsId;
    //规格Id
    private Integer specificationId;
    //规格说明
    private String value;
    private String name;
    //规格图片
    private String picUrl;

}
