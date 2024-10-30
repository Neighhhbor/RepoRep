package com.hxxdemo.weixinsaomalogin.util;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 类名: MyX509TrustManager  * 描述: 信任管理器   * 开发人员： weining  * 创建时间：  2017/5/5  */
public class WechatX509TrustManager implements X509TrustManager {

    // 检查客户端证书
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    // 检查服务器端证书
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    // 返回受信任的X509证书数组
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}