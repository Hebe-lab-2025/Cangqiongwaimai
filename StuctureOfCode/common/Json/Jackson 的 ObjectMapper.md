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

很好的一段代码 👍 这其实是你项目里统一定制 Jackson 的 ObjectMapper，用来控制 JSON ↔ Java 的转换规则。下面我按核心点帮你拆解说明（面试可直接说）。

⸻

🧠 这一类是干什么的？

👉 作用：全局 JSON 序列化/反序列化策略
- Java → JSON（序列化）
- JSON → Java（反序列化）

特别是：
- 统一日期格式
- 忽略未知字段
- 支持 Java 8 时间类型（LocalDate/LocalDateTime/LocalTime）

⸻

🧩 关键知识点一：继承 ObjectMapper

```public class JacksonObjectMapper extends ObjectMapper {```

这意味着：
- 你在原生 ObjectMapper 基础上增强
- Spring Boot 可直接注入使用
- 是“配置即全局生效”的典型设计

📌 面试关键词：

自定义 ObjectMapper 实现全局 JSON 序列化策略

⸻

🧩 关键知识点二：日期格式常量
```
public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
```
说明：
- 避免 魔法值
- 统一格式
- final → 不能修改
- static → 类级共享
- 常量命名 → 全大写 + 下划线 ✔

⸻

🧩 关键知识点三：忽略未知字段
```
this.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
```
含义：
- JSON 多字段不报错
- 高兼容性
- 后端对前端变更更健壮

❌ 否则会抛异常：
```
UnrecognizedPropertyException
```

⸻

🧩 关键知识点四：注册时间处理模块
```
SimpleModule simpleModule = new SimpleModule()
    .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(...))
```
作用：

✔ 解决 Jackson 不支持 Java 8 时间类问题
✔ 自定义时间格式
✔ 防止 timestamp 输出（1970 long值）

支持类型：

- Java 类型	JSON 例子
- LocalDate	“2025-01-01”
- LocalTime	“18:20:01”
- LocalDateTime	“2025-01-01 18:20”


⸻

🔗 总结一句话（可背面试）
```
该类自定义了 Jackson 的 ObjectMapper，
统一配置了日期时间的序列化/反序列化格式，
忽略未知字段异常，
并注册 Java 8 时间模块，
实现了项目级 JSON 解析行为的标准化。
```
⸻

⭐ Bonus：你刚好问到的 JSON {} / []
```
在你这个类里关系是：
- ObjectMapper 负责 → {} ↔ Java Object
- 也负责 → [] ↔ List / Collection
```
例子：

```JSON 对象 {} → Java Bean```

```{"id": 1, "name": "Tom"}```

→ User

JSON 数组 [] → List
```
[{"id":1},{"id":2}]

→ List<User>
```
⸻

👉 要不要继续深入？

我可以帮你做：
- ✅ 画 JacksonObjectMapper UML 类图
- ✅ 写 @Configuration 注入 Spring Boot
- ✅ 出 JSON ↔ Java 反序列化练习题
- ✅ 讲清楚：
@RestController + @RequestBody 里是怎么调用它的
