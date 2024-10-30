package example_project.utils;

public class StringUtils {
    // 字符串反转方法
    public static String reverse(String input) {
        return new StringBuilder(input).reverse().toString();
    }
}
