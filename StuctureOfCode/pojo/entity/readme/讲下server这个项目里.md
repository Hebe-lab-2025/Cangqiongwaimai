很好 👍 我就专门讲 sky-server 这个项目本身——你可以把它理解为：

-  这是“真正运行起来的后端服务”
-  所有接口、业务逻辑、数据库访问都在这里发生
-  其它模块都是给它“打工”的

⸻

🧭 sky-server 项目总体作用

sky-server 负责：
- 启动 Spring Boot 应用
- 对外暴露 REST API
- 承载业务逻辑
- 访问数据库、Redis、OSS、微信等外部系统
- 登录鉴权（JWT）
- 异步任务、WebSocket 推送

- 👉 你每天访问的接口，其实都是 sky-server 提供的

⸻

📂 sky-server 目录逐个讲（很重要）

我按你截图里的包名解释：

⸻

-  1）controller —— 对外 API（前端调用这里）
```
controller
 ├─ admin（管理端）
 └─ user（用户端）
```
做的事：
- 接收 HTTP 请求
- 参数校验
- 调用 service
- 返回统一 Result / VO

示例：
```
@PostMapping("/login")
public Result login(@RequestBody EmployeeLoginDTO dto){
    return employeeService.login(dto);
}
```
- 👉 不能写 SQL、不能写业务细节
- 👉 只做“入口 + 出口”

⸻

-  2）service —— 业务层（最核心）

service
 ├─ impl（实现类）
 └─ interface（接口）

负责：
- 业务流程编排
- 事务 @Transactional
- 组合多张表操作
- 调用 mapper
- 调用 Redis、OSS、微信支付

例如：
```
public void submitOrder(OrdersSubmitDTO dto){
    // 1. 校验购物车
    // 2. 计算金额
    // 3. 保存订单
    // 4. 扣库存
}
```
- 👉 真正的“业务大脑”在这里

⸻

-  3）mapper —— 数据访问层（DAO）
```
mapper
 └─ *.xml (resources/mapper/)
```
作用：
- 操作数据库
- MyBatis 映射
- 只写 CRUD，不写业务

示例：
```
@Mapper
public interface OrdersMapper {
    void insert(Orders order);
}
```
配 XML：
```
<insert id="insert">
    insert into orders (...) values (...)
</insert>
```
- 👉 你可以这样理解：

Service 说“我要下单”
Mapper 说“我去数据库写一条记录”

⸻

-  4）config —— SpringBoot 配置类
```
config
 ├─ WebMvcConfiguration
 ├─ SwaggerConfig
 └─ Redis config 等
```
功能：
- JSON 转换器
- 拦截器注册
- Swagger 文档
- 静态资源映射
- CORS

⸻

-  5）interceptor —— 拦截器（JWT 登录校验）

例：
- JwtTokenUserInterceptor
- JwtTokenAdminInterceptor

功能：
- 未登录禁止访问
- 校验 token 合法性
- 将 userId 存入 ThreadLocal

- 👉 等价于：大门口的保安

⸻

-  6）aspect —— AOP 切面（可选）

功能示例：
- 日志增强
- 权限切面
- 自动填充 createTime / updateTime

例如保存谁创建的数据

⸻

-  7）handler —— 全局异常处理

统一把异常 → Result.error

好处：
- controller 不用 try…catch
- 错误结构统一
- 防止框架异常泄漏

⸻

-  8）task —— 定时任务

例：
- 自动完成订单
- 每日统计报表
- 超时关单

⸻

-  9）websocket —— 实时消息推送

用于：
- 管理端推送新订单提醒
- 实时状态更新

⸻

-  🔟 SkyApplication —— 启动类
```
@SpringBootApplication
public class SkyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
    }
}
```
- 👉 你点击绿色三角运行的就是它

⸻

🧩 一句话总结 sky-server

- 👉 sky-server = 真正跑起来的后端
- 👉 对外提供接口
- 👉 承载业务逻辑
- 👉 调用数据库、Redis、消息队列、OSS、微信

其它模块都只是帮它“提供类和工具”

⸻

🎁 送你一张“请求全流程图”
```
前端 -> Controller -> Service -> Mapper -> Database
                  ↓
                 VO/Result -> 返回前端
```

⸻

🙌 你可以继续让我讲：
- 🔍 “从点餐到支付”完整调用链讲解
- 🧭 画一个“请求时序图”
- 🧪 模拟面试：请解释后端项目架构
- 🛠 手把手带你新建一个 server 模块
- 🔍 任选一个类我逐行讲（比如 OrderController）
