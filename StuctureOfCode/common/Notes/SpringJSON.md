太好了，这 5 个刚好是一整套，“理解 + 实战 + 面试” 全配齐 👌
我一次性给你，都用 Markdown，方便你直接存笔记。

⸻

1️⃣ FastJSON ↔ Jackson 对照表

功能 & API 对照

场景	FastJSON	Jackson	说明
```
对象 ➜ JSON 字符串	JSON.toJSONString(obj)	objectMapper.writeValueAsString(obj)	FastJSON 工具方法风格；Jackson 依赖 ObjectMapper 实例
JSON 字符串 ➜ 对象	JSON.parseObject(str, User.class)	objectMapper.readValue(str, User.class)	常见反序列化
JSON 字符串 ➜ List	JSON.parseArray(str, User.class)	objectMapper.readValue(str, new TypeReference<List<User>>() {})	集合在 Jackson 里要用 TypeReference
动态 JSON 对象	JSONObject obj = JSON.parseObject(str)	JsonNode node = objectMapper.readTree(str)	JSONObject vs JsonNode
动态 JSON 数组	JSONArray arr = JSON.parseArray(str)	ArrayNode arr = (ArrayNode) objectMapper.readTree(str)	两者都是树模型
忽略未知字段	默认宽松，可配 Feature	mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false)	你在 JacksonObjectMapper 里已经这样配了
自定义日期格式	@JSONField(format = "yyyy-MM-dd HH:mm:ss") 或全局配置	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") 或自定义 ObjectMapper	两者都支持注解 + 全局配置
Spring MVC 默认集成	❌ 不是默认	✅ 默认（MappingJackson2HttpMessageConverter）	Spring Boot 默认用 Jackson
中文社区	很多（国内用得多）	也很多	
历史安全问题	多次 RCE 漏洞（autoType）	相对更稳定	下面单独讲
```

⸻

2️⃣ FastJSON 安全漏洞风险与防范

2.1 发生过什么风险？

历史上 FastJSON 1.x 多次出现 反序列化漏洞，核心点：
- 支持 autoType：根据 JSON 中的 @type 字段，自动加载指定类并反序列化
- 攻击者可以构造恶意 payload：
- 指定某些 “gadget” 类（如某些有副作用的类）
- 触发远程代码执行（RCE）或命令执行

典型危险 payload 示例（示意）：
```
{
  "@type": "com.xxx.MaliciousClass",
  "cmd": "whoami"
}
```
如果：
- autoType 打开
- 类在 classpath 可访问
- 反序列化到 Object / Map 之类

👉 就有可能触发危险行为。

所以 FastJSON 一直在修补黑名单/白名单列表。
现在官方更推荐使用 fastjson2，架构和安全策略都有改进。

⸻

2.2 防范思路（实战建议）

✅ 1. 升级版本
- 推荐用 fastjson2
- 如果必须用 fastjson 1.x，务必使用最新版本（漏洞已被修复的版本）

✅ 2. 禁用 autoType（关键！）

确保你项目里没有类似：

```ParserConfig.getGlobalInstance().setAutoTypeSupport(true);  // ❌ 非常危险```

如果要启用，必须 严格指定白名单包：

```ParserConfig.getGlobalInstance().addAccept("com.sky.xxx.");```

⚠️ 一般 Web 应用 不建议随便开 autoType。

✅ 3. 不要解析不可信任的 JSON 到 Object / Map<Object,Object>

更安全的方式：
- 用具体类型解析：

```User u = JSON.parseObject(json, User.class);```

- 而不是：

```Object obj = JSON.parse(json);      // ❌ 更容易触发 autoType```

✅ 4. 边界放在 Controller 里
- 对外 HTTP 接口 → 建议用 Jackson + Spring MVC 的 @RequestBody
- FastJSON 只在：
- 内部调用第三方接口
- 内网工具
- 已经有强控制的场景

⸻

3️⃣ 10 道 Spring JSON 处理面试题（附简短英文答案）

Q1. How does Spring Boot convert Java objects to JSON in a @RestController?

```
Spring Boot uses HttpMessageConverter implementations.
For JSON, it uses MappingJackson2HttpMessageConverter by default,
which delegates to a Jackson ObjectMapper to serialize and deserialize objects.
```

⸻

Q2. What is the difference between @Controller and @RestController?

```
@RestController is a convenience annotation that combines @Controller and @ResponseBody.
It tells Spring to write the return value directly to the HTTP response body as JSON or another format,
instead of rendering a view.
```
⸻

Q3. How do you customize JSON date format globally in Spring Boot?
```
I define a custom ObjectMapper bean, or customize Jackson through a Jackson2ObjectMapperBuilder.
For example, I register a module that sets serializers and deserializers for LocalDateTime,
or I use a dedicated config class like JacksonObjectMapper and inject it into Spring.
```
（这里就是你的 JacksonObjectMapper）

⸻

Q4. How does @RequestBody work?
```
@RequestBody tells Spring to read the HTTP request body,
convert the JSON into a Java object using HttpMessageConverter,
and bind it to the method parameter.
```
⸻

Q5. How do you ignore unknown JSON properties during deserialization?
```
With Jackson, I configure the ObjectMapper with
mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
or I use the @JsonIgnoreProperties(ignoreUnknown = true) annotation on the class.
```
⸻

