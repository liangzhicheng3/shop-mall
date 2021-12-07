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
@TableName("test_specification")
public class SpecificationEntity extends Model<SpecificationEntity> {

	//主键
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	//规格名称
	private String name;
	//排序
	private Integer sortOrder;

}
