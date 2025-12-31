好，那我就一口气帮你把 1–5 都补齐，你可以直接当项目/面试听稿用 💪

⸻

1️⃣ JwtProperties 结构图（Mermaid）
```
classDiagram
    class JwtProperties {
        <<Component>>
        - String adminSecretKey
        - long adminTtl
        - String adminTokenName
        - String userSecretKey
        - long userTtl
        - String userTokenName
    }

    class ApplicationYml {
        + sky.jwt.admin-secret-key
        + sky.jwt.admin-ttl
        + sky.jwt.admin-token-name
        + sky.jwt.user-secret-key
        + sky.jwt.user-ttl
        + sky.jwt.user-token-name
    }

    ApplicationYml --> JwtProperties : @ConfigurationProperties(prefix="sky.jwt")
```
这个图表达的重点：
- application.yml 里的 sky.jwt.*
通过 @ConfigurationProperties(prefix = "sky.jwt")
→ 自动绑定到 JwtProperties 的 6 个字段上。

⸻

2️⃣ JwtUtil（工具类示例）

尽量贴近你现在项目习惯：
- 传入 secretKey + ttl + claims
- 使用 io.jsonwebtoken（jjwt）

依赖（Maven）
```
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
```
工具类代码
```
package com.sky.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;

import java.util.Date;
import java.util.Map;

public class JwtUtil {

    /**
     * 创建 JWT
     *
     * @param secretKey 签名密钥
     * @param ttlMillis 过期时间（毫秒）
     * @param claims    自定义负载（例如：userId, role 等）
     * @return token 字符串
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)      // 自定义负载
                .setIssuedAt(now)       // 签发时间
                .signWith(SignatureAlgorithm.HS256, secretKey); // HS256 + 密钥

        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    /**
     * 解析 JWT，返回 Claims
     *
     * @param secretKey 密钥
     * @param token     前端传来的 token
     * @return Claims   含自定义字段的负载
     */
    public static Claims parseJWT(String secretKey, String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 校验 token 是否有效（签名正确 & 未过期）
     * 解析失败会抛异常，调用方决定怎么处理
     */
    public static boolean validateToken(String secretKey, String token) {
        try {
            parseJWT(secretKey, token);
            return true;
        } catch (ExpiredJwtException e) {
            // 过期
            return false;
        } catch (SignatureException e) {
            // 签名错误
            return false;
        } catch (Exception e) {
            // 其他错误
            return false;
        }
    }
}
```

⸻

3️⃣ 登录接口 + 生成 token 示例

假设你有一个后台员工登录接口 /admin/employee/login：

Controller 示例
```
package com.sky.controller.admin;

import com.sky.properties.JwtProperties;
import com.sky.constant.JwtClaimsConstant;
import com.sky.result.Result;
import com.sky.entity.Employee;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    public Result<String> login(@RequestBody EmployeeLoginDTO dto) {
        // 1. 校验用户名 + 密码（示意）
        Employee emp = employeeService.login(dto);

        // 2. 组装 JWT Claims
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, emp.getId());
        claims.put("username", emp.getUsername());

        // 3. 生成 token（使用 admin 端配置）
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims
        );

        // 4. 返回 token（可以放在 body 或 header）
        return Result.success(token);
    }
}
```
JwtClaimsConstant 你已经有：
```
public class JwtClaimsConstant {
    public static final String EMP_ID = "empId";
    public static final String USER_ID = "userId";
}
```

⸻

4️⃣ 拦截器校验 token 示例（Admin 端）

你项目里已经有 JwtTokenAdminInterceptor，这里写一个简化版思路：
```
package com.sky.interceptor;

import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.constant.JwtClaimsConstant;
import com.sky.result.Result;
import com.sky.result.ResultCode;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. 从请求头中取出 token
        String tokenName = jwtProperties.getAdminTokenName(); // 比如：token
        String token = request.getHeader(tokenName);

        if (token == null || token.isEmpty()) {
            // 未登录
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        try {
            // 2. 解析 token
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);

            // 3. 从 claims 中取出 empId，保存到 BaseContext（ThreadLocal）
            Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
            BaseContext.setCurrentId(empId);

            return true;
        } catch (Exception e) {
            // token 非法或过期
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束，清理 ThreadLocal，防止内存泄漏
        BaseContext.removeCurrentId();
    }
}
```
配合你已有的：
```
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void setCurrentId(Long id) { threadLocal.set(id); }
    public static Long getCurrentId() { return threadLocal.get(); }
    public static void removeCurrentId() { threadLocal.remove(); }
}
```

⸻

