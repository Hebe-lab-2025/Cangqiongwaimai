
```
package com.sky.constant;

public class JwtClaimsConstant {

    public static final String EMP_ID = "empId";
    public static final String USER_ID = "userId";
    //public static final String PHONE = "phone";
    //public static final String USERNAME = "username";
    //public static final String NAME = "name";

}

```

很好，这个例子又是一个标准的常量类（Constant Class），我帮你解释一下它的作用和写法 👍

⸻

🧩 这段代码在做什么？
```
package com.sky.constant;

public class JwtClaimsConstant {

    public static final String EMP_ID = "empId";
    public static final String USER_ID = "userId";
    //public static final String PHONE = "phone";
    //public static final String USERNAME = "username";
    //public static final String NAME = "name";

}
```

- - 👉 作用是：
统一管理 JWT 中 Claim 字段名称

也就是：
- empId
- userId
- phone、username、name（目前注释掉）

这些都是 JWT Token 里的 键名

⸻

🧠 为什么要这样写？

① 避免“魔法字符串”（Magic String）

❌ 不推荐：

```claims.get("empId");```

- - 👉 容易拼写错误
- - 👉 全项目到处复制粘贴
- - 👉 不好维护

⸻

✅ 推荐：

```claims.get(JwtClaimsConstant.EMP_ID);```

- ✔ IDE 自动补全
- ✔ 改一次，全系统生效
- ✔ 不容易写错
- ✔ 代码语义清晰

⸻

🔒 为什么用 public static final？

| 关键字 | 作用 |
|--------|------|
| `public` | 全项目可访问 |
| `static` | 属于类本身，不需要 `new` |
| `final` | 不允许重新赋值（常量） |


- - 👉 使用方式：

```JwtClaimsConstant.USER_ID```


⸻

🛡️ 它和你前面的 AutoFillConstant 类关系类似

你现在已经掌握两种典型常量用途：

| 常量类 | 用途 |
|--------|------|
| `AutoFillConstant` | 统一 setter 方法名 |
| `JwtClaimsConstant` | 统一 JWT claim 字段名 |


这都是企业开发标准写法 ✔

⸻

### ✨ 你可以继续做（我可以陪你做）
- 写一个 生成 JWT 的工具类
- 讲解 JWT 的结构 header.payload.signature
- 帮你实现：
- 登录生成 token
- 拦截器校验 token

## 解释：
- claim vs payload
- exp / iat / sub 等字段含义
- 模拟真实面试题：

为什么要用常量类存 JWT claims？

⸻

简单总结一句：

常量类（xxxConstant）= 用来集中存放不会变化但会被多处使用的值

你现在写法是完全正确的工程化代码 👏



