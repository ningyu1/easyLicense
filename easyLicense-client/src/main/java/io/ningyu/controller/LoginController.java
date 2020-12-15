package io.ningyu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 模拟登录
 * @author ningyu
 */
@Controller
public class LoginController {

    /**
     * 模拟登录验证
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
     * 用户首页
     * @return java.lang.String
     */
    @RequestMapping("/userIndex")
    public String userIndex(){
        return "userIndex";
    }

}
