package com.hxxdemo.githubLogin.util;


import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Url工具类
 */
public class UrlUtil {

    /**
     * 拼接url参数
     *
     * @param url
     * @param key
     * @param value
     * @return 拼接后的url
     */
    public static String joinParam(String url, String key, String value) {
        Map<String, String> param = new HashMap<String, String>();
        param.put(key, value);
        return joinParam(url, param);
    }

    /**
     * 拼接url参数
     *
     * @param url
     * @param param
     * @return 拼接后的url
     */
    public static String joinParam(String url, Map<String, String> param) {
        if (url == null || param == null || param.isEmpty()) {
            return url;
        }

        StringBuilder sb = new StringBuilder(url);
        if (url.indexOf("?") == -1) {
            sb.append("?");
        } else {
            sb.append("&");
        }

        boolean first = true;
        for (Entry<String, String> entry : param.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=");
            if (StringUtils.isNotEmpty(value)) {
                try {
                    sb.append(URLEncoder.encode(value, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

}
