很好👍 这个问题问得非常关键：

constant 里的是不是都不可以改？

✔️ 正确答案是：

不完全是，看“改什么”

⸻

✅ 情况 1：final 修饰的 基本类型 👉 绝对不能改

```public static final int AGE = 18;```

不能：

```AGE = 20;   // ❌ 编译报错```

👉 结论
- 数字、布尔、字符、字符串引用本身
- 不能重新赋值

⸻

⚠️ 情况 2：final 修饰 引用类型（对象、集合） 👉 引用不能改，但内容能改

```public static final List<String> LIST = new ArrayList<>();```

不能：

```LIST = new ArrayList<>();   // ❌ 引用不能变```

但可以：

```LIST.add("hello");          // ✅ 内容可以变```

👉 结论
- final 限制的是 引用指向不能变
- 对象内部数据是否能改 → 取决于类本身是否可变

⸻

🧠 结合你现在的代码

```public static final String SET_CREATE_TIME = "setCreateTime";```

这是 不可变的

原因：
- String 本身在 Java 是 immutable（不可变对象）
- 再加上 final
- 所以 既不能重指向，也不能改内容

- ✔ 这个常量绝对安全
- ✔ 这是最佳实践
- ✔ 常量类写法是对的

⸻

🏷️ 再总结一句话（面试可用）

final 约束的是“变量绑定关系”
- 基本类型：值不能改
- 引用类型：引用不能改，但对象可能能改
- String：本来就不可变，更安全

⸻

如果你愿意我还能继续帮你：
- 🔍 画图解释 final 引用 vs 对象
- 🧪 出 5 道 Java final 面试题
- 🧭 讲 final / finally / finalize 区别
- 🏷️ 讲常量命名规范：SET_CREATE_TIME 为什么全大写
