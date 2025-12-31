```
package com.sky.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * 对象映射器:基于jackson将Java对象转为json，或者将json转为Java对象
 * 将JSON解析为Java对象的过程称为 [从JSON反序列化Java对象]
 * 从Java对象生成JSON的过程称为 [序列化Java对象到JSON]
 */
public class JacksonObjectMapper extends ObjectMapper {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    //public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    public JacksonObjectMapper() {
        super();
        //收到未知属性时不报异常
        this.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

        //反序列化时，属性不存在的兼容处理
        this.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        SimpleModule simpleModule = new SimpleModule()
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)))
                .addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)))
                .addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)))
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)))
                .addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)))
                .addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));

        //注册功能模块 例如，可以添加自定义序列化器和反序列化器
        this.registerModule(simpleModule);
    }
}
```

很好，这个类写得很标准 👍 我帮你从“继承 + 实际业务设计”的角度说明它在做什么，以及你应该理解的重点。

⸻

🧭 这个类本质上在做什么？

👉 自定义 ObjectMapper

```public class JacksonObjectMapper extends ObjectMapper```

也就是：
- 继承 ObjectMapper
- 在构造方法里
- 修改默认配置
- 注册时间格式处理模块
- 统一项目中的日期时间 JSON 格式

一句话：

- ✔️ 统一前端/后端 JSON 时间格式
- ✔️ 兼容未知字段
- ✔️ 避免每次手动写注解
- ✔️ 使用继承实现“定制 Jackson 行为”

⸻

✅ 关键点一：继承在这里如何使用？

父类：

```ObjectMapper（Jackson 官方）```

子类：

```JacksonObjectMapper（你项目自己的规则）```

👉 典型的 “is-a”关系

```自定义的 JacksonObjectMapper 仍然是一个 ObjectMapper```
但带有你系统的默认行为

好处：
- 可以在任何需要 ObjectMapper 的地方透明替换
- Spring Boot 会自动使用它作为全局 JSON 处理器

⸻

✅ 关键点二：它调整了哪些行为？

1）忽略未知字段（防止反序列化报错）

```this.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);```

作用：
- 前端多传字段
- 后端实体没有
- ❌ 不会报错
- - ✔️ 直接忽略

面试常问：why？

为了前后端解耦
JSON 字段变动不导致服务挂掉

⸻

2）Java 8 时间类处理（非常重要）

Java 8 时间类：
```
- LocalDateTime
- LocalDate
- LocalTime
```
👉 默认 Jackson 不认识格式
👉 需要你自己告诉它怎么序列化/反序列化

自定义格式
```
public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
```
注册格式处理器
```
SimpleModule simpleModule = new SimpleModule()
        .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(...))
        .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(...))
```
效果：
```
Java 类型	JSON 示例
LocalDate	2025-01-01
LocalTime	10:30:00
LocalDateTime	2025-01-01 10:30
```
你前端不需要写 @JsonFormat 了
👉 全局自动生效 ✔

⸻

✅ 关键点三：为什么不用 static 工具类？而用继承？

你的重点问题是：

“什么时候使用继承？”

👉 这个类就是一个极好例子
```
✔ 继承合适，因为：
- Jackson 已有一个核心对象：ObjectMapper
- 我们要：“在它基础上追加行为”
- 并且：保留类型含义不变
```
这是典型 use case：当你需要“定制框架默认行为”时，用继承非常合适。

⸻

🔥 结合实际业务设计：你应该学会什么？

你已经见过两个好例子：
| 设计目标 | 使用方式 |
|----------|-----------|
| 业务异常统一处理 | 继承 `BaseException` |
| JSON 统一规则 | 继承 `ObjectMapper` |


👉 共性
- 都是已有抽象概念
- 你只是“添加规则/行为”
- is-a 关系成立
- 父类替代性不被破坏

⸻

🧠 面试可答（简洁版）

In my project we create a custom JacksonObjectMapper by extending ObjectMapper.
We override the default configuration to ignore unknown properties,
and register custom serializers and deserializers for Java 8 time API such as LocalDateTime.
This gives us consistent JSON date formats across all controllers without adding annotations everywhere,
and it demonstrates a real business use of inheritance: customizing framework components while preserving type semantics.

⸻

👉 如果你愿意，我可以继续帮你
- 📝 用英文写一个“JacksonObjectMapper 设计说明”，可放在简历/项目总结
- 🧭 画一个 JacksonObjectMapper UML 类图（Mermaid）
- 🛠 教你如何让它在 Spring Boot 中全局生效（@Bean 配置）
- 🎯 出 5 道关于 序列化、反序列化、日期格式、ObjectMapper 的面试题
