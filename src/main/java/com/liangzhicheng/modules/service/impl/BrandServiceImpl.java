package com.liangzhicheng.modules.service.impl;

import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.modules.entity.BrandEntity;
import com.liangzhicheng.modules.mapper.IBrandMapper;
import com.liangzhicheng.modules.service.IBrandService;
import org.springframework.stereotype.Service;

@Service
public class BrandServiceImpl extends BaseServiceImpl<IBrandMapper, BrandEntity> implements IBrandService {

    public int delete(Integer id) {
        BrandEntity brand = this.get(id);
        if(ToolUtil.isNotNull(brand)){
            brand.setIsShow(0);
            return baseMapper.updateById(brand);
        }
        return 0;
    }

}
