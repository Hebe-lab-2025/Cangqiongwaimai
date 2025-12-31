太好了，这几块加起来刚好是一整套“Jackson + 时间处理”的知识体系。我给你一口气整理好，直接可以当笔记 / 面试素材用。👇

⸻

1️⃣ JacksonObjectMapper 类结构图（UML）

用 Mermaid 表示（你可以直接贴进支持 Mermaid 的工具里看图）：
```
classDiagram
    class ObjectMapper {
        <<class>>
        +writeValueAsString(Object): String
        +readValue(String, Class)
        +registerModule(Module)
        +configure(DeserializationFeature, boolean)
    }

    class JacksonObjectMapper {
        <<class>>
        +DEFAULT_DATE_FORMAT: String
        +DEFAULT_DATE_TIME_FORMAT: String
        +DEFAULT_TIME_FORMAT: String
        +JacksonObjectMapper()
    }

    class SimpleModule {
        <<class>>
        +addSerializer(Class, JsonSerializer)
        +addDeserializer(Class, JsonDeserializer)
    }

    class LocalDateTimeSerializer
    class LocalDateSerializer
    class LocalTimeSerializer
    class LocalDateTimeDeserializer
    class LocalDateDeserializer
    class LocalTimeDeserializer

    ObjectMapper <|-- JacksonObjectMapper
    JacksonObjectMapper --> SimpleModule
    SimpleModule --> LocalDateTimeSerializer
    SimpleModule --> LocalDateSerializer
    SimpleModule --> LocalTimeSerializer
    SimpleModule --> LocalDateTimeDeserializer
    SimpleModule --> LocalDateDeserializer
    SimpleModule --> LocalTimeDeserializer
```
逻辑关系（口语版）：
- JacksonObjectMapper 继承 ObjectMapper
- 在构造器里：
- configure(...) 设置忽略未知字段
- new SimpleModule()，往里加各种 serializer / deserializer
- registerModule(simpleModule)
- 最后通过 WebMvcConfiguration.extendMessageConverters 把它塞进 MappingJackson2HttpMessageConverter 里，全局生效

⸻

2️⃣ 面试版解释回答（可背诵）

你可以直接背下面这一段，用在 “你们项目里 JSON 是怎么处理的？” 这种问题：

In my project I created a custom JacksonObjectMapper that extends Jackson’s ObjectMapper.
In the constructor I configure it to ignore unknown properties and register a SimpleModule
with custom serializers and deserializers for Java 8 time types like LocalDate, LocalTime, and LocalDateTime,
using our own date format patterns such as "yyyy-MM-dd HH:mm".
Then I plug this ObjectMapper into Spring MVC via a MappingJackson2HttpMessageConverter,
so all REST APIs use the same consistent JSON formatting and parsing rules.

如果面试官追问 “Why did you do that?” 可以接一句：

This gives us centralized control over JSON behavior,
avoids duplicated annotations on every DTO,
and prevents common issues with time formatting and unknown fields when front-end evolves.

⸻

3️⃣ SimpleDateFormat vs DateTimeFormatter 对比

这是面试&实战都很重要的一块，整理成表格：

| 对比项 | SimpleDateFormat | DateTimeFormatter |
|--------|------------------|-------------------|
| 所在包 / 版本 | `java.text`（老 API） | `java.time.format`（Java 8+） |
| 是否线程安全 | ❌ 不是线程安全 | ✅ 线程安全（不可变） |
| 是否可变 | 可变（内部有可变状态） | 不可变（immutable） |
| 设计风格 | 面向对象 + 维护内部状态 | 函数式 + 不可变 |
| 推荐程度 | 新项目不推荐（仅维护老代码时使用） | Java 8+ 推荐使用 |
| 典型用法 | `new SimpleDateFormat("yyyy-MM-dd")` | `DateTimeFormatter.ofPattern("yyyy-MM-dd")` |
| 配合的时间类 | `Date` / `Calendar` | `LocalDate` / `LocalDateTime` / `ZonedDateTime` 等 |
| 线程安全解决方案 | 每个线程一份 / ThreadLocal / 每次 new | 直接共享一个 `DateTimeFormatter` 常量 |


