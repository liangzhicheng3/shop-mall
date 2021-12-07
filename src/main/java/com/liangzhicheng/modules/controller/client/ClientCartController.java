package com.liangzhicheng.modules.controller.client;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.common.bean.RedisBean;
import com.liangzhicheng.common.utils.BeansUtil;
import com.liangzhicheng.common.utils.JSONUtil;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.config.mvc.resolver.annotation.UserParam;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.*;
import com.liangzhicheng.modules.entity.vo.*;
import com.liangzhicheng.modules.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Api(tags = {"购物车"})
@RestController
@RequestMapping(value = "/client/cart")
public class ClientCartController extends BaseController {

    @Resource
    private IApiGoodsService apiGoodsService;
    @Resource
    private IApiProductService apiProductService;
    @Resource
    private IApiCartService apiCartService;
    @Resource
    private IApiGoodsSpecificationService apiGoodsSpecificationService;
    @Resource
    private IApiCouponService apiCouponService;
    @Resource
    private IApiAddressService apiAddressService;
    @Resource
    private RedisBean redisBean;
    @Resource
    private IApiCouponUserService apiCouponUserService;

    @ApiOperation(value = "添加商品到购物车")
    @PostMapping(value = "/save")
    @ApiImplicitParams({@ApiImplicitParam(name = "goodsId", value = "商品id", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "productId", value = "商品id", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "goodsNum", value = "商品数量", required = false, dataType = "Integer")})
    public ResponseResult save(@UserParam UserEntity user,
                               Integer goodsId,
                               Integer productId,
                               Integer goodsNum){
        GoodsEntity goods = apiGoodsService.get(goodsId);
        //校验商品是否可以购买
        ToolUtil.isFalse(ToolUtil.isNull(goods)
                        || goods.getIsOnSale() != 1
                        || goods.getIsDelete() == 1,
                "商品已下架");
        //获取规格信息，校验规格库存是否充足
        ProductEntity product = apiProductService.getSelf(productId);
        ToolUtil.isFalse(ToolUtil.isNull(product)
                        || product.getGoodsNumber() < goodsNum,
                "库存不足");
        //校验购物车中是否存在此规格商品
        Map<String, Object> cartParams = new HashMap<>();
        cartParams.put("userId", user.getId());
        cartParams.put("goodsId", goodsId);
        cartParams.put("productId", productId);
        LinkedHashMap<String, Object> cartMap = apiCartService.queryListSelf(cartParams);
        List<CartEntity> cartList = (List<CartEntity>) cartMap.get("records");
        CartEntity cart = cartList != null && cartList.size() > 0 ? cartList.get(0) : null;
        if(ToolUtil.isNull(cart)){
            //添加规格名称和规格值
            String[] goodsSpecificationValue = this.handleGoodsSpecificationValue(product, goodsId);
            apiCartService.save(user, goods, product, goodsNum, goodsSpecificationValue);
        }else{
            //存在购物车中，则数量增加
            ToolUtil.isFalse((cart.getNumber() + goodsNum) > product.getGoodsNumber(), "库存不足");
            cart.setNumber(cart.getNumber() + goodsNum);
            apiCartService.update(cart);
        }
        return buildSuccessInfo(this.get(user));
    }

    @ApiOperation(value = "更新指定的购物车信息")
    @PostMapping(value = "/update")
    @ApiImplicitParams({@ApiImplicitParam(name = "cartId", value = "购物车id", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "goodsId", value = "商品id", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "productId", value = "商品id", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "goodsNum", value = "商品数量", required = false, dataType = "Integer")})
    public ResponseResult update(@UserParam UserEntity user,
                                 Integer cartId,
                                 Integer goodsId,
                                 Integer productId,
                                 Integer goodsNum){
        //获取规格信息，校验规格库存是否充足
        ProductEntity product = apiProductService.getSelf(productId);
        ToolUtil.isFalse(ToolUtil.isNull(product)
                        || product.getGoodsNumber() < goodsNum,
                "库存不足");
        //校验是否已经存在productId商品在购物车
        CartEntity cart = apiCartService.get(cartId);
        if(ToolUtil.isNotNull(cart)){
            //更新商品数量
            if(cart.getProductId().equals(productId)){
                cart.setNumber(goodsNum);
                apiCartService.update(cart);
                return buildSuccessInfo(this.get(user));
            }
            boolean updateFlag = false;
            Map<String, Object> cartParams = new HashMap<>();
            cartParams.put("goodsId", goodsId);
            cartParams.put("productId", productId);
            LinkedHashMap<String, Object> cartMap = apiCartService.queryListSelf(cartParams);
            List<CartEntity> cartList = (List<CartEntity>) cartMap.get("records");
            CartEntity oldCart = cartList != null && cartList.size() > 0 ? cartList.get(0) : null;
            if(ToolUtil.isNull(oldCart)){
                //添加规格名称和规格值
                updateFlag = true;
            }else{
                //合并购物车已有的product信息，删除已有记录
                Integer newNumber = goodsNum + oldCart.getNumber();
                ToolUtil.isFalse(product.getGoodsNumber() < newNumber, "库存不足");
                apiCartService.delete(oldCart.getId() + "");
                //添加规格名称和规格值
                updateFlag = true;
            }
            if(updateFlag){
                cart.setGoodsSn(product.getGoodsSn());
                cart.setProductId(productId);
                cart.setMarketPrice(product.getRetailPrice());
                cart.setRetailPrice(product.getRetailPrice());
                cart.setNumber(goodsNum);
                cart.setGoodsSpecifitionIds(product.getGoodsSpecificationIds());
                String[] goodsSpecificationValue = this.handleGoodsSpecificationValue(product, goodsId);
                if(goodsSpecificationValue != null){
                    cart.setGoodsSpecifitionNameValue(StringUtils.join(goodsSpecificationValue, ";"));
                }
                apiCartService.update(cart);
            }
        }
        return buildSuccessInfo(this.get(user));
    }

