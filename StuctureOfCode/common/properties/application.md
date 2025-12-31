```
server:
  port: 8080

spring:
  profiles:
    active: dev
  main:
    allow-circular-references: true
  datasource:
    druid:
      driver-class-name: ${sky.datasource.driver-class-name}
      url: jdbc:mysql://${sky.datasource.host}:${sky.datasource.port}/${sky.datasource.database}?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true
      username: ${sky.datasource.username}
      password: ${sky.datasource.password}
  redis:
    host: ${sky.redis.host}
    port: ${sky.redis.port}
    password: ${sky.redis.password}
    database: ${sky.redis.database}

mybatis:
  #mapper配置文件
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.sky.entity
  configuration:
    #开启驼峰命名
    map-underscore-to-camel-case: true

logging:
  level:
    com:
      sky:
        mapper: debug
        service: info
        controller: info

sky:
  jwt:
    # 设置jwt签名加密时使用的秘钥
    admin-secret-key: itcast
    # 设置jwt过期时间
    admin-ttl: 7200000
    # 设置前端传递过来的令牌名称
    admin-token-name: token
    user-secret-key: itheima
    user-ttl: 7200000
    user-token-name: authentication
  alioss:
    endpoint: ${sky.alioss.endpoint}
    access-key-id: ${sky.alioss.access-key-id}
    access-key-secret: ${sky.alioss.access-key-secret}
    bucket-name: ${sky.alioss.bucket-name}
  wechat:
    appid: ${sky.wechat.appid}
    secret: ${sky.wechat.secret}
    mchid : ${sky.wechat.mchid}
    mchSerialNo: ${sky.wechat.mchSerialNo}
    privateKeyFilePath: ${sky.wechat.privateKeyFilePath}
    apiV3Key: ${sky.wechat.apiV3Key}
    weChatPayCertFilePath: ${sky.wechat.weChatPayCertFilePath}
    notifyUrl: ${sky.wechat.notifyUrl}
    refundNotifyUrl: ${sky.wechat.refundNotifyUrl}
  shop:
    address: 北京市海淀区上地十街10号
  baidu:
    ak: your-ak
```

太好了，你这份 application.yml 已经非常接近“真实项目配置”了 👍
我按模块帮你解释每一段在干嘛、占什么角色。

⸻

1️⃣ 基本信息 & 环境
```
server:
  port: 8080

spring:
  profiles:
    active: dev
  main:
    allow-circular-references: true
```
- server.port: 8080
→ 启动后 HTTP 端口是 8080（访问：http://localhost:8080）
- spring.profiles.active: dev
→ 当前使用 dev 环境（开发环境）
→ 可以配合 application-dev.yml / application-prod.yml 做多环境配置
- allow-circular-references: true
→ 允许 Spring 里出现“循环依赖”的 Bean
→ 一般是为了兼容老代码，生产环境不太推荐长期依赖这个

⸻

2️⃣ 数据源（MySQL）& Redis
```
spring:
  datasource:
    druid:
      driver-class-name: ${sky.datasource.driver-class-name}
      url: jdbc:mysql://${sky.datasource.host}:${sky.datasource.port}/${sky.datasource.database}?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true
      username: ${sky.datasource.username}
      password: ${sky.datasource.password}
  redis:
    host: ${sky.redis.host}
    port: ${sky.redis.port}
    password: ${sky.redis.password}
    database: ${sky.redis.database}
```
🧠 关键点
- 使用 Druid 数据源（连接池）
- 所有关键参数（host / port / username / password）都没有写死，而是引用：
```
${sky.datasource.xxx}
${sky.redis.xxx}
```
👉 表示这些值会从“其他配置源”读取，例如：
- application-dev.yml
- 环境变量
- 配置中心

📌 好处：不在仓库里明文写数据库账号密码
→ 更安全，也方便多环境切换

⸻

3️⃣ MyBatis 配置
```
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.sky.entity
  configuration:
    map-underscore-to-camel-case: true
```
- mapper-locations
→ 告诉 MyBatis 去哪里找 *.xml mapper 文件
- type-aliases-package: com.sky.entity
→ 这个包下的实体类可以在 XML 中用简名引用，少写全路径
- map-underscore-to-camel-case: true
→ 开启下划线 → 驼峰 映射
- DB 字段：user_name
- Java 属性：userName
→ 自动映射，无需写 as userName

⸻

