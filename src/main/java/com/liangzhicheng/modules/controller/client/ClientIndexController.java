package com.liangzhicheng.modules.controller.client;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.common.utils.BeansUtil;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.BrandEntity;
import com.liangzhicheng.modules.entity.CartEntity;
import com.liangzhicheng.modules.entity.CategoryEntity;
import com.liangzhicheng.modules.entity.GoodsEntity;
import com.liangzhicheng.modules.entity.vo.BrandVO;
import com.liangzhicheng.modules.entity.vo.GoodsVO;
import com.liangzhicheng.modules.service.IApiBrandService;
import com.liangzhicheng.modules.service.IApiCartService;
import com.liangzhicheng.modules.service.IApiCategoryService;
import com.liangzhicheng.modules.service.IApiGoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@Api(tags = {"首页"})
@RestController
@RequestMapping(value = "/client/index")
public class ClientIndexController extends BaseController {

    @Resource
    private IApiGoodsService apiGoodsService;
    @Resource
    private IApiCartService apiCartService;
    @Resource
    private IApiBrandService apiBrandService;
    @Resource
    private IApiCategoryService apiCategoryService;

    @ApiOperation(value = "首页")
    @PostMapping(value = "/all")
    public ResponseResult all(){
        Map<String, Object> resultMap = new HashMap<>();
        //上新商品
        List<GoodsEntity> goodsListNew = apiGoodsService.list(
                Wrappers.<GoodsEntity>lambdaQuery()
                        .select(GoodsEntity::getId,
                                GoodsEntity::getName,
                                GoodsEntity::getListPicUrl,
                                GoodsEntity::getRetailPrice)
                        .eq(GoodsEntity::getIsNew, 1)
                        .eq(GoodsEntity::getIsDelete, 0)
                        .orderByDesc(GoodsEntity::getId)
                        .last("LIMIT 3"));
        resultMap.put("goodsListNew", BeansUtil.copyList(goodsListNew, GoodsVO.class));
        //热门商品
        List<GoodsEntity> goodsListHot = apiGoodsService.list(
                Wrappers.<GoodsEntity>query()
                        .select(GoodsEntity.class, info -> {
                            String column = info.getColumn();
                            return !"keywords".equals(column)
                                    && !"update_time".equals(column)
                                    && !"sort_order".equals(column)
                                    && !"primary_product_id".equals(column)
                                    && !"create_user_id".equals(column)
                                    && !"create_user_dept_id".equals(column)
                                    && !"update_user_id".equals(column);
                        })
                        .eq("is_hot", 1)
                        .eq("is_delete", 0)
                        .orderByDesc("id")
                        .last("LIMIT 3"));
        resultMap.put("goodsListHot", BeansUtil.copyList(goodsListHot, GoodsVO.class));
        //当前购物车中
        List<CartEntity> cartList = new ArrayList<>();
        String userId = "15";
        if(ToolUtil.isNotBlank(userId)){
            Map<String, Object> cartParams = new HashMap<>();
            cartParams.put("userId", userId);
            LinkedHashMap<String, Object> cartMap = apiCartService.queryListSelf(cartParams);
            cartList = (List<CartEntity>) cartMap.get("records");
        }
        if(ToolUtil.listSizeGT(cartList) && ToolUtil.listSizeGT(goodsListHot)){
            for(GoodsEntity goods : goodsListHot){
                for(CartEntity cart : cartList){
                    if(goods.getId().equals(cart.getGoodsId())){
                        goods.setCartNum(cart.getNumber());
                    }
                }
            }
        }
        //品牌
        List<BrandEntity> brandList = apiBrandService.list(
                Wrappers.<BrandEntity>query()
                        .select(BrandEntity.class, info -> {
                            String column = info.getColumn();
                            return !"sort_order".equals(column)
                                    && !"is_show".equals(column)
                                    && !"floor_price".equals(column)
                                    && !"is_new".equals(column)
                                    && !"new_pic_url".equals(column)
                                    && !"new_sort_order".equals(column);
                        })
                        .eq("is_new", 1)
                        .orderByAsc("new_sort_order")
                        .last("LIMIT 3"));
        resultMap.put("brandList", BeansUtil.copyList(brandList, BrandVO.class));
        //分类
        List<CategoryEntity> categoryList = apiCategoryService.list(
                Wrappers.<CategoryEntity>lambdaQuery()
                        .select(CategoryEntity::getId,
                                CategoryEntity::getName)
                        .eq(CategoryEntity::getParentId, 0)
                        .ne(CategoryEntity::getName, "推荐")
                        .orderByDesc(CategoryEntity::getId)
        );
        if(ToolUtil.listSizeGT(categoryList)){
            List<Map<String, Object>> categoryListNew = new ArrayList<>(categoryList.size());
            for(Iterator<CategoryEntity> it = categoryList.iterator(); it.hasNext();){
                CategoryEntity category = it.next();
                List<CategoryEntity> childrenList = apiCategoryService.list(
                        Wrappers.<CategoryEntity>lambdaQuery()
                                .select(CategoryEntity::getId)
                                .eq(CategoryEntity::getParentId, category.getId())
                );
                if(ToolUtil.listSizeGT(childrenList)){
                    List<Integer> childrenCategoryIds = new ArrayList<>(childrenList.size());
                    for(Iterator<CategoryEntity> childrens = childrenList.iterator(); childrens.hasNext();){
                        CategoryEntity children = childrens.next();
                        childrenCategoryIds.add(children.getId());
                    }
                    List<GoodsEntity> goodsList = apiGoodsService.list(
                            Wrappers.<GoodsEntity>lambdaQuery()
                                    .select(GoodsEntity::getId,
                                            GoodsEntity::getName,
                                            GoodsEntity::getListPicUrl,
                                            GoodsEntity::getRetailPrice)
                                    .in(GoodsEntity::getCategoryId, childrenCategoryIds)
                                    .last("LIMIT 6")
                    );
                    Map<String, Object> categoryMapNew = new HashMap<>();
                    categoryMapNew.put("id", category.getId());
                    categoryMapNew.put("name", category.getName());
                    categoryMapNew.put("goodsList", goodsList);
                    categoryListNew.add(categoryMapNew);
                }
            }
            resultMap.put("categoryList", categoryListNew);
        }
        return buildSuccessInfo(resultMap);
    }

}
