package io.ningyu.controller;

import io.ningyu.config.EasyLicenseConfig;
import io.ningyu.license.verify.LicenseVerify;
import io.ningyu.license.verify.LicenseVerifyParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 模拟登录
 *
 * @author ningyu
 */
@RestController
public class TestController {

    private static Logger logger = LogManager.getLogger(TestController.class);

    @Autowired
    private EasyLicenseConfig easyLicenseConfig;

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
     * @return java.util.Map<java.lang.String, java.lang.Object>
     */
    @GetMapping(value = "/info", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public Map<String, Object> info() {
        return LicenseVerify.info();
    }

    @GetMapping("/verify")
    @ResponseBody
    public Map<String, Object> verify() {
        Map<String, Object> result = new HashMap<>();
        boolean verifyResult = LicenseVerify.verify();
        if (verifyResult) {
            result.put("result", "success");
        } else {
            result.put("result", "您的证书无效，请核查服务器是否取得授权或重新申请证书！");
        }
        return result;
    }

    @PostMapping("/upgrade")
    @ResponseBody
    public Map<String, Object> upgradeLicense(@RequestParam("file") MultipartFile file) {
        Map<String,Object> resultMap = new HashMap<>(2);
        if (file.isEmpty()) {
            resultMap.put("result","error");
            resultMap.put("msg","license上传失败！license更新失败！");
            return resultMap;
        }
        String fileName = UUID.randomUUID().toString().replaceAll("-","");
        String filePath = easyLicenseConfig.getTmp();
        File dest = new File(filePath + fileName);
        try {
            file.transferTo(dest);
            logger.info("++++++++ 开始更新证书 ++++++++");
            LicenseVerifyParam param = new LicenseVerifyParam();
            param.setSubject(easyLicenseConfig.getSubject());
            param.setAlias(easyLicenseConfig.getPublicAlias());
            param.setStorePass(easyLicenseConfig.getStorePass());
            param.setLicensePath(filePath + fileName);
            param.setPublicKeysStorePath(easyLicenseConfig.getPublicKeysStorePath());
            //安装证书
            LicenseVerify.install(param);
            logger.info("++++++++ 证书更新结束 ++++++++");
            resultMap.put("result","success");
            resultMap.put("msg","license更新成功！");
        } catch (IOException e) {
            logger.error(e.toString(), e);
            resultMap.put("result","error");
            resultMap.put("msg","license上传失败！license更新失败！");
        }
        return resultMap;
    }

}
