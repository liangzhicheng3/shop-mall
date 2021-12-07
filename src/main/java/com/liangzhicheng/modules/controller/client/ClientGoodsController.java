package com.liangzhicheng.modules.controller.client;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.common.utils.BeansUtil;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.config.mvc.resolver.annotation.UserParam;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.*;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.entity.vo.*;
import com.liangzhicheng.modules.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@Api(tags = {"商品"})
@RestController
@RequestMapping(value = "/client/goods")
public class ClientGoodsController extends BaseController {

    @Resource
    private IApiBrandService apiBrandService;
    @Resource
    private IApiCategoryService apiCategoryService;
    @Resource
    private IApiGoodsService apiGoodsService;
    @Resource
    private IApiGoodsSpecificationService apiGoodsSpecificationService;
    @Resource
    private IApiProductService apiProductService;
    @Resource
    private IApiAttributeService apiAttributeService;
    @Resource
    private IApiFootprintService apiFootprintService;
    @Resource
    private IApiCartService apiCartService;
    @Resource
    private IApiSearchHistoryService apiSearchHistoryService;

    @ApiOperation(value = "获取分类")
    @GetMapping(value = "/category/{id}")
    public Object getCategory(@PathVariable("id") Integer id) {
        CategoryEntity currentCategory = apiCategoryService.get(id);
        Integer parentId = currentCategory.getParentId();
        CategoryEntity parentCategory = apiCategoryService.get(parentId);
        Map<String, Object> params = new HashMap<>();
        params.put("parentId", parentId);
        LinkedHashMap<String, Object> categoryMap = apiCategoryService.queryList(params);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("currentCategory", currentCategory);
        resultMap.put("childrenCategory", categoryMap.get("records"));
        resultMap.put("parentCategory", parentCategory);
        return buildSuccessInfo(resultMap);
    }

    @ApiOperation(value = "商品首页")
    @PostMapping(value = "/index")
    public ResponseResult index(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        params.put("isOnSale", 1);
        params.put("isDelete", 0);
        return buildSuccessInfo(apiGoodsService.queryList(query));
    }

