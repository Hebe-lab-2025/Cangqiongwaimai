```
package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，用于创建AliOssUtil对象
 */
@Configuration
@Slf4j
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("开始创建阿里云文件上传工具类对象：{}",aliOssProperties);
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
```

很好，这个 **OssConfiguration** 是一个典型的 Spring Boot 配置类，用来**创建阿里云 OSS 上传工具类对象（AliOssUtil Bean）**。
我把关键点讲清楚👇

---

## 🧭 这段代码整体做什么？

👉 在 Spring 容器中注册一个：

```
AliOssUtil Bean
```

作用：

* 封装阿里云 OSS 上传功能
* 其它类可以直接 `@Autowired` 注入使用
* 不需要每次 new 工具类

---

## 🔍 逐行拆解理解

### 1）配置类声明

```java
@Configuration
public class OssConfiguration {
```

含义：

* 告诉 Spring：这里面有 Bean 定义
* 启动时会执行

---

### 2）日志注解

```java
@Slf4j
```

作用：

* 自动生成 `log.info()` 日志对象
* 方便打印配置参数

---

### 3）创建 Bean

```java
@Bean
@ConditionalOnMissingBean
public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
```

含义：

| 注解                          | 作用                |
| --------------------------- | ----------------- |
| `@Bean`                     | 添加到 Spring 容器     |
| `@ConditionalOnMissingBean` | 只有不存在同类型 Bean 才创建 |

👉 防止重复创建 Bean
👉 方便自定义扩展覆盖

---

### 4）从配置对象中取值

```java
aliOssProperties.getEndpoint()
aliOssProperties.getAccessKeyId()
aliOssProperties.getAccessKeySecret()
aliOssProperties.getBucketName()
```

这些值来源于：

```
application.yml
```

例如：

```yaml
sky:
  alioss:
    endpoint: xxx.aliyuncs.com
    access-key-id: xxx
    access-key-secret: xxx
    bucket-name: my-bucket
```

---

### 5）创建工具类

```java
return new AliOssUtil(...);
```

👉 里面封装上传方法

你之前看过：

```java
ossClient.putObject(bucketName, objectName, InputStream)
```

---

## 🧰 最终使用方式

任何地方可以这样注入：

```java
@Autowired
private AliOssUtil aliOssUtil;
```

然后：

```java
String url = aliOssUtil.upload(bytes, fileName);
```

---

## 🧠 一句话总结

> 这个配置类负责创建并管理 `AliOssUtil`，
> 读取 OSS 配置 → 封装工具 → 提供 Bean 注入。

---

如果你愿意，我还能继续帮你👇

* 🧪 讲 `AliOssProperties` 的作用
* ⚙️ application.yml OSS 配置示例
* 🚀 写一个前端 + 后端完整文件上传流程
* 🛡️ 讲 OSS 防盗链 / 过期访问签名
* ☁️ 对比：OSS vs MinIO vs AWS S3


