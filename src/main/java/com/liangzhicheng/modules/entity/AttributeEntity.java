package com.liangzhicheng.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("test_attribute")
public class AttributeEntity extends Model<AttributeEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //所属种类
    private Integer attributeCategoryId;
    //属性名称
    private String name;
    //当添加商品时，该属性的添加类别：0为手功输入，1为选择输入，2为多行文本输入
    private Integer inputType;
    //即选择输入，则attr_name对应的值的取值就是该这字段值
    private String value;
    //排序
    private Integer sortOrder;

    /**
     * 引用字段
     */
    //所属种类名称
    @TableField(exist = false)
    private String categoryName;

}
