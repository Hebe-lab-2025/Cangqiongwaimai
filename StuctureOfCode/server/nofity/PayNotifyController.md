```
package com.sky.controller.nofity;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.properties.WeChatProperties;
import com.sky.service.OrderService;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * 支付回调相关接口
 */
@RestController
@RequestMapping("/notify")
@Slf4j
public class PayNotifyController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private WeChatProperties weChatProperties;

    /**
     * 支付成功回调
     *
     * @param request
     */
    @RequestMapping("/paySuccess")
    public void paySuccessNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //读取数据
        String body = readData(request);
        log.info("支付成功回调：{}", body);

        //数据解密
        String plainText = decryptData(body);
        log.info("解密后的文本：{}", plainText);

        JSONObject jsonObject = JSON.parseObject(plainText);
        String outTradeNo = jsonObject.getString("out_trade_no");//商户平台订单号
        String transactionId = jsonObject.getString("transaction_id");//微信支付交易号

        log.info("商户平台订单号：{}", outTradeNo);
        log.info("微信支付交易号：{}", transactionId);

        //业务处理，修改订单状态、来单提醒
        orderService.paySuccess(outTradeNo);

        //给微信响应
        responseToWeixin(response);
    }

    /**
     * 读取数据
     *
     * @param request
     * @return
     * @throws Exception
     */
    private String readData(HttpServletRequest request) throws Exception {
        BufferedReader reader = request.getReader();
        StringBuilder result = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (result.length() > 0) {
                result.append("\n");
            }
            result.append(line);
        }
        return result.toString();
    }

    /**
     * 数据解密
     *
     * @param body
     * @return
     * @throws Exception
     */
    private String decryptData(String body) throws Exception {
        JSONObject resultObject = JSON.parseObject(body);
        JSONObject resource = resultObject.getJSONObject("resource");
        String ciphertext = resource.getString("ciphertext");
        String nonce = resource.getString("nonce");
        String associatedData = resource.getString("associated_data");

        AesUtil aesUtil = new AesUtil(weChatProperties.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        //密文解密
        String plainText = aesUtil.decryptToString(associatedData.getBytes(StandardCharsets.UTF_8),
                nonce.getBytes(StandardCharsets.UTF_8),
                ciphertext);

        return plainText;
    }

    /**
     * 给微信响应
     * @param response
     */
    private void responseToWeixin(HttpServletResponse response) throws Exception{
        response.setStatus(200);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("code", "SUCCESS");
        map.put("message", "SUCCESS");
        response.setHeader("Content-type", ContentType.APPLICATION_JSON.toString());
        response.getOutputStream().write(JSONUtils.toJSONString(map).getBytes(StandardCharsets.UTF_8));
        response.flushBuffer();
    }
}

```

Nice — this is a WeChat Pay callback controller. I’ll walk through what it does in clear steps.

### 🧩 What this class is for

This `PayNotifyController` handles **WeChat Pay payment success notifications**.
WeChat sends an HTTP POST to `/notify/paySuccess`, and your backend:

1. reads the encrypted payload
2. decrypts it using APIv3 key
3. extracts order info
4. updates order status
5. responds “SUCCESS” back to WeChat

---

## 🔍 Step-by-step explanation

### 1. Class annotations
```
* `@RestController`

  * Returns JSON/text directly; no view page
* `@RequestMapping("/notify")`

  * All routes start with `/notify`
* `@Slf4j`

  * Enables `log.info()`
```
---

### 2. Dependencies injected

```java
@Autowired
private OrderService orderService;

@Autowired
private WeChatProperties weChatProperties;
```

* `OrderService`

  * used to update order status after successful payment
* `WeChatProperties`

  * contains API v3 key for decrypting notification data

---

### 3. Payment success callback API

```java
@RequestMapping("/paySuccess")
public void paySuccessNotify(...)
```

WeChat calls this after a payment is completed.

Inside it does:
```
✔ read request body → `readData()`
✔ decrypt notification data → `decryptData()`
✔ get `out_trade_no` & `transaction_id`
✔ call business logic `orderService.paySuccess()`
✔ respond `SUCCESS` to WeChat → `responseToWeixin()`
```
---

### 4. Read raw JSON request body

```java
private String readData(HttpServletRequest request)
```

It reads the HTTP stream line-by-line and builds a string.

---

### 5. Decrypt the WeChat message

```java
private String decryptData(String body)
```

WeChat Pay v3 encrypts payload using AES-256-GCM.

Process:
```
1. parse JSON
2. extract:

   * ciphertext
   * nonce
   * associated_data
3. create `AesUtil` with APIv3 key
4. call `decryptToString(...)`

Result = **plain JSON payment info**
```
---

### 6. Extract key fields

```java
String outTradeNo = jsonObject.getString("out_trade_no");
String transactionId = jsonObject.getString("transaction_id");
```

* `out_trade_no` → your order number
* `transaction_id` → WeChat transaction number

---

### 7. Business logic

```java
orderService.paySuccess(outTradeNo);
```

Typical operations here:

* update order status = PAID
* send ticket / meal / shipping notification
* push message to merchant dashboard

