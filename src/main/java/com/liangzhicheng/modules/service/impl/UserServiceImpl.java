package com.liangzhicheng.modules.service.impl;

import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.bean.RedisBean;
import com.liangzhicheng.common.utils.JSONUtil;
import com.liangzhicheng.modules.entity.UserEntity;
import com.liangzhicheng.modules.mapper.IUserMapper;
import com.liangzhicheng.modules.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class UserServiceImpl extends BaseServiceImpl<IUserMapper, UserEntity> implements IUserService {

    @Resource
    private RedisBean redisBean;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(UserEntity user) {
        user.setRegisterTime(new Date());
        return baseMapper.insert(user);
    }

    @Override
    public UserEntity getByCache(String userId) {
        String userInfo = redisBean.hGet(Constants.USER_INFO, userId);
        return JSONUtil.parseObject(userInfo, UserEntity.class);
    }

}
