package com.liangzhicheng.modules.service.impl;

import cn.hutool.core.map.MapUtil;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.exception.TransactionException;
import com.liangzhicheng.common.pay.wechat.object.RefundResult;
import com.liangzhicheng.common.pay.wechat.singleton.WechatSingleton;
import com.liangzhicheng.common.pay.wechat.utils.MD5Util;
import com.liangzhicheng.common.pay.wechat.utils.XmlUtil;
import com.liangzhicheng.common.bean.RedisBean;
import com.liangzhicheng.common.utils.ResourceUtil;
import com.liangzhicheng.common.utils.TimeUtil;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.modules.entity.OrderEntity;
import com.liangzhicheng.modules.entity.OrderGoodsEntity;
import com.liangzhicheng.modules.entity.UserEntity;
import com.liangzhicheng.modules.service.IApiOrderGoodsService;
import com.liangzhicheng.modules.service.IApiOrderService;
import com.liangzhicheng.modules.service.IApiWechatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class ApiWechatServiceImpl implements IApiWechatService {

    @Resource
    private IApiOrderService apiOrderService;
    @Resource
    private IApiOrderGoodsService apiOrderGoodsService;
    @Resource
    private HttpServletRequest request;
    @Resource
    private RedisBean redisBean;

    @Override
    public Map<Object, Object> prepay(Map<String, Object> params) {
        //用户id
        UserEntity user = MapUtil.get(params, "user", UserEntity.class);
        //订单id
        Integer orderId = MapUtil.getInt(params, "orderId");
        OrderEntity order = apiOrderService.getSelf(orderId);
        ToolUtil.isFalse(!order.getUserId().equals(user.getId()), ApiConstant.PARAM_ERROR);
        ToolUtil.isFalse(ToolUtil.isNull(order), "订单已取消");
        ToolUtil.isFalse(order.getPayStatus() == 2, "订单已支付，请勿重复操作");
        try{
            //构建请求参数
            Map<Object, Object> requestParams = this.buildRequestParams(order, user);
            String xml = XmlUtil.mapToXmlStr(requestParams);
            log.info("[订单支付] 统一下单请求 xml：{}", xml);
            //发送请求
            Map<String, Object> responseParams = XmlUtil.xmlStrToMap(this.sendOnce(ResourceUtil.getValue("wechat.requestUrl"), xml));
            //响应处理
            return this.responseHandle(responseParams, order);
        }catch(Exception e){
            log.error("[订单支付] 发生异常，信息为：{}", e.getMessage());
            throw new TransactionException("支付失败！");
        }
    }

    @Override
    public String orderQuery(Map<String, Object> params) {
        Integer userId = MapUtil.getInt(params, "userId");
        Integer orderId = MapUtil.getInt(params, "orderId");
        OrderEntity order = apiOrderService.getSelf(orderId);
        ToolUtil.isFalse(!order.getUserId().equals(userId), ApiConstant.PARAM_ERROR);
        Map<Object, Object> requestParams = this.buildRequestParams(order.getOrderSn());
        String xml = XmlUtil.mapToXmlStr(requestParams);
        Map<String, Object> responseParams = null;
        try{
            responseParams = XmlUtil.xmlStrToMap(this.sendOnce(ResourceUtil.getValue("wechat.orderQueryUrl"), xml));
        }catch(Exception e){
            log.error("[订单查询] 失败，信息为：{}", e.getMessage());
            throw new TransactionException("查询失败！");
        }
        String tradeState = MapUtil.getStr(responseParams, "trade_state");
        if("SUCCESS".equals(tradeState)){
            //业务处理
            order.setPayStatus(2);
            order.setOrderStatus(201);
            order.setShippingStatus(0);
            order.setPayTime(new Date());
            apiOrderService.update(order);
            return "支付成功";
        }else if("USERPAYING".equals(tradeState)){
            //重新查询正在支付中
            Integer num = Integer.parseInt(redisBean.hGet(Constants.MALL_ORDER_QUERY_NUM, orderId.toString()));
            if(num == null){
                redisBean.hSet(Constants.MALL_ORDER_QUERY_NUM, orderId.toString(), "1");
                this.orderQuery(params);
            }else if(num <= 3){
                redisBean.hDelete(Constants.MALL_ORDER_QUERY_NUM, orderId.toString());
                this.orderQuery(params);
            }else{
                return "查询失败！";
            }
        }else{
            return "查询失败！";
        }
        return "查询未知错误！";
    }

    /**
     * 微信退款
     * @param params 参数map对象
     * @return 退款结果
     */
    @Override
    public RefundResult refund(Map<String, Object> params) {
        //订单号
        String outTradeNo = MapUtil.getStr(params, "outTradeNo");
        //订单金额
        Double orderMoney = MapUtil.getDouble(params, "orderMoney");
        //退款金额
        Double refundMoney = MapUtil.getDouble(params, "refundMoney");
        //转换金额格式
        BigDecimal bdOrderMoney = new BigDecimal(orderMoney, MathContext.DECIMAL32);
        BigDecimal bdRefundMoney = new BigDecimal(refundMoney, MathContext.DECIMAL32);
        //构建请求参数
        Map<Object, Object> requestParams = this.buildRequestParams(outTradeNo, bdOrderMoney, bdRefundMoney);
        String xml = XmlUtil.mapToXmlStr(requestParams);
        //发送请求
        String responseXml = this.sendPost(xml, WechatSingleton.getSSLConnectionSocket());
        return (RefundResult) XmlUtil.xmlStrToBean(responseXml, RefundResult.class);
    }

    /**
     * 微信查询退款
     * @param params 参数map对象
     * @return 查询退款结果
     */
    @Override
    public Map<String, Object> refundQuery(Map<String, Object> params) {
        //订单号
        String outTradeNo = MapUtil.getStr(params, "outTradeNo");
        //退款号 this.generateRefundNo()
        String outRefundNo = MapUtil.getStr(params, "outRefundNo");
        Map<Object, Object> requestParams = this.buildRequestParams(outTradeNo);
        String xml = XmlUtil.mapToXmlStr(requestParams);
        HttpPost httPost = new HttpPost(ResourceUtil.getValue("wechat.refundQueryUrl"));
        httPost.addHeader("Connection", "keep-alive");
        httPost.addHeader("Accept", "*/*");
        httPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httPost.addHeader("Host", "api.mch.weixin.qq.com");
        httPost.addHeader("X-Requested-With", "XMLHttpRequest");
        httPost.addHeader("Cache-Control", "max-age=0");
        httPost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
        httPost.setEntity(new StringEntity(xml, "UTF-8"));
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(WechatSingleton.getSSLConnectionSocket()).build();
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httPost);
            HttpEntity entity = response.getEntity();
            String xmlStr = EntityUtils.toString(entity, "UTF-8");
            log.info("[退款查询] result：{}", xmlStr);
            return XmlUtil.xmlStrToMap(xmlStr);
            //可将信息持久化
        } catch (Exception e) {
            log.error("[退款查询发生异常] 异常信息：{}", e.getMessage());
            return null;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("[退款查询IO发生异常] 异常信息：{}", e.getMessage());
            }
        }
    }

    /**
     * 得到请求微信支付请求参数
     * @param order 订单信息
     * @param user 用户信息
     * @return 请求参数map对象
     */
    private Map<Object, Object> buildRequestParams(OrderEntity order, UserEntity user){
        Map<Object, Object> params = new TreeMap<>();
        //微信分配的公众账号id（企业号corpid即为此appId或微信支付申请成功后在邮件中的appid）
        params.put("appid", ResourceUtil.getValue("wechat.appId"));
        //微信支付分配的商户号
        params.put("mch_id", ResourceUtil.getValue("wechat.mchId"));
        //随机字符串，不长于32位，推荐随机数生成算法
        params.put("nonce_str", ToolUtil.generateRandomNum(18).toUpperCase());
        // 商户订单编号
        params.put("out_trade_no", order.getOrderSn());
        //描述
        params.put("body", "xxx-支付");
        //商品名称处理
        Map<String, Object> orderGoodsParams = new HashMap<>();
        orderGoodsParams.put("order_id", order.getId());
        LinkedHashMap<String, Object> orderGoodsMap = apiOrderGoodsService.queryList(orderGoodsParams);
        List<OrderGoodsEntity> orderGoodsList = (List<OrderGoodsEntity>) orderGoodsMap.get("records");
        if(ToolUtil.listSizeGT(orderGoodsList)){
            String body = "xxx-";
            for(Iterator<OrderGoodsEntity> it = orderGoodsList.iterator(); it.hasNext();){
                OrderGoodsEntity orderGoods = it.next();
                body += orderGoods.getGoodsName() + "、";
            }
            if(body.length() > 0){
                body = body.substring(0, body.length() - 1);
            }
            //商品描述
            params.put("body", body);
        }
        //支付金额
        params.put("total_fee", order.getActualPrice().multiply(new BigDecimal(100)).intValue());
        //回调地址
        params.put("notify_url", ResourceUtil.getValue("wechat.notifyUrl"));
        //交易类型APP
        params.put("trade_type", ResourceUtil.getValue("wechat.tradeType"));
        params.put("spbill_create_ip", ToolUtil.getClientUrl(request));
        params.put("openid", user.getWeixinOpenid());
        //签名前必须要参数全部写在前面
        //生成paySign，参考https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=7_7&index=5
        params.put("sign", this.encrypt(params, ResourceUtil.getValue("wechat.paySign")));
        return params;
    }

    /**
     * 得到请求微信订单查询、退款查询请求参数
     * @param orderSn 订单号
     * @return 请求参数map对象
     */
    private Map<Object, Object> buildRequestParams(String orderSn){
        Map<Object, Object> params = new TreeMap<>();
        //微信分配的公众账号ID（企业号corpid即为此appId或微信支付申请成功后在邮件中的appid）
        params.put("appid", ResourceUtil.getValue("wechat.appId"));
        //微信支付分配的商户号
        params.put("mch_id", ResourceUtil.getValue("wechat.mchId"));
        //随机字符串，不长于32位，推荐随机数生成算法
        params.put("nonce_str", ToolUtil.generateRandomString(18).toUpperCase());
        //下单生成的订单号
        params.put("out_trade_no", orderSn);
        //签名前必须要参数全部写在前面
        params.put("sign", this.encrypt(params, ResourceUtil.getValue("wechat.paySign")));
        return params;
    }

    /**
     * 得到请求微信退款请求参数
     * @param outTradeNo 订单号
     * @param bdOrderMoney 格式化后订单金额
     * @param bdRefundMoney 格式化后退款金额
     * @return 请求参数map对象
     */
    private Map<Object, Object> buildRequestParams(String outTradeNo, BigDecimal bdOrderMoney, BigDecimal bdRefundMoney) {
        Map<Object, Object> params = new TreeMap<>();
        //微信分配的公众账号id（企业号corpid即为此appId或微信支付申请成功后在邮件中的appid）
        params.put("appid", ResourceUtil.getValue("wechat.appId"));
        //微信支付分配的商户号
        params.put("mch_id", ResourceUtil.getValue("wechat.mchId"));
        //随机字符串，不长于32位，推荐随机数生成算法
        params.put("nonce_str", ToolUtil.generateRandomString(16));
        //下单生成的订单号
        params.put("out_trade_no", outTradeNo);
        //商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
        params.put("out_refund_no", this.generateRefundNo());
        //订单总金额，单位为分
        params.put("total_fee", bdOrderMoney.multiply(new BigDecimal(100)).intValue());
        //退款总金额，订单总金额，单位为分
        params.put("refund_fee", bdRefundMoney.multiply(new BigDecimal(100)).intValue());
        //操作员帐号，默认为商户号
        params.put("op_user_id", ResourceUtil.getValue("wechat.mchId"));
        //签名前必须要参数全部写在前面
        params.put("sign", this.encrypt(params, ResourceUtil.getValue("wechat.paySign")));
        return params;
    }

    /**
     * 只请求一次，不做重试
     * @param url 地址
     * @param params 参数
     * @return 请求后结果
     * @throws Exception
     */
    private String sendOnce(final String url, String params) throws Exception {
        BasicHttpClientConnectionManager connManager;
        connManager = new BasicHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", SSLConnectionSocketFactory.getSocketFactory())
                        .build(), null, null, null
        );
        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connManager)
                .build();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(10000).build();
        httpPost.setConfig(requestConfig);
        StringEntity postEntity = new StringEntity(params, "UTF-8");
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.addHeader("User-Agent", "wxpay sdk java v1.0 " + ResourceUtil.getValue("wechat.mchId"));
        httpPost.setEntity(postEntity);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        String result = EntityUtils.toString(httpEntity, "UTF-8");
        log.info("[订单支付] 发送单次请求结果：{}", result);
        return result;

    }

    /**
     * 发送post请求到微信进行退款
     * @param xml
     * @param sslcsf
     * @return 请求结果
     */
    private String sendPost(String xml, SSLConnectionSocketFactory sslcsf) {
        log.info("[退款请求] send request params：{}", xml);
        HttpPost httPost = new HttpPost(ResourceUtil.getValue("wechat.refundUrl"));
        httPost.addHeader("Connection", "keep-alive");
        httPost.addHeader("Accept", "*/*");
        httPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httPost.addHeader("Host", "api.mch.weixin.qq.com");
        httPost.addHeader("X-Requested-With", "XMLHttpRequest");
        httPost.addHeader("Cache-Control", "max-age=0");
        httPost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
        httPost.setEntity(new StringEntity(xml, "UTF-8"));
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslcsf).build();
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httPost);
            HttpEntity entity = response.getEntity();
            String xmlStr = EntityUtils.toString(entity, "UTF-8");
            log.info("[退款响应] receive response params：{}", xmlStr);
            return xmlStr;
        } catch (Exception e) {
            log.error("[退款发生异常] 异常信息：{}", e.getMessage());
            return null;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("[退款IO发生异常] 异常信息：{}", e.getMessage());
            }
        }
    }

    /**
     * 支付响应结果处理
     * @param responseParams 响应参数
     */
    private Map<Object, Object> responseHandle(Map<String, Object> responseParams, OrderEntity order) {
        //响应报文
        String returnCode = MapUtil.getStr(responseParams, "return_code");
        String returnMsg = MapUtil.getStr(responseParams, "return_msg");
        if(returnCode.equalsIgnoreCase("FAIL")){
            log.error("[订单支付] 失败，returnMsg为：{}", returnMsg);
            throw new TransactionException("支付失败！");
        }else if(returnCode.equalsIgnoreCase("SUCCESS")){
            String resultCode = MapUtil.getStr(responseParams, "result_code");
            String errCodeDes = MapUtil.getStr(responseParams, "err_code_des");
            if(resultCode.equalsIgnoreCase("FAIL")){
                log.error("[订单支付] 失败，errCodeDes为：{}", errCodeDes);
                throw new TransactionException("支付失败！");
            }else if(resultCode.equalsIgnoreCase("SUCCESS")){
                String prepayId = MapUtil.getStr(responseParams, "prepay_id");
                Map<Object, Object> resultMap = new TreeMap<>();
                resultMap.put("appId", ResourceUtil.getValue("wechat.appId"));
                resultMap.put("timeStamp", TimeUtil.formatTime(System.currentTimeMillis() / 1000, Constants.DATE_TIME_PATTERN));
                resultMap.put("nonceStr", ToolUtil.generateRandomString(32));
                resultMap.put("package", "prepay_id=" + prepayId);
                resultMap.put("signType", "MD5");
                resultMap.put("paySign", this.encrypt(resultMap, ResourceUtil.getValue("wechat.paySign")));
                //业务处理
                order.setPayId(prepayId);
                order.setPayStatus(1);
                apiOrderService.update(order);
                return resultMap;
            }
        }
        return null;
    }

    /**
     * 生成退款交易号
     * @return 退款交易号
     */
    private String generateRefundNo() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String refundNo = dateFormat.format(new Date());
        String str = "000000" + (int) (Math.random() * 1000000);
        return refundNo + str.substring(str.length() - 6);
    }

    /**
     * 根据签名加密请求参数
     * @param params 请求参数
     * @param paySign 加密值
     * @return 加密后
     */
    private String encrypt(Map<Object, Object> params, String paySign) {
        boolean encode = false;
        Set<Object> keysSet = params.keySet();
        Object[] keys = keysSet.toArray();
        Arrays.sort(keys);
        StringBuffer temp = new StringBuffer();
        boolean first = true;
        for (Object key : keys) {
            if (first) {
                first = false;
            } else {
                temp.append("&");
            }
            temp.append(key).append("=");
            Object value = params.get(key);
            String valueString = "";
            if (null != value) {
                valueString = value.toString();
            }
            if (encode) {
                try {
                    temp.append(URLEncoder.encode(valueString, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                temp.append(valueString);
            }
        }
        temp.append("&key=");
        temp.append(paySign);
        log.info("[退款参数] encrypt：{}", temp.toString());
        return MD5Util.getMessageDigest(temp.toString());
    }

}