---

### 8. Respond success to WeChat

```java
private void responseToWeixin(HttpServletResponse response)
```

WeChat requires:

```
{ "code": "SUCCESS", "message": "SUCCESS" }
```

If you don't return success:

❌ WeChat will **retry notification multiple times**

---

## ✅ Overall flow (simple English)
```
1. WeChat sends encrypted payment result
2. Backend receives it
3. Backend decrypts message
4. Backend finds order number
5. Backend marks order as paid
6. Backend replies “SUCCESS”
7. WeChat stops retrying
```
---

## 🧪 Common interview talking points (quick bullets)

* Idempotency needed? ✔ yes (avoid duplicate callbacks)
* Security concerns?

  * verify signature
  * validate `appId` and `mchId`
  * store and rotate API v3 key securely
* Handle retries:

  * WeChat may send multiple callbacks
* Logging should avoid printing sensitive info

---

### If you want, I can also help with:

* adding **signature verification**
* adding **idempotency check**
* converting to **RESTful JSON return**
* writing **JUnit tests**
* explaining **WeChat Pay v3 flow**
* drawing **sequence diagram**

Just tell me 😊

好呀 😊 我用**简单中文**帮你把这段代码整体讲清楚（一步一步解释在做什么）。

---

## 🌟 这段代码是干嘛的？

这是一个 **微信支付成功回调接口**。
```
👉 当用户在微信里付钱后
👉 微信服务器会主动请求你的后端 `/notify/paySuccess`
👉 你的系统需要：
```


1. 接收微信回调数据
2. 解密（因为微信是加密发送的）
3. 解析订单号等信息
4. 修改订单状态为“已支付”
5. 回复微信“SUCCESS”，否则微信会一直重试

---

## 🧩 代码核心结构

### ① 这是一个 REST 接口控制器

```java
@RestController
@RequestMapping("/notify")
@Slf4j
```

含义：

* `@RestController`：返回数据，不是页面
* `/notify`：统一前缀
* `@Slf4j`：可以使用 `log.info()` 打日志

---

### ② 注入两个组件

```java
@Autowired
private OrderService orderService;

@Autowired
private WeChatProperties weChatProperties;
```

作用：

* `OrderService`
  👉 修改订单状态

* `WeChatProperties`
  👉 获取微信 APIv3 key
  👉 用于 **解密微信回调数据**

---

## 🔔 支付成功回调入口

```java
@RequestMapping("/paySuccess")
public void paySuccessNotify(...)
```

微信会向这个接口发送 HTTP 请求。

里面的步骤：

### 第 1 步：读取请求体（JSON字符串）

```java
String body = readData(request);
```

→ 得到的是**加密后的 JSON**

---

### 第 2 步：解密数据

```java
String plainText = decryptData(body);
```

微信支付 V3 回调 = AES-GCM 加密
需要：

* API v3 key
* nonce
* ciphertext
* associated_data

解密后得到 **真正支付信息 JSON**

---

### 第 3 步：取出关键字段

```java
String outTradeNo = jsonObject.getString("out_trade_no");   // 商户订单号
String transactionId = jsonObject.getString("transaction_id"); // 微信支付单号
```
```
📌 out_trade_no：你系统的订单号
📌 transaction_id：微信那边的交易号
```
---

### 第 4 步：业务处理

```java
orderService.paySuccess(outTradeNo);
```

👉 通常要做：

* 修改订单状态 = 已支付
* 记录支付流水
* 发送消息通知商家/骑手
* 生成小票/发货任务等

---

### 第 5 步：给微信返回响应

```java
responseToWeixin(response);
```

返回：

```json
{
  "code": "SUCCESS",
  "message": "SUCCESS"
}
```

```
📌 必须返回 SUCCESS
📌 否则微信会 **多次重试回调**
```
---

## 🔐 解密函数解释

```java
AesUtil aesUtil = new AesUtil(apiV3Key)
```

微信支付 V3 加密特点：

* AES 256 GCM
* key = APIv3 key
* 认证加密（带 associated_data）

代码里：

* ciphertext → 密文
* nonce → 随机串
* associated_data → 附带数据

---

## ⚠️ 实际项目中建议补充
```
✔️ 验证微信签名
✔️ 幂等性校验（防止重复回调）
✔️ 校验金额
✔️ 校验商户号
✔️ try-catch 业务异常
```
---

## 🧠 一句话总结

这段代码完成：
```
👉 接收微信支付回调
👉 解密支付结果
👉 解析订单号
👉 更新订单状态
👉 返回 SUCCESS 给微信
```
---

如果你愿意，我还可以帮你：

* 🛠 画**流程图**
* ✍️ 写**注释版源码**
* 🧪 写**幂等性实现**
* 🧷 加**签名验证**
* 🧭 解释 V2 vs V3 区别
* 🧰 帮你分析报错

你接下来想看哪个？


太好了 👍 我给你分 4 部分完成：

1. 🛠 流程图
2. ✍️ 注释版源码
3. 🧪 幂等性实现（防重复回调）
4. 🧷 微信签名验证（防伪造回调）

