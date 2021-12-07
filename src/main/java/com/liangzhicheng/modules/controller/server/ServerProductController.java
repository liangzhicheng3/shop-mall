package com.liangzhicheng.modules.controller.server;

import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.ProductEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.service.IProductService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping(value = "/server/product")
public class ServerProductController extends BaseController {

    @Resource
    private IProductService productService;

    @PostMapping(value = "/save")
    public ResponseResult save(@RequestBody ProductEntity product){
        int rows = productService.insert(product);
        if(rows > 0){
            return buildSuccessInfo();
        }
        return buildFailedInfo(ApiConstant.BASE_FAIL_CODE);
    }

    @PostMapping(value = "/delete")
    public ResponseResult delete(@RequestParam String ids){
        productService.delete(ids);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/update")
    public ResponseResult update(@RequestBody ProductEntity product){
        productService.update(product);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/list")
    public ResponseResult list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        LinkedHashMap<String, Object> resultMap = productService.queryListSelf(query);
        return buildSuccessInfo(resultMap);
    }

    @GetMapping(value = "/get/{id}")
    public ResponseResult get(@PathVariable("id") Integer id){
        return buildSuccessInfo(productService.get(id));
    }

    @GetMapping(value = "/get/by/goods/id/{goodsId}")
    public ResponseResult getByGoodsId(@PathVariable("goodsId") Integer goodsId){
        Map<String, Object> params = new HashMap<>();
        params.put("goodsId", goodsId);
        LinkedHashMap<String, Object> resultMap = productService.queryList(params);
        return buildSuccessInfo((List<ProductEntity>) resultMap.get("list"));
    }

}
