package com.liangzhicheng.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("test_config")
public class ConfigEntity extends Model<ConfigEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    //key
    @NotBlank(message = "参数名不能为空")
    private String keyName;
    //值
    @NotBlank(message = "参数值不能为空")
    private String value;
    //备注
    private String remark;

}
