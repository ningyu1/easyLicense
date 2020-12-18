package io.ningyu.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * io.ningyu.utils
 *
 * @author ningyu
 * @create 2020/12/18 1:56 下午
 */
public class RegexUtilTest {
    @Test
    public void test_equal(){
        String source="ab.d.c";
        org.junit.Assert.assertFalse(RegexUtil.equalsWildcard(source, ".b.d.c"));
        org.junit.Assert.assertFalse(RegexUtil.equalsWildcard(source, "a..d.c"));
        org.junit.Assert.assertFalse(RegexUtil.equalsWildcard(source, "a.*.d.c"));
        org.junit.Assert.assertFalse(RegexUtil.equalsWildcard(source, "abad.c"));
        org.junit.Assert.assertFalse(RegexUtil.equalsWildcard(source, "a*ad.c"));
        org.junit.Assert.assertFalse(RegexUtil.equalsWildcard(source, "ab.d.."));
        org.junit.Assert.assertTrue(RegexUtil.equalsWildcard(source, "ab.d.c"));
        org.junit.Assert.assertTrue(RegexUtil.equalsWildcard(source, source));
        org.junit.Assert.assertTrue(RegexUtil.equalsWildcard(source, "a*.d.c"));
        org.junit.Assert.assertTrue(RegexUtil.equalsWildcard(source, "ab.*.c"));
        org.junit.Assert.assertTrue(RegexUtil.equalsWildcard(source, "ab*.c"));
        org.junit.Assert.assertTrue(RegexUtil.equalsWildcard(source, "ab*.d*c"));
        org.junit.Assert.assertTrue(RegexUtil.equalsWildcard(source, "ab.*.*"));
        org.junit.Assert.assertTrue(RegexUtil.equalsWildcard(source, "ab.d.*"));
        org.junit.Assert.assertTrue(RegexUtil.equalsWildcard(source, "ab.d*.c*"));
        org.junit.Assert.assertTrue(RegexUtil.equalsWildcard(source, "*"));
    }
    @Test
    public void test_ip(){
        String source="10.25.0.1";
        org.junit.Assert.assertFalse(RegexUtil.equalsWildcard(source, "10.26.*.*"));
        org.junit.Assert.assertFalse(RegexUtil.equalsWildcard(source, "10.25.*.2"));
        org.junit.Assert.assertTrue(RegexUtil.equalsWildcard(source, "10.25.*.*"));
        org.junit.Assert.assertTrue(RegexUtil.equalsWildcard(source, "10.25.0.*"));
        org.junit.Assert.assertTrue(RegexUtil.equalsWildcard(source, "10.25.*.1*"));
        org.junit.Assert.assertTrue(RegexUtil.equalsWildcard(source, "*"));
    }

    @Test
    public void test_checkWildcard(){
        //匹配部分
        List<String> expectedList = new ArrayList<>();
        expectedList.add("10.20.0.31");
        expectedList.add("10.20.0.2*");

        List<String> serverList = new ArrayList<>();
        serverList.add("10.20.0.1");
        org.junit.Assert.assertFalse(RegexUtil.checkWildcard(expectedList, serverList));

        serverList.clear();
        serverList.add("10.20.0.11");
        org.junit.Assert.assertFalse(RegexUtil.checkWildcard(expectedList, serverList));

        serverList.clear();
        serverList.add("10.20.2.21");
        org.junit.Assert.assertFalse(RegexUtil.checkWildcard(expectedList, serverList));

        serverList.clear();
        serverList.add("10.20.0.25");
        org.junit.Assert.assertTrue(RegexUtil.checkWildcard(expectedList, serverList));

        serverList.clear();
        serverList.add("10.20.0.31");
        org.junit.Assert.assertTrue(RegexUtil.checkWildcard(expectedList, serverList));

        serverList.clear();
        serverList.add("10.20.0.121");
        org.junit.Assert.assertFalse(RegexUtil.checkWildcard(expectedList, serverList));

        //匹配所有
        expectedList = new ArrayList<>();
        expectedList.add("*");
        serverList.clear();
        serverList.add("10.20.0.11");
        serverList.add("10.20.0.12");
        org.junit.Assert.assertTrue(RegexUtil.checkWildcard(expectedList, serverList));

        //只匹配一个
        expectedList = new ArrayList<>();
        expectedList.add("10.20.0.31");
        expectedList.add("10.20.0.2*");
        serverList.clear();
        serverList.add("10.20.0.11");
        serverList.add("10.20.0.21");
        org.junit.Assert.assertTrue(RegexUtil.checkWildcard(expectedList, serverList));

        //一个都匹配不到
        serverList.clear();
        serverList.add("10.20.0.32");
        serverList.add("10.20.0.44");
        org.junit.Assert.assertFalse(RegexUtil.checkWildcard(expectedList, serverList));
    }

    @Test
    public void test_check(){
        //匹配所有
        List<String> expectedList = new ArrayList<>();
        expectedList.add("*");
        List<String> serverList = new ArrayList<>();
        serverList.add("sdf;ljksdf");
        org.junit.Assert.assertTrue(RegexUtil.check(expectedList, serverList));

        //匹配一个
        expectedList = new ArrayList<>();
        expectedList.add("sdflkjsdf09");
        serverList = new ArrayList<>();
        serverList.add("sdflkjsdf99");
        serverList.add("sdflkjsdf09");
        org.junit.Assert.assertTrue(RegexUtil.check(expectedList, serverList));

        //一个都匹配不到
        expectedList = new ArrayList<>();
        expectedList.add("sdflkjsdf09");
        serverList = new ArrayList<>();
        serverList.add("sdflkjsdf88");
        serverList.add("sdflkjsdf99");
        serverList.add("sdflkjsdf77");
        org.junit.Assert.assertFalse(RegexUtil.check(expectedList, serverList));
    }
}
