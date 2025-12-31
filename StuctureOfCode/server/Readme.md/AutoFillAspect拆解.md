好，这个 AutoFillAspect 我给你拆成「做什么」「在哪儿拦」「怎么填」，
看完你就能自己讲给面试官了。😄

⸻

1️⃣ 整体干嘛用？

一句话：
AutoFillAspect 是一个 AOP 切面，在执行 Mapper 上带 @AutoFill 的方法之前，
自动给实体的公共字段赋值，比如：
- 	createTime
- 	updateTime
- 	createUser
- 	updateUser

所以 Service/Mapper 代码里不用每次手写 setXX，只要加注解就行。

⸻

2️⃣ 顶部几个注解
```
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
```
- 	@Aspect：声明这是一个 切面类（里面有切点 + 通知）。
- 	@Component：交给 Spring 容器管理，AOP 才能生效。
- 	@Slf4j：Lombok 帮你生成 log 日志对象。

⸻

3️⃣ 切入点：拦哪些方法？
```
@Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
public void autoFillPointCut(){}
```
这句话可以拆开理解：
# 1.	execution(* com.sky.mapper.*.*(..))
- 	包：com.sky.mapper
- 	任意类：*
- 	任意方法：*
- 	任意参数：(..)
👉 也就是：所有 Mapper 的方法


# 2.	&& @annotation(com.sky.annotation.AutoFill)
- 	并且方法上还要 标了 @AutoFill 注解

综合：

只拦截：com.sky.mapper 包下，且方法上有 @AutoFill 的 Mapper 方法。
（比如 @AutoFill(INSERT) 的 insert(Employee employee)）

⸻

4️⃣ 前置通知：执行 Mapper 前先自动填字段
```
@Before("autoFillPointCut()")
public void autoFill(JoinPoint joinPoint){
    log.info("开始进行公共字段自动填充...");
```
- 	@Before：前置通知，在目标方法（Mapper）执行之前调用。
- 	JoinPoint：当前被拦截的方法调用的上下文（方法名、参数等）。

⸻

5️⃣ 先搞清楚：本次是 INSERT 还是 UPDATE？

```
MethodSignature signature = (MethodSignature) joinPoint.getSignature();
AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
OperationType operationType = autoFill.value();
```
流程：
```
	1.	从 JoinPoint 拿到方法签名 MethodSignature
	2.	再从方法上读出 @AutoFill 注解对象
	3.	注解里有枚举属性：OperationType value()
👉 得到 INSERT 或 UPDATE
```
所以是不是插入/更新，是由注解参数决定的，不是靠方法名猜。

⸻

6️⃣ 拿到实体对象（参数）
```
Object[] args = joinPoint.getArgs();
if(args == null || args.length == 0){
    return;
}

Object entity = args[0];
```
- 	joinPoint.getArgs()：拿到该 Mapper 方法的 所有实参。
- 	约定：第一个参数就是实体对象（比如 Employee、Dish、Setmeal）。
- 	所以后面全是对 entity 做反射调用。

⸻

7️⃣ 准备要填的值：时间 + 当前用户
```
LocalDateTime now = LocalDateTime.now();
Long currentId = BaseContext.getCurrentId();
```
- 	now：当前时间，用来填 createTime / updateTime
- 	currentId：从 BaseContext（ThreadLocal）里拿当前登录用户 id
👉 在登录拦截器里已经 BaseContext.setCurrentId(...) 过了

⸻

8️⃣ INSERT：填 4 个公共字段
```
if(operationType == OperationType.INSERT){
    try {
        Method setCreateTime = entity.getClass()
            .getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
        Method setCreateUser = entity.getClass()
            .getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
        Method setUpdateTime = entity.getClass()
            .getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
        Method setUpdateUser = entity.getClass()
            .getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

        setCreateTime.invoke(entity,now);
        setCreateUser.invoke(entity,currentId);
        setUpdateTime.invoke(entity,now);
        setUpdateUser.invoke(entity,currentId);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```
关键点：
- 	用 entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class) 找 setter 方法
这里 "setCreateTime" 等字符串从 AutoFillConstant 里拿，避免魔法字符串。
- 	再用 invoke(entity, 值) 真正调用 setter。

等价效果：
```
entity.setCreateTime(now);
entity.setCreateUser(currentId);
entity.setUpdateTime(now);
entity.setUpdateUser(currentId);
```
只是改成用 反射 + 常量，做到对所有实体通用。

⸻

9️⃣ UPDATE：只填 2 个字段
```
}else if(operationType == OperationType.UPDATE){
    try {
        Method setUpdateTime = entity.getClass()
            .getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
        Method setUpdateUser = entity.getClass()
            .getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

        setUpdateTime.invoke(entity,now);
        setUpdateUser.invoke(entity,currentId);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```
更新时逻辑：
- 	不改 createTime / createUser
- 	只更新 updateTime / updateUser

⸻

🔚 总结一句（你可以直接背）

AutoFillAspect 是一个 AOP 切面，
通过 @Pointcut 拦截 Mapper 中标注了 @AutoFill 的方法，
在方法执行前根据操作类型（INSERT/UPDATE），
使用反射统一为实体对象的 createTime、updateTime、createUser、updateUser 字段赋值，
从而减少重复代码，统一维护公共字段逻辑。

⸻

如果你愿意，下一步我可以帮你：
- 	用 具体 Mapper 方法 走一遍完整流程（下断点视角一步步看）
- 	画一张「Controller → Service → Mapper → AutoFillAspect → DB」的时序图
- 	或者帮你改成更安全一点的版本（不 printStackTrace，用 log + 兜底逻辑）
