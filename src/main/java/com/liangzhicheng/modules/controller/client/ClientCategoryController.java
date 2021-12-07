package com.liangzhicheng.modules.controller.client;

import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.common.utils.BeansUtil;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.CategoryEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.entity.vo.CategoryVO;
import com.liangzhicheng.modules.service.IApiCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Api(tags = {"分类"})
@RestController
@RequestMapping(value = "/client/category")
public class ClientCategoryController extends BaseController {

    @Resource
    private IApiCategoryService apiCategoryService;

    @ApiOperation(value = "获取分类目录")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "分类id", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "pageNo", value = "当前页码", defaultValue = "1", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", defaultValue = "10", required = false, dataType = "Integer")})
    @PostMapping(value = "/list")
    public ResponseResult list(Integer id,
                               Integer pageNo,
                               Integer pageSize){
        Map<String, Object> params = new HashMap<>();
        params.put("parentId", 0);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("sortField", "sort_order");
        params.put("sortOrder", "ASC");
        Query query = new Query(params);
        LinkedHashMap<String, Object> categoryMap = apiCategoryService.queryList(query);
        List<CategoryEntity> categoryList = (List<CategoryEntity>) categoryMap.get("records");

        CategoryVO currentCategory = null;
        if(id != null){
            currentCategory = BeansUtil.copyEntity(apiCategoryService.get(id), CategoryVO.class);
        }
        if(currentCategory == null && ToolUtil.listSizeGT(categoryList)){
            currentCategory = BeansUtil.copyEntity(categoryList.get(0), CategoryVO.class);
        }else{
            currentCategory = new CategoryVO();
        }
        //获取子分类
        if (currentCategory != null && currentCategory.getId() != null) {
            params.put("parentId", currentCategory.getId());
            LinkedHashMap<String, Object> subMap = apiCategoryService.queryList(params);
            currentCategory.setSubCategoryList(BeansUtil.copyList((List<CategoryEntity>) subMap.get("records"), CategoryVO.class));
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("categoryList", categoryList);
        resultMap.put("currentCategory", currentCategory);
        return buildSuccessInfo(resultMap);
    }

    @ApiOperation(value = "获取分类目录当前分类")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "id", required = false, dataType = "Integer")})
    @PostMapping(value = "/get")
    public ResponseResult get(Integer id) {
        Map<String, Object> params = new HashMap<>();
        params.put("parentId", 0);
        CategoryVO currentCategory = null;
        if (id != null) {
            currentCategory = BeansUtil.copyEntity(apiCategoryService.get(id), CategoryVO.class);
        }
        //获取子分类
        if (currentCategory != null && currentCategory.getId() != null) {
            params.put("parentId", currentCategory.getId());
            LinkedHashMap<String, Object> subMap = apiCategoryService.queryList(params);
            currentCategory.setSubCategoryList(BeansUtil.copyList((List<CategoryEntity>) subMap.get("records"), CategoryVO.class));
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("currentCategory", currentCategory);
        return buildSuccessInfo(resultMap);
    }

}