    @ApiOperation(value = "减少商品后的购物车")
    @PostMapping(value = "/minus")
    @ApiImplicitParams({@ApiImplicitParam(name = "goodsId", value = "商品id", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "productId", value = "商品id", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "goodsNum", value = "商品数量", required = false, dataType = "Integer")})
    public ResponseResult minus(@UserParam UserEntity user,
                                Integer goodsId,
                                Integer productId,
                                Integer goodsNum){
        Map<String, Object> cartParams = new HashMap<>();
        cartParams.put("userId", user.getId());
        cartParams.put("goodsId", goodsId);
        cartParams.put("productId", productId);
        LinkedHashMap<String, Object> cartMap = apiCartService.queryListSelf(cartParams);
        List<CartEntity> cartList = (List<CartEntity>) cartMap.get("records");
        CartEntity cart = cartList != null && cartList.size() > 0 ? cartList.get(0) : null;
        int cartNum = 0;
        if(ToolUtil.isNotNull(cart)){
            if(cart.getNumber() > goodsNum){
                cart.setNumber(cart.getNumber() - goodsNum);
                apiCartService.update(cart);
                cartNum = cart.getNumber();
            }else if(cart.getNumber() == 1){
                apiCartService.delete(cart.getId() + "");
            }
        }
        return buildSuccessInfo(cartNum);
    }

    @ApiOperation(value = "是否选择购物车商品") //是否选择商品，如果已选择，则取消选择，批量操作
    @PostMapping(value = "/checked")
    @ApiImplicitParams({@ApiImplicitParam(name = "productIds", value = "商品ids", required = false, dataType = "String"),
            @ApiImplicitParam(name = "isChecked", value = "是否选择：0否，1是", required = false, dataType = "Integer")})
    public ResponseResult checked(@UserParam UserEntity user,
                                  String productIds,
                                  Integer isChecked){
        ToolUtil.isFalse(ToolUtil.isBlank(productIds, isChecked + ""), ApiConstant.PARAM_IS_NULL);
        StringTokenizer tokenizer = new StringTokenizer(productIds, ",");
        List<Integer> pIds = new ArrayList<>(tokenizer.countTokens());
        while(tokenizer.hasMoreElements()){
            String productId = tokenizer.nextToken();
            if(ToolUtil.isNull(productId)){
                continue;
            }
            pIds.add(Integer.parseInt(productId));
        }
        apiCartService.lambdaUpdate()
                .eq(CartEntity::getUserId, user.getId())
                .in(CartEntity::getProductId, pIds)
                .set(CartEntity::getChecked, isChecked)
                .update();
        return buildSuccessInfo(this.get(user));
    }

    @ApiOperation(value = "删除购物车商品")
    @PostMapping(value = "/delete")
    @ApiImplicitParams({@ApiImplicitParam(name = "productIds", value = "商品ids", required = false, dataType = "String")})
    public ResponseResult delete(@UserParam UserEntity user,
                                 String productIds){
        ToolUtil.isFalse(ToolUtil.isBlank(productIds), ApiConstant.PARAM_IS_NULL);
        StringTokenizer tokenizer = new StringTokenizer(productIds, ",");
        List<Integer> pIds = new ArrayList<>(tokenizer.countTokens());
        while(tokenizer.hasMoreElements()){
            String productId = tokenizer.nextToken();
            if(ToolUtil.isNull(productId)){
                continue;
            }
            pIds.add(Integer.parseInt(productId));
        }
        apiCartService.remove(
                Wrappers.<CartEntity>lambdaQuery()
                        .eq(CartEntity::getUserId, user.getId())
                        .in(CartEntity::getProductId, pIds));
        return buildSuccessInfo(this.get(user));
    }

