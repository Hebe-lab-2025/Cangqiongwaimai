```
package com.sky.config;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.interceptor.JwtTokenUserInterceptor;
import com.sky.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;
    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    /**
     * 注册自定义拦截器
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/employee/login");

        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/user/login")
                .excludePathPatterns("/user/shop/status");
    }

    @Bean
    public Docket docket1(){
        log.info("准备生成接口文档...");
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("苍穹外卖项目接口文档")
                .version("2.0")
                .description("苍穹外卖项目接口文档")
                .build();

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("管理端接口")
                .apiInfo(apiInfo)
                .select()
                //指定生成接口需要扫描的包
                .apis(RequestHandlerSelectors.basePackage("com.sky.controller.admin"))
                .paths(PathSelectors.any())
                .build();

        return docket;
    }

    @Bean
    public Docket docket2(){
        log.info("准备生成接口文档...");
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("苍穹外卖项目接口文档")
                .version("2.0")
                .description("苍穹外卖项目接口文档")
                .build();

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户端接口")
                .apiInfo(apiInfo)
                .select()
                //指定生成接口需要扫描的包
                .apis(RequestHandlerSelectors.basePackage("com.sky.controller.user"))
                .paths(PathSelectors.any())
                .build();

        return docket;
    }

    /**
     * 设置静态资源映射，主要是访问接口文档（html、js、css）
     * @param registry
     */
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始设置静态资源映射...");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 扩展Spring MVC框架的消息转化器
     * @param converters
     */
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        //创建一个消息转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //需要为消息转换器设置一个对象转换器，对象转换器可以将Java对象序列化为json数据
        converter.setObjectMapper(new JacksonObjectMapper());
        //将自己的消息转化器加入容器中
        converters.add(0,converter);
    }
}

```


对，这一行正是关键 wiring 点，我帮你拆开讲清楚：
```
MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
// 关键一行
converter.setObjectMapper(new JacksonObjectMapper());
converters.add(0, converter);
```

1️⃣ 这行代码在做什么？

文字版：
- 把你自定义的 JacksonObjectMapper
- 装进 Spring MVC 的 JSON 消息转换器里，
- 让 所有 @RestController 的 JSON 输入/输出
- 都按照你的规则来序列化 / 反序列化。

更具体一点：
```
	•	MappingJackson2HttpMessageConverter
	•	是 Spring MVC 用来处理 JSON ⇆ Java 对象 的核心组件
	•	默认内部有一个自己的 ObjectMapper
	•	setObjectMapper(new JacksonObjectMapper())
	•	把默认的 ObjectMapper 换成你自定义的那个子类：
	•	忽略未知字段
	•	自定义时间格式 yyyy-MM-dd HH:mm
	•	注册 LocalDate/LocalDateTime/LocalTime 序列化器
	•	converters.add(0, converter);
	•	插到 索引 0：让它优先级最高
	•	后面其它 converter（默认的）排在它后面
```


所以：
- 你的 JacksonObjectMapper 不只是一个工具类，
- 而是通过这行代码，被“嵌入”到了整个 Web 层的 JSON 处理链路里。

⸻

2️⃣ 结合请求/响应流程看一眼

举个真实请求例子：
```
POST /user/order/submit
Content-Type: application/json

{
  "addressBookId": 1,
  "remark": "少辣",
  "orderTime": "2025-01-01 18:30"
}
```
Spring 处理流程（简化）：

```
	1.	Spring 找到 OrderController.submit(@RequestBody OrdersSubmitDTO dto)
	2.	看到 @RequestBody + Content-Type: application/json
→ 使用 HttpMessageConverter 列表
	3.	第一位 converter 就是你加的：
```

```MappingJackson2HttpMessageConverter (with JacksonObjectMapper)```

```
	4.	这个 converter 使用 JacksonObjectMapper：
	•	按你定义的日期格式解析 "2025-01-01 18:30" → LocalDateTime
	•	忽略 JSON 里多余字段
	•	把 JSON {} 解析成 OrdersSubmitDTO 对象

返回响应时也是同样流程，只是方向反过来（Java 对象 → JSON 字符串）。
```
⸻

3️⃣ 面试话术（可以直接说）
```
In my project, I extend WebMvcConfigurationSupport and override extendMessageConverters.
I create a MappingJackson2HttpMessageConverter and set a custom JacksonObjectMapper on it,
which configures global JSON behavior such as date formats and ignoring unknown properties.
Then I insert this converter at index 0, so Spring MVC will always use my customized ObjectMapper
for all REST JSON serialization and deserialization.
```
⸻

4️⃣ 小优化建议（不是必须）

现在你是：

```converter.setObjectMapper(new JacksonObjectMapper());```

也可以稍微“Spring 化”一点（方便复用）：
```
@Autowired
private JacksonObjectMapper jacksonObjectMapper;

// ...

converter.setObjectMapper(jacksonObjectMapper);
```
或者：
```
@Bean
public ObjectMapper objectMapper() {
    return new JacksonObjectMapper();
}
```
本质一样：都是让 同一个配置好的 ObjectMapper
成为全局 JSON 处理的标准。

⸻

如果你愿意，下一步我可以帮你：
```
	•	画一张 “请求 → HttpMessageConverter → Controller” 的时序图（Mermaid）
	•	或者把 WebMvcConfiguration + JacksonObjectMapper + JwtInterceptor
整合成一段 “Web 层基础框架设计” 的英文项目说明。
```
