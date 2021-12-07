package com.liangzhicheng.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("test_goods_attribute")
public class GoodsAttributeEntity extends Model<GoodsAttributeEntity> {

	//主键
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	//商品id
	private Integer goodsId;
	//属性id
	private Integer attributeId;
	//属性值
	private String value;

}
