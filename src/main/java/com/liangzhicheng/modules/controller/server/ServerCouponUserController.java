package com.liangzhicheng.modules.controller.server;

import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.CouponUserEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.service.ICouponUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping(value = "/server/coupon/user")
public class ServerCouponUserController extends BaseController {

    @Resource
    private ICouponUserService couponUserService;

    @PostMapping(value = "/save")
    public ResponseResult save(@RequestBody CouponUserEntity couponUser){
        int rows = couponUserService.insert(couponUser);
        if(rows > 0){
            buildSuccessInfo();
        }
        return buildFailedInfo(ApiConstant.BASE_FAIL_CODE);
    }

    @PostMapping(value = "/delete")
    public ResponseResult delete(@RequestParam String ids){
        couponUserService.delete(ids);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/update")
    public ResponseResult update(@RequestBody CouponUserEntity couponUser){
        couponUserService.update(couponUser);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/list")
    public ResponseResult list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        return buildSuccessInfo(couponUserService.queryListSelf(query));
    }

    @GetMapping(value = "/get/{id}")
    public ResponseResult get(@PathVariable("id") Integer id){
        return buildSuccessInfo(couponUserService.get(id));
    }

}
