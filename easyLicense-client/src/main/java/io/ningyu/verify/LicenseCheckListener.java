package io.ningyu.verify;

import io.ningyu.config.EasyLicenseConfig;
import io.ningyu.license.verify.LicenseVerify;
import io.ningyu.license.verify.LicenseVerifyParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 在项目启动时安装证书
 *
 * @author ningyu
 */
@Component
public class LicenseCheckListener implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger logger = LogManager.getLogger(LicenseCheckListener.class);

    @Autowired
    private EasyLicenseConfig easyLicenseConfig;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //root application context 没有parent
        ApplicationContext context = event.getApplicationContext().getParent();
        if(context == null){
            if(StringUtils.isNotBlank(easyLicenseConfig.getLicensePath())){
                logger.info("++++++++ 开始安装证书 ++++++++");
                LicenseVerifyParam param = new LicenseVerifyParam();
                param.setSubject(easyLicenseConfig.getSubject());
                param.setAlias(easyLicenseConfig.getPublicAlias());
                param.setStorePass(easyLicenseConfig.getStorePass());
                param.setLicensePath(easyLicenseConfig.getLicensePath());
                param.setPublicKeysStorePath(easyLicenseConfig.getPublicKeysStorePath());
                //安装证书
                LicenseVerify.install(param);
                logger.info("++++++++ 证书安装结束 ++++++++");
            }
        }
    }
}
