package com.hu.oneclick.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/11/29
 * @since JDK 1.8.0
 */
public class OneClickUtil {
    private static Pattern humpPattern = Pattern.compile("[A-Z]");
    private static Pattern linePattern = Pattern.compile("_(\\w)");

    /**
     * 驼峰转下划线
     *
     * @Param: [str]
     * @return: java.lang.String
     * @Author: MaSiyi
     * @Date: 2021/11/26
     */
    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 下划线转驼峰
     *
     * @Param: [str]
     * @return: java.lang.String
     * @Author: MaSiyi
     * @Date: 2021/11/26
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
