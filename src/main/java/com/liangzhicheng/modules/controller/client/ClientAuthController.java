package com.liangzhicheng.modules.controller.client;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.exception.TransactionException;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.common.bean.RedisBean;
import com.liangzhicheng.common.utils.BeansUtil;
import com.liangzhicheng.common.utils.JSONUtil;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.UserEntity;
import com.liangzhicheng.modules.entity.vo.UserVO;
import com.liangzhicheng.modules.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = {"登录"})
@RestController
@RequestMapping(value = "/client/auth")
public class ClientAuthController extends BaseController {

    @Resource
    private IUserService userService;
    @Resource
    private RedisBean redisBean;

    @ApiOperation(value = "登录")
    @PostMapping("/login")
    public ResponseResult login(String mobile, String password) {
        String content = "用户名或密码错误！";
        ToolUtil.isFalse(ToolUtil.isBlank(mobile)
                || ToolUtil.isBlank(password), content);
        //用户登录
        UserEntity user = userService.getOne(
                Wrappers.<UserEntity>lambdaQuery()
                        .eq(UserEntity::getMobile, mobile));
        ToolUtil.isFalse(ToolUtil.isNull(user), content);
        if(!user.getPassword().equals(password)){
            throw new TransactionException(content);
        }
        user.setPassword("");
        redisBean.hSet(Constants.USER_INFO, user.getId().toString(), JSONUtil.toJSONString(user));
        return buildSuccessInfo(BeansUtil.copyEntity(user, UserVO.class));
    }

}
