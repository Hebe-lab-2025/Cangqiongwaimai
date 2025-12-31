```
public class JacksonObjectMapper extends ObjectMapper {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    //public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
```

很好，这几行代码是 自定义 ObjectMapper 的配置常量定义部分，我给你用简单清晰的方式拆开解释 👇

⸻

✅ 1. 这一行在干嘛？

```public class JacksonObjectMapper extends ObjectMapper {```

- - -  含义：
- 你创建了 Jackson 的子类
- 继承自 ObjectMapper
- 目的是：在里面写自己的默认序列化/反序列化规则

📌 也就是说：

你不是直接用 Jackson 的默认行为
而是包装了一层：“我们的项目专用 JSON 规则”

⸻

✅ 2. 这三行是什么？
```
public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
```
- - -  它们是常量（constant），作用是：
- 日期格式
- 日期+时间格式
- 时间格式

常量名	含义	示例值
```
DEFAULT_DATE_FORMAT	只包含日期	2025-01-31
DEFAULT_DATE_TIME_FORMAT	日期+时间	2025-01-31 18:30
DEFAULT_TIME_FORMAT	只包含时间	18:30:00
```

⸻

🧠 3. 为什么要public static final？

```✔ public```

- - -  外部类也能用，例如：

```JacksonObjectMapper.DEFAULT_DATE_TIME_FORMAT```


⸻

✔ static

- - -  属于类，不属于对象
- - -  不用 new，也能直接用

⸻

✔ final

- - -  不能被修改（常量）

```DEFAULT_DATE_FORMAT = "abc"   // ❌ 会报错```

📌 这样做的好处：
- 防止被其他人随意改格式
- 全局统一规则
- 线程安全
- IDE 自动提示
- 不会出现魔法字符串 "yyyy-MM-dd"

⸻

🎯 4. 这些常量在哪里用？

- - -  用在下面这段：

```DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)```

意思是：
```
Jackson 在 转 JSON / 解析 JSON 时
按你指定的格式处理 LocalDate / LocalDateTime / LocalTime
```
⸻

⭐ 一句话总结（超好记）

这几行代码是定义 全局统一的日期/时间格式常量，
被 JacksonObjectMapper 用来自定义 JSON 的序列化与反序列化格式，
通过 public static final 保证不可变、全局共享、统一标准。

⸻

🙌 如果你愿意，我还能继续帮你：

你可以选 ⬇️

- 1️⃣ 画一张 JacksonObjectMapper 类结构图
- 2️⃣ 帮你写“面试版解释回答”（可背诵）
- 3️⃣ 帮你对比 SimpleDateFormat vs DateTimeFormatter
- 4️⃣ 写一个练习题：手写自定义 ObjectMapper
- 5️⃣ 讲清楚：为什么 LocalDateTime 不能默认 JSON 解析
