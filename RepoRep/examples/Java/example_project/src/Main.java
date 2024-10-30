package example_project;

import example_project.utils.StringUtils;
import example_project.utils.NetworkUtils;

public class Main {
    public static void main(String[] args) {
        // 测试 StringUtils 的 reverse 方法
        String input = "Hello, World!";
        String reversed = StringUtils.reverse(input);
        System.out.println("Reversed: " + reversed);

        // 测试 NetworkUtils 的 ping 方法
        boolean reachable = NetworkUtils.ping("127.0.0.1");
        System.out.println("Is reachable: " + reachable);
    }
}
