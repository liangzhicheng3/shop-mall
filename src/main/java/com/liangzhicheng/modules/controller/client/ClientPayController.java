package com.liangzhicheng.modules.controller.client;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.pay.wechat.object.RefundResult;
import com.liangzhicheng.common.pay.wechat.utils.XmlUtil;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.config.mvc.resolver.annotation.UserParam;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.OrderEntity;
import com.liangzhicheng.modules.entity.UserEntity;
import com.liangzhicheng.modules.service.IApiOrderService;
import com.liangzhicheng.modules.service.IApiWechatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Api(tags = {"支付"})
@RestController
@RequestMapping(value = "/client/pay")
public class ClientPayController extends BaseController {

    @Resource
    private IApiWechatService apiWechatService;
    @Resource
    private IApiOrderService apiOrderService;

    @ApiOperation(value = "下单预支付")
    @PostMapping("/prepay")
    @ApiImplicitParams({@ApiImplicitParam(name = "orderId", value = "订单id", required = false, dataType = "Integer")})
    public ResponseResult prepay(@UserParam UserEntity user,
                                 Integer orderId){
        Map<String, Object> params = new HashMap<>();
        params.put("user", user);
        params.put("orderId", orderId);
        return buildSuccessInfo(apiWechatService.prepay(params));
    }

    @ApiOperation(value = "订单查询")
    @PostMapping("/order/query")
    @ApiImplicitParams({@ApiImplicitParam(name = "orderId", value = "订单id", required = false, dataType = "Integer")})
    public ResponseResult orderQuery(@UserParam UserEntity user,
                                     Integer orderId){
        Map<String, Object> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("orderId", orderId);
        return buildSuccessInfo(apiWechatService.orderQuery(params));
    }

    @ApiOperation(value = "订单退款")
    @PostMapping("/refund")
    @ApiImplicitParams({@ApiImplicitParam(name = "orderId", value = "订单id", required = false, dataType = "Integer")})
    public ResponseResult refund(@UserParam UserEntity user,
                                 Integer orderId){
        OrderEntity order = apiOrderService.getSelf(orderId);
        ToolUtil.isFalse(!order.getUserId().equals(user.getId()), ApiConstant.PARAM_ERROR);
        Map<String, Object> params = new HashMap<>();
        params.put("outTradeNo", order.getOrderSn());
//        params.put("orderMoney", order.getActualPrice());
//        params.put("refundMoney", order.getActualPrice());
        params.put("orderMoney", 0.01);
        params.put("refundMoney", 0.01);
        return buildSuccessInfo(apiWechatService.refund(params));
    }

    @ApiOperation(value = "退款查询")
    @PostMapping("/refund/query")
    @ApiImplicitParams({@ApiImplicitParam(name = "orderId", value = "订单id", required = false, dataType = "Integer")})
    public ResponseResult refundQuery(@UserParam UserEntity user,
                                      Integer orderId){
        OrderEntity order = apiOrderService.getSelf(orderId);
        ToolUtil.isFalse(!order.getUserId().equals(user.getId()), ApiConstant.PARAM_ERROR);
        Map<String, Object> params = new HashMap<>();
        params.put("outTradeNo", order.getOrderSn());
        params.put("outRefundNo", "outRefundNo");
        return buildSuccessInfo(apiWechatService.refundQuery(params));
    }

    @ApiIgnore
    @PostMapping(value = "/notify", produces = "text/html;charset=UTF-8")
    public String notify(HttpServletRequest request, HttpServletResponse response) {
        try{
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Access-Control-Allow-Origin", "*");
            InputStream in = request.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.close();
            in.close();
            String responseXml = new String(out.toByteArray(), "UTF-8");
            RefundResult refundResult = (RefundResult) XmlUtil.xmlStrToBean(responseXml, RefundResult.class);
            String resultCode = refundResult.getResult_code();
            //订单编号
            String outTradeNo = refundResult.getOut_trade_no();
            if(resultCode.equalsIgnoreCase("FAIL")){
                log.error("订单为{}支付失败！", outTradeNo);
                response.getWriter().write(this.setXml("SUCCESS", "OK"));
            }else if(resultCode.equalsIgnoreCase("SUCCESS")){
                log.error("订单为{}支付成功！", outTradeNo);
                //业务处理
                OrderEntity order = apiOrderService.getOne(
                        Wrappers.<OrderEntity>lambdaQuery()
                                .eq(OrderEntity::getOrderSn, outTradeNo)
                                .eq(OrderEntity::getPayStatus, 1));
                order.setPayStatus(2);
                order.setOrderStatus(201);
                order.setShippingStatus(0);
                order.setPayTime(new Date());
                apiOrderService.update(order);
                response.getWriter().write(this.setXml("SUCCESS", "OK"));
            }
        }catch (Exception e){
            log.error("[订单支付] 回调失败，信息为：{}", e.getMessage());
        }
        return "";
    }

    /**
     * 返回微信服务
     * @param returnCode 响应的code
     * @param returnMsg 响应的message
     * @return xml结果
     */
    private String setXml(String returnCode, String returnMsg) {
        return "<xml><return_code><![CDATA[" + returnCode + "]]></return_code><return_msg><![CDATA[" + returnMsg + "]]></return_msg></xml>";
    }

}
