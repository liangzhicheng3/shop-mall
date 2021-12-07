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
@TableName("test_address")
public class AddressEntity extends Model<AddressEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //用户id
    private Integer userId;
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
    //是否默认：0否，1是
    private Integer isDefault;

    /**
     * 引用字段
     */
    @TableField(exist = false)
    private String shopUserName;

}
