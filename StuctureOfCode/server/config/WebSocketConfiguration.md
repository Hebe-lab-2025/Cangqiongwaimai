```
package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类，用于注册WebSocket的Bean
 */
@Configuration
public class WebSocketConfiguration {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
```

很好 👍 这是 **Spring WebSocket 配置类**。
我给你一份**可以直接放在 GitHub README 的完整 Markdown 说明**（含概念+使用+注意事项+示例）。


好～给你一份 **完全中文说明版**，可以直接放到 **GitHub / README.md** 里使用 👍
（包含：作用解释、代码、什么时候用/不用、示例、常见问题）

---

# ✅ Spring Boot WebSocket 配置说明

此配置类用于在 Spring Boot 中注册使用 `@ServerEndpoint` 注解的 WebSocket 服务端。

---

## ⭐ 一句话说明

👉 创建一个 **ServerEndpointExporter Bean**，让所有带 `@ServerEndpoint` 的类自动生效并注册为 WebSocket 服务。

---

## 📌 完整代码

```java
package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类，用于注册WebSocket的Bean
 */
@Configuration
public class WebSocketConfiguration {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
```

---

## 🧠 ServerEndpointExporter 是什么？

`ServerEndpointExporter` 的作用：

* 扫描所有 `@ServerEndpoint` 注解
* 自动注册为 WebSocket 端点
* 让 WebSocket 在 Spring Boot 中生效

如果**没有**它：

❌ `@ServerEndpoint` 不会生效
❌ 客户端连接失败
❌ WebSocket 端点无法注册

如果**有**它：

✔ 自动注册端点
✔ 无需额外 XML 配置
✔ 与 Spring Boot 嵌入式容器完美结合

---

## 🧭 什么时候需要配置它？

👇 **需要配置的场景**

* 使用 **Spring Boot 内嵌容器**

  * 内嵌 Tomcat（默认）
  * 内嵌 Jetty
  * 内嵌 Undertow

---

## 🚫 什么时候不需要配置？

👇 **不要配置的情况**

* 部署到外部独立服务器：

  * 外置 Tomcat
  * Jetty
  * WebLogic
  * WildFly

原因：

⚠️ 外部容器已自动注册
⚠️ 重复注册会报错

---

## 🛠 示例：定义一个 WebSocket 端点

```java
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

@ServerEndpoint("/ws/chat")
@Component
public class ChatWebSocket {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("连接建立：" + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("收到消息：" + message);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("连接关闭：" + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
}
```

---

## 💬 浏览器客户端测试示例

```javascript
const socket = new WebSocket("ws://localhost:8080/ws/chat");

socket.onopen = () => socket.send("你好 WebSocket!");

socket.onmessage = (msg) => console.log(msg.data);
```

---

## 🎯 常见应用场景

* 实时聊天系统
* 消息推送
* 订单状态推送
* 实时股票/行情
* 实时监控大屏
* 游戏实时同步

---

## 🧩 总结表

| 功能                   | 情况 |
| -------------------- | -- |
| 注册 WebSocket 端点      | ✔  |
| 扫描 `@ServerEndpoint` | ✔  |
| Spring Boot 内嵌容器必需   | ✔  |
| 外置容器不要用              | ⚠️ |

---

## 🔥 面试高频问题

### ❓ 为什么需要 ServerEndpointExporter？

👉 因为 Spring Boot 使用嵌入式容器，并**不会自动注册 JSR-356 WebSocket 端点**。

---

### ❓ 不用 STOMP 可以吗？

✔ 可以
👉 `@ServerEndpoint` 就是 **原生 WebSocket API**

对比：

| 技术           | 说明                |
| ------------ | ----------------- |
| 原生 WebSocket | `@ServerEndpoint` |
| 消息代理模式       | STOMP + SockJS    |
| Spring 封装    | `@MessageMapping` |

---

## 🚀 你可以继续学这些（可选）

告诉我你选哪一项 👇

1️⃣ WebSocket 群发消息
2️⃣ 私聊（点对点推送）
3️⃣ 在线用户列表与登录绑定
4️⃣ 前端 HTML Demo 页面
5️⃣ WebSocket + Token 鉴权
6️⃣ Spring Security + WebSocket 集成


---


# ✅ Spring Boot WebSocket Configuration

This configuration registers WebSocket endpoints annotated with `@ServerEndpoint` in a Spring Boot application.

---

## ⭐ What this class does (in one sentence)

👉 It creates a **ServerEndpointExporter Bean** so that all classes annotated with `@ServerEndpoint` are automatically registered as WebSocket endpoints.

---

## 📌 Full Code

```java
package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket configuration.
 * Registers WebSocket endpoints annotated with @ServerEndpoint
 */
@Configuration
public class WebSocketConfiguration {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
```

---

## 🧠 What is `ServerEndpointExporter`?

`ServerEndpointExporter`:

* scans `@ServerEndpoint` classes
* registers them with underlying WebSocket container
* allows WebSocket to work inside Spring Boot

Without it:

❌ `@ServerEndpoint` will not take effect
❌ client cannot connect
❌ no endpoint gets registered

With it:

✔ WebSocket endpoint auto-discovery
✔ you don’t need XML config
✔ integrates Java EE WebSocket with Spring Boot

---

## 🧭 When you MUST use it

You need `ServerEndpointExporter` when:

* using **Spring Boot embedded container**
  (e.g., `spring-boot-starter-tomcat` embedded)

---

## 🚫 When you should NOT use it

Do **NOT** declare this bean when:

* deploying to **external container** like:

  * standalone Tomcat
  * Jetty
  * WebLogic
  * WildFly

Because external servers already register endpoints.

Otherwise:

⚠️ double registration
⚠️ exceptions about endpoint already exists

---

## 🛠 How to define a WebSocket endpoint

Example endpoint class 👇

```java
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

@ServerEndpoint("/ws/chat")
@Component
public class ChatWebSocket {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connection opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received: " + message);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Connection closed: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
}
```

---

## 💬 Test via JavaScript client

```javascript
const socket = new WebSocket("ws://localhost:8080/ws/chat");

socket.onopen = () => socket.send("Hello Server!");

socket.onmessage = (msg) => console.log(msg.data);
```

---

## 🎯 Typical use cases

* real-time chat
* notification push
* stock price streaming
* dashboard update push
* game live state sync

---

## 🧩 Summary

| Feature                    | Status |
| -------------------------- | ------ |
| Registers WebSocket Bean   | ✔      |
| Scans `@ServerEndpoint`    | ✔      |
| Works with embedded server | ✔      |
| Avoid external server case | ⚠️     |

---

## 🔥 Common interview questions

### ❓ Why do we need `ServerEndpointExporter`?

Because Spring Boot uses embedded container and **does not auto-register JSR-356 WebSocket endpoints**.

---

### ❓ Can WebSocket work without STOMP?

Yes:

* `@ServerEndpoint` → Java standard API
* no extra broker needed

---

### ❓ Difference between:

| Type                         | Tech              |
| ---------------------------- | ----------------- |
| Low-level WebSocket          | `@ServerEndpoint` |
| High-level message broker    | STOMP + SockJS    |
| Spring Messaging abstraction | `@MessageMapping` |

---

## 🚀 Want the next step?

Tell me which one you want:

1️⃣ **WebSocket + Login token + user online list**
2️⃣ **Broadcast message example**
3️⃣ **Point-to-point messaging example**
4️⃣ **Front-end HTML demo page**
5️⃣ **Spring Security + WebSocket integration**



