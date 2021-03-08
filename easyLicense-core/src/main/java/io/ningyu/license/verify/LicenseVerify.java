package io.ningyu.license.verify;

import de.schlichtherle.license.*;
import io.ningyu.license.CustomKeyStoreParam;
import io.ningyu.license.CustomLicenseManager;
import io.ningyu.license.LicenseCheckModel;
import io.ningyu.license.LicenseManagerHolder;
import io.ningyu.utils.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * License校验类
 *
 * @author ningyu
 */
public class LicenseVerify {

    private static Logger logger = LoggerFactory.getLogger(LicenseVerify.class);

    /**
     * 安装License证书
     * @author ningyu
     */
    public static synchronized LicenseContent install(LicenseVerifyParam param) {
        LicenseContent result = null;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //1. 安装证书
        try{
            LicenseManager licenseManager = LicenseManagerHolder.getInstance(initLicenseParam(param));
            licenseManager.uninstall();

            result = licenseManager.install(new File(param.getLicensePath()));
            logger.info(MessageFormat.format("证书安装成功，证书有效期：{0} - {1}",format.format(result.getNotBefore()),format.format(result.getNotAfter())));
        }catch (Exception e){
            //这里不输出失败的具体原因
            logger.error("证书安装失败！原因：{}", ExceptionHandler.handle(e));
        }

        return result;
    }

    /**
     * 校验License证书
     * @author ningyu
     * @return boolean
     */
    public static boolean verify() {
        LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //2. 校验证书
        try {
            LicenseContent licenseContent = licenseManager.verify();
            logger.info(MessageFormat.format("证书校验通过，许可证有效期：{0} - {1}",format.format(licenseContent.getNotBefore()),format.format(licenseContent.getNotAfter())));
            return true;
        }catch (Exception e){
            //这里不输出失败的具体原因
            logger.error("证书校验失败！原因：{}", ExceptionHandler.handle(e));
            return false;
        }
    }

    public static Map info() {
        CustomLicenseManager licenseManager = (CustomLicenseManager) LicenseManagerHolder.getInstance(null);
        Map info = new HashMap();
        try {
            final LicenseContent content = licenseManager.getContent();
            //主题
            info.put("Subject", content.getSubject());
            //许可时间
            info.put("Issued", content.getIssued());
            //许可开始时间
            info.put("startDate", content.getNotBefore());
            //许可结束时间
            info.put("endDate", content.getNotAfter());
            //许可类型 User
            info.put("ConsumerType", content.getConsumerType());
            //许可数量 1
            info.put("ConsumerAmount", content.getConsumerAmount());
            //扩展属性
            LicenseCheckModel expectedCheckModel = (LicenseCheckModel) content.getExtra();
            info.put("IpAddress", String.join(",", expectedCheckModel.getIpAddress()));
            info.put("MacAddress", String.join(",", expectedCheckModel.getMacAddress()));
            info.put("CpuSerial", String.join(",", expectedCheckModel.getCpuSerial()));
            info.put("MainBoardSerial", String.join(",", expectedCheckModel.getMainBoardSerial()));
        } catch (Exception e) {
            logger.error("证书读取失败！原因：{}", ExceptionHandler.handle(e));
            info.put("result", ExceptionHandler.handle(e));
        }
        return info;
    }

    /**
     * 初始化证书生成参数
     * @author ningyu
     * @return de.schlichtherle.license.LicenseParam
     */
    private static LicenseParam initLicenseParam(LicenseVerifyParam param){
        Preferences preferences = Preferences.userNodeForPackage(LicenseVerify.class);

        CipherParam cipherParam = new DefaultCipherParam(param.getStorePass());

        KeyStoreParam publicStoreParam = new CustomKeyStoreParam(LicenseVerify.class
                ,param.getPublicKeysStorePath()
                ,param.getAlias()
                ,param.getStorePass()
                ,null);

        return new DefaultLicenseParam(param.getSubject()
                ,preferences
                ,publicStoreParam
                ,cipherParam);
    }

}
