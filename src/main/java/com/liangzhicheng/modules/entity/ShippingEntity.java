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
@TableName("test_shipping")
public class ShippingEntity extends Model<ShippingEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //物流代码
    private String code;
    //物流名称
    private String name;
    //状态：0正常，1删除
    private Integer status;

}
