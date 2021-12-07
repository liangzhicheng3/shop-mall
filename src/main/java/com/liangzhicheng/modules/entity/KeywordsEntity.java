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
@TableName("test_keywords")
public class KeywordsEntity extends Model<KeywordsEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //关键字
    private String keywords;
    //热门
    private Integer isHot;
    //默认
    private Integer isDefault;
    //显示
    private Integer isShow;
    //排序
    private Integer sortOrder;
    //关键词的跳转链接
    private String schemeUrl;
    //类型
    private Integer type;

}
