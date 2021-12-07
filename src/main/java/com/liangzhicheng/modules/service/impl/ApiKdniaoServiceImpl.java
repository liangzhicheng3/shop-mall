package com.liangzhicheng.modules.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.exception.TransactionException;
import com.liangzhicheng.modules.service.IApiKdniaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ApiKdniaoServiceImpl implements IApiKdniaoService {

    @Override
    public List<Map<String, Object>> getShippingTrack(String shippingCode, String shippingNo) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try{
            String json = "{'OrderCode':'','ShipperCode':'" + shippingCode + "','LogisticCode':'" + shippingNo + "'}";
            Map<String, String> requestParams = new HashMap<>();
            requestParams.put("RequestData", encoder(json, "UTF-8"));
            requestParams.put("EBusinessID", Constants.KDNIAO_BUSINESS_ID);
            requestParams.put("RequestType", "1002");
            String dataSign = encrypt(json, Constants.KDNIAO_APP_KEY, "UTF-8");
            requestParams.put("DataSign", encoder(dataSign, "UTF-8"));
            requestParams.put("DataType", "2");
            String result = sendPost(Constants.KDNIAO_URL, requestParams);
            JSONObject jsonObject = JSONUtil.parseObj(result);
            if(jsonObject != null){
                JSONArray jsonArray = jsonObject.getJSONArray("Traces");
                for(int i = 0; i < jsonArray.size(); i++){
                    JSONObject item = jsonArray.getJSONObject(i);
                    Map<String, Object> map = new HashMap<>();
                    map.put("AcceptTime", item.get("AcceptTime") + "");
                    map.put("AcceptStation", item.get("AcceptStation") + "");
                    resultList.add(map);
                }
            }
        }catch(Exception e){
            log.error("[快递鸟] handle error：{}", e.getMessage());
            throw new TransactionException("服务发生异常，请联系管理员！");
        }
        return resultList;
    }

    private String encoder(String content, String charset) throws UnsupportedEncodingException {
        return URLEncoder.encode(content, charset);
    }

    private String encrypt(String content, String key, String charset) throws UnsupportedEncodingException, Exception {
        if(key != null){
            return base64(MD5(content + key, charset), charset);
        }
        return base64(MD5(content, charset), charset);
    }

    private String base64(String value, String charset) throws UnsupportedEncodingException {
        return base64Encode(value.getBytes(charset));
    }

    private String base64Encode(byte[] data) {
        StringBuffer sb = new StringBuffer();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;
        while (i < len) {
            b1 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            b2 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            b3 = data[i++] & 0xff;
            sb.append(base64EncodeChars[b1 >>> 2]);
            sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
            sb.append(base64EncodeChars[b3 & 0x3f]);
        }
        return sb.toString();
    }

    private char[] base64EncodeChars = new char[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/'
    };

    private String MD5(String str, String charset) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(charset));
        byte[] result = md.digest();
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < result.length; i++) {
            int val = result[i] & 0xff;
            if (val <= 0xf) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toLowerCase();
    }

    private String sendPost(String url, Map<String, String> params) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            //发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //POST方法
            conn.setRequestMethod("POST");
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
            //获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            //发送请求参数
            if (params != null) {
                StringBuilder param = new StringBuilder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (param.length() > 0) {
                        param.append("&");
                    }
                    param.append(entry.getKey());
                    param.append("=");
                    param.append(entry.getValue());
                }
                out.write(param.toString());
            }
            //flush输出流的缓冲
            out.flush();
            //定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

//    public static void main(String[] args) {
//        ApiKdniaoServiceImpl service = new ApiKdniaoServiceImpl();
//        try {
//            List result = service.getShippingTrack("YD", "3907732889203");
//            log.info("[快递鸟] main test success：{}", result);
//        } catch (Exception e) {
//            log.warn("[快递鸟] main test error：{}", e.getMessage());
//        }
//    }

}
