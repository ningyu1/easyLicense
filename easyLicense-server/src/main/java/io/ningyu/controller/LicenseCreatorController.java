package io.ningyu.controller;

import io.ningyu.license.LicenseCheckModel;
import io.ningyu.license.LicenseCreator;
import io.ningyu.license.LicenseCreatorParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 用于生成证书文件，不能放在给客户部署的代码里
 * @author ningyu
 */
@RestController
@RequestMapping("/license")
public class LicenseCreatorController {

    /**
     * 证书生成路径
     */
    @Value("${easy-license.licensePath}")
    private String licensePath;

    /**
     * 获取服务器硬件信息
     * @return ServerInfo
     */
    @RequestMapping(value = "/getServerInfos",produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public LicenseCheckModel getServerInfos() {
        LicenseCheckModel serverInfo = new LicenseCheckModel();
        serverInfo.copyTo();
        return serverInfo;
    }

    /**
     * 生成证书
     * @param param 生成证书需要的参数
     * <p>
     * {
     *     "subject": "subjectName",
     *     "privateAlias": "privateKey",
     *     "keyPass": "5T7Zz5Y0dJFcqTxvzkH5LDGJJSGMzQ",
     *     "storePass": "3538cef8e7",
     *     "licensePath": "xxx/cert/license.lic",
     *     "privateKeysStorePath": "xxx/cert/privateKeys.keystore",
     *     "issuedTime": "2020-04-26 14:48:12",
     *     "expiryTime": "2020-12-31 00:00:00",
     *     "consumerType": "User",
     *     "consumerAmount": 1,
     *     "description": "这是证书描述信息",
     *     "licenseCheckModel": {
     *         "ipAddress": [
     *             "192.168.245.1",
     *             "10.0.5.22"
     *         ],
     *         "macAddress": [
     *             "00-50-56-C0-00-01",
     *             "50-7B-9D-F9-18-41"
     *         ],
     *         "cpuSerial": "BFEBFBFF000406E3",
     *         "mainBoardSerial": "L1HF65E00X9"
     *     }
     * }
     * </p>
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @RequestMapping(value = "/generateLicense",produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public Map<String,Object> generateLicense(@RequestBody(required = true) LicenseCreatorParam param) {
        Map<String,Object> resultMap = new HashMap<>(2);

        if(StringUtils.isBlank(param.getLicensePath())){
            param.setLicensePath(licensePath);
        } else if (!param.getLicensePath().endsWith(".lic")){
            String fileName = param.getLicensePath().endsWith("/") ? param.getLicensePath() + "license.lic" : param.getLicensePath() + "/license.lic";
            param.setLicensePath(fileName);
        }

        LicenseCreator licenseCreator = new LicenseCreator(param);
        boolean result = licenseCreator.generateLicense();

        if(result){
            resultMap.put("result","ok");
            resultMap.put("msg",param);
        }else{
            resultMap.put("result","error");
            resultMap.put("msg","证书文件生成失败！");
        }

        return resultMap;
    }

}
