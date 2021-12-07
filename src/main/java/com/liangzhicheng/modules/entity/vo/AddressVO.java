package com.liangzhicheng.modules.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class AddressVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键
    private Long id;
    //会员ID
    private Long userId;
    //收货人姓名
    private String userName;
    //手机
    private String telNumber;
    //邮编
    private String postalCode;
    //省
    private String provinceName;
    //市
    private String cityName;
    //区
    private String countyName;
    //详细收货地址信息
    private String detailInfo;
    //默认
    private Integer isDefault;

    /**
     * 引用字段
     */
    //全称地址
    @TableField(exist = false)
    private String fullRegion;

}