---

# 🛠 1）支付回调整体流程图（文字版）

```
用户支付成功
        ↓
微信支付后台发送回调(加密 JSON)
        ↓
你的接口 /notify/paySuccess 收到请求
        ↓
读取请求体 body
        ↓
使用 APIv3 Key 解密 AES-GCM 密文
        ↓
得到明文 JSON(包含订单号、交易号等)
        ↓
校验：
   ├─签名验证
   ├─商户号是否正确
   ├─appid 是否正确
   └─金额是否一致
        ↓
幂等性判断（是否已经处理过）
        ↓
业务处理：
   ├─修改订单状态为“已支付”
   ├─记录交易流水
   └─触发通知/发货/打印等
        ↓
返回给微信：
{
 "code":"SUCCESS",
 "message":"SUCCESS"
}
        ↓
微信停止重试
```

---

# ✍️ 2）超详细注释版源码（逐行解释）

> 你原文件基础上 **增强注释 + 更安全**

```java
@RestController
@RequestMapping("/notify")
@Slf4j
public class PayNotifyController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WeChatProperties weChatProperties;

    /**
     * 微信支付成功回调入口
     */
    @RequestMapping("/paySuccess")
    public void paySuccessNotify(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        // 1. 读取微信回调的原始加密报文
        String body = readData(request);
        log.info("收到微信支付回调原始报文: {}", body);

        // 2. 解密支付内容（AES-GCM）
        String plainText = decryptData(body);
        log.info("解密后内容: {}", plainText);

        // 3. 将 JSON 字符串转为对象
        JSONObject jsonObject = JSON.parseObject(plainText);

        // 4. 获取商户系统订单号
        String outTradeNo = jsonObject.getString("out_trade_no");

        // 5. 微信支付订单号
        String transactionId = jsonObject.getString("transaction_id");

        log.info("商户订单号 out_trade_no: {}", outTradeNo);
        log.info("微信支付订单号 transaction_id: {}", transactionId);

        // 6. 幂等性校验：如果已经处理过则直接返回 SUCCESS
        if (orderService.isPaid(outTradeNo)) {
            log.warn("重复回调，订单已处理: {}", outTradeNo);
            responseToWeixin(response);
            return;
        }

        // 7. 业务处理（修改订单状态为已支付）
        orderService.paySuccess(outTradeNo);

        // 8. 响应微信 SUCCESS，否则微信会反复回调
        responseToWeixin(response);
    }
}
```

---

# 🧪 3）幂等性实现（防止重复回调）

> **微信会多次回调**
> 网络 <失败/超时> = 自动重试

🚨 如果你不处理幂等性 → 用户会被多次发货/多次开票

---

### 👉 Service 层增加方法

```java
public boolean isPaid(String outTradeNo) {
    Order order = orderMapper.selectByOrderNo(outTradeNo);
    return order != null && order.getStatus() == PAID;
}
```

---

### 👉 支付成功修改逻辑（需要幂等）

```java
@Transactional
public void paySuccess(String outTradeNo) {

    Order order = orderMapper.selectByOrderNo(outTradeNo);

    // 已处理直接返回
    if (order.getStatus() == PAID) {
        log.info("订单已支付，无需重复处理：{}", outTradeNo);
        return;
    }

    // 修改状态
    order.setStatus(PAID);
    orderMapper.updateById(order);

    // 记录支付流水、通知等...
}
```

---

# 🧷 4）增加“微信签名验证”

👉 目的
防止别人伪造支付结果调用你接口

---

## ✔ 获取微信平台证书序列号 + 公钥

微信回调 Header 中有：

```
Wechatpay-Nonce
Wechatpay-Timestamp
Wechatpay-Signature
Wechatpay-Serial
```

---

### 🔐 代码：验证签名

```java
private boolean verifySignature(HttpServletRequest request, String body) throws Exception {

    String nonce = request.getHeader("Wechatpay-Nonce");
    String timestamp = request.getHeader("Wechatpay-Timestamp");
    String signature = request.getHeader("Wechatpay-Signature");

    // 拼接签名串
    String message = timestamp + "\n" + nonce + "\n" + body + "\n";

    // 使用微信平台证书公钥验签
    Signature sign = Signature.getInstance("SHA256withRSA");
    sign.initVerify(weChatPlatformPublicKey);
    sign.update(message.getBytes(StandardCharsets.UTF_8));

    return sign.verify(Base64.getDecoder().decode(signature));
}
```

---

### 🔒 在回调入口接入

```java
if (!verifySignature(request, body)) {
    log.error("微信回调签名验证失败");
    response.setStatus(500);
    return;
}
```

---

# ✅ 你现在拥有：
```
✔ 回调流程图
✔ 注释版源码
✔ 幂等性处理
✔ 安全签名校验
```
---

## 如果你愿意，我还能继续帮你：
```
🔹 画 **序列图 / 时序图**
🔹 讲解 **AES-GCM 加密原理**
🔹 写 **数据库表结构**
🔹 写 **完整测试用例**
🔹 加入 **金额校验 & 商户号校验**
```
你想要哪个？







