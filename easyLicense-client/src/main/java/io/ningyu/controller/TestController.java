package io.ningyu.controller;

import io.ningyu.license.verify.LicenseVerify;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 模拟登录
 *
 * @author ningyu
 */
@RestController
public class TestController {

    /**
     * 模拟登录验证
     * @author zifangsky
     * @date 2018/7/9 17:09
     * @since 1.0.0
     * @param username 用户名
     * @param password 密码
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @PostMapping("/check")
    @ResponseBody
    public Map<String,Object> test(@RequestParam(required = true) String username, @RequestParam(required = true) String password){
        Map<String,Object> result = new HashMap<>(1);
        System.out.println(MessageFormat.format("用户名：{0}，密码：{1}",username,password));
        //模拟登录
        System.out.println("模拟登录流程");
        result.put("code",200);

        return result;
    }

    /**
     * 模拟登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @return java.util.Map<java.lang.String, java.lang.Object>
     */
    @GetMapping("/info")
    @ResponseBody
    public Map<String, Object> info(@RequestParam(required = true) String username, @RequestParam(required = true) String password) {
        return LicenseVerify.info();
    }

    @GetMapping("/verify")
    @ResponseBody
    public Map<String, Object> verify(@RequestParam(required = true) String username, @RequestParam(required = true) String password) {
        Map<String, Object> result = new HashMap<>();
        boolean verifyResult = LicenseVerify.verify();
        if (verifyResult) {
            result.put("result", "success");
        } else {
            result.put("result", "您的证书无效，请核查服务器是否取得授权或重新申请证书！");
        }
        return result;
    }

}