    @ApiOperation(value = "获取购物车信息")
    @PostMapping(value = "/get")
    public ResponseResult get(@UserParam UserEntity user) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", user.getId());
        LinkedHashMap<String, Object> cartMap = apiCartService.queryList(params);

        List<CartEntity> cartList = (List<CartEntity>) cartMap.get("records");
        if(ToolUtil.listSizeGT(cartList)){
            //获取购物车统计信息
            Integer goodsNum = 0,
                    checkedGoodsNum = 0;
            BigDecimal goodsAmount = new BigDecimal(0.00),
                    checkedGoodsAmount = new BigDecimal(0.00);
            for(Iterator<CartEntity> it = cartList.iterator(); it.hasNext();){
                CartEntity cart = it.next();
                goodsNum += cart.getNumber();
                goodsAmount = goodsAmount.add(cart.getRetailPrice().multiply(new BigDecimal(cart.getNumber())));
                if(cart.getChecked() != null && cart.getChecked() == 1){
                    checkedGoodsNum += cart.getNumber();
                    checkedGoodsAmount = checkedGoodsAmount.add(cart.getRetailPrice().multiply(new BigDecimal(cart.getNumber())));
                }
            }
            //获取优惠信息显示
            Map<String, Object> couponParams = new HashMap<>();
            couponParams.put("enabled", true);
            Integer[] sendType = new Integer[]{0, 7};
            couponParams.put("sendType", sendType);
            List<CouponInfoVO> couponInfoList = new ArrayList<>();
            LinkedHashMap<String, Object> couponMap = apiCouponService.queryList(couponParams);
            List<CouponEntity> couponList = (List<CouponEntity>) couponMap.get("records");
            if(ToolUtil.listSizeGT(couponList)){
                CouponInfoVO couponInfo = new CouponInfoVO();
                BigDecimal fullSub = new BigDecimal(0);
                BigDecimal minAmount = new BigDecimal(100000);
                for(Iterator<CouponEntity> it = couponList.iterator(); it.hasNext();){
                    CouponEntity coupon = it.next();
                    BigDecimal subAfterAmount = coupon.getMinGoodsAmount().subtract(checkedGoodsAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
                    if(coupon.getSendType() == 0
                            && subAfterAmount.doubleValue() > 0.0
                            && minAmount.compareTo(coupon.getMinGoodsAmount()) > 0){
                        fullSub = coupon.getTypeMoney();
                        minAmount = coupon.getMinGoodsAmount();
                        couponInfo.setType(1);
                        couponInfo.setMessage(coupon.getName() + "，还差" + subAfterAmount + "元");
                    }else if(coupon.getSendType() == 0
                            && subAfterAmount.doubleValue() < 0.0
                            && fullSub.compareTo(coupon.getTypeMoney()) < 0){
                        fullSub = coupon.getTypeMoney();
                        couponInfo.setMessage("可使用满减券" + coupon.getName());
                    }
                    if(coupon.getSendType() == 7
                            && subAfterAmount.doubleValue() > 0.0){
                        couponInfo = new CouponInfoVO();
                        couponInfo.setType(1);
                        couponInfo.setMessage("满￥" + coupon.getMinAmount() + "元免配送费，还差" + subAfterAmount + "元");
                        couponInfoList.add(couponInfo);
                    }else if(coupon.getSendType() == 7){
                        couponInfo = new CouponInfoVO();
                        couponInfo.setMessage("满￥" + coupon.getMinAmount() + "元免配送费");
                        couponInfoList.add(couponInfo);
                    }
                }
                if(ToolUtil.isNotBlank(couponInfo.getMessage())){
                    couponInfoList.add(couponInfo);
                }
            }
            Map<String, Object> cartTotalMap = new HashMap<>();
            cartTotalMap.put("goodsCount", goodsNum);
            cartTotalMap.put("goodsAmount", goodsAmount);
            cartTotalMap.put("checkedGoodsCount", checkedGoodsNum);
            cartTotalMap.put("checkedGoodsAmount", checkedGoodsAmount);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("cartTotal", cartTotalMap);
            resultMap.put("cartList", BeansUtil.copyList(cartList, CartVO.class));
            resultMap.put("couponInfoList", couponInfoList);
            return buildSuccessInfo(resultMap);
        }
        return buildSuccessInfo();
    }

