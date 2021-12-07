package com.liangzhicheng.modules.controller.client;

import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.config.context.SpringContextHolder;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.strategy.Area;
import com.liangzhicheng.modules.strategy.service.IArea;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"地区"})
@RestController
@RequestMapping(value = "/client/area")
public class ClientAreaController extends BaseController {

    //策略模式
    @ApiOperation(value = "获取地区列表")
    @GetMapping(value = "/list/{type}/{id}")
    public ResponseResult list(@PathVariable("type") String type,
                               @PathVariable("id") String id){
        Area area = new Area(type, id);
        IArea areaObject = SpringContextHolder.getBean(area.getType(), IArea.class);
        return buildSuccessInfo(areaObject.list(area));
    }

//    @ApiOperation(value = "获取国家列表")
//    @GetMapping(value = "/country")
//    public ResponseResult listCountry(){
//        Area area = new Area("country", "");
//        IArea areaObject = SpringContextHolder.getBean(area.getType(), IArea.class);
//        return buildSuccessInfo(areaObject.list(area));
//    }
//
//    @ApiOperation(value = "获取省列表")
//    @GetMapping(value = "/province/{id}")
//    public ResponseResult listProvince(@PathVariable("id") String id){
//        Area area = new Area("province", id);
//        IArea areaObject = SpringContextHolder.getBean(area.getType(), IArea.class);
//        return buildSuccessInfo(areaObject.list(area));
//    }
//
//    @ApiOperation(value = "获取市列表")
//    @PostMapping(value = "/city/{id}")
//    public ResponseResult listCity(@PathVariable("id") String id){
//        Area area = new Area("city", id);
//        IArea areaObject = SpringContextHolder.getBean(area.getType(), IArea.class);
//        return buildSuccessInfo(areaObject.list(area));
//    }
//
//    @ApiOperation(value = "获取区列表")
//    @PostMapping(value = "/district/{id}")
//    public ResponseResult listDistrict(@PathVariable("id") String id){
//        Area area = new Area("district", id);
//        IArea areaObject = SpringContextHolder.getBean(area.getType(), IArea.class);
//        return buildSuccessInfo(areaObject.list(area));
//    }

}
