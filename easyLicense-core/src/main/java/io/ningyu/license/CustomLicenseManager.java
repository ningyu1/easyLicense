package io.ningyu.license;

import de.schlichtherle.license.*;
import de.schlichtherle.xml.GenericCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * 自定义LicenseManager，用于增加额外的服务器硬件信息校验
 *
 * @author ningyu
 */
public class CustomLicenseManager extends LicenseManager {

    private static Logger logger = LoggerFactory.getLogger(CustomLicenseManager.class);

    //XML编码
    private static final String XML_CHARSET = "UTF-8";
    //默认BUFSIZE
    private static final int DEFAULT_BUFSIZE = 8 * 1024;

    private static volatile LicenseCheckModel SERVER_INFOS = null;

    public CustomLicenseManager() {

    }

    public CustomLicenseManager(LicenseParam param) {
        super(param);
    }

    /**
     * 复写create方法
     *
     * @param content
     * @param notary
     * @return byte[]
     */
    @Override
    protected synchronized byte[] create(
            LicenseContent content,
            LicenseNotary notary)
            throws Exception {
        initialize(content);
        this.validateCreate(content);
        final GenericCertificate certificate = notary.sign(content);
        return getPrivacyGuard().cert2key(certificate);
    }

    /**
     * 复写install方法，其中validate方法调用本类中的validate方法，校验IP地址、Mac地址等其他信息
     *
     * @param key
     * @param notary
     * @return LicenseContent
     */
    @Override
    protected synchronized LicenseContent install(
            final byte[] key,
            final LicenseNotary notary)
            throws Exception {
        final GenericCertificate certificate = getPrivacyGuard().key2cert(key);

        notary.verify(certificate);
        final LicenseContent content = (LicenseContent) this.load(certificate.getEncoded());
        this.validate(content);
        setLicenseKey(key);
        setCertificate(certificate);

        return content;
    }

    /**
     * 复写verify方法，调用本类中的validate方法，校验IP地址、Mac地址等其他信息
     *
     * @param notary
     * @return LicenseContent
     */
    @Override
    protected synchronized LicenseContent verify(final LicenseNotary notary)
            throws Exception {
        GenericCertificate certificate = getCertificate();

        // Load license key from preferences,
        final byte[] key = getLicenseKey();
        if (null == key) {
            throw new NoLicenseInstalledException(getLicenseParam().getSubject());
        }

        certificate = getPrivacyGuard().key2cert(key);
        notary.verify(certificate);
        final LicenseContent content = (LicenseContent) this.load(certificate.getEncoded());
        this.validate(content);
        setCertificate(certificate);

        return content;
    }

    /**
     * 校验生成证书的参数信息
     *
     * @param content 证书正文
     */
    protected synchronized void validateCreate(final LicenseContent content)
            throws LicenseContentException {
        super.validate(content);
        final Date now = new Date();
        final Date notBefore = content.getNotBefore();
        final Date notAfter = content.getNotAfter();
        if (null != notAfter && now.after(notAfter)) {
            throw new LicenseContentException("证书失效时间不能早于当前时间");
        }
        if (null != notBefore && null != notAfter && notAfter.before(notBefore)) {
            throw new LicenseContentException("证书生效时间不能晚于证书失效时间");
        }
        final String consumerType = content.getConsumerType();
        if (null == consumerType) {
            throw new LicenseContentException("用户类型不能为空");
        }
    }


    /**
     * 复写validate方法，增加IP地址、Mac地址等其他信息校验
     *
     * @param content
     */
    @Override
    protected synchronized void validate(final LicenseContent content)
            throws LicenseContentException {
        //1. 首先调用父类的validate方法
        super.validate(content);

        //2. 然后校验自定义的License参数
        //License中可被允许的参数信息
        LicenseCheckModel expectedCheckModel = (LicenseCheckModel) content.getExtra();
        //当前服务器真实的参数信息
        LicenseCheckModel serverCheckModel = getServerInfos();

        if (expectedCheckModel != null && serverCheckModel != null) {
            //校验IP地址
            if (!check(expectedCheckModel.getIpAddress(), serverCheckModel.getIpAddress())) {
                throw new LicenseContentException("当前服务器的IP没在授权范围内");
            }

            //校验Mac地址
            if (!check(expectedCheckModel.getMacAddress(), serverCheckModel.getMacAddress())) {
                throw new LicenseContentException("当前服务器的Mac地址没在授权范围内");
            }

            //校验主板序列号
            if (!check(expectedCheckModel.getMainBoardSerial(), serverCheckModel.getMainBoardSerial())) {
                throw new LicenseContentException("当前服务器的主板序列号没在授权范围内");
            }

            //校验CPU序列号
            if (!check(expectedCheckModel.getCpuSerial(), serverCheckModel.getCpuSerial())) {
                throw new LicenseContentException("当前服务器的CPU序列号没在授权范围内");
            }
        } else {
            throw new LicenseContentException("没有读到有效的授权信息");
        }
    }


    /**
     * 重写XMLDecoder解析XML
     *
     * @param encoded XML类型字符串
     * @return java.lang.Object
     */
    private Object load(String encoded) {
        BufferedInputStream inputStream = null;
        XMLDecoder decoder = null;
        try {
            inputStream = new BufferedInputStream(new ByteArrayInputStream(encoded.getBytes(XML_CHARSET)));

            decoder = new XMLDecoder(new BufferedInputStream(inputStream, DEFAULT_BUFSIZE), null, null);

            return decoder.readObject();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            try {
                if (decoder != null) {
                    decoder.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                logger.error("XMLDecoder解析XML失败", e);
            }
        }

        return null;
    }

    /**
     * 获取当前服务器需要额外校验的License参数
     *
     * @return LicenseCheckModel
     */
    private LicenseCheckModel getServerInfos() {
        if(SERVER_INFOS == null){
            synchronized (CustomLicenseManager.class){
                if(SERVER_INFOS == null){
                    SERVER_INFOS = new LicenseCheckModel();
                    SERVER_INFOS.copyTo();
                }
            }
        }
        return SERVER_INFOS;
    }

    /**
     * 校验当前服务器的IP/Mac地址是否在可被允许的IP范围内<br/>
     * 如果存在IP在可被允许的IP/Mac地址范围内，则返回true
     *
     * @param expectedList
     * @param serverList
     * @return boolean
     */
    private boolean check(List<String> expectedList, List<String> serverList) {
        if (expectedList != null && expectedList.size() > 0) {
            if (serverList != null && serverList.size() > 0) {
                for (String expected : expectedList) {
                    //通配符*不控制
                    if (expected.equals("*")) {
                        return true;
                    }
                    if (serverList.contains(expected.trim())) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            //证书中没有控制信息也作为不控制处理
            return true;
        }
    }

}
