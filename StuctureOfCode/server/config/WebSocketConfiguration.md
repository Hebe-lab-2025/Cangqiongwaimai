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


太好了，这 6 个一起学刚好可以变成一个**完整的 WebSocket 聊天 Demo**，我直接给你一份可以放到 GitHub 的 **README.md**，包含后端代码 + 前端 HTML + 鉴权思路，全中文说明。你可以整块复制用。

---

# 💬 Spring Boot WebSocket 实战：群发 / 私聊 / 在线用户 / Token 鉴权

基于 `@ServerEndpoint` + `ServerEndpointExporter` 的简单聊天系统示例。

> **前置条件**：
> 已经有下面这个配置类（上一个消息里讲过）：

```java
@Configuration
public class WebSocketConfiguration {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
```

---

## 目录

1. [WebSocket 群发消息](#1-websocket-群发消息)
2. [私聊（点对点）](#2-私聊点对点推送)
3. [在线用户列表与登录绑定](#3-在线用户列表与登录绑定)
4. [前端 HTML Demo 页面](#4-前端-html-demo-页面)
5. [WebSocket + Token 鉴权](#5-websocket--token-鉴权思路)
6. [Spring Security + WebSocket 集成思路](#6-spring-security--websocket-集成思路)

---

## 1️⃣ WebSocket 群发消息

### 1.1 后端：简单群聊端点

```java
package com.sky.websocket;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 简单群聊 WebSocket，所有人互相能看到消息
 */
@ServerEndpoint("/ws/chat")
public class ChatBroadcastEndpoint {

    // 保存所有连接中的会话（线程安全）
    private static final Set<Session> SESSIONS = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.add(session);
        System.out.println("有新连接加入，sessionId = " + session.getId());
        sendToAll("系统提示：有新用户加入，当前在线人数：" + SESSIONS.size());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        String text = "用户[" + session.getId() + "]：" + message;
        System.out.println("收到消息：" + text);
        sendToAll(text);
    }

    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session);
        System.out.println("连接关闭，sessionId = " + session.getId());
        sendToAll("系统提示：有用户离开，当前在线人数：" + SESSIONS.size());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("连接异常，sessionId = " + (session != null ? session.getId() : "null"));
        error.printStackTrace();
    }

    // 群发工具方法
    private void sendToAll(String message) {
        for (Session s : SESSIONS) {
            try {
                s.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

---

## 2️⃣ 私聊（点对点推送）

### 2.1 思路

* 使用 `userId` 作为用户身份标识
* 通过 WebSocket 的 **URL 参数** 传入，例如：
  `ws://localhost:8080/ws/chat?userId=tom`
* 在 `@OnOpen` 里解析 `userId`，保存到 `Map<userId, Session>`
* 收到消息时，区分：

  * 无前缀 → 群发
  * `@某人:内容` → 私发给某人

> 示例协议：
> `@jack:hello` → 发给 userId 为 `jack` 的用户

### 2.2 私聊 + 群聊混合示例

```java
package com.sky.websocket;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/ws/im")
public class ChatPrivateEndpoint {

    // userId -> Session
    private static final Map<String, Session> USER_SESSION_MAP = new ConcurrentHashMap<>();

    // 全部 session，用于群发
    private static final Set<Session> SESSIONS = new CopyOnWriteArraySet<>();

    private String userId; // 当前连接绑定的用户ID

    @OnOpen
    public void onOpen(Session session) {
        this.userId = parseUserId(session);
        if (userId == null || userId.isEmpty()) {
            // 没有 userId，直接关闭
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "userId is required"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        USER_SESSION_MAP.put(userId, session);
        SESSIONS.add(session);

        System.out.println("用户上线：userId = " + userId + " , sessionId = " + session.getId());
        sendToAll("系统提示：用户 [" + userId + "] 上线，当前在线人数：" + USER_SESSION_MAP.size());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("收到消息，来自 " + userId + " ：" + message);

        // 私聊协议：@toUserId:内容
        if (message.startsWith("@")) {
            int index = message.indexOf(":");
            if (index > 1) {
                String toUserId = message.substring(1, index);
                String content = message.substring(index + 1);
                sendToUser(toUserId, "【私聊】来自 [" + userId + "]：" + content);
            } else {
                sendToUser(userId, "系统提示：私聊格式错误，应为 @userId:内容");
            }
        } else {
            // 否则群发
            sendToAll("【群聊】[" + userId + "]：" + message);
        }
    }

    @OnClose
    public void onClose(Session session) {
        if (userId != null) {
            USER_SESSION_MAP.remove(userId);
        }
        SESSIONS.remove(session);
        System.out.println("用户下线：userId = " + userId);
        sendToAll("系统提示：用户 [" + userId + "] 下线，当前在线人数：" + USER_SESSION_MAP.size());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket 发生错误，userId = " + userId);
        error.printStackTrace();
    }

    // 工具：解析 URL 里的 userId
    private String parseUserId(Session session) {
        String query = session.getQueryString(); // 例如 userId=tom
        if (query == null || query.isEmpty()) {
            return null;
        }
        // 只处理最简单的情况：userId=xxx
        String[] parts = query.split("&");
        for (String part : parts) {
            if (part.startsWith("userId=")) {
                return URLDecoder.decode(part.substring("userId=".length()), StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    // 群发
    private void sendToAll(String message) {
        for (Session s : SESSIONS) {
            try {
                s.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 发给某个用户
    private void sendToUser(String toUserId, String message) {
        Session targetSession = USER_SESSION_MAP.get(toUserId);
        if (targetSession == null) {
            // 对方不在线，回一条提示给自己
            Session selfSession = USER_SESSION_MAP.get(userId);
            if (selfSession != null) {
                try {
                    selfSession.getBasicRemote().sendText("系统提示：用户 [" + toUserId + "] 不在线");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        try {
            targetSession.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

---

## 3️⃣ 在线用户列表与登录绑定

### 3.1 在线用户 Map

上面已经有了：

```java
private static final Map<String, Session> USER_SESSION_MAP = new ConcurrentHashMap<>();
```

它本身就是一个简单的**在线用户表**：

* `key`：业务侧 userId（可以是用户表主键 / 手机号 / 邮箱）
* `value`：对应的 WebSocket 会话

### 3.2 暴露一个获取在线用户数量的方法（可选工具类）

```java
public static int getOnlineCount() {
    return USER_SESSION_MAP.size();
}
```

如果想在别的地方用，可以把 Map 抽到一个单独的 `WebSocketSessionManager` 类里。

> 注意：
>
> * 这是单机内存方案
> * 多实例部署时，需要用 Redis / DB 来同步在线状态（属于进阶内容）

---

## 4️⃣ 前端 HTML Demo 页面

一个最简单的静态页面，可以直接放 `resources/static/ws-demo.html`。

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>WebSocket 聊天 Demo</title>
</head>
<body>
<h2>WebSocket 聊天 Demo</h2>

<div>
    <label>用户ID：</label>
    <input id="userId" placeholder="例如: tom">
    <button onclick="connect()">连接</button>
    <button onclick="disconnect()">断开</button>
</div>

<div style="margin-top: 10px;">
    <textarea id="log" cols="80" rows="15" readonly></textarea>
</div>

<div style="margin-top: 10px;">
    <input id="msg" style="width: 400px;" placeholder="普通消息=群发，@userId:内容=私聊">
    <button onclick="sendMsg()">发送</button>
</div>

<script>
    let socket = null;

    function log(text) {
        const logArea = document.getElementById("log");
        logArea.value += text + "\n";
        logArea.scrollTop = logArea.scrollHeight;
    }

    function connect() {
        const userId = document.getElementById("userId").value.trim();
        if (!userId) {
            alert("请先输入用户ID");
            return;
        }

        if (socket && socket.readyState === WebSocket.OPEN) {
            alert("已经连接了");
            return;
        }

        const url = "ws://" + window.location.host + "/ws/im?userId=" + encodeURIComponent(userId);
        socket = new WebSocket(url);

        socket.onopen = function () {
            log("✅ 连接成功");
        };

        socket.onmessage = function (event) {
            log("📩 收到：" + event.data);
        };

        socket.onclose = function () {
            log("❌ 连接关闭");
        };

        socket.onerror = function (error) {
            log("⚠️ 发生错误: " + error);
        };
    }

    function disconnect() {
        if (socket) {
            socket.close();
            socket = null;
        }
    }

    function sendMsg() {
        const text = document.getElementById("msg").value;
        if (!socket || socket.readyState !== WebSocket.OPEN) {
            alert("请先连接 WebSocket");
            return;
        }
        socket.send(text);
        document.getElementById("msg").value = "";
    }
</script>

</body>
</html>
```

> 使用方式：
> 启动 Spring Boot → 浏览器打开
> `http://localhost:8080/ws-demo.html`
> 开两个浏览器窗口，分别填 `tom`、`jack`，互相发消息测试。

---

## 5️⃣ WebSocket + Token 鉴权思路

**目标**：
只允许携带合法 Token 的用户建立 WebSocket 连接。

> 简化方案（演示级）：
> 使用 URL 参数传 Token：
> `ws://localhost:8080/ws/im?userId=tom&token=xxxx`

### 5.1 在 `@OnOpen` 里解析 token 并校验

这里只写伪代码 / 示例逻辑：

```java
private String token;

@OnOpen
public void onOpen(Session session) {
    Map<String, String> params = parseQueryParams(session.getQueryString());
    this.userId = params.get("userId");
    this.token = params.get("token");

    if (!isValidToken(userId, token)) {
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Invalid token"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    // 后面逻辑同前面：加入在线用户 Map 等
}
```

`isValidToken` 的实现可以是你项目里的逻辑，例如：

* JWT 校验
* Redis 里查登录态
* 调用用户服务接口

> **注意**：
> `@ServerEndpoint` 里直接注入 Spring Bean 需要额外处理（例如借助静态 SpringContextHolder），这一块属于进阶用法，这里就用 `isValidToken` 代表即可。

---

## 6️⃣ Spring Security + WebSocket 集成思路

这里给的是**整体思路**，而不是完整大段配置代码，方便你以后接住 Security 相关内容。

### 6.1 常见几种做法

1. **先用 HTTP 登录 → 再用 WebSocket**

   * 用户先通过正常登录接口（表单 / JSON）获取 Token / Session
   * 前端在连接 WebSocket 时带上：

     * `Cookie`（包含 Session）
     * 或 URL 参数中的 Token
   * WebSocket 只负责读取已经认证过的身份

2. **通过 Filter 在握手前做校验（进阶）**

   * 自定义 `Filter` 或 `HandshakeInterceptor`
   * 在 HTTP 升级为 WebSocket 前，从 Header / 参数里取 Token
   * 校验后，把用户信息放入 `Session` 或 `SecurityContext`

### 6.2 简单 HttpSecurity 例子（伪代码）

如果你用 `Spring Security`：

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/ws/**").permitAll()   // 先放开 WebSocket 地址（Demo）
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

实际生产中，你可以：

* 对 `/login` 提供登录接口
* 对 `/ws/**` 要求携带 Token，并在 Filter 中验签
* 验签通过后，允许升级为 WebSocket 连接

---

## ✅ 总结

* `ServerEndpointExporter`：让 `@ServerEndpoint` 在 Spring Boot 中生效
* 群发：用 `Set<Session>` 循环发送
* 私聊：用 `Map<userId, Session>` 精准发送
* 在线用户：`Map` 的 key 集合就是在线列表
* 前端：原生 `WebSocket` + 简单 HTML 即可调试
* 鉴权：在 `@OnOpen` 里解析 `userId + token`，不合法就关闭连接
* Security：推荐「先 HTTP 认证，再 WebSocket 复用身份」的方式

---

如果你愿意，下一步我可以帮你做一个：

* ✅ **“订单状态推送”版本**（比如下单后实时推送状态变化）
* 或者把上面代码**改成更贴近外卖项目的业务字段**（比如 `userId` 改成 `userId/shopId/orderId` 组合之类的）。

