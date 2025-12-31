
```
package com.sky.enumeration;

/**
 * 数据库操作类型
 */
public enum OperationType {

    /**
     * 更新操作
     */
    UPDATE,

    /**
     * 插入操作
     */
    INSERT

}
```

很好 👍
你已经开始把“操作类型”从常量类升级为 枚举 enum，这一步非常专业。
下面我帮你快速说明它的作用、使用场景、以及为什么要用 enum。

⸻

✅ 这段代码在做什么？
```
package com.sky.enumeration;

/**
 * 数据库操作类型
 */
public enum OperationType {

    /**
     * 更新操作
     */
    UPDATE,

    /**
     * 插入操作
     */
    INSERT
}
```

- 👉 定义了两种数据库操作类型：
- UPDATE —— 更新
- INSERT —— 新增

📌 一般用于：
- 字段自动填充
- 审计日志（谁改了什么）
- MyBatis / AOP 判断当前操作类型
- 乐观锁、变更记录

⸻

🧠 为什么要用 enum 而不是 String / int 常量？

# ✨ 优点对比

| 方式 | 问题 | enum 的优势 |
|------|------|--------------|
| magic string | 容易拼写错 | 编译期检查 |
| int 常量 | 无语义 | 可读性强 |
| 字符串 | 可传入任何值 | 枚举值受限、类型安全 |
| if 写死 | 维护困难 | 支持 `switch`、结构清晰 |


- 👉 例如：

❌ 不推荐：

```if ("UPDATE".equals(type)) {}```

✅ 推荐：

```if (type == OperationType.UPDATE) {}```

- ✔ 类型安全
- ✔ 不会拼错
- ✔ IDE 自动补全

⸻

🧭 常见用法（在 AOP 中判断操作类型）

例如自定义注解：
```
@AutoFill(OperationType.INSERT)
public void insert(Employee emp) { ... }
```

在切面中读取：

```
OperationType operationType = autoFill.value();

if (operationType == OperationType.INSERT) {
    // 填充 createTime / createUser
} else if (operationType == OperationType.UPDATE) {
    // 填充 updateTime / updateUser
}
```
- 👉 这就是 MyBatis Plus 自动填充思想
- 👉 也是很多实战项目的做法

⸻

🧭 进阶升级（可选）

你可以给枚举加：
- 数值 code
- 描述 message
- 自定义方法

例如：
```
public enum OperationType {

    UPDATE("update"),
    INSERT("insert");

    private final String value;

    OperationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
```

⸻

🏁 总结一句（面试可用）

- OperationType 将数据库操作类型抽象成枚举，
- 通过 enum 提供类型安全、可读性更高的值约束，
- 常用于自动填充、日志审计和 AOP 方法拦截。

⸻

如果你愿意，我还能帮你：
- 🧩 写完整 自动填充 AOP + 注解 + enum 实战代码
- 🧭 enum vs constant 高级面试题
- ✍ 讲 JPA/MyBatis/MyBatis-Plus 中 enum 映射
- 🧪 出 5 道“枚举+注解+AOP”的综合面试题
