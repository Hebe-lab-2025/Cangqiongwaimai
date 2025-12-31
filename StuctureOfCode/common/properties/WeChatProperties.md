```
package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.jwt")
@Data
public class JwtProperties {

    /**
     * 管理端员工生成jwt令牌相关配置
     */
    private String adminSecretKey;
    private long adminTtl;
    private String adminTokenName;

    /**
     * 用户端微信用户生成jwt令牌相关配置
     */
    private String userSecretKey;
    private long userTtl;
    private String userTokenName;

}
```

# 很好，这个类和刚才的 JwtProperties 一样，属于 配置绑定类，
# 只是这里专门用于 微信支付 / 小程序相关配置 👍
# 我帮你拆开说明：每一行作用、yml 怎么写、在业务里如何使用、为什么这么设计。

⸻

✅ 这个类是做什么的？

#  把 Spring Boot 配置文件里的：

```sky.wechat.*```

统一映射为一个 Java 对象，用于：
- 小程序登录
- 微信支付
- 退款回调
- 证书校验
- API v3 调用签名

# 📌 核心目的：
把敏感配置从业务代码中剥离出来（安全 + 可维护）

⸻

🧩 注解逐行解释

#  @Component

# @Component

- 交给 Spring 容器管理
- 允许被注入：

```
@Autowired
private WeChatProperties weChatProperties;
```

⸻

#  @ConfigurationProperties

```@ConfigurationProperties(prefix = "sky.wechat")```

👉 绑定 yml 配置：

```sky.wechat.xxx```

Spring Boot 自动完成属性映射。

⸻

#  @Data

# Lombok 自动生成：
- getter / setter
- toString
- equals & hashCode

你不需要手写这些样板代码。

⸻

🧠 字段含义（非常重要）
```
private String appid;              // 小程序 appid
private String secret;             // 小程序 app secret
private String mchid;              // 商户号 MchID
private String mchSerialNo;        // 商户 API 证书序列号
private String privateKeyFilePath; // 商户私钥
private String apiV3Key;           // API v3 加密 key
private String weChatPayCertFilePath; // 平台证书
private String notifyUrl;          // 支付回调
private String refundNotifyUrl;    // 退款回调
```

⸻

📌 它们在微信生态里的用途

| 字段 | 用途 |
|------|------|
| `appid` | 标识你的微信小程序 |
| `secret` | 小程序登录 `code2session` 使用 |
| `mchid` | 微信支付商户号 |
| `mchSerialNo` | 证书序列号，用于签名 |
| `privateKeyFilePath` | 商户私钥路径，用于 RSA 签名 |
| `apiV3Key` | API v3 回调解密 key |
| `weChatPayCertFilePath` | 微信支付平台证书路径 |
| `notifyUrl` | 支付结果回调地址 |
| `refundNotifyUrl` | 退款结果回调地址 |


👉 这些用于 WeChat Pay V3 标准流程

⸻

🗂 配套 application.yml 写法（帮你补全）
```
sky:
  wechat:
    appid: wx1234567890abcdef
    secret: xxxxxxxx
    mchid: 1234567890
    mch-serial-no: ABCDEF1234567890
    private-key-file-path: /data/apiclient_key.pem
    api-v3-key: 1234567890abcdef1234567890abcdef
    we-chat-pay-cert-file-path: /data/wechatpay.pem
    notify-url: https://yourdomain.com/pay/notify
    refund-notify-url: https://yourdomain.com/pay/refund/notify
```
📌 注意：
- yml → 短横线命名
- Java → 驼峰命名
- Spring Boot 自动绑定 ✔

⸻

🚀 在业务中怎么用？

微信小程序登录
```
String url = "https://api.weixin.qq.com/sns/jscode2session?appid="
        + weChatProperties.getAppid()
        + "&secret=" + weChatProperties.getSecret()
        + "&js_code=" + code 
        + "&grant_type=authorization_code";
```

⸻

微信支付签名
```
weChatPayUtil.init(
    weChatProperties.getMchid(),
    weChatProperties.getMchSerialNo(),
    weChatProperties.getPrivateKeyFilePath(),
    weChatProperties.getApiV3Key()
);
```

⸻

🔐 为什么必须用配置文件而不是硬编码？

因为：
- appId / secret / key 都是敏感信息
- 生产环境和测试环境不同
- 证书路径在服务器不一样
- 方便轮换密钥
- 不能提交到 GitHub

👉 企业真实安全规范要求：

❌ 不能写成：

```String appid = "wx123";```

✔ 必须：
- yml
- KMS
- Nacos
- Vault
- 环境变量

⸻

🎯 面试官问：这一段是干嘛的？

你可以这样回答：

- We use @ConfigurationProperties to bind WeChat Mini-Program 
      and WeChat Pay configuration into a strongly typed POJO.
- It contains appid, secret, merchant information,
      API v3 keys and callback URLs.

- This avoids hardcoded sensitive credentials in code,
      supports multiple environments and simplifies integration with WeChat Pay.

👍 非常标准的答案。

⸻

🙌 如果你愿意，我还能继续帮你：

请选择 👇

- 1️⃣ 写完整 WeChat 登录接口（code → openid）
- 2️⃣ 写 微信支付下单接口（JSAPI）
- 3️⃣ 写 支付回调 controller
- 4️⃣ 写 退款接口
- 5️⃣ 讲解：
	- •	V2 vs V3 API 区别
	- •	RSA 签名机制
	- •	回调验签流程
