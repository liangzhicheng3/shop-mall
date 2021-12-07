package com.liangzhicheng.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("test_user")
public class UserEntity extends Model<UserEntity> {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //用户名称
    private String username;
    //密码
    private String password;
    //性别
    private Integer gender;
    //出生日期
    private Date birthday;
    //注册时间
    private Date registerTime;
    //最后登录时间
    private Date lastLoginTime;
    //最后登录ip
    private String lastLoginIp;
    //用户等级
    private Integer userLevelId;
    //昵称
    private String nickname;
    //手机号码
    private String mobile;
    //注册ip
    private String registerIp;
    //头像
    private String avatar;
    //微信id
    private String weixinOpenid;

    /**
     * 引用字段
     */
    //会员级别
    @TableField(exist = false)
    private String levelName;

}
