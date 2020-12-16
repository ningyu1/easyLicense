package io.ningyu.license;

import java.io.Serializable;

/**
 * License基类
 *
 * @author ningyu
 */
public class BaseLicenseParam implements Serializable {

    private static final long serialVersionUID = 4365403351071789571L;

    public BaseLicenseParam() {
    }

    public BaseLicenseParam(String subject, String alias, String storePass, String licensePath) {
        this.subject = subject;
        this.alias = alias;
        this.storePass = storePass;
        this.licensePath = licensePath;
    }

    /**
     * 证书subject
     */
    private String subject;

    /**
     * 密钥别称
     */
    private String alias;

    /**
     * 访问密钥库的密码
     */
    private String storePass;

    /**
     * 证书路径
     */
    private String licensePath;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getStorePass() {
        return storePass;
    }

    public void setStorePass(String storePass) {
        this.storePass = storePass;
    }

    public String getLicensePath() {
        return licensePath;
    }

    public void setLicensePath(String licensePath) {
        this.licensePath = licensePath;
    }

    @Override
    public String toString() {
        return "BaseLicenseParam{" +
                "subject='" + subject + '\'' +
                ", alias='" + alias + '\'' +
                ", storePass='" + storePass + '\'' +
                ", licensePath='" + licensePath + '\'' +
                '}';
    }
}
