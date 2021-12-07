package com.liangzhicheng.modules.controller.server;

import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.GoodsSpecificationEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.service.IGoodsSpecificationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping(value = "/server/goods/spec")
public class ServerGoodsSpecificationController extends BaseController {

    @Resource
    private IGoodsSpecificationService goodsSpecificationService;

    @PostMapping(value = "/save")
    public ResponseResult save(@RequestBody GoodsSpecificationEntity goodsSpecification){
        int rows = goodsSpecificationService.insert(goodsSpecification);
        if(rows > 0){
            return buildSuccessInfo();
        }
        return buildFailedInfo(ApiConstant.BASE_FAIL_CODE);
    }

    @PostMapping(value = "/delete")
    public ResponseResult delete(@RequestParam String ids){
        goodsSpecificationService.delete(ids);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/update")
    public ResponseResult update(@RequestBody GoodsSpecificationEntity goodsSpecification){
        goodsSpecificationService.update(goodsSpecification);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/list")
    public ResponseResult list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        return buildSuccessInfo(goodsSpecificationService.queryListSelf(query));
    }

    @GetMapping(value = "/get/{id}")
    public ResponseResult get(@PathVariable("id") Integer id){
        return buildSuccessInfo(goodsSpecificationService.get(id));
    }

}
