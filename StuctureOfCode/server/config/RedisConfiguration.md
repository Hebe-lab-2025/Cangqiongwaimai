```
package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("开始创建redis模板对象...");
        RedisTemplate redisTemplate = new RedisTemplate();
        //设置redis的连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置redis key的序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
```

这是 **Redis 配置类**，核心就两件事 👍

### ✅ 一句话讲清楚

👉 在 Spring 中创建一个 **RedisTemplate Bean**，并把 **Redis 的 key 设为字符串存储**，方便阅读与使用。

---

## 🧭 代码做了什么（超简明）

1. `@Configuration`

   * 说明：这是配置类

2. `@Bean redisTemplate(...)`

   * 说明：向 Spring 容器**注册 RedisTemplate**

3. `setConnectionFactory`

   * 说明：指定用哪个 Redis 连接（地址、端口、密码都来自 yml）

4. `setKeySerializer(new StringRedisSerializer())`

   * 🔥 重点
   * 让 **key 以字符串形式存储**
   * 在 redis-cli 里不会乱码
   * 例如：

     ```
     user:token:123
     code:login:phone
     ```

---

## ⚠️ 默认 value 序列化方式

* key ✔ 可读字符串
* value ❌ 仍是 JDK 二进制（看不懂）

如果你想 value 也是 JSON（推荐生产）

👉 再加一行：

```java
redisTemplate.setValueSerializer(new org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer());
```

---

## 🧠 总结

| 作用               | 结果 |
| ---------------- | -- |
| 创建 RedisTemplate | ✔  |
| 配置连接工厂           | ✔  |
| key 变可读字符串       | ✔  |
| value 仍是 JDK 序列化 | ⚠️ |

---

如果你愿意，我可以继续帮你：

* 🚀 配完整 JSON 版 RedisTemplate
* 🧪 演示验证码/缓存/登录保存到 Redis
* 📌 讲 Redis 五大数据结构在项目中的使用

很好，这段总结已经非常清晰、准确 👍
你对这个 Redis 配置类的理解是 **完全正确的**，尤其是下面三点：

* ✔️ 这是 Spring 的配置类
* ✔️ 注册并配置 `RedisTemplate`
* ✔️ 将 **key 序列化为字符串，避免乱码**

👉 你还正确指出：**value 默认是 JDK 序列化，不可读**，这也是面试和实战中的高频点。

---

如果你想把它升级为 **“项目里常用的 JSON 版本 RedisTemplate”**，推荐这样改 👇

```java
redisTemplate.setKeySerializer(new StringRedisSerializer());
redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
redisTemplate.setHashKeySerializer(new StringRedisSerializer());
redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
redisTemplate.afterPropertiesSet();
```

这样可以得到：

* ✅ key 可读
* ✅ value 是 JSON
* ✅ Hash 结构也可读
* ✅ 兼容对象存储（User/DTO 都行）

---

### 你接下来想让我帮你哪一块？👇

你可以选一个告诉我：

1️⃣ **完整 JSON 版 RedisTemplate 配置类（生产可用）**
2️⃣ **结合业务示例：验证码 / token / 缓存登录状态**
3️⃣ **解释 Redis 五种数据结构如何在项目中用**
4️⃣ **讲解 RedisTemplate 和 StringRedisTemplate 区别**
5️⃣ **深入序列化：JDK vs JSON vs ProtoStuff 对比**

我可以按你现在学的进度来讲，不会脱节 😊


下面是可直接放到 **GitHub README** 的完整 Markdown 版本（结构清晰、可复制）。
我已经补充了 **生产可用配置代码 + 业务示例 + 面试要点**👇

---

# ✅ Spring Boot Redis Configuration — RedisTemplate Explained

This document explains how to configure `RedisTemplate` in Spring Boot, why we set serializers, and how to use it in real projects.

---

## ⭐ What this configuration does (in one sentence)

👉 It creates a **RedisTemplate Bean** and makes **Redis keys readable strings instead of binary garbage**.

---

## 🧩 Full Redis Configuration Class (Copy & Use)

```java
package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // Set Redis connection
        redisTemplate.setConnectionFactory(connectionFactory);

        // Key as readable string
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // Value stored as JSON instead of Java binary
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Hash key readable
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // Hash value JSON
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Initialize
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
```

---

## 🎯 Why we must set serializers

### ❌ Default behavior (bad)

| Item  | Format                |
| ----- | --------------------- |
| key   | binary                |
| value | JDK serialized binary |

Problems:

* unreadable in redis-cli
* hard to debug
* not cross-language
* migration unfriendly

---

### ✅ After configuration

| Item       | Format          |
| ---------- | --------------- |
| key        | readable string |
| value      | JSON            |
| hash key   | string          |
| hash value | JSON            |

Example in Redis CLI:

```
user:token:123
login:captcha:18888888888
cart:user:9527
```

Readable ✔
Debuggable ✔
Frontend compatible ✔

---

## 🛠 Quick Usage Examples

### 1️⃣ Save login verification code

```java
redisTemplate.opsForValue().set("login:code:18888888888", "839201");
```

### 2️⃣ Save login token

```java
redisTemplate.opsForValue().set("user:token:123", "eyJhbGciOiJIUzI1NiJ9");
```

### 3️⃣ Save Java Object as JSON automatically

```java
User user = new User(1, "Tom");
redisTemplate.opsForValue().set("user:1", user);
```

Stored JSON automatically:

```json
{
  "id": 1,
  "name": "Tom"
}
```

No extra code required 🎉

---

## 🧪 Hash example (common in caching user profile)

```java
redisTemplate.opsForHash().put("user:profile:1", "name", "Tom");
redisTemplate.opsForHash().put("user:profile:1", "age", "18");
```

CLI output:

```
hgetall user:profile:1
```

Readable ✔

---

## 🔥 `RedisTemplate` vs `StringRedisTemplate`

| Feature         | RedisTemplate | StringRedisTemplate |
| --------------- | ------------- | ------------------- |
| key             | Object        | String              |
| value           | Object        | String              |
| JSON support    | yes           | no                  |
| recommended for | real projects | simple KV           |

👉 Rule of thumb

* Business system → `RedisTemplate`
* Simple string cache → `StringRedisTemplate`

---

## 🧠 Common Interview Questions (with answers)

### ❓ Why set `StringRedisSerializer` for keys?

Because default key is binary and **unreadable in redis-cli**.

---

### ❓ Why use `GenericJackson2JsonRedisSerializer`?

* cross-language
* version upgrade safe
* readable JSON
* avoids JDK serialization header

---

### ❓ Can Redis store Java objects?

Yes ✔
They are automatically converted to **JSON** via serializer.

---

## 🧭 Summary

| Goal                 | Achieved |
| -------------------- | -------- |
| create RedisTemplate | ✔        |
| key readable         | ✔        |
| value JSON           | ✔        |
| hash JSON            | ✔        |
| production ready     | ✔        |

---

## 🚀 What you can learn next

* Redis 5 data structures
* Cache penetration/breakdown/avalanche
* Spring Cache + Redis
* Lua + Redis distributed lock
* Token + Session storage design

---

### 👍 If you want, I can continue with:

* ✔ login & token full code
* ✔ 秒杀防超卖 Redis + Lua
* ✔ Spring Cache + Redis project integration
* ✔ Redis cluster + Sentinel explanation



