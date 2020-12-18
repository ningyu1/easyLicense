package io.ningyu.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * io.ningyu.utils.RegexUtil
 *
 * @author ningyu
 * @create 2020/12/18 1:52 下午
 */
public class RegexUtil {

    /**
     * 通配符匹配
     *
     * @param source
     * @param regex  : 含有通配符,通配符只有一个:*.<br>
     *               *表示任何字符,不限个数
     * @return
     */
    public static boolean equalsWildcard(String source, String regex) {
        regex = regex.replace(".", "\\.");
        regex = regex.replace("*", "(.*)");
        Pattern p = Pattern.compile("^" + regex + "$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(source);
        return m.find();
    }

    /**
     * 查找数组中是否包含给定字符
     *
     * @param strArray
     * @param j
     * @return
     */
    public static boolean isContains(String[] strArray, String j) {
        if (strArray == null || strArray.length == 0) {
            return false;
        }
        for (int i = 0; i < strArray.length; i++) {
            boolean bo = equalsWildcard(strArray[i], j);
            if (bo) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验当前服务器的IP是否在可被允许的IP范围内<br/>
     *
     * @param expectedList
     * @param serverList
     * @return boolean
     */
    public static boolean checkWildcard(List<String> expectedList, List<String> serverList) {
        if (expectedList != null && expectedList.size() > 0) {
            if (serverList != null && serverList.size() > 0) {
                for (String expected : expectedList) {
                    //通配符*不控制
                    if (expected.equals("*")) {
                        return true;
                    }
                    if (RegexUtil.isContains(serverList.toArray(new String[serverList.size()]), expected.trim())) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            //证书中没有控制信息也作为不控制处理
            return true;
        }
    }

    /**
     * 校验当前服务器的Mac地址/cpu序列号/主板序列号是否在可被允许的IP范围内<br/>
     *
     * @param expectedList
     * @param serverList
     * @return boolean
     */
    public static boolean check(List<String> expectedList, List<String> serverList) {
        if (expectedList != null && expectedList.size() > 0) {
            if (serverList != null && serverList.size() > 0) {
                for (String expected : expectedList) {
                    //通配符*不控制
                    if (expected.equals("*")) {
                        return true;
                    }
                    if (serverList.contains(expected.trim())) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            //证书中没有控制信息也作为不控制处理
            return true;
        }
    }
}
