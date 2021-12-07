package com.liangzhicheng.modules.entity.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="GoodsListVO")
public class GoodsListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //扩展
    private Object categoryFilter;
    //扩展
    private Object goodsList;

}
