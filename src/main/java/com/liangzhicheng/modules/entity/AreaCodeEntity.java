package com.liangzhicheng.modules.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("test_area_code")
public class AreaCodeEntity extends Model<AreaCodeEntity> {

    //地区id
    private String areaId;
    //地区编码
    private String areaCode;
    //地区层级
    private Integer areaLevel;

}
