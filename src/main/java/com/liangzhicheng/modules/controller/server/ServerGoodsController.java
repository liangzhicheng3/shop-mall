package com.liangzhicheng.modules.controller.server;

import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.page.PageResult;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.GoodsEntity;
import com.liangzhicheng.modules.service.IGoodsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/server/goods")
public class ServerGoodsController extends BaseController {

    @Resource
    private IGoodsService goodsService;

    @PostMapping(value = "/save")
    public ResponseResult save(@RequestBody GoodsEntity goods){
        int rows = goodsService.insert(goods);
        if(rows > 0){
            return buildSuccessInfo();
        }
        return buildFailedInfo(ApiConstant.BASE_FAIL_CODE);
    }

    @PostMapping(value = "/delete")
    public ResponseResult delete(@RequestParam String ids){
        goodsService.delete(ids);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/update")
    public ResponseResult update(@RequestBody GoodsEntity goods){
        goodsService.update(goods);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/list")
    public ResponseResult list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        query.put("isDelete", 0);
        Integer total = goodsService.getTotal(query);
        List<GoodsEntity> goodsList = new ArrayList<>(total);
        if(total > 0){
            goodsList = goodsService.queryList(query);
        }
        return buildSuccessInfo(new PageResult(query.getPageNo(), query.getPageSize(), goodsList, total));
    }

    @GetMapping(value = "/get/{id}")
    public ResponseResult get(@PathVariable("id") Integer id){
        return buildSuccessInfo(goodsService.get(id));
    }

}
