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
@TableName("test_brand")
public class BrandEntity extends Model<BrandEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //品牌名称
    private String name;
    //图片
    private String listPicUrl;
    //描述
    private String simpleDesc;
    //图片
    private String picUrl;
    //排序
    private Integer sortOrder;
    //显示：0否，1是
    private Integer isShow;
    //
    private BigDecimal floorPrice;
    //app显示图片
    private String appListPicUrl;
    //新品牌：0否，1是
    private Integer isNew;
    //图片
    private String newPicUrl;
    //排序
    private Integer newSortOrder;

}
