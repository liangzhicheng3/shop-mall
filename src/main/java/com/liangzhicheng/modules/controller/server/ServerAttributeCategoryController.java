package com.liangzhicheng.modules.controller.server;

import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.AttributeCategoryEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.service.IAttributeCategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping(value = "/server/attr/category")
public class ServerAttributeCategoryController extends BaseController {

    @Resource
    private IAttributeCategoryService attributeCategoryService;

    @PostMapping(value = "/save")
    public ResponseResult save(@RequestBody AttributeCategoryEntity attributeCategory){
        int rows = attributeCategoryService.insert(attributeCategory);
        if(rows > 0){
            return buildSuccessInfo();
        }
        return buildFailedInfo(ApiConstant.BASE_FAIL_CODE);
    }

    @PostMapping(value = "/delete")
    public ResponseResult delete(@RequestParam String ids){
        attributeCategoryService.delete(ids);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/update")
    public ResponseResult update(@RequestBody AttributeCategoryEntity attributeCategory){
        attributeCategoryService.update(attributeCategory);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/list")
    public ResponseResult list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        return buildSuccessInfo(attributeCategoryService.queryList(query));
    }

    @GetMapping(value = "/get/{id}")
    public ResponseResult update(@PathVariable("id") Integer id){
        return buildSuccessInfo(attributeCategoryService.get(id));
    }

}