4️⃣ 日志级别
```
logging:
  level:
    com:
      sky:
        mapper: debug
        service: info
        controller: info
```
- com.sky.mapper: debug
→ Mapper 层日志更详细（SQL 打印）
- service / controller: info
→ 业务 & 控制层只打印关键信息

👉 这样可以在开发时看到 SQL，又不会被一堆无用日志淹没。

⸻

5️⃣ 自定义配置：sky.jwt
```
sky:
  jwt:
    admin-secret-key: itcast
    admin-ttl: 7200000
    admin-token-name: token
    user-secret-key: itheima
    user-ttl: 7200000
    user-token-name: authentication
```
对应你写的：
```
@ConfigurationProperties(prefix = "sky.jwt")
public class JwtProperties {
    private String adminSecretKey;
    private long adminTtl;
    private String adminTokenName;
    private String userSecretKey;
    private long userTtl;
    private String userTokenName;
}
```
👉 作用：
- 统一管理 JWT 签名密钥、过期时间、Header 名称
- 管理端 / 用户端 两套配置，方便安全隔离

⸻

6️⃣ 自定义配置：阿里云 OSS
```
  alioss:
    endpoint: ${sky.alioss.endpoint}
    access-key-id: ${sky.alioss.access-key-id}
    access-key-secret: ${sky.alioss.access-key-secret}
    bucket-name: ${sky.alioss.bucket-name}
```
对应：
```
@ConfigurationProperties(prefix = "sky.alioss")
public class AliOssProperties {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
}
```
- 这里同样用 ${sky.alioss.xxx} 占位符
- 真正的值通常放在 “更安全的地方”（比如别的 yml / 环境变量 / 配置中心）

👉 方便未来做环境切换 & 密钥轮换。

⸻

7️⃣ 自定义配置：微信小程序 & 微信支付
```
  wechat:
    appid: ${sky.wechat.appid}
    secret: ${sky.wechat.secret}
    mchid : ${sky.wechat.mchid}
    mchSerialNo: ${sky.wechat.mchSerialNo}
    privateKeyFilePath: ${sky.wechat.privateKeyFilePath}
    apiV3Key: ${sky.wechat.apiV3Key}
    weChatPayCertFilePath: ${sky.wechat.weChatPayCertFilePath}
    notifyUrl: ${sky.wechat.notifyUrl}
    refundNotifyUrl: ${sky.wechat.refundNotifyUrl}
```
对应：
```
@ConfigurationProperties(prefix = "sky.wechat")
public class WeChatProperties {
    private String appid;
    private String secret;
    private String mchid;
    private String mchSerialNo;
    private String privateKeyFilePath;
    private String apiV3Key;
    private String weChatPayCertFilePath;
    private String notifyUrl;
    private String refundNotifyUrl;
}
```
⚠️ 这里同样用了 ${sky.wechat.xxx} 占位符：
- 说明真正的敏感信息（appid、密钥、证书路径等）
不会写在这个文件里，而是从其他地方注入
- 这样做的目的：
- 避免把微信密钥提交到 Git
- 可以通过环境变量 / 外部配置覆盖

⸻

8️⃣ 其他自定义配置：店铺地址 & 百度地图
```
  shop:
    address: 北京市海淀区上地十街10号
  baidu:
    ak: your-ak
```
对应你在 OrderServiceImpl 里的代码：
```
@Value("${sky.shop.address}")
private String shopAddress;

@Value("${sky.baidu.ak}")
private String ak;
```
👉 用于：
- 调用百度地图 API
- 做配送范围计算（经纬度 → 路线规划）

⸻

✅ 总结一句话（你可以拿来当“解释 application.yml 的回答”）

- In this application.yml we configure the server port, active Spring profile,
    data sources with Druid, Redis, MyBatis mapping rules, and logging levels.

- Under the custom sky namespace we centralize JWT, AliOSS, WeChat,
    shop address and Baidu map settings,
    using property placeholders like ${sky.datasource.*}
    so that sensitive values can be injected
    from external environments instead of hard-coding them in the codebase.

⸻

如果你愿意，下一步我可以帮你做：

- 1️⃣ 画一张 配置结构总图（Mermaid）
- 2️⃣ 写一份 “Spring Boot 配置体系” 面试答案
- 3️⃣ 给你 sky.datasource.* / sky.redis.* 对应的 application-dev.yml 示例
- 4️⃣ 讲清楚：属性优先级（命令行 > 环境变量 > yml）
