package com.liangzhicheng.modules.controller.client;

import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.common.bean.RedisBean;
import com.liangzhicheng.common.utils.JSONUtil;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.config.mvc.resolver.annotation.UserParam;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.CartEntity;
import com.liangzhicheng.modules.entity.ProductEntity;
import com.liangzhicheng.modules.entity.UserEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.entity.vo.CouponUserVO;
import com.liangzhicheng.modules.entity.vo.GoodsPurchaseVO;
import com.liangzhicheng.modules.service.IApiCartService;
import com.liangzhicheng.modules.service.IApiCouponUserService;
import com.liangzhicheng.modules.service.IApiProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Api(tags = {"优惠券"})
@RestController
@RequestMapping(value = "/client/coupon")
public class ClientCouponController extends BaseController {

    @Resource
    private IApiCouponUserService apiCouponUserService;
    @Resource
    private IApiCartService apiCartService;
    @Resource
    private RedisBean redisBean;
    @Resource
    private IApiProductService apiProductService;

    @ApiOperation(value = "获取优惠券列表")
    @PostMapping("/list")
    public ResponseResult list(@UserParam UserEntity user) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", user.getId());
        Query query = new Query(params);
        return buildSuccessInfo(apiCouponUserService.getCouponUser(query));
    }

    @ApiOperation(value = "根据商品获取优惠券列表")
    @PostMapping("/list/by/goods")
    @ApiImplicitParams({@ApiImplicitParam(name = "type", value = "类型", defaultValue = "cart", required = false, dataType = "String")})
    public ResponseResult listByGoods(@UserParam UserEntity user,
                                      String type) {
        BigDecimal goodsTotalPrice = new BigDecimal(0.00);
        if("cart".equals(type)){
            Map<String, Object> cartParams = new HashMap<>();
            cartParams.put("userId", user.getId());
            LinkedHashMap<String, Object> cartMap = apiCartService.queryListSelf(cartParams);
            List<CartEntity> cartList = (List<CartEntity>) cartMap.get("records");
            if(ToolUtil.listSizeGT(cartList)){
                for(Iterator<CartEntity> it = cartList.iterator(); it.hasNext();){
                    CartEntity cart = it.next();
                    if(cart.getChecked() != null && cart.getChecked() == 1){
                        goodsTotalPrice = goodsTotalPrice.add(cart.getRetailPrice().multiply(new BigDecimal(cart.getNumber())));
                    }
                }
            }
        }else{
            String value = redisBean.hGet(Constants.MALL_GOODS_PURCHASE, user.getId().toString());
            GoodsPurchaseVO goodsPurchase = JSONUtil.parseObject(value, GoodsPurchaseVO.class);
            ProductEntity product = apiProductService.getSelf(goodsPurchase.getProductId());
            goodsTotalPrice = product.getRetailPrice().multiply(new BigDecimal(goodsPurchase.getNumber()));
        }
        //获取可用优惠券
        Map<String, Object> couponParams = new HashMap<>();
        couponParams.put("userId", user.getId());
        couponParams.put("couponStatus", 1);
        List<CouponUserVO> couponUserList = apiCouponUserService.getCouponUser(couponParams);
        List<CouponUserVO> useList = null;
        List<CouponUserVO> notUseList = null;
        if(ToolUtil.listSizeGT(couponUserList)){
            useList = new ArrayList<>(couponUserList.size());
            notUseList = new ArrayList<>(couponUserList.size());
            for(Iterator<CouponUserVO> it = couponUserList.iterator(); it.hasNext();){
                CouponUserVO couponUser = it.next();
                if(goodsTotalPrice.compareTo(couponUser.getMinGoodsAmount()) >= 0){
                    couponUser.setEnabled(1);
                    useList.add(couponUser);
                }else{
                    couponUser.setEnabled(0);
                    notUseList.add(couponUser);
                }
            }
            useList.addAll(notUseList);
        }
        return buildSuccessInfo(useList);
    }

}
