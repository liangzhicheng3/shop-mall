package com.liangzhicheng.modules.service.impl;

import com.liangzhicheng.modules.entity.CategoryEntity;
import com.liangzhicheng.modules.mapper.ICategoryMapper;
import com.liangzhicheng.modules.service.ICategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServiceImpl extends BaseServiceImpl<ICategoryMapper, CategoryEntity> implements ICategoryService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(CategoryEntity category) {
        if("L1".equals(category.getLevel())){
            category.setParentId(0);
        }
        return baseMapper.insert(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(CategoryEntity category) {
        if("L1".equals(category.getLevel())){
            category.setParentId(0);
        }
        return baseMapper.updateById(category);
    }


}
