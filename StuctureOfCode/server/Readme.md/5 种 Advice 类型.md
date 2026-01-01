很好 👍 我来把这三块内容都补充完整（全部 **Markdown 可直接粘贴**）

---

## ✅ 5 种 Advice 类型


| Advice 类型 | 触发时机 | 说明 |
|-------------|----------|------|
| Before | 方法执行前 | 常用于权限、日志、参数校验 |
| After | 方法执行后（无论是否异常） | 常用于清理资源 |
| AfterReturning | 方法正常返回后 | 可拿到返回值做处理 |
| AfterThrowing | 方法抛异常时 | 统一异常/报警 |
| Around | 方法执行前后都包裹 | 最强大，可控制是否执行目标方法 |


---

## ✅ Spring AOP 示例（概念级，不依赖具体框架配置）

```markdown
1. 定义 Aspect
   - 日志切面 LogAspect

2. 定义 Pointcut
   - 拦截 service 层所有 public 方法
   - execution(* com.example.service.*.*(..))

3. 定义 Advice
   - Before：打印方法名和参数
   - AfterReturning：打印返回值
   - Around：统计执行时间
```

**一句话理解**

> AOP = 找地方(pointcut) ➜ 定规则(aspect) ➜ 写要做的事(advice)

---

## ✅ AOP vs 拦截器 vs 过滤器（高频面试题）


| 对比项 | AOP | 拦截器 (Interceptor) | 过滤器 (Filter) |
|--------|-----|-----------------------|------------------|
| 所属层 | Spring | Spring MVC | Servlet 规范 |
| 作用范围 | 方法级 | Controller 级 | Web 请求级 |
| 粒度 | 细 | 中 | 粗 |
| 本质 | 代理 | 反射 | 回调链 |
| 典型用途 | 日志、事务、权限 | 登录校验 | 编码、安全、跨域 |


**记忆口诀**

* Filter：过大门 🚪（最外层）
* Interceptor：过大厅 🚶（MVC 层）
* AOP：进房间 🛋（具体方法）

---

如果你愿意，我还能继续补充👇
🟢 事务为什么必须用 AOP
🟢 @Transactional 底层原理
🟢 JDK 动态代理 vs CGLIB
🟢 常见面试陷阱（自调用事务失效）
