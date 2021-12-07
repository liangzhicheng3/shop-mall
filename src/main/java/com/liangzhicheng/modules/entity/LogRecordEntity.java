package com.liangzhicheng.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_log_record")
public class LogRecordEntity extends Model<LogRecordEntity> {

    @TableId(value = "id", type = IdType.AUTO)
    //主键
    private Long id;
    //用户id
    private String userId;
    //操作记录
    private String operate;
    //请求方法
    private String method;
    //请求参数
    private String params;
    //ip地址
    private String ip;
    //创建时间
    private Date createDate;

}
