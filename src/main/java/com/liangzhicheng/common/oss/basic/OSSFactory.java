package com.liangzhicheng.common.oss.basic;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.oss.object.CloudStorage;
import com.liangzhicheng.common.oss.service.*;
import com.liangzhicheng.common.utils.JSONUtil;
import com.liangzhicheng.config.context.SpringContextHolder;
import com.liangzhicheng.modules.entity.ConfigEntity;
import com.liangzhicheng.modules.service.IConfigService;

public final class OSSFactory {

    private static IConfigService configService;

    static {
        configService = SpringContextHolder.getBean("configService");
    }

    public static CloudStorageService build() {
        //获取云存储配置信息
        ConfigEntity config = configService.getOne(Wrappers.<ConfigEntity>lambdaQuery()
                .eq(ConfigEntity::getKeyName, Constants.CLOUD_STORAGE_CONFIG_KEY));
        CloudStorage cloudStorage = JSONUtil.parseObject(config.getValue(), CloudStorage.class);
        switch(cloudStorage.getType()){
            case 1:
                return new QiniuCloudStorageService(cloudStorage);
            case 2:
                return new AliCloudStorageService(cloudStorage);
            case 3:
                return new TencentCloudStorageService(cloudStorage);
            case 4:
                return new ServerStorageService(cloudStorage);
        }
        return null;
    }

}
