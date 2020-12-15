package io.ningyu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 *
 * @author ningyu
 */
@Component
@ConfigurationProperties(prefix = "easy-license")
public class EasyLicenseConfig {
    /** 项目名称 */
    private String name;

    /** 版本 */
    private String version;

    /** 版权年份 */
    private String copyrightYear;

    /**
     * 临时文件文职
     */
    @Value("${easy-license.tmp}")
    private String tmp;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getCopyrightYear()
    {
        return copyrightYear;
    }

    public void setCopyrightYear(String copyrightYear)
    {
        this.copyrightYear = copyrightYear;
    }

    public String getTmp() {
        return this.tmp.endsWith("/") ? tmp : (tmp + "/");
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }
}
