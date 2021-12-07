package com.liangzhicheng.modules.service.impl;

import com.liangzhicheng.modules.entity.ConfigEntity;
import com.liangzhicheng.modules.mapper.IConfigMapper;
import com.liangzhicheng.modules.service.IConfigService;
import org.springframework.stereotype.Service;

@Service("configService")
public class ConfigServiceImpl extends BaseServiceImpl<IConfigMapper, ConfigEntity> implements IConfigService {

}
