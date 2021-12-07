package com.liangzhicheng.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liangzhicheng.modules.entity.tree.TreeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("test_category")
public class CategoryEntity extends TreeEntity<CategoryEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //分类名称
    private String name;
    //关键字
    private String keywords;
    //描述
    private String frontDesc;
    //父节点
    private Integer parentId;
    //排序
    private Integer sortOrder;
    //首页展示
    private Integer showIndex;
    //显示
    private Integer isShow;
    //banner图片
    private String bannerUrl;
    //icon链接
    private String iconUrl;
    //图片
    private String imgUrl;
    //手机banner
    private String wapBannerUrl;
    //级别
    private String level;
    //类型
    private Integer type;
    //
    private String frontName;

    /**
     * 引用字段
     */
    //显示
    @TableField(exist = false)
    private String show;

}
