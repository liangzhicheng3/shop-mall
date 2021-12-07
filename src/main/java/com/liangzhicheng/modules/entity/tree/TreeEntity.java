package com.liangzhicheng.modules.entity.tree;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.List;

/**
 * 包装树形实体类
 * @param <T>
 */
@Data
public class TreeEntity<T> extends Model<TreeEntity<T>> {

    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private String value;
    @TableField(exist = false)
    private List<?> children;

}
