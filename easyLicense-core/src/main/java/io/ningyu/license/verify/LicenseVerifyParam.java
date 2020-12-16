package io.ningyu.license.verify;

import io.ningyu.license.BaseLicenseParam;

import java.io.Serializable;

/**
 * License校验类需要的参数
 *
 * @author ningyu
 */
public class LicenseVerifyParam extends BaseLicenseParam implements Serializable {

    private static final long serialVersionUID = 2944688925668423875L;
    /**
     * 密钥库存储路径
     */
    private String publicKeysStorePath;

    public LicenseVerifyParam() {

    }

    public LicenseVerifyParam(String subject, String alias, String storePass, String licensePath, String publicKeysStorePath) {
        super(subject, alias, storePass, licensePath);
        this.publicKeysStorePath = publicKeysStorePath;
    }

    public String getPublicKeysStorePath() {
        return publicKeysStorePath;
    }

    public void setPublicKeysStorePath(String publicKeysStorePath) {
        this.publicKeysStorePath = publicKeysStorePath;
    }

    @Override
    public String toString() {
        return "LicenseVerifyParam{" +
                "subject='" + getSubject() + '\'' +
                ", alias='" + getAlias() + '\'' +
                ", storePass='" + getStorePass() + '\'' +
                ", licensePath='" + getLicensePath() + '\'' +
                ", publicKeysStorePath='" + publicKeysStorePath + '\'' +
                '}';
    }
}
