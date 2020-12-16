# EasyLicense

## 项目介绍
使用 `TrueLicense `生成和验证`License证书`（服务器许可）

## 开源许可
Apache License Version 2.0

## 技术依赖
* `Spring Boot`：项目基础架构
* `TrueLicense`：基于`Java`实现的生成和验证服务器许可的简单框架

## 环境依赖
* `JDK8+`

## 两个子项目说明

- `easyLicense-client`: 验证`License证书`demo
- `easyLicense-core`: 证书核心
- `easyLicense-server`: 生成`License证书`demo

## easyLicense-server

### 获取服务器硬件信息

1. 通过页面查看，用于授权时获取客户机器信息时使用

请求地址：`http://127.0.0.1:8080/index`

<img width="450" alt="截图1" src="/screenshot/1.jpg"/>

2. 通过RESTful接口

GET `http://127.0.0.1:8080/license/getServerInfos`

<img width="450" alt="截图2" src="/screenshot/2.jpg"/>

### 生成证书 ：

1. 通过页面

请求地址：`http://127.0.0.1:8080/gen`

<img width="450" alt="截图3" src="/screenshot/3.jpg"/>
<img width="450" alt="截图4" src="/screenshot/4.jpg"/>

2. 通过RESTful接口

POST http://127.0.0.1:8080/license/generate 

Content-Type: application/json

```json
{
    "subject": "SUBJECT",
    "privateAlias": "alias",
    "keyPass": "12345678",
    "storePass": "87654321",
    "privateKeysStorePath": "f2a67dbac33040018d9247b43357a3f8",
    "issuedTime": "2018-07-10 00:00:01",
    "expiryTime": "2020-12-04 18:59:59",
    "consumerType": "User",
    "consumerAmount": 1,
    "description": "这是证书描述信息",
    "licenseCheckModel": {
        "ipAddress": [
            "192.168.8.63",
            "10.0.5.22"
        ],
        "macAddress": [
            "a4:83:e7:5a:5e:64",
            "50-7B-9D-F9-18-41"
        ],
        "cpuSerial": [
            "BFEBFBFF000806EAXX"
        ],
        "mainBoardSerial": [
            "FVFZ2E0HL41020"
        ]
    }
}
```
ps. privateKeysStorePath需要先上传私钥文件获取文件id

### 上传私钥文件

1. 通过页面

请求地址：`http://127.0.0.1:8080/gen`

点击上传私钥

<img width="450" alt="截图3" src="/screenshot/3.jpg"/>

2. 通过RESTful接口：

POST http://127.0.0.1:8080/license/upload

Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW

<img width="450" alt="截图5" src="/screenshot/5.jpg"/>

### 下载License文件

1. 通过页面

请求地址：`http://127.0.0.1:8080/gen`

生成后点击下载证书

<img width="450" alt="截图4" src="/screenshot/4.jpg"/>

2. 通过RESTful接口：

GET http://127.0.0.1:8080/license/download?fileName=f9ecda0677a84e9590824b9d57937106

|属性名|属性值|
|  ----  | ----  |
|fileName|文件id|

## easyLicense-client

## 生成证书

### 生成命令
```shell script
keytool -genkeypair -keysize 1024 -validity 3650 -alias "alias" -keystore "privateKeys.keystore" -storepass "123456a" -keypass "123456a" -dname "CN=localhost, OU=localhost, O=localhost, L=SH, ST=SH, C=CN"
```

ps. `TrueLicense`对密码格式有要求，必须包含数字和字母

### 导出命令
```shell script
keytool -exportcert -alias "alias" -keystore "privateKeys.keystore" -storepass "123456a" -file "certfile.cer"
```

ps. `TrueLicense`对密码格式有要求，必须包含数字和字母

### 导入命令
```shell script
keytool -import -alias "alias" -file "certfile.cer" -keystore "publicCerts.keystore" -storepass "123456a"
```

ps. `TrueLicense`对密码格式有要求，必须包含数字和字母

## 集成到项目中使用

1. 引入easylicense-core依赖

```xml
<dependency>
	<groupId>io.ningyu</groupId>
	<artifactId>easyLicense-core</artifactId>
	<version>1.0.0</version>
</dependency>
```

2. 生成证书

```java
public class Demo() {

    /**
     * 生成证书
     */
    public static void generate() throws Exception {
        LicenseCreatorParam param = new LicenseCreatorParam();
        param.setSubject("主题");
        param.setAlias("别名");
        param.setKeypass("密码");
        param.setStorepass("密钥访问密码");
        param.setPrivateKeysStorePath("私钥路径");
        param.setIssuedTime("授权生效日期");
        param.setExpiryTime("授权过期日期");
        param.setConsumerType("固定User");
        param.setConsumerAmount(1);//固定1
        param.setDescription("许可证说明");
        param.setIpAddress("10.0.0.1");//多个ip使用英文逗号分割
        param.setMacAddress("a4:83:e7:5a:5e:64");//多个mac使用英文逗号分割
        param.setCpuSerial("BFEBFBFF000806EB");//多个cpu序列号使用英文逗号分割
        param.setMainBoardSerial("FVFZ2E0HL411");//多个主板序列号使用英文逗号分割
        LicenseCreator licenseCreator = new LicenseCreator(param);
        boolean result = licenseCreator.generateLicense();
    }
}
```

3. 安装证书

```java
public class Demo() {

    /**
     * 安装证书
     */
    public static void install() throws Exception {
        LicenseVerifyParam param = new LicenseVerifyParam();
        param.setSubject("subject");
        param.setAlias("alias");
        param.setStorePass("storePass");
        param.setLicensePath("licensePath");
        param.setPublicKeysStorePath("publicKeyPath");
        LicenseVerify.install(param);
    }
}
```

4. 验证证书

```java
public class Demo() {

    /**
     * 验证证书
     */
    public static boolean verify() {
        boolean verifyResult = LicenseVerify.verify();
        if(verifyResult){
            return true;
        }else{
            response.setCharacterEncoding("utf-8");
            Map<String,String> result = new HashMap<>(1);
            result.put("result","您的证书无效，请核查服务器是否取得授权或重新申请证书！");
            response.getWriter().write(JSON.toJSONString(result));
            return false;
        }
    }
}
```



