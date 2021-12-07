package com.liangzhicheng.modules.controller.server;

import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.AttributeEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.service.IAttributeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping(value = "/server/attr")
public class ServerAttributeController extends BaseController {

    @Resource
    private IAttributeService attributeService;

    @PostMapping(value = "/save")
    public ResponseResult save(@RequestBody AttributeEntity attribute){
        int rows = attributeService.insert(attribute);
        if(rows > 0){
            return buildSuccessInfo();
        }
        return buildFailedInfo(ApiConstant.BASE_FAIL_CODE);
    }

    @PostMapping(value = "/delete")
    public ResponseResult delete(@RequestParam String ids){
        attributeService.delete(ids);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/update")
    public ResponseResult update(@RequestBody AttributeEntity attribute){
        attributeService.update(attribute);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/list")
    public ResponseResult list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        return buildSuccessInfo(attributeService.queryListSelf(query));
    }

    @GetMapping(value = "/get/{id}")
    public ResponseResult get(@PathVariable("id") Integer id){
        return buildSuccessInfo(attributeService.get(id));
    }

}