    @ApiOperation(value = "获取商品列表")
    @PostMapping(value = "/list")
    @ApiImplicitParams({@ApiImplicitParam(name = "categoryId", value = "分类id", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "brandId", value = "品牌id", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "keywords", value = "关键字", required = false, dataType = "String"),
            @ApiImplicitParam(name = "isNew", value = "上新商品", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "isHot", value = "热卖商品", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "pageNo", value = "当前页码", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "sortField", value = "排序字段", required = false, dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序规则", required = false, dataType = "String")})
    public ResponseResult list(@UserParam UserEntity user,
                               Integer categoryId,
                               Integer brandId,
                               String keywords,
                               Integer isNew,
                               Integer isHot,
                               Integer pageNo,
                               Integer pageSize,
                               String sortField,
                               String sortOrder){
        Map<String, Object> params = new HashMap<>();
        params.put("brandId", brandId);
        params.put("keywords", keywords);
        params.put("isNew", isNew);
        params.put("isHot", isHot);
        params.put("isOnSale", 1);
        params.put("isDelete", 0);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("sortField", sortField);
        params.put("sortOrder", sortOrder);
        //筛选的分类
        List<CategoryVO> categoryFilter = new ArrayList<>();
        CategoryVO categoryRoot = new CategoryVO();
        categoryRoot.setId(0);
        categoryRoot.setName("全部");
        categoryRoot.setChecked(false);
        categoryFilter.add(categoryRoot);

        LinkedHashMap<String, Object> goodsMap = apiGoodsService.queryList(params);
        List<GoodsEntity> goodsList = (List<GoodsEntity>) goodsMap.get("records");
        if(ToolUtil.listSizeGT(goodsList)){
            List<Integer> categoryIds = new ArrayList<>(goodsList.size());
            for(Iterator<GoodsEntity> it = goodsList.iterator(); it.hasNext();){
                categoryIds.add(it.next().getCategoryId());
            }
            //查找二级分类的parentId
            Map<String, Object> categoryParams = new HashMap<>();
            categoryParams.put("ids", categoryIds);
            LinkedHashMap<String, Object> parentMap = apiCategoryService.queryList(categoryParams);
            List<CategoryEntity> categoryList = (List<CategoryEntity>) parentMap.get("records");
            if(ToolUtil.listSizeGT(categoryList)){
                List<Integer> parentIds = new ArrayList<>(categoryList.size());
                for(Iterator<CategoryEntity> it = categoryList.iterator(); it.hasNext();){
                    parentIds.add(it.next().getParentId());
                }
                //查找一级分类
                categoryParams = new HashMap<>();
                categoryParams.put("ids", parentIds);
                categoryParams.put("sortField", "sort_order");
                categoryParams.put("sortOrder", "ASC");
                LinkedHashMap<String, Object> topMap = apiCategoryService.queryList(categoryParams);
                List<CategoryEntity> topList = (List<CategoryEntity>) topMap.get("records");
                if(ToolUtil.listSizeGT(topList)){
                    categoryFilter.addAll(BeansUtil.copyList(topList, CategoryVO.class));
                }
            }
        }
        //分类条件
        if(categoryId != null && categoryId > 0){
            Map<String, Object> categoryParams = new HashMap<>();
            categoryParams.put("parentId", categoryId);
            LinkedHashMap<String, Object> categoryMap = apiCategoryService.queryList(categoryParams);
            List<CategoryEntity> categoryList = (List<CategoryEntity>) categoryMap.get("records");
            if(ToolUtil.listSizeGT(categoryList)){
                List<Integer> categoryIds = new ArrayList<>(categoryList.size());
                for(Iterator<CategoryEntity> it = categoryList.iterator(); it.hasNext();){
                    categoryIds.add(it.next().getId());
                }
                categoryIds.add(categoryId);
                params.put("categoryIds", categoryIds);
            }
        }
        Query query = new Query(params);
        goodsMap = apiGoodsService.queryList(query);
        for(Iterator<CategoryVO> it = categoryFilter.iterator(); it.hasNext();){
            CategoryVO categoryVO = it.next();
            if(categoryId != null
                    && (categoryVO.getId() == 0 || categoryId.equals(categoryVO.getId()))
                    || categoryId == null && categoryVO.getId() == null) {
                categoryVO.setChecked(true);
            }else{
                categoryVO.setChecked(false);
            }
        }
        //商品关键字搜索保存
        if(ToolUtil.isNotBlank(keywords)){
            SearchHistoryEntity searchHistory = new SearchHistoryEntity();
            searchHistory.setKeywords(keywords);
            searchHistory.setAddTime(System.currentTimeMillis() / 1000);
            searchHistory.setUserId(user != null ? user.getId().toString() : "");
            apiSearchHistoryService.save(searchHistory);
        }
        GoodsListVO goodsListVO = new GoodsListVO(categoryFilter, goodsMap);
        return buildSuccessInfo(goodsListVO);
    }

    @ApiOperation(value = "获取商品列表")
    @PostMapping(value = "/list/product")
    @ApiImplicitParams({@ApiImplicitParam(name = "categoryId", value = "分类id", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "isNew", value = "上新商品", required = false, dataType = "Integer"),
//            @ApiImplicitParam(name = "discount", value = "折扣", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "pageNo", value = "当前页码", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "sortField", value = "排序字段", required = false, dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序规则", required = false, dataType = "String")})
    public ResponseResult listProduct(Integer categoryId,
                                      Integer isNew,
//                                      Integer discount,
                                      Integer pageNo,
                                      Integer pageSize,
                                      String sortField,
                                      String sortOrder){
        Map<String, Object> params = new HashMap<>();
        params.put("isNew", isNew);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("sortField", sortField);
        params.put("sortOrder", sortOrder);
        if(ToolUtil.isNotBlank(sortField, sortOrder)){
            switch(sortField){
                case "price":
                    params.put("sortField", "retail_price");
                    break;
                case "sell":
                    params.put("sortField", "orderNum");
                    break;
            }
        }
        //折扣：0不限，1特价，2团购
//        if(discount != null){
//            switch(discount){
//                case 1:
//                    params.put("isHot", 1);
//                    break;
//                case 2:
//                    params.put("isGroup", true);
//                    break;
//            }
//        }
        //分类条件
        if(categoryId != null && categoryId > 0){
            Map<String, Object> categoryParams = new HashMap<>();
            categoryParams.put("parentId", categoryId);
            LinkedHashMap<String, Object> categoryMap = apiCategoryService.queryList(categoryParams);
            List<CategoryEntity> categoryList = (List<CategoryEntity>) categoryMap.get("records");
            if(ToolUtil.listSizeGT(categoryList)){
                List<Integer> categoryIds = new ArrayList<>(categoryList.size());
                for(Iterator<CategoryEntity> it = categoryList.iterator(); it.hasNext();){
                    categoryIds.add(it.next().getId());
                }
                categoryIds.add(categoryId);
                params.put("categoryIds", categoryIds);
            }
        }
        Query query = new Query(params);
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        List<GoodsEntity> goodsList = apiGoodsService.listProduct(query); //商品目录
        //当前购物车中
        List<CartEntity> cartList = new ArrayList<>();
        String userId = "15";
        if(ToolUtil.isNotBlank(userId)){
            Map<String, Object> cartParams = new HashMap<>();
            cartParams.put("userId", userId);
            LinkedHashMap<String, Object> cartMap = apiCartService.queryListSelf(cartParams);
            cartList = (List<CartEntity>) cartMap.get("records");
        }
        if(ToolUtil.listSizeGT(cartList) && ToolUtil.listSizeGT(goodsList)){
            for(GoodsEntity goods : goodsList){
                for(CartEntity cart : cartList){
                    if(goods.getId().equals(cart.getGoodsId())){
                        goods.setCartNum(cart.getNumber());
                    }
                }
            }
        }
        PageInfo<GoodsVO> pageInfo = new PageInfo<>(BeansUtil.copyList(goodsList, GoodsVO.class));
        Map<String, Object> resultMap = new LinkedHashMap<>(4);
        resultMap.put("records", pageInfo.getList());
        resultMap.put("total", pageInfo.getTotal());
        resultMap.put("pageNo", pageInfo.getPageNum());
        resultMap.put("pageSize", pageInfo.getPageSize());
        return buildSuccessInfo(resultMap);
    }

    @ApiOperation(value = "获取商品列表筛选的分类列表")
    @PostMapping(value = "/select")
    @ApiImplicitParams({@ApiImplicitParam(name = "categoryId", value = "分类id", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "keywords", value = "关键字", required = false, dataType = "String"),
            @ApiImplicitParam(name = "isNew", value = "上新商品", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "isHot", value = "热卖商品", required = false, dataType = "Integer")})
    public ResponseResult getSelect(Integer categoryId,
                                 String keywords,
                                 Integer isNew,
                                 Integer isHot){
        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", categoryId);
        params.put("keyword", keywords);
        params.put("isNew", isNew);
        params.put("isHot", isHot);
        params.put("isOnSale", 1);
        params.put("isDelete", 0);
        //分类条件
        if(categoryId != null && categoryId > 0){
            Map<String, Object> categoryParams = new HashMap<>();
            categoryParams.put("parentId", categoryId);
            LinkedHashMap<String, Object> categoryMap = apiCategoryService.queryList(categoryParams);
            List<CategoryEntity> categoryList = (List<CategoryEntity>) categoryMap.get("records");
            if(ToolUtil.listSizeGT(categoryList)){
                List<Integer> categoryIds = new ArrayList<>(categoryList.size());
                for (CategoryEntity category : categoryList) {
                    categoryIds.add(category.getId());
                }
                params.put("categoryIds", categoryIds);
            }
        }
        //筛选的分类
        List<CategoryVO> categoryFilter = new ArrayList<>();
        CategoryVO categoryRoot = new CategoryVO();
        categoryRoot.setId(0);
        categoryRoot.setName("全部");

        LinkedHashMap<String, Object> goodsMap = apiGoodsService.queryList(params);
        List<GoodsEntity> goodsList = (List<GoodsEntity>) goodsMap.get("records");
        if(ToolUtil.listSizeGT(goodsList)){
            List<Integer> categoryIds = new ArrayList<>(goodsList.size());
            for(Iterator<GoodsEntity> it = goodsList.iterator(); it.hasNext();){
                categoryIds.add(it.next().getCategoryId());
            }
            //查找二级分类的parentId
            Map<String, Object> categoryParams = new HashMap<>();
            categoryParams.put("ids", categoryIds);
            LinkedHashMap<String, Object> parentMap = apiCategoryService.queryList(categoryParams);
            List<CategoryEntity> categoryList = (List<CategoryEntity>) parentMap.get("records");
            if(ToolUtil.listSizeGT(categoryList)){
                List<Integer> parentIds = new ArrayList<>(categoryList.size());
                for(Iterator<CategoryEntity> it = categoryList.iterator(); it.hasNext();){
                    parentIds.add(it.next().getId());
                }
                //查找一级分类
                categoryParams.put("ids", parentIds);
                LinkedHashMap<String, Object> topMap = apiCategoryService.queryList(categoryParams);
                List<CategoryEntity> topList = (List<CategoryEntity>) topMap.get("records");
                if(ToolUtil.listSizeGT(topList)){
                    categoryFilter.addAll(BeansUtil.copyList(topList, CategoryVO.class));
                }
            }
        }
        return buildSuccessInfo(categoryFilter);
    }

    @ApiOperation(value = "获取商品规格") //用于购物车编辑时选择规格
    @GetMapping(value = "/sku/{id}")
    public ResponseResult getSku(@PathVariable("id") Integer id){
        Map<String, Object> params = new HashMap<>();
        params.put("goodsId", id);
        LinkedHashMap<String, Object> goodsSpecificationMap = apiGoodsSpecificationService.queryListSelf(params);
        LinkedHashMap<String, Object> productMap = apiProductService.queryListSelf(params);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("specificationList", goodsSpecificationMap.get("records"));
        resultMap.put("productList", productMap.get("records"));
        return buildSuccessInfo(resultMap);
    }

    @ApiOperation(value = "获取商品详情")
    @GetMapping(value = "/detail/{id}")
    public ResponseResult getDetail(@PathVariable("id") Integer id){
        Long userId = 1L;
        GoodsEntity goods = apiGoodsService.get(id);

        Map<String, Object> params = new HashMap<>();
        params.put("goodsId", id);

        Map<String, Object> attrParams = new HashMap<>();
        attrParams.put("fields", "ga.value, attr.name");
        attrParams.put("goodsId", id);
        attrParams.put("sortField", "ga.id");
        attrParams.put("sortOrder", "ASC");
        LinkedHashMap<String, Object> attrMap = apiAttributeService.queryListSelf(attrParams);

        Map<String, Object> specificationParams = new HashMap<>();
        specificationParams.put("fields", "gs.*, s.name");
        specificationParams.put("specification", true);
        specificationParams.put("goodsId", id);
        specificationParams.put("sortField", "s.sort_order");
        specificationParams.put("sortOrder", "ASC");
        LinkedHashMap<String, Object> specificationMap = apiGoodsSpecificationService.queryListSelf(specificationParams);
        List<Map<String, Object>> specificationList = null;
        List<GoodsSpecificationEntity> goodsSpecificationList = (List<GoodsSpecificationEntity>) specificationMap.get("records");
        if(ToolUtil.listSizeGT(goodsSpecificationList)){
            specificationList = new ArrayList<>();
            for(Iterator<GoodsSpecificationEntity> it = goodsSpecificationList.iterator(); it.hasNext();){
                GoodsSpecificationEntity specification = it.next();
                List<GoodsSpecificationEntity> tempList = null;
                for(int j = 0; j < specificationList.size(); j++){
                    if(specificationList.get(j).get("specificationId").equals(specification.getSpecificationId())){
                        tempList = (List<GoodsSpecificationEntity>) specificationList.get(j).get("valueList");
                        break;
                    }
                }
                if(tempList == null){
                    Map<String, Object> tempMap = new HashMap<>();
                    tempMap.put("specificationId", specification.getSpecificationId());
                    tempMap.put("name", specification.getName());
                    tempList = new ArrayList<>();
                    tempList.add(specification);
                    tempMap.put("valueList", tempList);
                    specificationList.add(tempMap);
                }else{
                    for(int j = 0; j < specificationList.size(); j++){
                        if(specificationList.get(j).get("specificationId").equals(specification.getSpecificationId())){
                            tempList = (List<GoodsSpecificationEntity>) specificationList.get(j).get("valueList");
                            tempList.add(specification);
                            break;
                        }
                    }
                }
            }
        }

        FootprintEntity footprint = apiFootprintService.getOne(
                Wrappers.<FootprintEntity>lambdaQuery()
                        .eq(FootprintEntity::getGoodsId, goods.getId())
                        .last("LIMIT 1"));
        if(ToolUtil.isNotNull(footprint)){
            footprint.setAddTime(System.currentTimeMillis() / 1000);
        }else{
            footprint = new FootprintEntity()
                    .setUserId(userId)
                    .setGoodsId(goods.getId())
                    .setAddTime(System.currentTimeMillis() / 1000);
        }
        apiFootprintService.saveOrUpdate(footprint);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("goods", goods);
        resultMap.put("productList", (List<ProductEntity>) apiProductService.queryListSelf(params).get("records"));
        resultMap.put("brand", apiBrandService.get(goods.getBrandId()));
        resultMap.put("attribute", (List<AttributeEntity>) attrMap.get("records"));
        resultMap.put("specificationList", specificationList);
        return buildSuccessInfo(resultMap);
    }

}
