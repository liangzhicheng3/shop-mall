package com.liangzhicheng.modules.controller.client;

import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.common.utils.BeansUtil;
import com.liangzhicheng.config.aop.annotation.Cache;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.entity.vo.BrandVO;
import com.liangzhicheng.modules.service.IApiBrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Api(tags = {"品牌"})
@RestController
@RequestMapping(value = "/client/brand")
public class ClientBrandController extends BaseController {

    @Resource
    private IApiBrandService apiBrandService;

    @Cache
    @ApiOperation(value = "获取品牌列表")
    @PostMapping(value = "/list")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNo", value = "当前页码", defaultValue = "1", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", defaultValue = "10", required = false, dataType = "Integer")})
    public ResponseResult list(Integer pageNo,
                               Integer pageSize){
        Map<String, Object> params = new HashMap<>();
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("sortField", "id");
        params.put("sortOrder", "ASC");
        Query query = new Query(params);
        return buildSuccessInfo(apiBrandService.queryList(query));
    }

    @ApiOperation(value = "获取品牌详情")
    @GetMapping(value = "/get/{id}")
    public ResponseResult get(@PathVariable("id") Integer id){
        return buildSuccessInfo(BeansUtil.copyEntity(apiBrandService.get(id), BrandVO.class));
    }

}
