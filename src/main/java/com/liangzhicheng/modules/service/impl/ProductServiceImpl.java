package com.liangzhicheng.modules.service.impl;

import com.github.pagehelper.PageInfo;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.modules.entity.GoodsSpecificationEntity;
import com.liangzhicheng.modules.entity.ProductEntity;
import com.liangzhicheng.modules.mapper.IProductMapper;
import com.liangzhicheng.modules.service.IGoodsSpecificationService;
import com.liangzhicheng.modules.service.IProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ProductServiceImpl extends BaseServiceImpl<IProductMapper, ProductEntity> implements IProductService {

    @Resource
    private IGoodsSpecificationService goodsSpecificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(ProductEntity product) {
        int rows = 0;
        String goodsSpecificationIds = product.getGoodsSpecificationIds();
        if(ToolUtil.isNotNull(goodsSpecificationIds)){
            String[] specIds = goodsSpecificationIds.split("_");
            for(int i = 0; i < specIds.length; i++){
                String[] firstIds = specIds[i].split(",");
                String[] secondIds = specIds[i + 1].split(",");
                for(int j = 0; j < firstIds.length; j++){
                    for(int k = 0; k < secondIds.length; k++){
                        if(ToolUtil.isNull(firstIds[j]) || ToolUtil.isNull(secondIds[k])){
                            continue;
                        }
                        product.setGoodsSpecificationIds(firstIds[j] + "_" + secondIds[k]);
                        ProductEntity entity = new ProductEntity();
                        BeanUtils.copyProperties(product, entity);
                        rows += baseMapper.insert(entity);
                    }
                }
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(ProductEntity product) {
        if(ToolUtil.isNull(product.getGoodsSpecificationIds())){
            product.setGoodsSpecificationIds("");
        }
        return baseMapper.updateById(product);
    }

    @Override
    public LinkedHashMap<String, Object> queryList(Map<String, Object> params) {
        List<ProductEntity> productList = baseMapper.queryListSelf(params);
        List<ProductEntity> resultList = new ArrayList<>(productList.size());
        if(productList != null && productList.size() > 0){
            for(Iterator<ProductEntity> it = productList.iterator(); it.hasNext();){
                ProductEntity product = it.next();
                String specificationIds = product.getGoodsSpecificationIds();
                String specificationValue = "";
                if(ToolUtil.isNotNull(specificationIds)){
                    StringTokenizer tokenizer = new StringTokenizer(specificationIds, "_");
                    while(tokenizer.hasMoreElements()){
                        GoodsSpecificationEntity goodsSpecification =
                                goodsSpecificationService.getById(tokenizer.nextToken());
                        if(ToolUtil.isNotNull(goodsSpecification)){
                            specificationValue += goodsSpecification.getValue() + ";";
                        }
                    }
                }
                product.setSpecificationValue(product.getGoodsName() + "->" + specificationValue);
                resultList.add(product);
            }
        }
        return this.pageResult(new PageInfo<>(resultList));
    }

}
