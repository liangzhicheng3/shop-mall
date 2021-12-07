package com.liangzhicheng.modules.controller.server;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.oss.group.AliCloudGroup;
import com.liangzhicheng.common.oss.group.QiniuCloudGroup;
import com.liangzhicheng.common.oss.group.ServerGroup;
import com.liangzhicheng.common.oss.group.TencentCloudGroup;
import com.liangzhicheng.common.oss.object.CloudStorage;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.common.utils.JSONUtil;
import com.liangzhicheng.common.utils.ValidateUtil;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.ConfigEntity;
import com.liangzhicheng.modules.service.IConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "云存储")
@RestController
@RequestMapping("/server/oss")
public class ServerOSSController extends BaseController {

    @Resource
    private IConfigService configService;

    @ApiOperation(value = "保存云存储配置")
    @GetMapping(value = "/save")
    public ResponseResult save(@RequestBody CloudStorage cloudStorage){
        ValidateUtil.validate(cloudStorage);
        switch(cloudStorage.getType()){
            case 1:
                ValidateUtil.validate(cloudStorage, QiniuCloudGroup.class);
                break;
            case 2:
                ValidateUtil.validate(cloudStorage, AliCloudGroup.class);
                break;
            case 3:
                ValidateUtil.validate(cloudStorage, TencentCloudGroup.class);
                break;
            case 4:
                ValidateUtil.validate(cloudStorage, ServerGroup.class);
                break;
        }
        configService.lambdaUpdate()
                .eq(ConfigEntity::getKeyName, Constants.CLOUD_STORAGE_CONFIG_KEY)
                .set(ConfigEntity::getValue, JSONUtil.toJSONString(cloudStorage))
                .update();
        return buildSuccessInfo();
    }

    @ApiOperation(value = "获取云存储配置")
    @GetMapping(value = "/get")
    public ResponseResult get(){
        ConfigEntity config = configService.getOne(Wrappers.<ConfigEntity>lambdaQuery()
                .eq(ConfigEntity::getKeyName, Constants.CLOUD_STORAGE_CONFIG_KEY));
        return buildSuccessInfo(JSONUtil.parseObject(config.getValue(), CloudStorage.class));
    }

}
