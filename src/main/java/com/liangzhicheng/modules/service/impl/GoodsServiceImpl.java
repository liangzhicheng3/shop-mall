package com.liangzhicheng.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.modules.entity.GoodsAttributeEntity;
import com.liangzhicheng.modules.entity.GoodsEntity;
import com.liangzhicheng.modules.entity.ProductEntity;
import com.liangzhicheng.modules.mapper.IGoodsMapper;
import com.liangzhicheng.modules.service.IGoodsAttributeService;
import com.liangzhicheng.modules.service.IGoodsService;
import com.liangzhicheng.modules.service.IProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class GoodsServiceImpl extends ServiceImpl<IGoodsMapper, GoodsEntity> implements IGoodsService {

    @Resource
    private IProductService productService;
    @Resource
    private IGoodsAttributeService goodsAttributeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(GoodsEntity goods) {
        this.existGoods(goods.getName());
        Integer id = baseMapper.getMax() + 1;
        goods.setId(id);
        //保存商品信息
        ProductEntity product = new ProductEntity(id, goods.getGoodsSn(),
                goods.getGoodsNumber(), goods.getRetailPrice(), goods.getMarketPrice());
        productService.save(product);
        //保存商品详情页显示的属性
        List<GoodsAttributeEntity> attrListRequest = goods.getAttributeEntityList();
        if(attrListRequest != null && attrListRequest.size() > 0){
            List<GoodsAttributeEntity> attrList = new ArrayList<>(attrListRequest.size());
            for(GoodsAttributeEntity attr : attrListRequest){
                attr.setGoodsId(id);
                attrList.add(attr);
            }
            goodsAttributeService.saveBatch(attrList);
        }
        Date currentTime = new Date();
        goods.setAddTime(currentTime);
        goods.setPrimaryProductId(product.getId());
        goods.setIsDelete(0);
        goods.setCreateUserDeptId(1L);
        goods.setCreateUserId(1L);
        goods.setUpdateUserId(1L);
        goods.setUpdateTime(currentTime);
        return baseMapper.insert(goods);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(String ids) {
        int rows = 0;
        if(ToolUtil.isNotNull(ids)){
            if(ids.contains(",")){
                StringTokenizer tokenizer = new StringTokenizer(ids, ",");
                while(tokenizer.hasMoreElements()){
                    rows += this.delete(Integer.parseInt(tokenizer.nextToken()));
                }
            }else{
                rows = this.delete(Integer.parseInt(ids));
            }
        }
        return rows;
    }

    public int delete(Integer id) {
        GoodsEntity goods = this.get(id);
        if(ToolUtil.isNotNull(goods)){
            goods.setIsOnSale(0);
            goods.setIsDelete(1);
            goods.setUpdateUserId(1L);
            goods.setUpdateTime(new Date());
            return baseMapper.updateById(goods);
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(GoodsEntity goods) {
        this.existGoods(goods.getName());
        List<GoodsAttributeEntity> attrListRequest = goods.getAttributeEntityList();
        if(attrListRequest != null && attrListRequest.size() > 0){
            for(Iterator<GoodsAttributeEntity> it = attrListRequest.iterator(); it.hasNext();){
                GoodsAttributeEntity goodsAttr = it.next();
                boolean result = goodsAttributeService.lambdaUpdate()
                        .eq(GoodsAttributeEntity::getGoodsId, goodsAttr.getGoodsId())
                        .eq(GoodsAttributeEntity::getAttributeId, goodsAttr.getAttributeId())
                        .set(GoodsAttributeEntity::getValue, goodsAttr.getValue()).update();
                if(!result){
                    goodsAttributeService.save(goodsAttr);
                }
            }
        }
        goods.setUpdateUserId(1L);
        goods.setUpdateTime(new Date());
        return baseMapper.updateById(goods);
    }

    @Override
//    @PermFilter(userAlias = "goods.create_user_id", deptAlias = "goods.create_user_dept_id")
    public Integer getTotal(Map<String, Object> params) {
        return baseMapper.getTotal(params);
    }

    @Override
//    @PermFilter(userAlias = "goods.create_user_id", deptAlias = "goods.create_user_dept_id")
    public List<GoodsEntity> queryList(Map<String, Object> params){
        return baseMapper.queryListSelf(params);
    }

    @Override
    public GoodsEntity get(Integer id) {
        return baseMapper.selectById(id);
    }

    /**
     * 判断商品名称是否存在
     * @param name
     */
    private void existGoods(String name){
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        List<GoodsEntity> goodsList = queryList(map);
        ToolUtil.isFalse(goodsList != null && goodsList.size() > 0, "商品名称已存在");
    }

}
