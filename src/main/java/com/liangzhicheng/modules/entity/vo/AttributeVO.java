package com.liangzhicheng.modules.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AttributeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键
    private Integer id;
    //所属种类
    private Integer attributeCategoryId;
    //名称
    private String name;
    //类型
    private Integer inputType;
    //值
    private String value;
    //排序
    private Integer sortOrder;

}
