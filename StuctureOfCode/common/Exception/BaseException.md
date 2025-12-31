```
package com.sky.exception;

/**
 * 业务异常
 */
public class BaseException extends RuntimeException {

    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
    }

}
```

很好，这个类同样是标准的自定义业务异常，而且命名很专业 👍

⸻

✅ 这段代码在做什么？
```
package com.sky.exception;

public class OrderBusinessException extends BaseException {

    public OrderBusinessException(String msg) {
        super(msg);
    }

}
```
👉 作用：

# 用来表示 与订单相关的业务异常

例如：
- 订单状态不允许操作
- 订单不存在
- 重复下单
- 订单已取消
- 支付异常

⸻

🧩 为什么继承 BaseException？

说明你的项目中有：

```public class BaseException extends RuntimeException { ... }```

👉 这样设计的好处：

| 好处 | 说明 |
|------|------|
| 统一异常结构 | 通过全局异常处理器统一返回格式 |
| 语义清晰 | 一看就能知道错误类型和业务含义 |
| 便于扩展 | 各业务域可以定义各自异常 |
| 避免滥用 RuntimeException | 粒度更细，更易于定位问题 |



⸻

🧭 典型使用场景
```
if(order == null){
    throw new OrderBusinessException("订单不存在");
}

if(order.getStatus() != PAID){
    throw new OrderBusinessException("当前订单状态不支持退款");
}
```
👉 然后在 全局异常处理器 中：
```
@ExceptionHandler(OrderBusinessException.class)
public Result handleOrderException(OrderBusinessException e){
    return Result.error(e.getMessage());
}
```

⸻

🧠 和 AccountLockedException 的区别

异常类	领域
- AccountLockedException	登录/账户域
- OrderBusinessException	订单域

👉 这就是 按业务域拆分异常类型
非常符合 DDD 分层思想

⸻

✨ 可选增强（进阶）

你也可以：

✔ 加错误码

```private final int code;```

✔ 让异常携带业务信息

例如：
- 订单号
- 用户 ID

```private Long orderId;```

✔ 甚至配合枚举

```throw new OrderBusinessException(OrderErrorCode.ORDER_NOT_FOUND);```


⸻

🏁 总结一句

- OrderBusinessException 是订单领域的业务异常，
- 继承 BaseException 便于全局统一处理，
- 语义清晰、粒度细化，是规范的企业级异常设计方式。

⸻

如果你愿意，我还能帮你：
- ✍️ 写 GlobalExceptionHandler 全局异常处理
- 🧭 设计 错误码 + 信息 的最佳实践
- 🧪 出 3 道“业务异常设计”面试题

  
