
好～那我就按你说的三块，一次性给全套：总流程图 → Orders 实战链路 → 面试题 + 参考答案 👍
ControllerServiceMapperAspectDB
⸻

1️⃣ 整体架构图：Controller → Service → Mapper → Aspect → DB

直接给你一张可贴进文档的 Mermaid 图：
```
flowchart LR

A[前端页面<br/>Vue / React] --> B[Controller<br/>CategoryController / OrderController]
B --> C[Service<br/>OrderServiceImpl]
C --> D[Mapper 接口<br/>OrderMapper.insert(orders)]

subgraph AOP层
    E[AutoFillAspect<br/>@Before Mapper 方法]
end

D -->|调用时被 AOP 拦截| E
E -->|反射填充公共字段<br/>createTime / createUser / updateTime / updateUser| D

D --> F[MyBatis / MyBatis-Plus<br/>SQL 映射 & 执行]
F --> G[(数据库<br/>orders 表 / order_detail 表)]

%% JWT & ThreadLocal
H[LoginController<br/>登录接口] --> I[解析 JWT]
I --> J[BaseContext.setCurrentId(userId)<br/>ThreadLocal 存当前用户]

%% 全局异常
C --> K{业务异常?}
K -- 抛出 BaseException 子类 --> L[GlobalExceptionHandler<br/>@ControllerAdvice]
L --> M[Result.error(msg)<br/>统一 JSON 返回]
```
你脑子里可以记成一句话：

前端 → Controller → Service → Mapper → AOP 切面（AutoFill）→ MyBatis → DB
JWT & ThreadLocal 负责“知道你是谁”，
全局异常负责“统一怎么报错”。

⸻

2️⃣ 用 Orders 举一个完整 AutoFill 链路（从 HTTP 请求到 SQL）

我们用你发过的 OrderServiceImpl.submitOrder 当例子。

场景：用户在小程序 / H5 下单

① 前端发请求
- URL：POST /user/order/submit
- Body 里带：收货地址 id、购物车信息等

② Controller 接收 DTO
```
@PostMapping("/submit")
public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO dto) {
    log.info("用户下单：{}", dto);
    OrderSubmitVO vo = orderService.submitOrder(dto);
    return Result.success(vo);
}
```
- 这里用的是 DTO：OrdersSubmitDTO，只承接前端入参。

③ Service 里组装 Entity

在你代码里：
```
@Transactional
public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
    // 省略：地址校验、购物车校验…

    Long userId = BaseContext.getCurrentId();

    Orders orders = new Orders();
    BeanUtils.copyProperties(ordersSubmitDTO, orders);
    orders.setOrderTime(LocalDateTime.now());
    orders.setPayStatus(Orders.UN_PAID);
    orders.setStatus(Orders.PENDING_PAYMENT);
    orders.setNumber(String.valueOf(System.currentTimeMillis()));
    orders.setAddress(addressBook.getDetail());
    orders.setPhone(addressBook.getPhone());
    orders.setConsignee(addressBook.getConsignee());
    orders.setUserId(userId);

    // ⭐ 关键：调用 mapper 插入订单
    orderMapper.insert(orders);

    // 后面：插入订单明细、清购物车、封装 VO 返回…
}
```
这一步：
- Orders = Entity，对应数据库 orders 表
- Service 负责：
- 校验
- 业务规则（状态、金额、编号…）
- 组装一个完整的 Orders 对象（还没填 createTime / createUser 这些）

④ Mapper 方法 + @AutoFill

你这边的 Mapper 一般像这样（伪代码）：
```
@Mapper
public interface OrderMapper {

    @AutoFill(OperationType.INSERT)
    void insert(Orders orders);

    // 其他方法...
}
```
- @AutoFill(OperationType.INSERT) 就是给 AOP 一个信号：
“这个方法是插入操作，请帮我自动填公共字段。”

⑤ 进入 AutoFillAspect（AOP 前置通知）

当 orderMapper.insert(orders) 被调用时：
```
@Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
public void autoFillPointCut(){}

@Before("autoFillPointCut()")
public void autoFill(JoinPoint joinPoint){
    log.info("开始进行公共字段自动填充...");

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
    OperationType operationType = autoFill.value(); // INSERT

    Object[] args = joinPoint.getArgs();
    if(args == null || args.length == 0){
        return;
    }

    Object entity = args[0];

    LocalDateTime now = LocalDateTime.now();
    Long currentId = BaseContext.getCurrentId(); // ⭐ 当前登录用户

    if(operationType == OperationType.INSERT){
        try {
            Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
            Method setCreateUser = entity.getClass().getDeclaredMethod("setCreateUser", Long.class);
            Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);

            setCreateTime.invoke(entity, now);
            setCreateUser.invoke(entity, currentId);
            setUpdateTime.invoke(entity, now);
            setUpdateUser.invoke(entity, currentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
这里做了几件事：
```
	1.	从方法签名上拿到 @AutoFill 注解，知道是 INSERT
	2.	从入参里拿到 Orders 实体对象
	3.	从 BaseContext 中拿当前用户 id（ThreadLocal）
	4.	通过 反射 找到 setCreateTime/setCreateUser/...
	5.	在真正执行 Mapper 逻辑前，先把字段都填好
