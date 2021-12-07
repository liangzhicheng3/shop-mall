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
@TableName("test_search_history")
public class SearchHistoryEntity extends Model<SearchHistoryEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //关键字
    private String keywords;
    //搜索来源，如PC、小程序、APP等
    private String from;
    //搜索时间
    private Long addTime;
    //用户id
    private String userId;

    /**
     * 引用字段
     */
    //用户名称
    @TableField(exist = false)
    private String userName;

}
