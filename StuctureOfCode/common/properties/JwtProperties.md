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
很好，这个类你已经写得非常标准了 👍
我帮你把它逐行拆开解释，并讲清楚它在项目里的作用、怎么用、yml 怎么写、为什么这样设计。

⸻

✅ 这个类整体是干什么的？

- 读取 application.yml 里的 JWT 配置，并封装成一个 Java 对象

用途包括：
- 生成 token
- 解析 token
- 设置过期时间
- 管理不同端（管理员端、用户端）的密钥

📌 好处
- 不把 secret key 写死在代码里（安全）
- 不用硬编码 TTL（灵活）
- yml 改配置，代码不改即可生效

⸻

🧩 逐行解释

```@Component```

- 交给 Spring 管理，允许 @Autowired 注入

⸻


```@ConfigurationProperties(prefix = "sky.jwt")```

- 绑定 yml 前缀为：

```sky.jwt.***```


⸻


@Data

- Lombok 自动生成：
- getter / setter
- equals
- hashCode
- toString
- 构造方法

不用你手写 ✔

⸻

🧠 字段解释

- 管理端（员工后台）
```
private String adminSecretKey;
private long adminTtl;
private String adminTokenName;
```

| 字段 | 含义 |
|------|------|
| `adminSecretKey` | 签名用密钥（管理员） |
| `adminTtl` | token 过期时间（管理员） |
| `adminTokenName` | HTTP Header 中 token 的字段名 |


常见 header：
```
Authorization
token
admin-token
```

⸻

- 用户端（小程序/手机 App）
```
private String userSecretKey;
private long userTtl;
private String userTokenName;
```
📌 为什么要分开？

| 管理端 | 用户端 |
|--------|--------|
| 安全级别高 | 量大、设备多 |
| 权限大 | 访问频繁 |
| TTL 较短 | TTL 较长 |
| 密钥要求更严格 | 可单独轮换 |


- 企业真实项目都会 区分两个 token 体系

⸻

🗂 配套 application.yml 写法（给你补充）
```
sky:
  jwt:
    admin-secret-key: admin123456789
    admin-ttl: 7200000          # 2 hours
    admin-token-name: Authorization

    user-secret-key: user123456789
    user-ttl: 604800000         # 7 days
    user-token-name: user-token
```
📌 yml 是 短横线命名（kebab-case）
📌 Java 是 驼峰命名（camelCase）
Spring Boot 会自动绑定 ✔

⸻

🚀 在代码中怎么用？

直接注入：
```
@Autowired
private JwtProperties jwtProperties;

- 生成管理员 token

String token = JwtUtil.createJWT(
        jwtProperties.getAdminSecretKey(),
        employeeId,
        jwtProperties.getAdminTtl()
);
```
- 生成用户 token
```
String token = JwtUtil.createJWT(
        jwtProperties.getUserSecretKey(),
        userId,
        jwtProperties.getUserTtl()
);
```

⸻

🧠 为什么不写成一个 secret？

因为：
- 不同端权限不同
- 方便密钥轮换
- 便于追踪 token 来源
- 安全域隔离（重要）

- 如果管理员 key 泄露
- 不会影响所有普通用户 token

⸻

🎯 面试一句话回答（可以直接背）
```
- We externalize JWT configuration using @ConfigurationProperties.

- The JwtProperties class binds to the sky.jwt prefix
   and holds two independent token configurations:
   admin-side and user-side.

- This supports different secrets, token names, and expiration policies,
   avoids hardcoding security credentials,
   and improves maintainability and security.
```
⸻

🙌 如果你愿意，我还能继续帮你：

你可以选 ⬇️

- 1️⃣ 写一个完整的 JwtUtil 工具类
- 2️⃣ 写好 登录接口 + 发 token
- 3️⃣ 写 拦截器解析 token
- 4️⃣ 讲解：
  - HS256 / RS256 区别
  - Refresh Token 设计
  - 单点登录（踢下线）
- 5️⃣ 出 10 道 JWT 高频面试


