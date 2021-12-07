package com.liangzhicheng.modules.controller.client;

import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.common.utils.BeansUtil;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.config.mvc.resolver.annotation.UserParam;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.AddressEntity;
import com.liangzhicheng.modules.entity.UserEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.entity.vo.AddressVO;
import com.liangzhicheng.modules.service.IApiAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Api(tags = {"收货地址"})
@RestController
@RequestMapping(value = "/client/address")
public class ClientAddressController extends BaseController {

    @Resource
    private IApiAddressService apiAddressService;

    @ApiOperation(value = "保存收货地址")
    @PostMapping(value = "/save")
    public ResponseResult save(@UserParam UserEntity user,
                               @RequestBody AddressEntity address){
        ToolUtil.isFalse(ToolUtil.isNull(address), ApiConstant.PARAM_ERROR);
        if(address.getId() == null || address.getId() == 0){
            address.setUserId(user.getId());
            apiAddressService.insert(address);
        }else{
            apiAddressService.update(address);
        }
        return buildSuccessInfo(BeansUtil.copyEntity(address, AddressVO.class));
    }

    @ApiOperation(value = "删除收货地址")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseResult delete(@UserParam UserEntity user,
                                 @PathVariable("id") Integer id){
        AddressEntity address = apiAddressService.get(id);
        ToolUtil.isFalse(!address.getUserId().equals(user.getId()), "无法查看，请联系管理员");
        apiAddressService.removeById(id);
        return buildSuccessInfo();
    }

    @ApiOperation(value = "获取收货地址列表")
    @PostMapping(value = "/list")
    public ResponseResult list(@UserParam UserEntity user){
        Map<String, Object> params = new HashMap<>();
        params.put("userId", user.getId());
        Query query = new Query(params);
        return buildSuccessInfo(apiAddressService.queryList(query));
    }

    @ApiOperation(value = "获取收货地址详情", response = AddressVO.class)
    @GetMapping(value = "/get/{id}")
    public ResponseResult get(@UserParam UserEntity user,
                              @PathVariable("id") Integer id){
        AddressEntity address = apiAddressService.get(id);
        ToolUtil.isFalse(!address.getUserId().equals(user.getId()), "无法查看，请联系管理员");
        return buildSuccessInfo(BeansUtil.copyEntity(address, AddressVO.class));
    }

}
