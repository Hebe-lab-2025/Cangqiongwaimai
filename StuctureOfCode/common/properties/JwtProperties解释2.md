```
package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.jwt")
@Data
public class JwtProperties {

    /**
     * 管理端员工生成jwt令牌相关配置
     */
    private String adminSecretKey;
    private long adminTtl;
    private String adminTokenName;

    /**
     * 用户端微信用户生成jwt令牌相关配置
     */
    private String userSecretKey;
    private long userTtl;
    private String userTokenName;

}
```

很好，这是典型的 Spring Boot YAML 配置说明题 👍
我按模块帮你逐项讲清楚，每一行“是干嘛的，在代码里怎么用”。

⸻

🗂 顶层前缀：sky
```
sky:

👉 代表你项目自己的业务配置命名空间
（避免与 spring 官方配置冲突）

对应的 Java 包：

com.sky.properties
com.sky.config
```

⸻

🔐 ① JWT 模块 —— sky.jwt
```
sky:
  jwt:
```
👉 存放 登录 token 相关配置

Spring Boot 会绑定到：
```
@ConfigurationProperties(prefix = "sky.jwt")
public class JwtProperties { ... }
```

⸻

🧾 admin 端（后台管理系统）

```
🔑 admin-secret-key

admin-secret-key: itcast
```
👉 JWT 签名用的 密钥

用途：
- 	生成 token
- 	校验 token 是否被篡改

Java 中使用：

```jwtBuilder.signWith(SignatureAlgorithm.HS256, adminSecretKey);```


⸻

⏳ admin-ttl

```admin-ttl: 7200000```

👉 token 有效期（毫秒）

```7200000 ms = 2 小时```

后台登录两小时后需要重新登录

⸻

🏷 admin-token-name

```admin-token-name: token```

👉 前端请求头中 携带 token 的 key 名

例如 HTTP 请求：

```Authorization: token xxxxxx```

后端拦截器读取：

```String token = request.getHeader(jwtProperties.getAdminTokenName());```


⸻

👤 user 端（小程序 / App）
```
user-secret-key

user-secret-key: itheima
```
👉 和 admin 密钥 分开存

原因：
- 	防止权限混用
- 	一端泄露不影响另一端
- 	可设置不同有效期策略

⸻
```
user-ttl

user-ttl: 7200000
```
👉 普通用户 token 有效期 = 2 小时

⸻
```
user-token-name

user-token-name: authentication
```
👉 用户端请求头 token 名称

⸻

☁ ② 阿里云 OSS 存储 —— sky.alioss
```
alioss:
  endpoint:
  access-key-id:
  access-key-secret:
  bucket-name:
```
👉 用于 文件上传（图片/视频/菜单图片）

常见用途：
- 	菜品图片
- 	套餐图片
- 	用户头像

含义说明：

| 配置 | 作用 |
|------|------|
| `endpoint` | OSS 访问域名 |
| `access-key-id` | 账户 Access Key |
| `access-key-secret` | 账户 Secret |
| `bucket-name` | 存储空间名称 |


在代码里：

```OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);```


⸻

💰 ③ 微信支付 —— sky.wechat
```
wechat:

👉 微信小程序 + 微信支付配置
```
📱 小程序登录

| 配置 | 作用 |
|------|------|
| `appid` | 小程序 ID |
| `secret` | 小程序密钥 |


💳 支付商户信息

| 配置 | 作用 |
|------|------|
| `mchid` | 商户号 |
| `mchSerialNo` | 商户证书序列号 |
| `privateKeyFilePath` | 商户私钥文件路径 |
| `apiV3Key` | 回调报文解密密钥（API v3 Key） |
| `weChatPayCertFilePath` | 微信支付平台证书文件路径 |


🔔 回调地址

| 配置 | 作用 |
|------|------|
| `notifyUrl` | 支付成功回调地址 |
| `refundNotifyUrl` | 退款结果回调地址 |


⸻

🛒 ④ 商家门店信息 —— sky.shop
```
shop:
  address: 北京市海淀区上地十街10号
```
👉 商家默认配送地址

用途：
- 	计算配送距离
- 	百度 / 高德地图 API 定位
- 	运费计算

⸻

🗺 ⑤ 百度地图 —— sky.baidu
```
baidu:
  ak: your-ak
```
👉 百度地图 API key

用途：
- 	地址解析
- 	距离计算
- 	外卖配送范围限制

⸻

🎯 总结一句话
```
sky.* 是你项目自定义业务配置，
通过 @ConfigurationProperties 自动绑定到 Java 类，
用于 JWT、OSS、微信支付、门店、地图等功能。
```
⸻

🔍 你学到的关键点
- 	YAML 层级结构
- 	prefix 实现属性绑定
- 	jwt admin/user 分离设计
- 	token 名称可配置
- 	微信/支付/OSS 参数来源配置中心
- 	真实企业项目结构

⸻

👉 下一步我可以帮你：
- 	🔐 画 配置 → Properties → 业务类 流程图
- 	🧩 写 JwtUtil 完整代码
- 	🛡 写拦截器验证 token
- 	🧪 出 10 道配置 + JWT 面试题
- 	📝 生成你的学习笔记 Markdown 版

只要告诉我一句：

👉 “来流程图” / “来面试题” / “来代码”
