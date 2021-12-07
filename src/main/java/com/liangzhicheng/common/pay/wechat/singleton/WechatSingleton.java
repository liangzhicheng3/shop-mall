package com.liangzhicheng.common.pay.wechat.singleton;

import com.liangzhicheng.common.pay.wechat.object.RefundResult;
import com.liangzhicheng.common.utils.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;

@Slf4j
public class WechatSingleton {

    private static SSLConnectionSocketFactory factory;

    public static SSLConnectionSocketFactory getSSLConnectionSocket() {
        if (null == factory) {
            setSSLConnectionSocket();
        }
        return factory;
    }

    private static void setSSLConnectionSocket() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            Thread.currentThread().getContextClassLoader();
            InputStream instream = new RefundResult().getClass().getResourceAsStream(ResourceUtil.getValue("wechat.certName"));
            try {
                keyStore.load(instream, ResourceUtil.getValue("wechat.mchId").toCharArray());
            } finally {
                instream.close();
            }
            SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, ResourceUtil.getValue("wechat.mchId").toCharArray()).build();
            factory = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        } catch (Exception e) {
            log.error("[WechatSingleton] 发生异常：{}", e.getMessage());
        }
    }

}
