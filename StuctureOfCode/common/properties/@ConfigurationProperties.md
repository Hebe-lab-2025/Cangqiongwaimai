这段就是 @ConfigurationProperties 注解的源码（反编译后的样子），来自 Spring Boot 框架本身。

我帮你用简单方式逐行解释👇

⸻

✅ 这是什么？
- 它是一个 注解定义
- 包路径：

```org.springframework.boot.context.properties.ConfigurationProperties```

- 作用：
👉 把 yml / properties 中的配置绑定到 Java 类属性上

例如你用的：
```
@ConfigurationProperties(prefix = "sky.jwt")
public class JwtProperties { ... }
```

⸻

🧩 逐行解释（通俗版）

```@Target({ElementType.TYPE, ElementType.METHOD})```

👉 这个注解可以作用在：
- 类（TYPE）
- 方法（METHOD）

⸻


```@Retention(RetentionPolicy.RUNTIME)```

👉 运行时仍然存在（反射可读）
Spring Boot 正是用反射读取它。

⸻


@Documented

👉 生成 Javadoc 时会把注解包含进去

⸻


@Indexed

👉 允许 Spring 在类路径扫描时索引它（加速启动）

⸻

🔥 核心：注解成员
```
@AliasFor("prefix")
String value() default "";

- value 是 prefix 的别名
- 所以你可以写：

@ConfigurationProperties("sky.jwt")
```
等价于👇

```@ConfigurationProperties(prefix = "sky.jwt")```


⸻

```
@AliasFor("value")
String prefix() default "";
```
👉 反向别名
二者互为 alias（真正源码非常优雅）

⸻


```boolean ignoreInvalidFields() default false;```

👉 如果 值类型不匹配 是否忽略？

比如：
```
age: abc

Java：

private int age;

- false（默认） → 报错
- true → 忽略错误字段
```
⸻


```boolean ignoreUnknownFields() default true;```

👉 YAML 中多出来的字段是否忽略？

```sky.jwt.aaa-bbb-ccc: 123   # Java 类里没有```

- true 默认忽略
- false 会报错

⸻

🧠 总结一句话（面试可背）

- @ConfigurationProperties 用来把配置文件的属性按前缀批量绑定到 Java Bean 中，
- 支持类型安全绑定、嵌套对象，并提供未知字段与无效字段的容错控制。

⸻

🎯 补充你项目里的真实绑定示例
```
YAML：

sky:
  jwt:
    admin-secret-key: itcast

Java：

@ConfigurationProperties(prefix = "sky.jwt")
public class JwtProperties {
    private String adminSecretKey;
}
```
👉 Spring Boot 自动完成：

```admin-secret-key  →  adminSecretKey```

（驼峰与中划线自动映射）

⸻

📌 如果你愿意，我还能继续帮你：
- 🧭 画 ConfigurationProperties 绑定流程图
- 🧪 出 10 道 SpringBoot 配置绑定面试题
- ⚔️ 对比：
- @Value
- @ConfigurationProperties
- 🛠️ 写一个自定义配置类 + 校验注解 @Validated
- 🕵️‍♂️ 带你 Debug 看真实绑定源码链路

👉 直接说：“选源码 / 面试题 / 流程图 / 对比 / debug”
