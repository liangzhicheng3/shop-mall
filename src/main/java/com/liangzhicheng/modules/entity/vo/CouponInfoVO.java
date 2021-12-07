package com.liangzhicheng.modules.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CouponInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //是否凑单 0否 1是
    private Integer type = 0;
    //显示信息
    private String message;

}
