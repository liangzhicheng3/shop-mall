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
@TableName("test_attribute_category")
public class AttributeCategoryEntity extends Model<AttributeCategoryEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //属性分类名称
    private String name;
    //启用：0关闭，1开启
    private Integer enabled;

}
