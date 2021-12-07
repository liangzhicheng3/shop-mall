package com.liangzhicheng.common.utils;

import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.exception.TransactionException;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class TimeUtil {

    //日期转换格式数组
    public static String[][] regularExp = new String[][]{
            //默认格式
            {"\\d{4}-((([0][1,3-9]|[1][0-2]|[1-9])-([0-2]\\d|[3][0,1]|[1-9]))|((02|2)-(([1-9])|[0-2]\\d)))\\s+([0,1]\\d|[2][0-3]|\\d):([0-5]\\d|\\d):([0-5]\\d|\\d)",
                    Constants.DATE_TIME_PATTERN},
            //日期格式（年月日）
            {"\\d{4}-((([0][1,3-9]|[1][0-2]|[1-9])-([0-2]\\d|[3][0,1]|[1-9]))|((02|2)-(([1-9])|[0-2]\\d)))",
                    Constants.DATE_PATTERN},
            //毫秒格式
            {"\\d{4}((([0][1,3-9]|[1][0-2]|[1-9])([0-2]\\d|[3][0,1]|[1-9]))|((02|2)(([1-9])|[0-2]\\d)))([0,1]\\d|[2][0-3])([0-5]\\d|\\d)([0-5]\\d|\\d)\\d{1,3}",
                    Constants.TIMESTAMP_PATTERN}
    };

    public static String format(Date date) {
        return format(date, Constants.DATE_TIME_PATTERN);
    }

    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static Date parse(String value) {
        return parse(value, Constants.DATE_PATTERN);
    }

    public static Date parse(String value, String pattern) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try{
            date = sdf.parse(value);
        }catch(ParseException e){
            throw new TransactionException("[日期格式化] 发生异常！");
        }
        return date;
    }

    public static String formatTime(Long time, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        if(time.toString().length() < 13){
            time = time * 1000L;
        }
        return dateFormat.format(new Date(time));
    }

    public static long parseTime(String value) {
        return parseTimeByExp(value).getTime() / 1000;
    }

    public static Date parseTimeByExp(String value) {
        try{
            String type = getTimeFormat(value);
            SimpleDateFormat sf = new SimpleDateFormat(type);
            return new Date((sf.parse(value).getTime()));
        }catch(Exception e){
            throw new TransactionException("[日期格式化] 发生异常！");
        }
    }

    public static String getTimeFormat(String value) {
        String style = null;
        if(ToolUtil.isBlank(value)){
            return null;
        }
        boolean flag = false;
        for(int i = 0; i < regularExp.length; i++){
            flag = value.matches(regularExp[i][0]);
            if(flag){
                style = regularExp[i][1];
            }
        }
        if(ToolUtil.isBlank(style)){
            log.info("value：{}", value);
            throw new TransactionException("[日期格式化] 未识别的日期格式！");
        }
        return style;
    }

//    public static void main(String[] args) {
//        long time = new Date().getTime();
//        System.out.println(getTimeFormat(time + ""));
//    }

}
