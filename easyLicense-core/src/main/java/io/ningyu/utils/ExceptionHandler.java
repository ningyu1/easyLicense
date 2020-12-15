package io.ningyu.utils;

import de.schlichtherle.license.IllegalPasswordException;
import de.schlichtherle.license.LicenseContentException;
import de.schlichtherle.license.LicenseNotaryException;
import de.schlichtherle.license.NoLicenseInstalledException;

import javax.crypto.BadPaddingException;

/**
 * 异常处理器
 *
 * @author ningyu
 */
public class ExceptionHandler {

    public static String handle(Exception e) {
        String defaultMessage = "您的证书无效，请核查服务器是否取得授权或重新申请证书！";
        if (e instanceof BadPaddingException
                || e instanceof IllegalPasswordException
                || e instanceof LicenseContentException
                || e instanceof LicenseNotaryException
                || e instanceof NoLicenseInstalledException) {
            return defaultMessage;
        }
        switch (e.getMessage()) {
            case "exc.licenseHasExpired":
                return "许可证已过期";
            case "exc.licenseIsNotYetValid":
                return "许可证尚未生效";
            default :
                if (e.getMessage().startsWith("exc.")) {
                    return defaultMessage;
                } else {
                    return e.getMessage();
                }
        }
    }
}