5️⃣ 10 道 JWT + Spring Boot 面试题（附简短要点）

Q1：什么是 JWT？结构是什么？
- A：JWT 是 JSON Web Token，用来做无状态认证。
- 结构：header.payload.signature（三段，Base64URL 编码）

⸻

Q2：JWT 和 Session 有什么区别？
- Session 存在服务器（有状态），JWT 存在客户端（无状态）
- JWT 适合分布式、微服务，减少服务器存储压力

⸻

Q3：JWT 一旦签发，怎么“强制用户下线”？
- 签发后本身是不可撤销的
- 常见做法：
- 维护 Token 黑名单（存 Redis）
- 或使用版本号 / 登录时间戳 + 服务端校验
- 或缩短 TTL + Refresh Token

⸻

Q4：HS256 和 RS256 有什么区别？
- HS256：对称加密，同一个 secret 生成+验证
- RS256：非对称加密，私钥签名、公钥验证，更安全，适合第三方验证场景

⸻

Q5：JWT 里通常放什么，不建议放什么？
- 建议：用户 ID、角色、基本标识信息
- 不建议：密码、银行卡号等敏感数据（JWT 很多场景只是 Base64 编码，不是加密）

⸻

Q6：JWT 存在前端哪里比较合适？
- 常见：
- Authorization header：Bearer xxx
- 或 localStorage / sessionStorage
- 注意：避免存入可被 CSRF 利用的 Cookie（要配合 SameSite / CSRF 防御）

⸻

Q7：如何在 Spring Boot 中统一管理 JWT 配置？
- 使用 @ConfigurationProperties(prefix = "xxx") + properties 类
- 比如：JwtProperties，把 secret、ttl、tokenName 解耦出代码

⸻

Q8：JWT 过期时间如何设计？
- Access Token：时间短（几分钟 / 几小时）
- Refresh Token：时间长（几天 / 几周）
- 后台管理系统通常比 C 端用户短

⸻

Q9：JWT 如何防止被篡改？
- 使用签名（HS256 / RS256）
- 验证 token 时必须用相同的 secret / 公钥验证
- 如果签名不匹配，直接拒绝

⸻

Q10：在 SpringMVC 里，JWT 验证可以放在哪一层？
- HandlerInterceptor（你项目里的 Admin/User 拦截器）
- 或 Spring Security Filter / OncePerRequestFilter
- 目标：在进入 Controller 之前拦截、解析、验证 token，并写入当前用户上下文

⸻

如果你想继续深入，我们可以下一步做：
- 把你现在项目里 JwtTokenAdminInterceptor / JwtTokenUserInterceptor 拿出来，对照梳理一遍调用链：
Login → 发 token → 前端携带 → Interceptor 解析 → BaseContext 使用
- 或者我帮你写一份“JWT 设计 + 实现”的面试长答案📝，可以直接用在 Amazon / 后端岗面试。

好，我把你的两项内容整理成**更清晰、可直接放笔记/README 的 Markdown 版本**👇

```markdown
## 🔜 下一步深入方向（JWT）

你可以从两个方向继续加强：

---

### ✅ 方向一：梳理当前项目中的 JWT 调用链

我们将基于你的：
- `JwtTokenAdminInterceptor`
- `JwtTokenUserInterceptor`
- `BaseContext`

完整过一遍调用流程：

Login  
→ 签发 Token  
→ 前端保存并携带 Token  
→ Interceptor 解析与校验  
→ 将用户信息写入 BaseContext  
→ 业务层直接获取当前用户并执行业务

🎯 目标：把 **调用链 + 时序图 + 关键代码位置** 全部梳理清楚

---

### ✅ 方向二：准备“JWT 设计 + 实现”面试长答案

我可以帮你输出一篇可直接使用的面试答案，内容包括：

- 为什么选择 JWT（无状态、可扩展、易水平扩展）
- JWT 结构：`header + payload + signature`
- token 签发与解析的流程
- Refresh Token vs Access Token
- 管理端 / 用户端拆分的原因
- Interceptor 中的鉴权策略
- BaseContext + ThreadLocal 设计思想
- 全局异常处理的配合方式
- 常见安全问题与最佳实践：
  - 过期控制
  - 密钥轮换
  - 重放攻击
  - HTTPS 必须
  - 不在 token 中放敏感信息

👉 输出形式：**可直接背诵 / 可直接投递面试的长答案**
```

你下一步想先做哪一个？

- 🔹 1 调用链梳理（结合你项目代码）
- 🔹 2 面试长答案
- 🔹 ✔️ 或者：**两个一起**
