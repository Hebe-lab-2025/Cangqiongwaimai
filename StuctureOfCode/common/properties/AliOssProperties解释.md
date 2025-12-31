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

很好，这两个注解放在一起，确实非常容易混淆。我帮你用最简单方式讲清 👇

⸻

✅ 这段代码在做什么？
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
👉 作用：
把 yml 中的 sky.alioss. 配置，自动绑定到这个类里，并把这个类注册为 Spring Bean*

⸻

```🧩 @ConfigurationProperties —— 负责“绑定配置”```

📌 作用

把配置文件中的属性 → 注入到 Java 对象

例如你的 application.yml
```
sky:
  alioss:
    endpoint: xxx
    access-key-id: aaa
    access-key-secret: bbb
    bucket-name: demo-bucket
```
🔗 由 prefix = sky.alioss 进行绑定

📌 绑定规则
| 配置 | 作用 |
|------|------|
| `notifyUrl` | 支付成功回调地址 |
| `refundNotifyUrl` | 退款结果回调地址 |


👉 自动支持 驼峰 & 短横线转换

⸻

🧭 @Component —— 负责“交给 Spring 管理”

📌 作用

把这个类注册为 Spring 容器中的一个 Bean

也就是：
- 能被自动注入
- 能被其他类直接使用
- 生命周期交给 Spring

示例：
```
@Autowired
private AliOssProperties aliOssProperties;
```

⸻

🎯 两者关系一句话

注解	作用
```
@ConfigurationProperties	负责把 yml 配置绑定到对象
@Component	负责把对象交给 Spring 管理
```
👉 合作关系：
```
yml 配置  →  ConfigurationProperties 绑定
Java Bean →  Component 注册成 Bean
```

⸻

🧠 打个比方（最形象）

东西	像什么
```
@ConfigurationProperties	USB 数据线（数据进去）
@Component	插座（供电、能被系统识别）

没有 @Component 👉 不会成为 Bean
没有 @ConfigurationProperties 👉 不会自动绑定配置
```
⸻

🧪 面试版回答（背诵用）
```
- @ConfigurationProperties is used to map configuration properties from application.yml
    to a Java object based on a prefix.
- @Component registers this object as a Spring Bean
    so that it can be injected and used in other components.
- The first one solves “data binding”,
    and the second solves “lifecycle management”.

⸻

🚀 如果你愿意，我还能帮你：
- 🧭 画 AliOssProperties 配置绑定流程图
- 🛠 写 Aliyun OSS 上传工具类
- 🔐 写 SpringBoot OSS 上传接口
- 🧪 出 8 道面试题（properties & @Value 区别）
- ⚠️ 讲清楚 为什么用 ConfigurationProperties 而不是 @Value

只要告诉我一声：

👉 “来 OSS 工具类” / “来流程图” / “来面试题”