    @ApiOperation(value = "订单提交前校验和填写相关订单信息")
    @PostMapping(value = "/validate")
    @ApiImplicitParams({@ApiImplicitParam(name = "couponUserId", value = "优惠券用户id", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "type", value = "类型", defaultValue = "cart", required = false, dataType = "String")})
    public ResponseResult validate(@UserParam UserEntity user,
                                   Integer couponUserId,
                                   String type){
        Map<String, Object> resultMap = new HashMap<>();
        //根据收货地址计算运费
        BigDecimal freightPrice = new BigDecimal(0.00);
        Map<String, Object> addressParams = new HashMap<>();
        addressParams.put("userId", user.getId());
        addressParams.put("isDefault", 1);
        LinkedHashMap<String, Object> addressMap = apiAddressService.queryList(addressParams);
        List<AddressEntity> addressList = (List<AddressEntity>) addressMap.get("records");
        if(ToolUtil.listSizeGT(addressList)){
            resultMap.put("checkedAddress", addressList.get(0));
        }else{
            resultMap.put("checkedAddress", "");
        }
        //获取需要购买的商品和总价
        List<CartVO> checkedGoodsList = new ArrayList<>();
        BigDecimal goodsTotalPrice = new BigDecimal(0.00);
        if("cart".equals(type)){
            Map<String, Object> resultData = (Map<String, Object>) this.get(user).getData();
            List<CartVO> cartList = (List<CartVO>) resultData.get("cartList");
            if(ToolUtil.listSizeGT(cartList)){
                for(Iterator<CartVO> it = cartList.iterator(); it.hasNext();){
                    CartVO cart = it.next();
                    if(cart.getChecked() == 1){
                        checkedGoodsList.add(cart);
                    }
                }
                goodsTotalPrice = (BigDecimal) ((Map<String, Object>) resultData.get("cartTotal")).get("checkedGoodsAmount");
            }
        }else{ //直接购买
            String value = redisBean.hGet(Constants.MALL_GOODS_PURCHASE, user.getId().toString());
            log.info("[订单提交前校验] redis hash get：{}", value);
            GoodsPurchaseVO goodsPurchase = JSONUtil.parseObject(value, GoodsPurchaseVO.class);
            ProductEntity product = apiProductService.getSelf(goodsPurchase.getProductId());
            //计算订单的费用
            //商品总价
            goodsTotalPrice = product.getRetailPrice().multiply(new BigDecimal(goodsPurchase.getNumber()));
            CartVO cart = new CartVO();
            cart.setGoodsName(product.getGoodsName());
            cart.setNumber(goodsPurchase.getNumber());
            cart.setRetailPrice(product.getRetailPrice());
            cart.setListPicUrl(product.getListPicUrl());
            checkedGoodsList.add(cart);
        }
        //获取可用优惠券信息
        BigDecimal couponPrice = new BigDecimal(0.00);
        if(couponUserId != null && couponUserId > 0){
            CouponUserEntity couponUser = apiCouponUserService.getSelf(couponUserId);
            if(ToolUtil.isNotNull(couponUser)){
                couponPrice = couponUser.getTypeMoney();
            }
        }
        //订单总价
        BigDecimal orderTotalPrice = goodsTotalPrice.add(freightPrice);
        //减去其它支付金额后，要实际支付的金额
        BigDecimal actualPrice = orderTotalPrice.subtract(couponPrice);
        resultMap.put("checkedGoodsList", checkedGoodsList);
        resultMap.put("freightPrice", freightPrice);
        resultMap.put("couponPrice", couponPrice);
        resultMap.put("goodsTotalPrice", goodsTotalPrice);
        resultMap.put("orderTotalPrice", orderTotalPrice);
        resultMap.put("actualPrice", actualPrice);
        return buildSuccessInfo(resultMap);
    }

    private String[] handleGoodsSpecificationValue(ProductEntity product, Integer goodsId){
        String[] goodsSpecificationValue = null;
        if (product.getGoodsSpecificationIds() != null && product.getGoodsSpecificationIds().length() > 0) {
            String[] ids = this.handleSpecificationIdsArray(product.getGoodsSpecificationIds());
            Map<String, Object> specParams = new HashMap<>();
            specParams.put("goodsId", goodsId);
            specParams.put("ids", ids);
            LinkedHashMap<String, Object> specMap = apiGoodsSpecificationService.queryListSelf(specParams);
            List<GoodsSpecificationEntity> specList = (List<GoodsSpecificationEntity>) specMap.get("records");
            if(ToolUtil.listSizeGT(specList)){
                goodsSpecificationValue = new String[specList.size()];
                for(int i = 0; i < specList.size(); i++) {
                    goodsSpecificationValue[i] = specList.get(i).getValue();
                }
            }
        }
        return goodsSpecificationValue;
    }

    private String[] handleSpecificationIdsArray(String ids) {
        String[] idsArray = null;
        if(ToolUtil.isNotBlank(ids)){
            String[] tempArray = ids.split("_");
            if(tempArray != null && tempArray.length > 0){
                idsArray = tempArray;
            }
        }
        return idsArray;
    }

}
