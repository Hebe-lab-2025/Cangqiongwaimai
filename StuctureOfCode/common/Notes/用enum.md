
很好，这是一个非常重要、也很“工程化”的问题 👍
下面给你一套简单、实用、能在面试里直接说的答案。

⸻

✅ 核心结论（先记住这一段）
- 只有固定、有限、可枚举的取值集合 → 用 enum
- 只是单个值本身很重要，无需形成类型体系 → 用常量

👉 一句话判断：

# “值是否天然属于同一类、并且取值范围是有限集合？”

- ✔ 是 → 用 enum
- ✖ 不是 → 用 常量

⸻

🧭 用表格快速对比

| 场景 | 用 enum | 用常量 |
|------|---------|--------|
| 取值是固定集合 | ✔ 强烈推荐 | ✖ 不推荐 |
| 想防止传入非法值 | ✔ 类型安全 | ✖ 做不到 |
| 需要 switch / 业务分支 | ✔ 适合 | 勉强可用 |
| 需要携带更多属性 | ✔ 可以 | ✖ 不行 |
| 只是一串文本或数字 | 不必使用 | ✔ 常量最简单 |
| 不想创建新类型 | ✖ 不合适 | ✔ 更方便 |



⸻

🧩 什么时候用 enum？

👉 满足以下 2～3 条就用 enum：
- 取值是固定有限集合
- 这些值有语义
- 你希望 编译期检查
- 希望避免传错值
- 希望支持业务分支判断

✅ 典型例子

✔ 操作类型
```
public enum OperationType {
    INSERT, UPDATE, DELETE
}
```

✔ 订单状态
```
public enum OrderStatus {
    NEW, PAID, SHIPPED, FINISHED, CANCELED
}
```

✔ 用户角色
```
public enum Role {
    ADMIN, USER, VISITOR
}
```

👉 特点：
- 值有限
- 取值明确
- 错了要报错
- 常参与业务判断

🎯 这是你前面 OperationType 用 enum 的正确原因

⸻

🧾 什么时候用常量（public static final）？

👉 适合：
- 不需要形成一个“类型”
- 只是一个单一值
- 不需要限制取值范围
- 只是为了统一管理 & 不写魔法值

✅ 典型例子

✔ 提示信息

```public static final String PASSWORD_ERROR = "密码错误";```

✔ JWT Claim 名称

```public static final String USER_ID = "userId";```

✔ 默认密码

```public static final String DEFAULT_PASSWORD = "123456";```

✔ 开关值或简单标志位
```
public static final int ENABLE = 1;
public static final int DISABLE = 0;
```
👉 特点：
- 只是单独值
- 不强制类型体系
- 不为了业务分支判断
- 更多为了避免魔法字符串/数字

⸻

⚠️ 重要：这几种情况不要滥用 enum

# ❌ 不要为这些创建 enum：
- 错误提示语
- 常用字符串 key
- 配置项名称
- SQL 字段名
- Redis key 前缀
- 默认密码

👉 用常量即可
👉 enum 反而显得“过度设计”

⸻

🧪 面试版回答（背下来可直接说）

- 当一组值构成有限集合，并且需要类型安全、可枚举性、编译期校验时，
- 使用 enum，例如状态、操作类型、角色。

- 当只是为了避免魔法值、提升可读性、集中管理字符串或数字时，
- 使用常量，例如提示信息、配置 key、默认值。

- 简单说：有类型→用 enum；只是值→用常量。

⸻

🎁 要不要我再帮你加点练习？

我可以给你：
# - 🔍 把你项目里的 enum/constant 逐个评审
# - 🧪 5 道 Java 面试题：enum vs constant
# - 🧭 把 StatusConstant 改造成 enum + 数据库存储映射
# - 🧩 讲 enum 在 MyBatis/MyBatis-Plus/JPA 里的用法
