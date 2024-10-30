package com.hxxdemo.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class BaiduConvertData {
    private static final Integer RETRY_TIMES = 3;
    private static final String BAIDU_OCPC_URL = "https://ocpc.baidu.com/ocpcapi/api/uploadConvertData";

    private static final String TOKEN = "Kkoq3xZ9Q3VwOPy5BRY47BFZbLrUIVOl@b5u70imgO5w5j3qL4NIg8yTnsbUEJ6GS";

    public static String getToken(){
        if (System.getenv("BAIDU_CONVERT_TOKEN") != null) {
            return System.getenv("BAIDU_CONVERT_TOKEN");
        }
        return TOKEN;
    }

    /**
     * 数据回传接口
     * @param token 用户回传数据api接口token
     * @param conversionTypeList 回传转化数据数组
     * @return 返回true代表成功 false代表失败
     */
    public static Boolean sendConvertData(String token, List<ConversionType> conversionTypeList) {

        JsonObject data = new JsonObject();
        // 设置API接口回传Token
        data.addProperty("token", token);
        // 设置API接口回传conversionTypes
        data.add("conversionTypes",
                new Gson().toJsonTree(conversionTypeList, new TypeToken<List<ConversionType>>() {}.getType()).getAsJsonArray());
        // 发送的完整请求数据
        // do some log
        System.out.println("req data: " + data.toString());
        // 向百度发送数据
        return sendWithRetry(data.toString());

    }

    private static boolean sendWithRetry(String msg) {
        // 发送请求
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(BAIDU_OCPC_URL);
        post.setHeader("Content-type", "application/json; charset=UTF-8");
        StringEntity entity = new StringEntity(msg, Charset.forName("UTF-8"));
        entity.setContentEncoding("UTF-8");
        post.setEntity(entity);
        // 添加失败重试
        int retry = RETRY_TIMES;
        for (int i = 0; i < retry; i++) {
            try {
                HttpResponse response = client.execute(post);
                // 检验状态码，如果成功接收数据
                int code = response.getStatusLine().getStatusCode();
                if (code == HttpStatus.SC_OK) {
                    String res = EntityUtils.toString(response.getEntity());
                    JsonObject returnData = new JsonParser().parse(res).getAsJsonObject();
                    // 打印返回结果
                    // do some log
                    System.out.println("retry times :" + i + ", res data: " + res);

                    int status = returnData.getAsJsonObject("header").get("status").getAsInt();
                    // status为4，代表服务端异常，可添加重试
                    if ( status != 4) {
                        return status == 0; // status == 0 代表回传数据成功，其余情况回传数据失败
                    }
                }
            } catch (IOException e) {
                // do some log
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String token = "Kkoq3xZ9Q3VwOPy5BRY47BFZbLrUIVOl@b5u70imgO5w5j3qL4NIg8yTnsbUEJ6GS";
        List<ConversionType> conversionTypes = new ArrayList<>();
        // 编辑一条转化数据
        ConversionType cv = new ConversionType();
        cv.setLogidUrl("https://aixcoder.com/#/?bd_vid=uANBIyIxUhNLgvw-I-tznWnzrH04g1PxnHTkPWfdnW04P1nYPf"); // 设置落地页url
        cv.setConvertType(3); // 设置转化类型
        conversionTypes.add(cv);
        System.out.println(BaiduConvertData.sendConvertData(token, conversionTypes));
    }
}