```

此时内存中的 orders 对象已经是：
```
id = null（还没插入，MyBatis 会返回）
userId = 当前用户
status = 待支付
orderTime = now
createTime = now      ← AutoFill 填的
createUser = userId   ← AutoFill 填的
updateTime = now      ← AutoFill 填的
updateUser = userId   ← AutoFill 填的
...
```


⑥ Mapper + MyBatis 执行 SQL
- AOP 返回后，真正的 OrderMapper.insert(orders) 会走到 MyBatis 的 Mapper XML 里，生成类似 SQL：
```
INSERT INTO orders
(user_id, number, status, order_time,
 create_time, create_user, update_time, update_user, ...)
VALUES
(#{userId}, #{number}, #{status}, #{orderTime},
 #{createTime}, #{createUser}, #{updateTime}, #{updateUser}, ...)
```
- 最终落库时已经带上“谁创建 / 谁更新 / 什么时候”的信息。

👉 项目中所有带 @AutoFill(OperationType.INSERT/UPDATE) 的 Mapper 方法，就不用每次手写这些字段了。

⸻

3️⃣ “AOP + JWT + ThreadLocal + 全局异常处理” 面试题训练（附参考答案）

我给你 8 题，够你在一次面试里反复用 🔁

⸻

# Q1. 你们项目里怎么做用户身份识别的？（JWT + ThreadLocal）

答：

We use JWT for stateless authentication.
When a user logs in successfully, we generate a JWT using a secret key defined in our JwtProperties.
On each request, a login interceptor extracts the token from the header, validates it, and parses the user id.
Then we store the user id in a BaseContext using ThreadLocal.
This way, any layer (service, aspect) in the same request thread can call BaseContext.getCurrentId() to know the current user without passing the id through every method parameter.

⸻

# Q2. 为什么要用 ThreadLocal 存 userId，而不是放在一个静态变量里？

答：

A static variable is shared by all threads, which would cause different users to overwrite each other’s data.
ThreadLocal provides a separate copy of the variable for each thread,
so in a web application each request thread has its own user id.
This guarantees thread safety and correct user context.

⸻

# Q3. 你们项目里 AOP 用在了哪些地方？

答：

In this project we mainly use Spring AOP for automatic audit field filling.
We created a custom annotation @AutoFill on mapper methods.
The AutoFillAspect intercepts those methods before execution,
reads the operation type (INSERT/UPDATE),
and uses reflection to set fields like createTime, createUser, updateTime, updateUser using the current user id from BaseContext.
This removes duplicated code in services and keeps audit logic centralized.

⸻

# Q4. 你如何设计全局异常处理？（BaseException + Result）

答：

We define a custom BaseException that extends RuntimeException,
and several business exceptions like OrderBusinessException, ShoppingCartBusinessException extend BaseException.
Then we use a @ControllerAdvice GlobalExceptionHandler with @ExceptionHandler(BaseException.class)
to catch these exceptions and return a unified JSON response using our Result wrapper,
for example Result.error(e.getMessage()).
This makes error handling consistent and keeps controllers clean.

⸻

# Q5. AOP 和 Filter / Interceptor 有什么区别？你为什么在 AutoFill 里选择 AOP？

答：

Filters and interceptors work at the web layer, around HTTP requests and responses.
AOP works at the method level inside the application,
for example around service or mapper methods.
Our AutoFill logic is tightly coupled with database operations on mapper methods,
so using AOP with a pointcut on com.sky.mapper.* is more accurate and avoids mixing persistence concerns into web filters or interceptors.

⸻

# Q6. 你怎么确保 BaseContext 不会造成内存泄漏？

答：

After storing the user id in BaseContext at the beginning of a request,
we call BaseContext.removeCurrentId() in an interceptor’s afterCompletion method.
This clears the ThreadLocal for that thread,
which is important in thread pools like Tomcat threads to prevent memory leak and data pollution between requests.

⸻

# Q7. JWT 配置是怎么注入的？为什么用 @ConfigurationProperties？

答：

We define a JwtProperties class annotated with @Component and @ConfigurationProperties(prefix = "sky.jwt").
Spring Boot automatically binds properties from application.yml under sky.jwt to this class,
such as admin-secret-key, admin-ttl, and admin-token-name.
Then we can inject JwtProperties anywhere using @Autowired and avoid hard-coded values.
This makes our code more flexible and convenient to configure in different environments.

⸻

# Q8. 请你整体介绍一下这个项目的异常 + 认证 + 审计设计（综合大题）

你可以按这个模板回答（面试官会很喜欢）👇

For authentication we use JWT plus a JwtProperties configuration class to read secret keys and token TTL from application.yml.
A login interceptor validates the token, parses the user id, and stores it in BaseContext using ThreadLocal.
For auditing, we built an @AutoFill annotation and an AutoFillAspect that intercepts mapper methods and automatically fills fields like createTime, createUser, updateTime, and updateUser based on the user id in BaseContext.
For error handling, we defined a BaseException hierarchy and a GlobalExceptionHandler with @ControllerAdvice to convert exceptions into a unified Result response.
Together, JWT, ThreadLocal, AOP, and global exception handling give us a clean and consistent backend architecture.

⸻

如果你愿意，下一步我可以帮你：
- 写一个 GlobalExceptionHandler 完整类（直接可用）
- 帮你把 上面所有回答整理成一页“项目架构自我介绍稿”，你面试可以直接背
- 拿 Orders / Category / User 任意一个类，我们从 HTTP → Controller → Service → Mapper → AOP → DB 全流程再“走一遍题” 💪
