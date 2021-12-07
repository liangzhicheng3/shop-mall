package com.liangzhicheng.common.utils;

import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.exception.TransactionException;
import com.liangzhicheng.common.utils.helper.MyX509TrustManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.util.*;

@Slf4j
public class ToolUtil {

    /**
     * 需求判断为false，true抛异常
     * @param result
     * @param message
     */
    public static void isFalse(boolean result, String message){
        isTrue(!result, message);
    }

    /**
     * 需求判断为false，true抛异常
     * @param result
     * @param code
     */
    public static void isFalse(boolean result, int code){
        isTrue(!result, code);
    }

    /**
     * 需求判断为false，true抛异常
     * @param result
     * @param code
     * @param message
     */
    public static void isFalse(boolean result, int code, String message){
        isTrue(!result, code, message);
    }

    /**
     * 需求判断为true，false抛异常
     * @param result
     * @param message
     */
    public static void isTrue(boolean result, String message){
        if(!result){
            throw new TransactionException(message);
        }
    }

    /**
     * 需求判断为true，false抛异常
     * @param result
     * @param code
     */
    public static void isTrue(boolean result, int code){
        if(!result){
            throw new TransactionException(code);
        }
    }

    /**
     * 需求判断为true，false抛异常
     * @param result
     * @param message
     */
    public static void isTrue(boolean result, int code, String message){
        if(!result){
            throw new TransactionException(code, message);
        }
    }

    public static boolean isNotNull(Object obj){
        return !isNull(obj);
    }

    /**
     * 判断对象或对象数组中每一个对象是否为空，对象为null，字符序列长度为0，集合类、Map类为empty
     * @param obj
     * @return boolean
     */
    public static boolean isNull(Object obj){
        if(obj == null){
            return true;
        }
        if(obj instanceof CharSequence){
            return ((CharSequence) obj).length() == 0;
        }
        if(obj instanceof Collection){
            return ((Collection) obj).isEmpty();
        }
        if(obj instanceof Map){
            return ((Map) obj).isEmpty();
        }
        if(obj instanceof Object[]){
            Object[] object = (Object[]) obj;
            if(object.length == 0){
                return true;
            }
            boolean empty = true;
            for(int i = 0; i < object.length; i++){
                if (!isNull(object[i])) {
                    empty = false;
                    break;
                }
            }
            return empty;
        }
        return false;
    }

    public static boolean isNotBlank(String... strs) {
        return !isBlank(strs);
    }

    public static boolean isBlank(String... strs) {
        for (String s : strs) {
            if (StringUtils.isBlank(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean listSizeGT(Collection collection) {
        if (collection == null || collection.size() == 0) {
            return false;
        }
        return true;
    }

    public static String generateRandomNum(Integer num) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String generateRandomString(Integer num) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 生成缓存key
     * @param key     前缀
     * @param className  类名
     * @param methodName 方法名
     * @return prefix_className_methodName
     */
    public static String generateCacheKey(String key, String className, String methodName) {
        className = className.substring(className.lastIndexOf(".") + 1);
        return key + "_" + className + "_" + methodName;
    }

    /**
     * 发送http请求
     * @param path 请求地址
     * @param method 请求方式（GET或POST）
     * @param output 提交的数据
     * @return 结果内容
     */
    public static String sendRequest(String path, String method, String output) {
        String result = "";
        HttpsURLConnection conn = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            //创建SSLContext对象，并使用自己指定的信任管理器初始化
            TrustManager[] tm = {new MyX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            //从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(path);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(method);

            //当output不为null时向输出流写数据
            if (isNotBlank(output)) {
                OutputStream outputStream = conn.getOutputStream();
                //注意编码格式
                outputStream.write(output.getBytes("UTF-8"));
                outputStream.close();
            }

            //从输入流读取返回内容
            inputStream = conn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            result = buffer.toString();
        } catch (Exception e) {
            log.error("发送请求发生异常：{}", e.getMessage());
        } finally { //释放资源
            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStreamReader != null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(conn != null){
                conn.disconnect();
            }
        }
        return result;
    }

    /**
     * @description 获取真实的ip地址
     * @param request
     * @return String
     */
    public static String getClientUrl(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if(isNotBlank(ip)){
            //多次反向代理后会有多个IP值，第一个IP才是真实IP
            int index = ip.indexOf(",");
            if(index != -1){
                return ip.substring(0, index);
            }else{
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if(isNotBlank(ip)){
            return ip;
        }
        return request.getRemoteAddr();
    }

    /**
     * SQL注入过滤
     * @param str
     * @return
     */
    public static String sqlInject(String str) {
        if(isBlank(str)){
            return null;
        }
        //去掉'|"|;|\字符
        str = StringUtils.replace(str, "'", "");
        str = StringUtils.replace(str, "\"", "");
        str = StringUtils.replace(str, ";", "");
        str = StringUtils.replace(str, "\\", "");
        //转换成小写
        str = str.toLowerCase();
        //非法字符
        String[] keywords = {"master", "truncate", "insert", "select", "delete", "update", "declare", "alert", "drop"};
        //判断是否包含非法字符
        for(String keyword : keywords){
            if(str.indexOf(keyword) != -1){
                throw new TransactionException("包含非法字符");
            }
        }
        return str;
    }

    public static String generateOrderSn() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return TimeUtil.format(cal.getTime(), Constants.TIMESTAMP_PATTERN) + generateRandomNum(6);
    }

//    public static void main(String[] args) {
//        //字符串替换
//        String str = "";
//        str = String.format("Hi,%s", "我是谁");
//        System.out.println(str);
//        str = String.format("Hi,%s:%s.%s.%s", "我是", "你", "我", "他");
//        System.out.println(str);
//    }

}
