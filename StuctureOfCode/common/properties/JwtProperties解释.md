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

很好，这个类写得非常标准 👍
我帮你 逐行、按模块解释它在做什么、为什么这样设计、怎么被 Spring 使用。

⸻

✅ 整体作用（一句话）

把 application.yml 中 sky.jwt.* 配置自动绑定到一个 Java 对象里

👉 方便在代码中读取 JWT 相关配置
👉 不用写死秘钥/过期时间
👉 支持多环境 & 安全

⸻

🧩 注解解释

# @Component

把这个类交给 Spring 容器管理
→ 可以直接注入：
```
@Autowired
private JwtProperties jwtProperties;
```

⸻

``` @ConfigurationProperties(prefix = "sky.jwt")```


意思是：

👉 读取 yml 中以 sky.jwt 开头的配置

例如 application.yml：
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
会自动绑定到这个类中 👇

⸻

# @Data


Lombok 自动生成：
- 	getter / setter
- 	toString
- 	equals & hashcode

👉 你不用手写这些方法

⸻

🔐 字段解释（非常重要）

🧑‍💼 管理端员工 token 配置
```
private String adminSecretKey;
private long adminTtl;
private String adminTokenName;

字段	作用
adminSecretKey	签名用秘钥
adminTtl	token 过期时间
adminTokenName	前端 header 中携带 token 的名字
```

⸻

👤 用户端（微信小程序用户）token 配置
```
private String userSecretKey;
private long userTtl;
private String userTokenName;
```
📌 为什么两套？

| 管理端 | 用户端 |
|--------|--------|
| 权限高 | 数量多 |
| 风险高 | 生命周期长 |
| TTL 可能较短 | TTL 可以较长 |
| 密钥可以不同 | 互不影响，便于隔离 |


⸻

🚀 在代码中如何使用？

生成管理员 token：
```
String token = JwtUtil.createJWT(
    jwtProperties.getAdminSecretKey(),
    employeeId,
    jwtProperties.getAdminTtl()
);

生成用户 token：

String token = JwtUtil.createJWT(
    jwtProperties.getUserSecretKey(),
    userId,
    jwtProperties.getUserTtl()
);
```
读取 header 名：
```
request.getHeader(jwtProperties.getUserTokenName());
```

⸻

🎯 面试可背答案

- JwtProperties is a configuration binding class.

- It uses @ConfigurationProperties(prefix = "sky.jwt")
      to map JWT-related settings in application.yml to Java fields.

- We separate admin-side and user-side token configurations
      so they can use different secrets, expiration times, and header names,
      improving security and flexibility.

⸻

如果你愿意，我还能继续帮你：
- 	📌 画 JwtProperties 结构图
- 	🔐 写 JwtUtil（完整可用）
- 	🧩 写登录接口 + token 生成
- 	🛡 写拦截器校验 token
- 	🧪 出 10 道 JWT + SpringBoot 面试题
