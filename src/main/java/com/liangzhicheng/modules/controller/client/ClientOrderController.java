package com.liangzhicheng.modules.controller.client;

import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.common.utils.BeansUtil;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.config.mvc.resolver.annotation.UserParam;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.OrderEntity;
import com.liangzhicheng.modules.entity.OrderGoodsEntity;
import com.liangzhicheng.modules.entity.UserEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.entity.vo.OrderVO;
import com.liangzhicheng.modules.service.IApiKdniaoService;
import com.liangzhicheng.modules.service.IApiOrderGoodsService;
import com.liangzhicheng.modules.service.IApiOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@Api(tags = {"订单"})
@RestController
@RequestMapping(value = "/client/order")
public class ClientOrderController extends BaseController {

    @Resource
    private IApiOrderService apiOrderService;
    @Resource
    private IApiOrderGoodsService apiOrderGoodsService;
    @Resource
    private IApiKdniaoService apiKdniaoService;

    @ApiOperation(value = "订单提交")
    @PostMapping(value = "/commit")
    @ApiImplicitParams({@ApiImplicitParam(name = "addressId", value = "收货地址id", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "couponUserId", value = "优惠券用户id", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "type", value = "类型", defaultValue = "cart", required = false, dataType = "String"),
            @ApiImplicitParam(name = "postscript", value = "补充说明", required = false, dataType = "String")})
    public ResponseResult commit(@UserParam UserEntity user,
                                 Integer addressId,
                                 Integer couponUserId,
                                 String type,
                                 String postscript){
        Map<String, Object> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("addressId", addressId);
        params.put("couponUserId", couponUserId);
        params.put("type", type);
        params.put("postscript", postscript);
        return buildSuccessInfo(apiOrderService.commit(params));
    }

    @ApiOperation(value = "订单取消")
    @PostMapping(value = "/cancel")
    @ApiImplicitParams({@ApiImplicitParam(name = "orderId", value = "订单id", required = false, dataType = "Integer")})
    public ResponseResult cancel(@UserParam UserEntity user,
                                 Integer orderId){
        Map<String, Object> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("orderId", orderId);
        return buildSuccessInfo(apiOrderService.cancel(params));
    }

    @ApiOperation(value = "确认收货")
    @PostMapping(value = "/confirm")
    @ApiImplicitParams({@ApiImplicitParam(name = "orderId", value = "订单id", required = false, dataType = "Integer")})
    public ResponseResult confirm(@UserParam UserEntity user,
                                  Integer orderId){
        OrderEntity order = apiOrderService.getSelf(orderId);
        if(ToolUtil.isNotNull(order)) {
            ToolUtil.isFalse(!order.getUserId().equals(user.getId()), ApiConstant.PARAM_ERROR);
            order.setOrderStatus(301);
            order.setShippingStatus(2);
            order.setConfirmTime(new Date());
            int rows = apiOrderService.update(order);
            if(rows > 0){
                return buildSuccessInfo("确认成功！");
            }
        }
        return buildFailedInfo("确认失败！");
    }

    @ApiOperation(value = "获取订单列表")
    @PostMapping("/list")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNo", value = "当前页码", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", required = false, dataType = "Integer")})
    public ResponseResult list(@UserParam UserEntity user,
                               Integer pageNo,
                               Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("sortField", "add_time");
        params.put("sortOrder", "DESC");
        Query query = new Query(params);
        LinkedHashMap<String, Object> orderMap = apiOrderService.queryList(query);
        List<OrderEntity> orderList = (List<OrderEntity>) orderMap.get("records");
        if(ToolUtil.listSizeGT(orderList)){
            for(Iterator<OrderEntity> it = orderList.iterator(); it.hasNext();){
                OrderEntity order = it.next();
                //订单商品
                Map<String, Object> orderGoodsParams = new HashMap<>();
                orderGoodsParams.put("orderId", order.getId());
                LinkedHashMap<String, Object> orderGoodsMap = apiOrderGoodsService.queryList(orderGoodsParams);
                List<OrderGoodsEntity> orderGoodsList = (List<OrderGoodsEntity>) orderGoodsMap.get("records");
                if(ToolUtil.listSizeGT(orderGoodsList)){
                    Integer goodsNum = 0;
                    for(Iterator<OrderGoodsEntity> its = orderGoodsList.iterator(); its.hasNext();){
                        OrderGoodsEntity orderGoods = its.next();
                        goodsNum += orderGoods.getNumber();
                        order.setOrderGoodsNum(goodsNum);
                    }
                }
            }
        }
        return buildSuccessInfo(orderMap);
    }

    @ApiOperation(value = "获取订单详情")
    @GetMapping(value = "/get/{id}")
    public ResponseResult get(@PathVariable("id") Integer orderId){
        OrderEntity order = apiOrderService.getSelf(orderId);
        ToolUtil.isFalse(ToolUtil.isNull(order), "订单不存在！");
        Map<String, Object> orderGoodsParams = new HashMap<>();
        orderGoodsParams.put("orderId", orderId);
        LinkedHashMap<String, Object> orderGoodsMap = apiOrderGoodsService.queryList(orderGoodsParams);
        //订单可操作的选择，取消，删除，支付，退换货，收货，确认订单，评论，再次购买
        Map<String, Object> handleOption = this.handleOption(order.getOrderStatus());
        List<Map<String, Object>> shippingList = null;
        if(ToolUtil.isNotBlank(order.getShippingNo(), order.getShippingCode())){
            shippingList = apiKdniaoService.getShippingTrack(order.getShippingCode(), order.getShippingNo());
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("order", BeansUtil.copyEntity(order, OrderVO.class));
        resultMap.put("orderGoods", orderGoodsMap.get("records"));
        resultMap.put("handleOption", handleOption);
        resultMap.put("shippingList", shippingList);
        return buildSuccessInfo(resultMap);
    }

    private Map<String, Object> handleOption(Integer orderStatus){
        Map<String, Object> optionMap = new HashMap<>();
        optionMap.put("cancel", false); //取消操作
        optionMap.put("delete", false); //删除操作
        optionMap.put("pay", false); //支付操作
        optionMap.put("return", false); //退换货操作
        optionMap.put("deliver", false); //确认收货操作
        optionMap.put("confirm", false); //确认订单操作
        optionMap.put("comment", false); //评论操作
        optionMap.put("purchase", false); //再次购买
        switch(orderStatus){
            case 0:
                //如果订单没有被取消，且没有支付，则可支付，可取消
                optionMap.put("cancel", true);
                optionMap.put("pay", true);
                break;
            case 101:
                //如果订单已经取消或是已完成，则可删除和再次购买
                optionMap.put("purchase", true);
                break;
            case 201:
                //如果订单已付款，没有发货，则可退款操作
                optionMap.put("cancel", true);
                break;
            case 300:
                //如果订单已经发货，没有收货，则可收货操作和退款、退货操作
                optionMap.put("confirm", true);
                break;
            case 301:
                //如果订单已经支付，且已经收货，则可完成交易、评论和再次购买
                optionMap.put("comment", true);
                optionMap.put("purchase", true);
                break;
            default:
                break;
        }
        return optionMap;
    }

}
