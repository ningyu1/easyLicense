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

## module说明

- `easyLicense-client`: 验证`License证书`demo
- `easyLicense-core`: 证书核心
- `easyLicense-server`: 生成`License证书`demo
- `cert`: 提供测试使用的证书文件

### 测试证书说明

```markdown
cert/certfile.cer: 证书文件
cert/privateKeys.keystore: 私钥
cert/publicCerts.keystore: 公钥
cert/license.lic: 文件内容如下
{
	"subject": "test",
	"alias": "privatekey",
	"keyPass": "123456a",
	"storePass": "123456a",
	"privateKeysStorePath": "privateKeys.keystore",
	"issuedTime": "2020-07-10 00:00:01",
	"expiryTime": "2021-12-04 18:59:59",
	"consumerType": "User",
	"consumerAmount": 1,
	"description": "这是证书描述信息",
	"licenseCheckModel": {
		"ipAddress": ["172.31.0.13", "192.168.8.*"],
		"macAddress": ["a4:83:e7:5a:5e:64", "50-7B-9D-F9-18-41"],
		"cpuSerial": ["*"],
		"mainBoardSerial": ["FVFZ2E0HL410"]
	}
}
```

## 许可证可控制范围

除了TrueLicense可控制的常规字段【主题、生效时间、过期时间、consumerType、consumerAmount】以外扩展了下面控制

* 可控制机器IP地址，支持IPv4，`*`代表不控制，可控制ip段 例如：10.5.10.* -> 允许10.5.10.以内任意ip
* 可控制机器MAC地址，`*`代表不控制
* 可控制cpu序列号，`*`代表不控制
* 可控制主板序列号，`*`代表不控制

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

* client是客户端使用的demo，修改`application.properties`
```properties
# 证书subject
easy-license.subject=test
# 公钥别称
easy-license.publicAlias=alias
# 访问公钥库的密码，`TrueLicense`对密码格式有要求，必须包含数字和字母
easy-license.storePass=123456a
# 证书位置
easy-license.licensePath=/Volumes/D/javatools/workspace/github/easyLicense/cert/license.lic
# 密钥库存储路径
easy-license.publicKeysStorePath=/Volumes/D/javatools/workspace/github/easyLicense/cert/publicCerts.keystore
```
* 运行`ClientApplication`启动客户端程序
* 启动时进行证书安装可以查看：`io.ningyu.verify.LicenseCheckListener`
* springmvc拦截器中进行证书校验查看：`io.ningyu.verify.LicenseCheckInterceptor`


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

## 使用keytool生成密钥对

以下命令在dos命令行执行，注意当前执行目录，最后生成的密钥对即在该目录下：

1. 首先要用KeyTool工具来生成私匙库：（-alias别名 –validity 3650表示10年有效）
```shell script
keytool -genkey -keysize 1024 -alias privatekey -keystore privateKeys.keystore -storepass "123456a" -keypass "123456a" -validity 3650 -dname "CN=localhost, OU=localhost, O=localhost, L=SH, ST=SH, C=CN"
```
ps.`-keysize`默认为2048，应该使用`-keysize 1024`，使用默认值太大会出现如下错误：
```
java.security.InvalidKeyException: The security strength of SHA-1 digest algorithm is not sufficient for this key size
```

2. 然后把私匙库内的公匙导出到一个文件当中：
```shell script
keytool -export -alias privatekey -file certfile.cer -keystore privateKeys.keystore -storepass "123456a"
```

3. 然后再把这个证书文件导入到公匙库：
```shell script
keytool -import -alias publiccert -file certfile.cer -keystore publicCerts.keystore -storepass "123456a"
```

4. 最后生成文件privateKeys.store、publicCerts.store拷贝出来备用。

ps. `TrueLicense`对密码格式有要求，必须包含数字和字母



