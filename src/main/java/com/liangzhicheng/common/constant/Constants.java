package com.liangzhicheng.common.constant;

public interface Constants {

    /**
     * 商城中相关常量
     */
    //缓存key
    String MALL_CACHE = "mall_cache";
    //商品购买缓存key
    String MALL_GOODS_PURCHASE = "mall_goods_purchase";
    //订单查询次数缓存key
    String MALL_ORDER_QUERY_NUM = "mall_order_query_num";

    //用户信息缓存key
    String USER_INFO = "user_info";

    //云存储key
    String CLOUD_STORAGE_CONFIG_KEY = "cloud_storage_config_key";

    /**
     * 日期格式相关常量
     */
    //默认格式
    String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    //日期格式（年月日）
    String DATE_PATTERN = "yyyy-MM-dd";
    //毫秒格式
    String TIMESTAMP_PATTERN = "yyyyMMddHHmmssSSS";

    /**
     * 快递鸟相关常量
     */
    //电商id
    String KDNIAO_BUSINESS_ID = "";
    //电商加密私钥，快递鸟提供，注意保管
    String KDNIAO_APP_KEY = "";
    //请求url
    String KDNIAO_URL = "http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";

    /**
     * 定时任务相关常量
     */
    //任务调度参数key
    String SCHEDULE_TASK_KEY = "schedule_task_key";
    //任务调度jobKey
    String SCHEDULE_TASK_JOB_KEY = "schedule_task_";

}

