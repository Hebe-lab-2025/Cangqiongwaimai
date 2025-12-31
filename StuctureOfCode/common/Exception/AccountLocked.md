```
package com.sky.exception;

/**
 * 账号被锁定异常
 */
public class AccountLockedException extends BaseException {

    public AccountLockedException() {
    }

    public AccountLockedException(String msg) {
        super(msg);
    }

}
```

很好，这段代码已经是标准的业务自定义异常类 👍
我帮你说明清楚：它是什么、为什么要这样写、怎么用、与父类 BaseException 的关系。

⸻

✅ 这段代码在做什么？
```
package com.sky.exception;

/**
 * 账号被锁定异常
 */
public class AccountLockedException extends BaseException {

    public AccountLockedException() {
    }

    public AccountLockedException(String msg) {
        super(msg);
    }

}
```
👉 作用：

定义一个**专门表示“账号被锁定”**的业务异常类型

⸻

🧩 它为什么要继承 BaseException？
- BaseException = 你项目里的自定义业务异常父类
- 所有业务异常都继承它，例如：

| 异常类 | 场景 |
|--------|------|
| `AccountLockedException` | 账号被锁 |
| `AccountNotFoundException` | 账号不存在 |
| `PasswordErrorException` | 密码错误 |
| `OrderStatusException` | 订单状态错误 |


👉 好处：
- 统一异常处理
- 统一返回 JSON 格式
- Controller 不需要 try-catch
- GlobalExceptionHandler 可以精准捕获

示例（常见写法）：
```
@ExceptionHandler(BaseException.class)
public Result handle(BaseException ex) {
    return Result.error(ex.getMessage());
}
```

⸻

🧭 构造方法解释

1️⃣ 无参构造方法
```
public AccountLockedException() { }

👉 可以在不传 message 时使用：

throw new AccountLockedException();
```

⸻

2️⃣ 带 message 的构造方法
```
public AccountLockedException(String msg) {
    super(msg);
}
```
👉 可以自定义提示：

```throw new AccountLockedException("账号因多次登录失败被锁定");```


⸻

🧠 真实使用场景（登录逻辑）
```
if (account.isLocked()) {
    throw new AccountLockedException("账号被锁定，请联系管理员");
}
```
然后：
- 全局异常处理器捕获
- 返回统一 JSON 响应，例如：

```{ "code": 0, "msg": "账号被锁定，请联系管理员" }```


⸻

✨ 这是“业务异常分层”的标准实践

✔ 不推荐

```throw new RuntimeException("账号被锁定");```

问题：
- 粒度粗
- 不好区分类型
- 难以精细化处理

⸻

✅ 推荐

```throw new AccountLockedException();```

优点：
- 可单独捕获
- 行为语义清晰
- 控制层 & 全局异常很好扩展
- 符合 DDD / Clean Architecture

⸻

🏁 面试/总结一句话

- AccountLockedException 是一个自定义的业务异常，
- 继承 BaseException，用于明确表达“账号被锁定”这一特定错误场景，
- 便于全局异常处理与精细化业务控制。

⸻

如果你愿意，我还能帮你：
- 🧾 写一个 GlobalExceptionHandler
- 🧭 画登录失败 → 账号锁定流程图
- 🧪 给你 5 道“自定义异常 vs RuntimeException”面试题
