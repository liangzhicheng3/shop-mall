package com.liangzhicheng.modules.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsPurchaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //商品id
    private Integer goodsId;
    //商品id
    private Integer productId;
    //数量
    private Integer number;

}
