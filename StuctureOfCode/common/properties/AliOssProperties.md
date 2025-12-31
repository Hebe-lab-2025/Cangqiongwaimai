```
package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.alioss")
@Data
public class AliOssProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

}

```
很好，这一块你已经在看properties 配置绑定模块了 👍
我帮你把图里圈出的内容讲清楚——它是干嘛的、为什么要这么设计、怎么用。

⸻

✅ 这个 properties 包是干什么的？

-  专门存放“配置参数映射类”
- 	项目中的配置放在：
- 	application.yml
- 	application-dev.yml
- 	application-prod.yml
- 	这些配置需要注入到 Java 对象里使用

所以定义一个专门的包：

```com.sky.properties```

把所有 配置映射类统一管理，例如：

| 类名 | 用途 |
|------|------|
| `AliOssProperties` | 阿里云 OSS 存储配置 |
| `JwtProperties` | JWT 令牌相关配置 |
| `WeChatProperties` | 微信相关配置 |


作用类似于：

“把 yml 中的配置文件内容 → 映射为 Java 对象”

⸻

✅ 你当前 AliOssProperties 代码含义是什么？
```
@Component
@ConfigurationProperties(prefix = "sky.alioss")
@Data
public class AliOssProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
}
```
逐行解释 👇

① ```@Component```

-  把这个类交给 Spring 容器管理
-  可以像普通 Bean 一样 @Autowired

⸻

② ```@ConfigurationProperties(prefix = "sky.alioss")```

-  作用：把 yml 的配置绑定到这个类上

比如 yml 里：
```
sky:
  alioss:
    endpoint: xxx
    accessKeyId: xxx
    accessKeySecret: xxx
    bucketName: xxx
```
Spring 会自动映射到：

```
yml key	Java field
endpoint	endpoint
accessKeyId	accessKeyId
accessKeySecret	accessKeySecret
bucketName	bucketName
```

⸻

③ @Data（Lombok）

-  自动生成以下方法：
- 	getter
- 	setter
- 	toString
- 	equals / hashCode
- 	构造函数

不用手写，非常方便。

⸻

🔎 这个类为什么只放字段、不写逻辑？

因为它就是一个：

纯粹的“配置承载对象”(Configuration Holder)

特点是：
- 	只存数据
- 	不写业务逻辑
- 	不做计算
- 	和 DTO/Entity 分开

-  这叫做 职责单一性（SRP）

⸻

🧠 怎么在业务代码中使用它？

直接注入：
```
@Autowired
private AliOssProperties aliOssProperties;
```
然后就能这样用：
```
aliOssClient.init(
    aliOssProperties.getEndpoint(),
    aliOssProperties.getAccessKeyId(),
    aliOssProperties.getAccessKeySecret(),
    aliOssProperties.getBucketName()
);
```
好处：
- 	❌ 不要硬编码 key/secret
- 	❌ 不要写死 endpoint
- 	❌ 不要散落到代码里

-  支持多环境
-  更安全
-  更易维护

⸻

🧩 总结一句话（面试可背）

We use a dedicated properties package to hold configuration binding classes.
Each class is annotated with @ConfigurationProperties to map YAML configuration into strongly-typed Java objects.
This avoids hard-coding values, supports environment switching, and keeps configuration concerns separate from business logic.

⸻

-  如果你愿意，下一步我可以帮你：
- 	🧭 画一张 配置绑定 UML 图
- 	✅ 写出 application.yml 对应内容
- 	🛠 手把手写：
- 	AliOSS 上传工具类
- 	Controller 上传接口
- 	🔐 讲解：
- 	为什么 key 不能硬编码
- 	如何把敏感配置放 Nacos / KMS
- 	🧪 出 5 道 SpringBoot @ConfigurationProperties 面试题

