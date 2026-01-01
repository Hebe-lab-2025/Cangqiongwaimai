
好～那我一口气都给你：讲 BaseContext + 画 AOP 自动填充流程图 + 给一点面试话术和小练习 👍

⸻

1️⃣ BaseContext 是啥？为什么要用它？
```
public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
```
它在干嘛？
- 	ThreadLocal<Long>：给每个线程一份独立的 userId
- 	不同请求 = 不同线程 = 不同的 ThreadLocal 值

典型使用流程
# 1.	登录成功时（在拦截器里）：
```
Long userId = 解析 JWT 得到的用户 id;
BaseContext.setCurrentId(userId);
```

# 2.	业务代码里任何地方（同一个请求线程）：
```
Long userId = BaseContext.getCurrentId();
// 用来填充 createUser / updateUser / 查询自己的订单等
```

# 3.	请求结束 / 拦截器的 afterCompletion

```BaseContext.removeCurrentId();```



👉 好处：
- 	不需要每层方法都传 userId 参数
- 	AutoFillAspect、OrderServiceImpl 等地方直接 BaseContext.getCurrentId() 就能拿到当前登录用户

⸻

2️⃣ AOP 自动填充整体流程图（Mermaid）

你可以直接把这段粘到 Markdown 预览里看图：
```
flowchart TD

A[Controller 接收请求] --> B[Service 调用]
B --> C[Mapper 方法<br/>如: orderMapper.insert(orders)]

C --> D{是否在 com.sky.mapper 包下<br/>并且带 @AutoFill ?}
D -- 否 --> E[直接执行 SQL<br/>不做任何处理]
D -- 是 --> F[进入 AutoFillAspect.autoFill()]

F --> G[通过 JoinPoint 拿到 MethodSignature]
G --> H[读取 @AutoFill 上的 OperationType<br/>INSERT / UPDATE]
F --> I[从 JoinPoint 拿参数 args[0]<br/>得到 entity 实体对象]

H --> J{OperationType}
J -- INSERT --> K[准备 now / currentId<br/>设置: createTime, createUser,<br/>updateTime, updateUser]
J -- UPDATE --> L[准备 now / currentId<br/>设置: updateTime, updateUser]

K --> M[反射 invoke setXXX(...) 给 entity 赋值]
L --> M[反射 invoke setXXX(...) 给 entity 赋值]

M --> N[返回 Mapper 原方法继续执行]
N --> O[MyBatis 生成 SQL 并执行]
O --> P[数据表中自动有审计字段]
```
你可以怎么理解：
```
AutoFillAspect 像一个“前台小助手”，
每次你拿着订单（entity）去 Mapper 插入 / 更新之前，
它先帮你把时间、操作人填好，再让你去数据库窗口办事。
```
⸻

3️⃣ AutoFill 简化版示例（帮助你自己写）

```比如你想做一个最简版，只自动填充 updateTime：```

# 1. 注解
```
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleAutoFill {
}
```
# 2. 切面
```
@Aspect
@Component
public class SimpleAutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.SimpleAutoFill)")
    public void simplePointCut(){}

    @Before("simplePointCut()")
    public void fillUpdateTime(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) return;

        Object entity = args[0];
        LocalDateTime now = LocalDateTime.now();

        try {
            Method setUpdateTime = entity.getClass()
                    .getDeclaredMethod("setUpdateTime", LocalDateTime.class);
            setUpdateTime.invoke(entity, now);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
# 3. Mapper 上使用
```
@SimpleAutoFill
void update(Dish dish);
```
👉 这样就能感受到：注解 + AOP 切面 + 反射 的组合味道。

⸻

# 4️⃣ AOP + AutoFill 面试回答模板（你可以背 😄）

# Q：你们项目里有用到 Spring AOP 吗？用来做什么？

A：
In my project we use Spring AOP to implement automatic audit field filling.
We created a custom annotation @AutoFill on MyBatis mapper methods.
An aspect AutoFillAspect intercepts mapper calls before execution,
reads the operation type like INSERT or UPDATE from the annotation,
gets the current user id from a BaseContext ThreadLocal,
and then uses reflection to set common fields such as createTime, createUser, updateTime, and updateUser.
This avoids duplicated code in services and keeps our audit logic consistent and centralized.

如果你想中文版本：

我们在项目里用 Spring AOP 做公共字段自动填充。
自定义了 @AutoFill 注解标在 Mapper 方法上，
切面 AutoFillAspect 在执行 Mapper 之前拦截，
根据注解里的操作类型（INSERT/UPDATE），
从 BaseContext 的 ThreadLocal 里取当前用户 id，
再通过反射给实体的 createTime/createUser/updateTime/updateUser 赋值。
这样业务代码不用反复写这些字段，审计逻辑也统一、好维护。

⸻

# 5️⃣ 小练习题（你可以自己试着答）

我给你 3 个练习问题，巩固思路：
1.	为什么 BaseContext 要用 ThreadLocal 存 userId，而不是一个 static 全局变量？
2.	如果一个 Mapper 方法有两个参数（比如 entity + 其他条件），AutoFillAspect 现在只拿 args[0]，可能会有什么问题？怎么改进？
3.	AutoFillAspect 通过反射找 setCreateTime，如果某个实体没有这个方法，会发生什么？你会怎么防御？

你可以先自己想一想，再发给我，我可以帮你改成面试官会很喜欢的答案。

⸻

如果你还想更深入，我可以接着帮你：
- 	🧭 再画一张 “Controller → Service → Mapper → Aspect → DB” 总体架构图
- 	🔍 结合 某个实体（如 Orders），帮你把 AutoFill 效果从请求 → SQL 全流程讲一遍
- 	🧪 做一整套 “AOP + JWT + ThreadLocal + 全局异常处理” 的面试题训练
