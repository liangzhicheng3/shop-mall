package com.liangzhicheng.modules.service;

import com.liangzhicheng.modules.entity.UserEntity;

public interface IUserService extends IBaseService<UserEntity> {

    int insert(UserEntity user);

    UserEntity getByCache(String userId);

}
