```
package com.sky.context;

public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
```

很好，这段代码已经非常贴近真实企业项目中的登录上下文实现了 👍
我帮你从四个方面讲清楚：它是干嘛的、ThreadLocal 是啥、怎么用、有哪些坑。

⸻

✅ 这段代码在做什么？
```
public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }
}
```
👉 本质作用：

在当前线程中存储并获取“当前用户 ID”

也就是一个线程级别的全局变量

常见使用场景：
- 获取当前登录用户 id
- 自动填充 createUser/updateUser
- 记录谁在操作数据（审计日志）
- 解决参数层层传递问题

⸻

🧠 ThreadLocal 是什么？一句话版本

# ThreadLocal = 为 每个线程 单独存储一份变量副本

不同线程互不影响：

| 线程 | 存的 ID |
|------|---------|
| A | 1001 |
| B | 2002 |

结论：

- 互不干扰 ✔

- 不会串值 ✔

- 不需要层层传递参数 ✔

⸻

🧭 典型使用流程（配合登录拦截器）

登录时写入

```BaseContext.setCurrentId(userId);```

业务层读取

```Long currentId = BaseContext.getCurrentId();```

请求结束后清理

```BaseContext.removeCurrentId();```

👉 这通常出现在：
- Filter
- Interceptor
- AOP 切面

⸻

📝 示例：拦截器中使用
```
public boolean preHandle(...) {
    Long userId = jwtParse(token);
    BaseContext.setCurrentId(userId);
    return true;
}

public void afterCompletion(...) {
    BaseContext.removeCurrentId();
}
```

⸻

⚠️ 非常重要：一定要 remove！！！

为什么？

线程池场景下：
- 线程会复用
- ThreadLocal 数据会残留
- 可能把 A 用户 ID 泄漏到 B 用户请求 😨

所以你的：
```
public static void removeCurrentId() {
    threadLocal.remove();
}
```
👉 非常正确 ✔
👉 必须在 finally/afterCompletion 调用 ✔

⸻

💡 工程级改进建议

✔ 建议 1：加 private 构造方法（防止被 new）

```private BaseContext(){}```


⸻

✔ 建议 2：把变量设置为 private static final

```private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();```


⸻

✔ 建议 3：可以放多种信息（不仅是 ID）

例如：
- userId
- username
- role
- tenantId（多租户）
- requestId（链路追踪）

也可以改成：

```ThreadLocal<Map<String,Object>>```


⸻

🏁 总结一句话（面试可用）

- BaseContext 使用 ThreadLocal 保存当前线程用户 ID，
- 避免层层传参，常用于登录态、审计字段填充；
- 线程结束或请求完成后必须 remove 以防止内存泄漏。

⸻

如果你愿意，我还能帮你：
- ✍ 写一个 Login Check Interceptor
- 🧾 实现 自动填充 createUser/updateUser AOP
- 🧩 讲 ThreadLocal 内存泄漏底层原理
- 🧪 出几道 MyBatis + ThreadLocal 面试题
