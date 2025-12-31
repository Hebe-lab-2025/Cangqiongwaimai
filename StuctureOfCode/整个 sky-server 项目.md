很好 👍 你现在已经把 整个 sky-server 项目结构看懂 80% 了，我们直接结合你截图里的包结构来讲，不空谈理论，只说你项目里具体“谁干啥”。

⸻

🧭 sky-server 项目结构作用说明（逐层讲）
```
sky-server
 ├─ annotation
 ├─ aspect
 ├─ config
 ├─ controller
 ├─ handler
 ├─ interceptor
 ├─ mapper
 ├─ service
 ├─ task
 ├─ websocket
 └─ SkyApplication
```
我下面一句话解释每一层👇

⸻

🏷 annotation —— 自定义注解

👉 你项目里的例子：
```
@AutoFill
```
作用：
- 给字段自动赋值（如 createTime、updateTime、userId）
- 自己造的小注解
- 不写重复样板代码

⸻

🌀 aspect —— AOP 切面编程

👉 对应：

```AutoFillAspect```

作用：
- 统一拦截 Mapper 方法
- 判断 @AutoFill 注解
- 自动填充公共字段

📌 关键词：AOP、无侵入增强

⸻

⚙️ config —— 框架级配置

包含：
- RedisConfiguration
- OssConfiguration
- WebMvcConfiguration
- WebSocketConfiguration

👉 作用：集中配置三方组件

例如：
- 自定义消息转换器 JSON
- 静态资源映射
- 跨域 CORS
- Swagger 文档
- WebSocket 通信
- OSS 文件上传

📌 面试官问：配置在哪？ ——就在这

⸻

🧭 controller —— 对外提供接口（HTTP API）

分两类：
```
admin 后台系统
user  小程序/客户端
```
举例：

| 包 | 给谁用 |
|----|---------|
| `controller.admin` | 商家 / 员工端 |
| `controller.user` | 顾客端 |


再细分功能模块：
```
EmployeeController
OrderController
DishController
SetmealController
ReportController
```
...

📌 作用总结
- 接收参数 DTO
- 返回结果 VO/Result
- 不写 SQL
- 不写事务
- 调用 Service

⸻

🧯 handler —— 全局异常处理

```GlobalExceptionHandler```

作用：
- 捕获所有异常
- 统一 JSON 返回格式
- 不让异常栈暴露给前端
- 把报错变成友好提示

⸻

🛡 interceptor —— 登录校验/JWT

包含：
```
JwtTokenAdminInterceptor
JwtTokenUserInterceptor
```
作用：
- 拦截请求
- 读取 token
- 校验登录
- 放入 BaseContext 当前 userId

📌 关键点：不是过滤器，是 Spring MVC 拦截器

⸻

🗺 mapper —— MyBatis 映射接口（DAO 层）

举例：
```
UserMapper
OrderMapper
DishMapper
SetmealMapper
```
作用：
- 定义数据库操作
- 调 XML 或注解 SQL
- 不做业务

📌 和数据库交互，专管 CRUD

⸻

🧠 service —— 业务逻辑层（核心）

包含接口 + impl 实现类
```
EmployeeService
EmployeeServiceImpl
OrderService
OrderServiceImpl
...
```
作用：
- 写业务规则
- 写事务（@Transactional）
- 组装多个 Mapper 操作
- 校验状态
- 执行核心流程

📌 真正的“业务大脑”

⸻

⏰ task —— 定时任务

例：
```
OrderTask
MyTask
```
作用：
- 取消超时订单
- 自动确认收货
- 统计报表
- 定时清理缓存

⸻

🌐 websocket —— 即时通讯

例：

```WebSocketServer```

作用：
- 实时推送新订单给商家端
- 催单提醒
- 不需要刷新页面

⸻

✅ 最重要的一条线：一次请求怎么走？

举例：用户下单
```
UserController
   ↓
OrderService
   ↓
OrderMapper
   ↓
MySQL
```
涉及：
- DTO（接收前端参数）
- Entity（对象持久化）
- VO（返回前端）
- Redis 缓存（可选）
- WebSocket 推送（新订单）

⸻

⭐ 你问的重点：MyBatis-Plus 可不可以不用 Mapper？

👉 你项目现在是 MyBatis 原生 + XML + Mapper 层

✔ 清晰
✔ 适合复杂 SQL
✔ 符合企业真实习惯

👉 如果换 MyBatis-Plus
- Service 可直接 CRUD
- 但你项目 SQL 很复杂
- 大量 join + 统计 + 分析报表

❌ 不推荐强行移除 mapper

⸻

🎯 一句话总结（你可以在面试说）
```
项目采用典型分层架构：Controller 负责接口输入输出，
Service 承担核心业务逻辑与事务管理，
Mapper 负责数据库访问；
通过 AOP + 自定义注解实现公共字段自动填充，
拦截器完成 JWT 登录校验，
WebSocket 实现实时消息推送，
定时任务处理订单超时与报表统计。
```
直接拉满 ✔✔✔

⸻

🙌 接下来我可以继续帮你

回复序号即可：

- 1️⃣ 逐行讲 EmployeeController
- 2️⃣ 逐行讲 OrderServiceImpl（下单全过程）
- 3️⃣ 讲 WebSocket 实现来单提醒
- 4️⃣ Mermaid 画一张系统架构图
- 5️⃣ 出 10 道 SpringBoot 架构面试题并给答案