你现在在 JacksonObjectMapper 里用的是：

DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)

- 这是正确姿势，可以放心声明成 static final 常量任意共享。

⸻

4️⃣ 练习题：手写一个自定义 ObjectMapper

给你一个完整“小作业”+参考答案。

题目

写一个 CustomObjectMapper，要求：
```
	1.	忽略 JSON 中的未知字段
	2.	日期时间按 "yyyy/MM/dd HH:mm:ss" 格式进行序列化/反序列化
	3.	支持 LocalDateTime
	4.	可直接在 Spring Boot 中配置成全局 ObjectMapper
```
参考代码
```
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomObjectMapper extends ObjectMapper {

    public static final String DATE_TIME_PATTERN = "yyyy/MM/dd HH:mm:ss";

    public CustomObjectMapper() {
        super();

        // 1. 忽略未知字段
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 2. 注册 Java8 时间序列化/反序列化
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

        SimpleModule module = new SimpleModule()
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dtf))
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dtf));

        this.registerModule(module);
    }
}

如果要让 Spring 全局使用它，可以写：

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public ObjectMapper objectMapper() {
        return new CustomObjectMapper();
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter(objectMapper());
        converters.add(0, converter);
    }
}
```
这和你现在 JacksonObjectMapper + WebMvcConfiguration 的设计非常接近，本质一模一样。

⸻

5️⃣ 为什么 LocalDateTime 不能“默认就解析好 JSON”？

这是很多人踩的坑，几句话帮你讲清楚（可以面试说）。

5.1 JSON 规范本身没有“标准日期类型”

JSON 只有几种原始类型：
- string
- number
- boolean
- null
- object { }
- array [ ]

👉 没有专门的 date 类型。

日期时间在 JSON 里本质上都是 字符串，例如：
```
"2025-01-31 18:20"
"2025-01-31T18:20:00Z"
1675159200000
```
不同系统/团队可能用不同格式，Jackson 没法猜你到底想用哪一种。

⸻

5.2 Java 8 时间 API 不是老的 Date

旧时代：
```
- java.util.Date + SimpleDateFormat
- Jackson 对 Date 有默认的支持（比如 timestamp 或 ISO）
```
但 Java 8 推出了新的时间类型：
- LocalDate
- LocalTime
- LocalDateTime
- ZonedDateTime
…

这些类型一开始不是 Jackson 原生支持的，需要引入 jackson-datatype-jsr310 模块，并注册。

你现在做的事情，其实就是：

自己手动为 LocalDate / LocalDateTime / LocalTime
告诉 Jackson：
“它们应该怎么从字符串解析、怎么输出为字符串”。

⸻

5.3 如果不做配置，会发生什么？

典型异常：
```
com.fasterxml.jackson.databind.exc.InvalidDefinitionException:
Cannot construct instance of `java.time.LocalDateTime`
(no Creators, like default constructor, exist)
```
或者：
```
com.fasterxml.jackson.databind.exc.MismatchedInputException:
Cannot deserialize value of type `java.time.LocalDateTime`
from String "2025-01-31 18:20":
Failed to deserialize java.time.LocalDateTime:
(… formatter not found …)
```
原因就是：
- Jackson 知道这是一个 Java 类型
- 但不知道用什么格式解析那个字符串

你在 JacksonObjectMapper 里注册了：
```
.addDeserializer(LocalDateTime.class,
    new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)))
.addSerializer(LocalDateTime.class,
    new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)))
```
👉 就等于告诉它：

当你看到 LocalDateTime 时，
就按照 "yyyy-MM-dd HH:mm" 这个 pattern
去解析 / 输出。

⸻

5.4 面试版一句话总结
```
LocalDateTime is not a primitive JSON type,
and JSON itself has no standard date representation,
so Jackson cannot guess how to parse or format it by default.
We need to register the Java 8 time module and provide explicit DateTimeFormatter patterns
to tell Jackson how to serialize and deserialize LocalDateTime consistently.
```
⸻

如果你愿意，下一步我可以帮你把：
- JacksonObjectMapper + WebMvcConfiguration + BaseException + Result
串联成一段 “后端基础框架设计” 项目亮点，用英文写好，你可以直接放到简历 / 面试回答里。
