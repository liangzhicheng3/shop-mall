package com.liangzhicheng.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("test_order_goods")
public class OrderGoodsEntity extends Model<OrderGoodsEntity> {

	//主键
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	//订单id
	private Integer orderId;
	//商品id
	private Integer goodsId;
	//商品名称
	private String goodsName;
	//商品序列号
	private String goodsSn;
	//商品id
	private Integer productId;
	//商品数量
	private Integer number;
	//市场价
	private BigDecimal marketPrice;
	//零售价格
	private BigDecimal retailPrice;
	//商品规格详情
	private String goodsSpecifitionNameValue;
	//虚拟商品
	private Integer isReal;
	//商品规格ids
	private String goodsSpecifitionIds;
	//图片链接
	private String listPicUrl;

}
