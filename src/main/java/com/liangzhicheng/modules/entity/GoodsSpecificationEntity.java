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

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("test_goods_specification")
public class GoodsSpecificationEntity extends Model<GoodsSpecificationEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //商品id
    private Integer goodsId;
    //规格id
    private Integer specificationId;
    //规格说明
    private String value;
    //规格图片
    private String picUrl;

    /**
     * 引用字段
     */
    //商品
    @TableField(exist = false)
    private String goodsName;
    //规格名称（服务端）
    @TableField(exist = false)
    private String specificationName;
    //规格名称（客户端）
    @TableField(exist = false)
    private String name;

}
