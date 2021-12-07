package com.liangzhicheng.modules.service.impl;

import com.liangzhicheng.modules.entity.CategoryEntity;
import com.liangzhicheng.modules.mapper.IApiCategoryMapper;
import com.liangzhicheng.modules.service.IApiCategoryService;
import org.springframework.stereotype.Service;

@Service
public class ApiCategoryServiceImpl extends BaseServiceImpl<IApiCategoryMapper, CategoryEntity>implements IApiCategoryService {

}
