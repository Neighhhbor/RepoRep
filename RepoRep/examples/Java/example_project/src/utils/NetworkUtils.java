package example_project.utils;

import example_project.utils.StringUtils;

public class NetworkUtils {
    // 模拟 ping，并调用 StringUtils 的 reverse 方法
    public static boolean ping(String ip) {
        System.out.println("Pinging: " + ip);
        String reversedIp = StringUtils.reverse(ip);
        System.out.println("Reversed IP: " + reversedIp);
        return true;
    }
}
