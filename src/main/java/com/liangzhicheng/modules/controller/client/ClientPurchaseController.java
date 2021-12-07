package com.liangzhicheng.modules.controller.client;

import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.common.bean.RedisBean;
import com.liangzhicheng.common.utils.JSONUtil;
import com.liangzhicheng.config.mvc.resolver.annotation.UserParam;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.UserEntity;
import com.liangzhicheng.modules.entity.vo.GoodsPurchaseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = {"商品购买"})
@RestController
@RequestMapping(value = "/client/purchase")
public class ClientPurchaseController extends BaseController {

    @Resource
    private RedisBean redisBean;

    @ApiOperation(value = "商品添加")
    @PostMapping(value = "/add")
    @ApiImplicitParams({@ApiImplicitParam(name = "goodsId", value = "商品id", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "productId", value = "商品id", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "goodsNum", value = "商品数量", required = false, dataType = "Integer")})
    public ResponseResult add(@UserParam UserEntity user,
                              Integer goodsId,
                              Integer productId,
                              Integer goodsNum){
        GoodsPurchaseVO goodsPurchase =
                new GoodsPurchaseVO(goodsId, productId, goodsNum);
        redisBean.hSet(Constants.MALL_GOODS_PURCHASE, user.getId().toString(), JSONUtil.toJSONString(goodsPurchase));
        return buildSuccessInfo();
    }

}
