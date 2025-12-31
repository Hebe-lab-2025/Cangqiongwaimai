```
package com.sky.constant;

/**
 * 密码常量
 */
public class PasswordConstant {

    public static final String DEFAULT_PASSWORD = "123456";

}
```

很好 👍
这又是一个常量类的典型例子，我帮你点明要点 + 给一点工程实践建议。

⸻

🧩 这段代码在做什么？
```
public class PasswordConstant {

    public static final String DEFAULT_PASSWORD = "123456";

}
```

👉 作用是：

- 定义系统中新用户/重置密码时的默认密码常量

例如：

```user.setPassword(PasswordConstant.DEFAULT_PASSWORD);```


⸻

🧠 为什么用 ```public static final？```

| 关键字 | 含义 |
|--------|------|
| `public` | 全局都能访问 |
| `static` | 属于类本身，不用 `new` |
| `final` | 只允许赋值一次，不能修改 |


👉 这就是“常量（constant）”的标准写法。

⸻

❗关于默认密码 —— 非常重要的实践提醒

``` - "123456" 可以在学习项目中使用 ```


# 但在真实系统里建议这样改进：

## ✅ 建议 1：不要硬编码在代码里

原因：
-  不安全
-  一旦泄露，全系统被知道默认密码
-  修改需要重新发版

👉 推荐：配置文件或环境变量

例如：
-  application.yml
-  环境变量 ENV
-  密钥管理服务

⸻

## ✅ 建议 2：默认密码必须加密存储（hash）

千万不要把明文密码存数据库

常见算法：
-  BCrypt（推荐）
-  PBKDF2
-  Argon2

示例（概念性说明）：

```String encoded = passwordEncoder.encode(PasswordConstant.DEFAULT_PASSWORD);```


⸻

## ✅ 建议 3：首次登录强制修改密码

典型规则：
-  第一次登录 → 检测 default password
-  提示用户修改
-  记录 flag：needChangePassword = true

⸻

📌 你现在的写法用在哪里最合适？

- ✔ 学习项目
- ✔ 演示系统
- ✔ 课程练习
- ✔ 本地 demo

⸻

🏁 总结一句话

- 这个类用于统一管理“默认密码常量”，
- public static final 保证默认密码标识不可被随意修改；
- 真实生产环境需要：不硬编码 + 加密存储 + 首登改密。

⸻

如果你愿意，我还能帮你：
-  写“首次登录必须改密码”的逻辑
-  集成 Spring Security + BCrypt
-  统一异常返回：密码错误 / 需要修改密码
-  出 3 道关于密码安全与常量设计的面试题
