package io.ningyu.controller;

import io.ningyu.config.EasyLicenseConfig;
import io.ningyu.license.LicenseCheckModel;
import io.ningyu.license.LicenseCreator;
import io.ningyu.license.LicenseCreatorParam;
import io.ningyu.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * 用于生成证书文件，不能放在给客户部署的代码里
 * @author ningyu
 */
@RestController
@RequestMapping("/license")
public class LicenseCreatorController {

    private static final Logger log = LoggerFactory.getLogger(LicenseCreatorController.class);

    /**
     * 系统基础配置
     */
    @Autowired
    EasyLicenseConfig easyLicenseConfig;

    /**
     * 获取服务器硬件信息
     * @return ServerInfo
     */
    @RequestMapping(value = "/server_info",produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
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
     *     "privateKeysStorePath": "上传后的文件id",
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
    @RequestMapping(value = "/generate",produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public Map<String,Object> generate(@RequestBody(required = true) LicenseCreatorParam param) {
        Map<String,Object> resultMap = new HashMap<>(2);

        //设置license文件
        StringBuffer licensePath = new StringBuffer();
        licensePath.append(easyLicenseConfig.getTmp());
        String licenseName = "license-" + UUID.randomUUID().toString().replaceAll("-","") + ".lic";
        licensePath.append(licenseName);
        param.setLicensePath(licensePath.toString());

        //设置私钥文件
        StringBuffer privateKeysStorePath = new StringBuffer();
        privateKeysStorePath.append(easyLicenseConfig.getTmp());
        privateKeysStorePath.append(param.getPrivateKeysStorePath());
        param.setPrivateKeysStorePath(privateKeysStorePath.toString());

        LicenseCreator licenseCreator = new LicenseCreator(param);
        boolean result = licenseCreator.generateLicense();

        if(result){
            //设置返回值
            param.setLicensePath(licenseName);
            param.setPrivateKeysStorePath(null);
            resultMap.put("result","ok");
            resultMap.put("msg",param);
        }else{
            resultMap.put("result","error");
            resultMap.put("msg","证书文件生成失败！");
        }

        return resultMap;
    }

    /**
     * 私钥上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ResponseBody
    public Map<String,Object> upload(@RequestParam("file") MultipartFile file) {
        Map<String,Object> resultMap = new HashMap<>(2);
        if (file.isEmpty()) {
            resultMap.put("result","error");
            resultMap.put("msg","上传失败！");
            return resultMap;
        }
        String fileName = UUID.randomUUID().toString().replaceAll("-","");
        String filePath = easyLicenseConfig.getTmp();
        File dest = new File(filePath + fileName);
        try {
            file.transferTo(dest);
            resultMap.put("result","ok");
            resultMap.put("msg", fileName);
            return resultMap;
        } catch (IOException e) {
            log.error(e.toString(), e);
            resultMap.put("result","error");
            resultMap.put("msg","上传失败！");
        }
        return resultMap;
    }

    /**
     * 证书下载
     *
     * @param filename
     * @throws IOException
     */
    @GetMapping(value = "download")
    public void download(@RequestParam("fileName") String filename) throws IOException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        // 设置信息给客户端不解析
        String type = new MimetypesFileTypeMap().getContentType("license.lic");
        // 设置contenttype，即告诉客户端所发送的数据属于什么类型
        response.setHeader("Content-type",type);
        // 设置编码
        String encode = new String("license.lic".getBytes("utf-8"), "iso-8859-1");
        // 设置扩展头，当Content-Type 的类型为要下载的类型时 , 这个信息头会告诉浏览器这个文件的名字和类型。
        response.setHeader("Content-Disposition", "attachment;filename=" + encode);

        StringBuffer licensePath = new StringBuffer();
        licensePath.append(easyLicenseConfig.getTmp());
        licensePath.append(filename);
        FileUtil.download(licensePath.toString(), response);
    }

}
