package com.liangzhicheng.modules.controller.server;

import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.SpecificationEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.service.ISpecificationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping(value = "/server/spec")
public class ServerSpecificationController extends BaseController {

    @Resource
    private ISpecificationService specificationService;

    @PostMapping(value = "/save")
    public ResponseResult save(@RequestBody SpecificationEntity specification){
        int rows = specificationService.insert(specification);
        if(rows > 0){
            return buildSuccessInfo();
        }
        return buildFailedInfo(ApiConstant.BASE_FAIL_CODE);
    }

    @PostMapping(value = "/delete")
    public ResponseResult delete(@RequestParam String ids){
        specificationService.delete(ids);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/update")
    public ResponseResult update(@RequestBody SpecificationEntity specification){
        specificationService.update(specification);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/list")
    public ResponseResult list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        return buildSuccessInfo(specificationService.queryList(query));
    }

    @GetMapping(value = "/get/{id}")
    public ResponseResult get(@PathVariable("id") Integer id){
        return buildSuccessInfo(specificationService.get(id));
    }

}
