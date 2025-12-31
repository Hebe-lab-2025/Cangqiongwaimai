
```
package com.sky.constant;

/**
 * 信息提示常量类
 */
public class MessageConstant {

    public static final String PASSWORD_ERROR = "密码错误";
    public static final String ACCOUNT_NOT_FOUND = "账号不存在";
    public static final String ACCOUNT_LOCKED = "账号被锁定";
    public static final String ALREADY_EXISTS = "已存在";
    public static final String UNKNOWN_ERROR = "未知错误";
    public static final String USER_NOT_LOGIN = "用户未登录";
    public static final String CATEGORY_BE_RELATED_BY_SETMEAL = "当前分类关联了套餐,不能删除";
    public static final String CATEGORY_BE_RELATED_BY_DISH = "当前分类关联了菜品,不能删除";
    public static final String SHOPPING_CART_IS_NULL = "购物车数据为空，不能下单";
    public static final String ADDRESS_BOOK_IS_NULL = "用户地址为空，不能下单";
    public static final String LOGIN_FAILED = "登录失败";
    public static final String UPLOAD_FAILED = "文件上传失败";
    public static final String SETMEAL_ENABLE_FAILED = "套餐内包含未启售菜品，无法启售";
    public static final String PASSWORD_EDIT_FAILED = "密码修改失败";
    public static final String DISH_ON_SALE = "起售中的菜品不能删除";
    public static final String SETMEAL_ON_SALE = "起售中的套餐不能删除";
    public static final String DISH_BE_RELATED_BY_SETMEAL = "当前菜品关联了套餐,不能删除";
    public static final String ORDER_STATUS_ERROR = "订单状态错误";
    public static final String ORDER_NOT_FOUND = "订单不存在";

}

```

这段代码同样写得非常好 👍——这是一个信息提示常量类，在真实项目中非常常见。我帮你从“是什么、为什么、怎么用、可以怎么改进”四个角度说清楚。

⸻

✅ 这段代码在做什么？

public class MessageConstant {

    public static final String PASSWORD_ERROR = "密码错误";
    public static final String ACCOUNT_NOT_FOUND = "账号不存在";
    public static final String ACCOUNT_LOCKED = "账号被锁定";
    public static final String ALREADY_EXISTS = "已存在";
    public static final String UNKNOWN_ERROR = "未知错误";
    public static final String USER_NOT_LOGIN = "用户未登录";
    public static final String CATEGORY_BE_RELATED_BY_SETMEAL = "当前分类关联了套餐,不能删除";
    public static final String CATEGORY_BE_RELATED_BY_DISH = "当前分类关联了菜品,不能删除";
    public static final String SHOPPING_CART_IS_NULL = "购物车数据为空，不能下单";
    public static final String ADDRESS_BOOK_IS_NULL = "用户地址为空，不能下单";
    public static final String LOGIN_FAILED = "登录失败";
    public static final String UPLOAD_FAILED = "文件上传失败";
    public static final String SETMEAL_ENABLE_FAILED = "套餐内包含未启售菜品，无法启售";
    public static final String PASSWORD_EDIT_FAILED = "密码修改失败";
    public static final String DISH_ON_SALE = "起售中的菜品不能删除";
    public static final String SETMEAL_ON_SALE = "起售中的套餐不能删除";
    public static final String DISH_BE_RELATED_BY_SETMEAL = "当前菜品关联了套餐,不能删除";
    public static final String ORDER_STATUS_ERROR = "订单状态错误";
    public static final String ORDER_NOT_FOUND = "订单不存在";

}

👉 本质上是：

项目中的统一错误提示/业务提示文案中心

例子：

```throw new BusinessException(MessageConstant.ACCOUNT_NOT_FOUND);```

而不是：

```throw new BusinessException**("账号不存在")**; // ❌ 魔法字符串```


⸻

🧠 为什么要写“常量类”而不是直接写字符串？

✅ 好处很多：

| 目的 | 解释 |
|------|------|
| 统一管理 | 所有常量集中在同一位置 |
| 避免拼写错误 | IDE 自动补全，减少低级错误 |
| 方便修改 | 改一处即可全局生效 |
| 支持国际化 | 将来可切换多语言 |
| 可读性好 | 常量名表达含义更清晰 |
| 符合规范 | 企业级项目中广泛采用 |



⸻

🔒 为什么要 public static final？

| 关键字 | 含义 |
|--------|------|
| `public` | 全项目可用 |
| `static` | 属于类本身，不用创建对象 |
| `final` | 不允许被重新赋值（常量） |


👉 使用方式：

```MessageConstant.PASSWORD_ERROR```


⸻

❓这些内容以后能不能“改文字”？

✋ 区分两种“修改”

| 修改什么 | 是否允许 | 说明 |
|-----------|-----------|------|
| 重新赋值常量 | ❌ 不允许 | `final` 限制，不能重新赋值 |
| 修改提示文本 | ✔ 允许 | 直接在代码中改字符串即可 |
| 运行时动态修改 | ❌ 不允许 | 常量在编译期就已确定 |
| 换语言、统一修改 | ✔ 强烈推荐 | 使用 `properties` 做国际化 |


👉 结论：
- 常量本身不能重指向
- 但你完全可以修改右侧提示内容

比如：

```public static final String PASSWORD_ERROR = "Password incorrect";```

仍然合法 ✔

⸻

🚀 再进阶一点：你现在写法已经接近真实企业规范

你现在有：
- AutoFillConstant —— 反射 / 公共字段
- JwtClaimsConstant —— JWT claim keys
- MessageConstant —— 提示信息

👉 这是 DDD / Clean Architecture 推荐做法

⸻

💡 如果继续改进，可以：

🔁 支持中英文国际化 i18n

把字符串放到：
```
messages_zh_CN.properties
messages_en_US.properties
```

然后：

```MessageSource.getMessage("password.error", null, locale);```


⸻

🧭 使用错误码 + 提示信息

例如：

```public static final String ACCOUNT_NOT_FOUND = "A001";```

再映射到文案：

| Code | Message |
|------|----------|
| A001 | 账号不存在 |
| A002 | 密码错误 |


👉 更专业（很多大厂/银行系统这样做）

⸻

🎯 总结一句（你可以背下来）

```
常量类用来集中管理固定不变但全局复用的值，比如：
– 提示信息
– 枚举名称
– JWT claims
– 反射方法名
```


```public static final``` 保证：类级别访问、不可重新赋值、统一规范化。

⸻

如果你愿意，下一步我可以帮你：
- 🧪 写一个 GlobalExceptionHandler 统一使用 MessageConstant
- 🎭 改造成国际化 i18n 版本
- 🧾 输出面试问题：
为什么要使用常量类而不是直接写字符串？

