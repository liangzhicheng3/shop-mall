package com.liangzhicheng.modules.service.impl;

import com.liangzhicheng.modules.entity.ProductEntity;
import com.liangzhicheng.modules.mapper.IApiProductMapper;
import com.liangzhicheng.modules.service.IApiProductService;
import org.springframework.stereotype.Service;

@Service
public class ApiProductServiceImpl extends BaseServiceImpl<IApiProductMapper, ProductEntity> implements IApiProductService {

}
