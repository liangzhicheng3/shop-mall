package com.liangzhicheng.modules.controller.server;

import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.CouponEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.service.ICouponService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping(value = "/server/coupon")
public class ServerCouponController extends BaseController {

    @Resource
    private ICouponService couponService;

    @PostMapping(value = "/save")
    public ResponseResult save(@RequestBody CouponEntity coupon){
        int rows = couponService.insert(coupon);
        if(rows > 0){
            buildSuccessInfo();
        }
        return buildFailedInfo(ApiConstant.BASE_FAIL_CODE);
    }

    @PostMapping(value = "/delete")
    public ResponseResult delete(@RequestParam String ids){
        couponService.delete(ids);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/update")
    public ResponseResult update(@RequestBody CouponEntity coupon){
        couponService.update(coupon);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/list")
    public ResponseResult list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        return buildSuccessInfo(couponService.queryList(query));
    }

    @GetMapping(value = "/get/{id}")
    public ResponseResult get(@PathVariable("id") Integer id){
        return buildSuccessInfo(couponService.get(id));
    }

    @PostMapping(value = "/publish")
    public ResponseResult publish(@RequestParam Map<String, Object> params){
        return buildSuccessInfo(couponService.publish(params));
    }

}