Q6. How do you return a different JSON structure to the client than your entity model?
```
I use DTO or VO classes.
I map from entities to DTOs (for example with BeanUtils or MapStruct)
and let Spring serialize the DTOs instead of exposing internal entities directly.
```
⸻

Q7. How do you handle JSON validation errors in Spring Boot?
```
I use @Valid or @Validated on @RequestBody parameters,
add constraints like @NotNull or @Size on the DTO,
and define an @ExceptionHandler(MethodArgumentNotValidException.class)
to convert validation errors into a clean JSON response.
```
⸻

Q8. How can you support both JSON and XML in one Spring Boot API?
```
I add both JSON and XML HttpMessageConverter implementations to the configuration,
and Spring uses content negotiation based on the Accept header or URL extension.
For example, Jackson for JSON and Jackson XML or JAXB for XML.
```
⸻

Q9. What is HttpMessageConverter and when would you write a custom one?
```
HttpMessageConverter is the strategy interface that converts HTTP requests and responses
to and from Java objects.
I write a custom converter when I need to support a non-standard media type or custom encoding,
or when I need specific logic beyond what Jackson provides.
```
⸻

Q10. How do you log JSON request/response bodies safely?
```
I usually use Spring filters or logback appenders to log JSON,
but I make sure to mask sensitive fields like passwords or tokens.
I never log the entire body blindly in production for security and performance reasons.
```
⸻

4️⃣ 自定义 HttpMessageConverter 示例

这里给你一个简单又实用的例子：
在 Spring Boot 中，用自定义的 JacksonObjectMapper 替换默认的 JSON 转换器。

4.1 自定义 ObjectMapper（你已经有）
```
// com.sky.json.JacksonObjectMapper
public class JacksonObjectMapper extends ObjectMapper {
    // 你的代码（日期格式 + 忽略未知字段 + 注册模块）
}
```
4.2 自定义配置：注册 HttpMessageConverter
```
import com.sky.json.JacksonObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public ObjectMapper objectMapper() {
        // 使用你自定义的 JacksonObjectMapper
        return new JacksonObjectMapper();
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建使用自定义 ObjectMapper 的 JSON 转换器
        MappingJackson2HttpMessageConverter jacksonConverter =
                new MappingJackson2HttpMessageConverter(objectMapper());

        // 放在第一位，优先使用
        converters.add(0, jacksonConverter);
    }
}
```
📌 效果：
- 所有 @RestController 的 JSON 读写
- 都走你的 JacksonObjectMapper 规则：
日期格式 / 忽略未知字段 / LocalDateTime 支持等

⸻

5️⃣ 重构项目：统一 JSON 序列化策略（建议方案）

你现在项目的典型状态：
- Controller / Service → Spring + Jackson（@RestController）
- 调用第三方接口 → 有 FastJSON（JSON.parseObject）
- 又有 JacksonObjectMapper → 做了日期统一

一个合理的 实战重构策略：

Step 1：明确“边界”
- 对外 API（前端、App 调用）：
- 一律使用 Jackson（Spring 默认，配合你的 JacksonObjectMapper）
- 对第三方 HTTP 接口（微信、百度地图等）：
- 可以继续使用 FastJSON（动态 JSON 更方便）
- 或逐步迁移到 Jackson 的 JsonNode

⸻

Step 2：统一“项目内部 JSON 工具”
```
比如写一个工具类 JsonUtils：

public class JsonUtils {

    private static final ObjectMapper mapper = new JacksonObjectMapper();

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialize error", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialize error", e);
        }
    }
}
```
要求团队约定：
- 业务代码不再直接 new ObjectMapper / JSON.toJSONString
- 都用 JsonUtils.*
- 这样未来要换实现（比如 Jackson ➜ Gson）也很容易

⸻

Step 3：控制 FastJSON 使用范围
- 只允许在类似 WeChatPayUtil、HttpClient 调第三方 这类组件中使用 FastJSON：
- 例如：JSONObject jsonObject = JSON.parseObject(response);
- 不允许到处在业务层随意 JSON.parseObject / toJSONString

可以按包做约束：
- com.sky.integration.*：可以 FastJSON
- com.sky.controller / service / mapper：内部只用 Jackson / DTO

⸻

Step 4：统一返回格式（你项目里已经有）

一般会有类似：
```
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
}
```
搭配你自己的异常体系 + GlobalExceptionHandler，
统一前端看到的 JSON 格式，例如：
```
{
  "code": 0,
  "msg": "OK",
  "data": {...}
}
```

⸻

Step 5：写到简历 / 面试里的总结句
```
In my Spring Boot project, I unified JSON serialization using a custom Jackson ObjectMapper,
configured in a global HttpMessageConverter.
Business APIs all rely on this Jackson configuration for consistent date formats and error responses,
while FastJSON is only used in isolated integration components for dynamic third-party JSON.
This separation keeps the core application consistent and secure, and makes it easier to evolve the JSON layer later.
```
⸻

如果你愿意，下一步我可以帮你把：
- GlobalExceptionHandler + Result + BaseException + JacksonObjectMapper
组合成一段 “后端基础框架设计” 的英文项目亮点，
你可以直接放简历 or 面试说。
