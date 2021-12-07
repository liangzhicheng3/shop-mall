package com.liangzhicheng.modules.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键
    private Long userId;
    //会员名称
    private String username;
    //性别
    private Integer gender;
    //出生日期
    private Date birthday;
    //注册时间
    private Date registerTime;
    //最后登录时间
    private Date lastLoginTime;
    //最后登录Ip
    private String lastLoginIp;
    //会员等级
    private Integer userLevelId;
    //别名
    private String nickname;
    //手机号码
    private String mobile;
    //注册Ip
    private String registerIp;
    //头像
    private String avatar;
    //微信Id
    private String weixinOpenid;

    //会员级别
    private String levelName;

}
