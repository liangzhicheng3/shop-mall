package com.liangzhicheng.modules.controller.server;

import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.common.utils.TreeUtil;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.CategoryEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.service.ICategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping(value = "/server/category")
public class ServerCategoryController extends BaseController {

    @Resource
    private ICategoryService categoryService;

    @PostMapping(value = "/save")
    public ResponseResult save(@RequestBody CategoryEntity category){
        int rows = categoryService.insert(category);
        if(rows > 0){
            return buildSuccessInfo();
        }
        return buildFailedInfo(ApiConstant.BASE_FAIL_CODE);
    }

    @PostMapping(value = "/delete")
    public ResponseResult delete(@RequestParam String ids){
        categoryService.delete(ids);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/update")
    public ResponseResult update(@RequestBody CategoryEntity category){
        categoryService.update(category);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/list")
    public ResponseResult list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        return buildSuccessInfo(categoryService.queryList(query));
    }

    @GetMapping(value = "/get/{id}")
    public ResponseResult get(@PathVariable("id") Integer id){
        return buildSuccessInfo(categoryService.get(id));
    }

    /**
     * 将结果集包装树形，提高性能
     * @return ResponseResult
     */
    @PostMapping(value = "/list/tree")
    public ResponseResult listTree(){
        LinkedHashMap<String, Object> resultMap = categoryService.queryList(new HashMap<>());
        List<CategoryEntity> categoryList = (List<CategoryEntity>) resultMap.get("records");
        if(ToolUtil.listSizeGT(categoryList)){
            for(Iterator<CategoryEntity> it = categoryList.iterator(); it.hasNext();){
                CategoryEntity category = it.next();
                category.setValue(category.getId() + "");
            }
            return buildSuccessInfo(TreeUtil.toTree(categoryList));
        }
        return buildFailedInfo(ApiConstant.BASE_FAIL_CODE);
    }

}
