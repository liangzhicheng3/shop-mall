package com.liangzhicheng.modules.controller.server;

import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.config.aop.annotation.LogRecord;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.OrderEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.service.IOrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/server/order")
public class ServerOrderController extends BaseController {

    @Resource
    private IOrderService orderService;

    @PostMapping(value = "/save")
    public ResponseResult save(@RequestBody OrderEntity order){
        int rows = orderService.insert(order);
        if(rows > 0){
            return buildSuccessInfo();
        }
        return buildFailedInfo(ApiConstant.BASE_FAIL_CODE);
    }

    @PostMapping(value = "/delete")
    public ResponseResult delete(@RequestParam String ids){
        orderService.delete(ids);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/update")
    public ResponseResult update(@RequestBody OrderEntity order){
        orderService.update(order);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/list")
    public ResponseResult list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        return buildSuccessInfo(orderService.queryListSelf(query));
    }

    @GetMapping(value = "/get/{id}")
    public ResponseResult get(@PathVariable("id") Integer id){
        return buildSuccessInfo(orderService.get(id));
    }

    @LogRecord(operate = "快递发货")
    @PostMapping(value = "/deliver")
    public ResponseResult deliver(@RequestBody OrderEntity order){
        orderService.deliver(order);
        return buildSuccessInfo();
    }

    @LogRecord(operate = "确认收货")
    @PostMapping(value = "/confirm")
    public ResponseResult confirm(@RequestParam Integer id){
        orderService.confirm(id);
        return buildSuccessInfo();
    }

}
